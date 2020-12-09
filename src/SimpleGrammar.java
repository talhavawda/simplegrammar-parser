import java.util.*;

public class SimpleGrammar {

	//FIELDS
	private Set<Character> variables;
	private Set<Character> terminals;
	private static Set<Character> specialTerminals;

	private boolean firstRule = true; //is the current rule to be added the first rule - to determine whether to make the LHS variable of a Production, the Start Variable
	private Character startVariable;

	/*
		Each key in the map is a Variable on the LHS of a Production Rule,
		and the value is the list of RHS of Production Rules that it generates

		Inner Map: (its a Map cos for each LHS Variable, the terminals on the RHS are unique (1 instance for each terminal))
			key: Character is the terminal on RHS
			value: String is Variables on RHS
	 */
	private Map<Character, Map<Character, String>> productionRules;



	public SimpleGrammar() {
		variables = new LinkedHashSet<>(); //LinkedHashSet so that the variables are stored in insertion order (S, A, B...)
		terminals = new TreeSet<>(); //TreeSet so that the terminals are stored in alphabetical order
		productionRules = new LinkedHashMap<>(); //LinkedHashMap so that the keys (Variables on LHS) are stored in insertion order (S, A, B...)

		if (specialTerminals == null) {
			//if this is the first grammar for the program then specialTerminals woudln't have been initialised yet, so initialise it
			specialTerminals = new HashSet<Character>();
			specialTerminals.add('{');
			specialTerminals.add('}');
			specialTerminals.add('+');
			specialTerminals.add('=');
			//TODO - add more
		}
	}

	public void addStartVariable(Character start){
		this.startVariable = start;
	}

	public void addProductionRule(String production) throws InvalidProductionException {

		String lhs = "";
		String rhs = "";

		/*
			Ensure that the production is a valid Simple-Grammar Production Rule
		 */

		if (production.contains("->")) {
			//this is a valid Production Rule (in general)
			//in the TestData.txt file, there are 2 whitespace characters on either side of the '->' (rewrite symbol)
			lhs = production.substring(0, production.indexOf(" "));
			rhs = production.substring(production.lastIndexOf(" ") + 1);
		} else {
			throw new InvalidProductionException(production, InvalidProductionException.NO_REWRITE);
		}

		if (lhs.length() == 0 | rhs.length() == 0) {
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_PRODUCTION);
		}

		if (lhs.length() > 1) {
			throw new InvalidProductionException(production, InvalidProductionException.NOT_CONTEXT_FREE);
		}

		Character lhsChar = lhs.charAt(0);


