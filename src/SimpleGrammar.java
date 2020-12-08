import java.util.*;

public class SimpleGrammar {

	//FIELDS
	private Set<Character> variables;
	private Set<Character> terminals;
	private static Set<Character> specialTerminals;

	private boolean firstRule = false; //to determine whether to make the LHS variable of a Production, the Start Variable
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

		this.variables.add(lhsChar); //Since variables is a Set, if it already contains lhsChar, it won't re-add it
		this.terminals.add(terminal); //Since terminals is a Set, if it already contains terminal, it won't re-add it

		if (firstRule == false) {
			this.startVariable = lhsChar;
			firstRule = true;
		}

		Map<Character, String> rhsMap;

		if (!productionRules.containsKey(lhsChar)) {
			rhsMap = new TreeMap<Character, String>(); //TreeMap so that the RHS side for this Variable is in ascending order of terminals
		} else {
			rhsMap = productionRules.get(lhsChar);
		}


		//TODO - check if logic correct; rushed cos battery dying
		//rhsMap.put(terminal, rhs.substring(1)); //NOTE - if terminal already exists, this overwrites it. what we want is for Exception to be raised
		if (!rhsMap.containsKey(terminal)) {
			rhsMap.put(terminal, rhs.substring(1));
		} else {
			throw new InvalidProductionException(production, InvalidProductionException.NOT_SIMPLE);
		}

		productionRules.put(lhsChar, rhsMap);



	}


	public void parseString(String s) {

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
