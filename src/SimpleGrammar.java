import java.util.*;

/*
	A class representing a Simple Grammar

	Simple (LL(1)) Grammars are a subset of Context-Free Grammars

	Simple-Grammar Constraints:
		-   There is a single Variable on the LHS of a Production Rule
		-   The RHS of a Production Rule begins with a single Terminal and is followed by 0 or more Variables
				--   If RHS contains only 1 character symbol then it is a single Terminal
				--   If RHS contains more than 1 character symbol, then the  subsequent character symbols after single the Terminal must all be Variables
		-   If there are two or more Production Rules with the same Variable on the LHS, them the RHS for each of
			these Production Rules must begin with a different Terminal
				--  A Terminal can only be used/appear once on the RHS for a Variable on the LHS
					-- A (LHS Variable, Terminal) paring is unique
						--  A Production Rule that starts with Variable A on the LHS will have a unique terminal at the start of the RHS
							Thus the Parsing Algorithm will be deterministic


	Assignment Specification:
		Valid Variables are uppercase alphabetic single-characters
		Valid Terminals are lowercase alphabetic single-characters and special single-characters (defined by us)
		There are no epsilon Production Rules
 */

public class SimpleGrammar {

	//FIELDS
	private Set<Character> variables;   //The set of Variables of this Grammar
	private Set<Character> terminals;   //The set of Terminals of this Grammar


	/*
		To keep track of whether current Production Rule that is being added is the first Production Rule for this grammar
		so that we can determine whether to make the LHS variable of a Production, the Start Variable
	 */
	private boolean firstRule = true;

	private Character startVariable; //The Start Variable of this Grammar


	/*
		The Production Rules of this Grammar

		We want to represent the Production Rules such that it is easy and efficient to find the Production Rule
		to apply next in the left-most derivation of a string

		In a left-most derivation, we get the leftmost Variable in the current sentential form, and find the Production Rule
		that has this leftmost Variable as the LHS Variable, the next input symbol as the Terminal that the RHS begins with,
		and the Variable(s) on the RHS of this Production Rule (if it exists) replaces the leftmost Variable in the current sentential form

		So for each Variable, we want to store the Production Rules where this Variable is the LHS Variable for those Production Rules
		The most efficient Data Structure for this is a Map with the keys being the Variables (A key represents the
		Character of the LHS Variable of a Production Rule) and we can the value for a key being a list of all the RHSs of Production Rules that this LHS Variable generates
		A Map will allow us to efficiently retrieve all the Production Rules for the leftmost Variable in the current sentential form (where it is the LHS Variable)
		In the Constructor, we use a LinkedHashMap to represent this Map of Production Rules so that the keys (LHS variables) are stored in insertion order (S, A, B...)

		However, since for a specific key (Variable on LHS of a Production Rule), (the value is the list of Production Rule RHSs)
		the first character symbol of the RHS of the Production Rules must be a Terminal, and the Terminals must be unique
		for each Production Rule RHSs, so instead of the value being a simple list of Strings, it is more efficient for it to be a Map
		where the key is the Terminal character/symbol that the RHS of the Production Rule starts with, and the value is the String of  Variables that follow this
		Terminal on the RHS
		So the value of the Production Rules Map is itself a Map
		This ensures that for a LHS Variable, there is (at most) only 1 Production Rule that begins with a specific Terminal
		and it allows us to efficiently retrieve the Variables on the RHS of the Production Rule whose LHS Variable is the
		leftmost Variable in the current sentential form and Terminal is the next input symbol
			-   We will not have to do any searching as there will only be one Entry to match the LHS Variable and RHS Terminal
				and the Map method that does the retrieval does it efficiently as possible

		Thus this Map of <Character, <Map of Character,String> is the most efficient way that we can use to represent
		and retrieve the Production Rules for this Grammar


		productionRules:
			A Map with Key being of type Character representing a LHS Variable of a Production Rule and the
			Value being an Inner Map (that represents the list of RHSs of Production Rules that the Key generates),
			with the Key of the Inner Map being of type Character that represents a Terminal (that the RHS of the Production Rule starts with)
			and the Value of the Inner Map being of type String that represents the Variables of the RHS of the Production Rule
			that follow the Terminal
	 */
	private Map<Character, Map<Character, String>> productionRules;