		if (lhsChar < 'A' || lhsChar > 'Z' ) { //ALT: Character.isUpperCase(lhsChar)
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_VARIABLE);
		}


		//RHS must start with a single terminal

		Character terminal = rhs.charAt(0);

		if ((terminal < 'a' || terminal > 'z') && !specialTerminals.contains(terminal)) {
			//ALT: if (!Character.isLowerCase(terminal) && Character.isLetter(terminal))
				//!Character.isLetter(terminal) - means that special terminals are all characters that are not a letter
					//for the current condition, a special terminal is one that is is in the specialTerminals array
				//and with lowercase letters being a terminal, a character is not a terminal if it is a uppercase letter (a Variable)
				//and such this (ALT) condition only becomes true if 'terminal' variable is an uppercase letter -> thus invalid terminal

			throw new InvalidProductionException(production, InvalidProductionException.INVALID_TERMINAL);
		}

		if (rhs.length() > 1) {
			//If RHS contains more than 1 character, then the remaining (besides the first) must all be Variables

			for (int i = 1; i < rhs.length(); i++) {

				Character variable = rhs.charAt(i);

				if (variable < 'A' || variable > 'Z') { //ALT: Character.isUpperCase(variable)
					throw new InvalidProductionException(production, InvalidProductionException.INVALID_VARIABLE);
				}

			}
		}



		/*
			Add Production Rule (if we reach this point, then the Production Rule is a valid Simple-Grammar Production)
		 */

		this.variables.add(lhsChar); //Since variables is a Set, if it already contains lhsChar, it won't re-add it | only adding variables from LHS of rules, because if its on the right of a rule, then its guaranteed to be on the left of a rule
		this.terminals.add(terminal); //Since terminals is a Set, if it already contains terminal, it won't re-add it

		if (firstRule == true) {
			this.startVariable = lhsChar;
			firstRule = false;
		}

		Map<Character, String> rhsMap;

		if (!productionRules.containsKey(lhsChar)) {
			rhsMap = new TreeMap<Character, String>(); //TreeMap so that the RHS side for this Variable is in ascending order of terminals
		} else {
			rhsMap = productionRules.get(lhsChar); //get value
		}


		if (!rhsMap.containsKey(terminal)) {
			rhsMap.put(terminal, rhs.substring(1));
		} else { //if for this Variable on the LHS of a production, it already has this same terminal on the RHS of the production, then this terminal cannot occur again on the RHS of another production for this LHS variable
			throw new InvalidProductionException(production, InvalidProductionException.NOT_SIMPLE);
		}

		productionRules.put(lhsChar, rhsMap);


	}


	public void parseString(String s) {

		System.out.println("\nParsing the input string '" + s + "' using leftmost derivation:");

		if (s.length() == 0) {
			System.out.println("\tThe input string is empty and as such does not belong to the language\nthat this grammar defines as there is no epsilon Production Rule");
		}


		Stack<Character> sententialFormVariables = new Stack<Character>();
		String sententialFormTerminals = "";
		//String derivation = "";

		sententialFormVariables.push(this.startVariable);
		System.out.print("\t" + this.startVariable + "\t=>\t");
		//derivation += "\t" + this.startVariable + "\t->\t";

		boolean acceptString = true; //whether this input string should be accepted (if it belongs to the language) or not

		/*
			For each character in the input string
		 */
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);

			if (sententialFormVariables.isEmpty()) {
				System.out.println("\n\nThe input string has not yet been exhausted but there are no Variables remaining in the sentential form to match to the LHS of a Production Rule");
				System.out.println("Thus, this input string '" + s + "' does not belong to the language that this grammar defines");
				acceptString = false;
				break;
			}

			Character leftmostVariable = sententialFormVariables.pop();

			Map<Character, String> rhsProductionsMap = productionRules.get(leftmostVariable);


			if (rhsProductionsMap.containsKey(c)) {

				sententialFormTerminals += c;
				String rhsVariables = rhsProductionsMap.get(c);

				for (int j = rhsVariables.length()-1; j >= 0; j--) { //we want the leftmost variable in rhsVariables to be at the top of the stack, so we push it last
					Character v = rhsVariables.charAt(j);
					sententialFormVariables.push(v);
				} //if rhsVariables is the empty string (Production rewrites to a terminal only) then this loop wont run and no Variables get pushed to the stack (which is what we want)


				/*
					Display this derivation step
				 */

				List<Character> sfVariablesList = new ArrayList<>(sententialFormVariables); //get a list of the stack so that we can display the sentential form

				/* The top of the stack is at the end of the list, so we have to traverse the list backwards*/

				String sfVariablesString = "";
				for (int j = sfVariablesList.size()-1; j >= 0; j--) {
					sfVariablesString += sfVariablesList.get(j);
				}

				System.out.print(sententialFormTerminals + sfVariablesString);

				if (i < s.length()-1) { //if not the last derivation, then put the derivation symbol for the next derivation
					System.out.print("\n\t\t=>\t");
				}


			} else  {
				//there is no matching production rule with leftmostVariable on LHS and c being the terminal on RHS
				//this string doesnt belong to the language that the grammar defines

				System.out.println("\n\nThere doesn't exist a Production Rule with Variable '" + leftmostVariable + "' on the LHS and the RHS starting with terminal '" + c + "'");
				System.out.println("Thus, this input string '" + s + "' does not belong to the language that this grammar defines");
				acceptString = false;
				break;
			}



		}

		if (!sententialFormVariables.isEmpty()) {
			System.out.println("\n\nThe input string has been exhausted but there are still Variables remaining in the final sentential form");
			System.out.println("Thus, this input string '" + s + "' does not belong to the language that this grammar defines");
			acceptString = false;
		}

		if (acceptString == true) {
			System.out.println("\n\nDerivation Complete (The string derived is equal to the input string)");
			System.out.println("Thus, this input string '" + s + "' belongs to the language that this grammar defines");
		}
	}

	@Override
	public String toString() {

		String result = "Simple Grammar:" + "\n\tVariables = " + variables + "\n\tTerminals = " + terminals + "\n\tStart Variable = " + startVariable +  "\n\tProduction Rules:\n";

		Set<Map.Entry<Character, Map<Character, String>>> productionRulesSet = productionRules.entrySet();

		for (Map.Entry<Character, Map<Character, String>> lhsProductionRules: productionRulesSet) {
			Character lhs =  lhsProductionRules.getKey();


			Map<Character, String> rhsProductionsMap = lhsProductionRules.getValue();
			Set<Map.Entry<Character, String>> rhsProductionsSet = rhsProductionsMap.entrySet(); //The RHS side of the Production Rules involving this LHS

			for (Map.Entry<Character, String> rhsProduction: rhsProductionsSet) {
				Character terminal = rhsProduction.getKey();
				String variables = rhsProduction.getValue();
				result += "\t\t" + lhs + " -> " + terminal + variables + "\n";
			}


		}

		return result;
	}
}