	/*
		The set of special single-character valid Terminals (in addition to the lowercase alphabetic characters)
		It will be initialised in the Constructor

		It is static as it applies to all SimpleGrammar objects for this program
	 */
	private static Set<Character> specialTerminals;


	/**
	 * CONSTRUCTOR
	 */
	public SimpleGrammar() {
		variables = new LinkedHashSet<>(); //LinkedHashSet so that the variables are stored in insertion order (S, A, B...)
		terminals = new TreeSet<>(); //TreeSet so that the terminals are stored in alphabetical order
		productionRules = new LinkedHashMap<>(); //LinkedHashMap so that the keys (Variables on LHS) are stored in insertion order (S, A, B...)

		if (specialTerminals == null) {
			//if this is the first grammar for the program then specialTerminals wouldn't have been initialised yet, so initialise it

			specialTerminals = new HashSet<Character>();
			/*
				Special Terminals are the 10 digits, the 4 base arithmetic and equalto operators and opening and closing braces
			 */
			specialTerminals.add('{');
			specialTerminals.add('}');
			specialTerminals.add('+');
			specialTerminals.add('-');
			specialTerminals.add('*');
			specialTerminals.add('/');
			specialTerminals.add('0');
			specialTerminals.add('1');
			specialTerminals.add('2');
			specialTerminals.add('3');
			specialTerminals.add('4');
			specialTerminals.add('5');
			specialTerminals.add('6');
			specialTerminals.add('7');
			specialTerminals.add('8');
			specialTerminals.add('9');

		}
	}


	public void addStartVariable(Character start){
		this.startVariable = start;
	}

	/**
	 *
	 * @param production - The Production Rule to be added to this SimpleGrammar (if it is a valid Simple-Grammar Production Rule )
	 * @throws InvalidProductionException if the Production Rule is not a Valid Simple-Grammar Production Rule
	 */

	public void addProductionRule(String production) throws InvalidProductionException {

		String lhs = ""; //the LHS of the Production Rule
		String rhs = ""; //the RHS of the Production Rule


		/*
			Ensure that this Production Rule is a valid Simple-Grammar Production Rule

				If this Production Rule violates any of the Simple-Grammar Production Rule constraints, then it shouldnt
				be added to this Simple Grammar, thus throw an InvalidProductionException (which this method returns -> the code below the Exception throw wont be executed)


				If this Production Rule is not a valid simple-grammar Production Rule (i.e. this Production Rule
				violates any one of the constraints of a simple-grammar), then this grammar is not a valid simple-grammar
				and we should discard it
					-   NOTE: this is done in the RunAssignment class
						-   It will know that this grammar is not a valid simple-grammar by this method throwing a InvalidProductionException
		 */

		if (production.contains("->")) { //if the Production Rule contains the Rewrite Symbol
			//this is a valid Production Rule (in general)

			//in the TestData.txt file, there's a whitespace character on either side of the '->' (rewrite symbol)
			lhs = production.substring(0, production.indexOf(" ")); //get LHS of Production Rule
			rhs = production.substring(production.lastIndexOf(" ") + 1); //get RHS of Production Rule

		} else {
			//This Production Rule is invalid as it doesn't contain the Rewrite Symbol
			throw new InvalidProductionException(production, InvalidProductionException.NO_REWRITE);
		}

		if (lhs.length() == 0 | rhs.length() == 0) {
			//This Production Rule is invalid as either the LHS or RHS contains no character symbols (Is empty)
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_PRODUCTION);
		}

		if (lhs.length() > 1) {
			//This Production Rule is invalid as it contains more than one symbol on the LHS
			// -> It is not a Context-Free Production Rule, and neither can be a Simple-Grammar Production Rule
			throw new InvalidProductionException(production, InvalidProductionException.NOT_CONTEXT_FREE);
		}

		Character lhsChar = lhs.charAt(0); //get the Variable on the LHS (There should only be 1 -> ensured by the above if statement)


		if (lhsChar < 'A' || lhsChar > 'Z' ) {
			//This Production Rule is invalid as the character symbol on the LHS is not a Valid Variable
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_LHS_VARIABLE);
		}


		//RHS must start with a single Terminal

		Character terminal = rhs.charAt(0);

		if ((terminal < 'a' || terminal > 'z') && !specialTerminals.contains(terminal)) { //if not a lowercase alphabetical character and also not a special terminal
			//This Production Rule is invalid as the first character symbol on the RHS is not a Valid Terminal
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_TERMINAL);
		}

		if (rhs.length() > 1) {
			//If RHS contains more than 1 character, then subsequent characters must all be Variables

			for (int i = 1; i < rhs.length(); i++) {

				Character variable = rhs.charAt(i);

				if (variable < 'A' || variable > 'Z') {
					//This Production Rule is invalid as a subsequent character symbol on the RHS (after the Terminal) is not a Valid Variable
					throw new InvalidProductionException(production, InvalidProductionException.INVALID_RHS_VARIABLE);
				}

			}
		}



		/*
			(Attempt to) Add Production Rule (if we reach this point, then the Production Rule is a valid Simple-Grammar Production, unless
			the (LHS Variable, Terminal) pair has alreadt occurred for a previous Production Rule
		 */

		this.variables.add(lhsChar); //Since variables is a Set, if it already contains lhsChar, it won't re-add it | only adding variables from LHS of rules, because if its on the right of a rule, then its guaranteed to be on the left of a rule
		this.terminals.add(terminal); //Since terminals is a Set, if it already contains terminal, it won't re-add it

		if (firstRule) { //if this is the first Production Rule of this Grammar, then make the LHS Variable of this Rule the Start Variable of the Grammar
			this.startVariable = lhsChar;
			firstRule = false;
		}

		Map<Character, String> rhsMap; //The Inner Map representing the RHSs of Production Rules where key is the Terminal, and value is the Variables, for this Variable on the LHS of Production Rule

		if (!productionRules.containsKey(lhsChar)) {
			// If this Variable hasn't been on the LHS of a Production Rule yet, then create a Inner Map object to store the RHSs of Production Rules for which it is the LHS
			rhsMap = new TreeMap<Character, String>(); //TreeMap so that the RHS side for this Variable is in ascending order of terminals
		} else {
			rhsMap = productionRules.get(lhsChar); //get value (Inner Map) for this LHS Variable
		}

		/*
			If for this LHS Variable (Variable on the LHS of a Production Rule) there is no Production Rule where the
			RHS begins with this Terminal, then add this Production Rule (by adding its RHS to the rhsMap)
			Else if for this LHS Variable there is already a Production Rule where the RHS begins with this Terminal,
			then this Production Rule is invalid as this Terminal cannot occur again (since it has already occurred before for a Production Rule)
			on the RHS of another Production Rule for this LHS variable
		 */
		if (!rhsMap.containsKey(terminal)) {
			rhsMap.put(terminal, rhs.substring(1));
		} else {
			throw new InvalidProductionException(production, InvalidProductionException.NOT_SIMPLE); //the method returns this Exception and doesnt execute the code below
		}

		productionRules.put(lhsChar, rhsMap); //Since this Production Rule is Valid, add it to the Grammar


	}

	/**
	 *  Parse a string using left-most derivation to determine whether it belongs to the language defined by this Simple-Grammar or not
	 *  Each step in the left-most derivation is displayed to the console as it occurs
	 *
	 *  		-   If there are two or more Production Rules with the same Variable on the LHS, them the RHS for each of
	 * 			these Production Rules must begin with a different Terminal
	 * 				--  A Terminal can only be used/appear once on the RHS for a Variable on the LHS
	 * 					-- A (LHS Variable, Terminal) paring is unique
	 * 						--  A Production Rule that starts with Variable A on the LHS will have a unique terminal at the start of the RHS
	 * 							Thus the Parsing Algorithm will be deterministic
	 *
	 *
	 * @param s - the string to be parsed
	 */
	public void parseString(String s) {

		System.out.println("\nParsing the input string '" + s + "' using leftmost derivation:");

		if (s.length() == 0) {
			System.out.println("\tThe input string is empty and as such does not belong to the language\n\tthat this grammar defines as there is no epsilon Production Rule");
			return; //input string is invalid so code below shouldn't be executed (return void)
		}

		/*
			A Sentential Form is represented as follows:
				The Terminals part is represented using a String
				The Variables part is represented using a Stack (each Variable is of type Character)
					-   This allows for efficient retrieval of the left-most variable in the Sentential Form
						as it is at the top of the stack and can be popped off, and the new variables in the Sentential Form
						will be pushed (from Right to Left) onto the top of the Stack (such that the new Variable at the top
						of the Stack was the leftmost Variable in the RHS of the Production Rule applied, and also
						the  leftmost Variable in the current sentential form (this meets the algorithm for left-most derivation))
		 */
		String sententialFormTerminals = "";
		Stack<Character> sententialFormVariables = new Stack<Character>();


		sententialFormVariables.push(this.startVariable); //push the Start Variable onto the Stack

		System.out.print("\t" + this.startVariable + "\t=>\t"); //Displaying the left-most derivation each part/step at a time -> Display the current sentential form


		boolean acceptString = true; //whether this input string should be accepted (if it belongs to the language) or not

		/*
			For each character in the input string

				Whitespace is important -> it is treated as part of the string
					->  If a string contains whitespace and whitespace is not a special terminal (default), then the string doesn't belong to the language
		 */
		for (int i = 0; i < s.length(); i++) {
			Character c = s.charAt(i);

			/*
				If there are no more variables remaining in the Sentential Form then this means that no Production Rule
				can be applied, so this string must be rejected
			 */
			if (sententialFormVariables.isEmpty()) {
				System.out.println("\n\nThe input string has not yet been exhausted but there are no Variables remaining in the sentential form\n\tto match to the LHS of a Production Rule");
				System.out.println("Thus, this input string '" + s + "' does not belong to the language that this grammar defines");
				acceptString = false;
				break;
			}



			/*
				Get the RHSs of the Production Rules with the left-most variable in the sentential form as the LHS Variable of the Production Rules
			 */
			Character leftmostVariable = sententialFormVariables.pop();
			Map<Character, String> rhsProductionsMap = productionRules.get(leftmostVariable);


			/*
				 If any the RHSs of the Production Rules contain the current input symbol as the Terminal that the RHS starts with,
				 then apply that Production Rule to the current sentential form in the derivation to get the next sentential form
			 */
			if (rhsProductionsMap.containsKey(c)) {

				sententialFormTerminals += c; //add this input symbol character to the sentential form
				String rhsVariables = rhsProductionsMap.get(c); //get the RHS of the Production Rule that was applied

				for (int j = rhsVariables.length()-1; j >= 0; j--) { //we want the leftmost variable in rhsVariables to be at the top of the stack, so we push it last (we traverse the variables in reverse)
					Character v = rhsVariables.charAt(j);
					sententialFormVariables.push(v);
				} //if rhsVariables is the empty string (Production rewrites to a terminal only) then this loop won't run and no Variables get pushed to the stack (which is what we want)


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

				if (i < s.length()-1) { //if this is not the last derivation, then put the derivation symbol for the next derivation
					System.out.print("\n\t\t=>\t");
				}


			} else  {

				//There is no matching production rule with leftmostVariable on LHS and c being the terminal on RHS
				//Thus this string doesn't belong to the language that the grammar defines

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

		if (acceptString) { // if (acceptString == true)
			System.out.println("\n\nDerivation Complete (The string derived is equal to the input string)");
			System.out.println("Thus, this input string '" + s + "' belongs to the language that this grammar defines");
		}
	}

	/**
	 * Displays all the components of this Simple Grammar: its Variables, Terminals, Start Variable, and Production Rules
	 * @return
	 */
	@Override
	public String toString() {

		String result = "Simple Grammar:" + "\n\tVariables = " + variables + "\n\tTerminals = " + terminals + "\n\tStart Variable = " + startVariable +  "\n\tProduction Rules:\n";

		Set<Map.Entry<Character, Map<Character, String>>> productionRulesSet = productionRules.entrySet();

		//Display each Production Rule
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
