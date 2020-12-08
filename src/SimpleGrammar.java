import java.util.*;

public class SimpleGrammar {

	//FIELDS
	Set<Character> variables;
	Set<Character> terminals;
	Character startVariable;

	/*
		Each key in the map is a Variable on the LHS of a Production Rule,
		and the value is the list of RHS of Production Rules that it generates
	 */
	Map<Character, List<String>> productionRules;

	public SimpleGrammar() {
		variables = new LinkedHashSet<>(); //LinkedHashSet so that the variables are stored in insertion order (S, A, B...)
		terminals = new TreeSet<>(); //TreeSet so that the terminals are stored in alphabetical order
		productionRules = new LinkedHashMap<>(); //LinkedHashMap so that the keys (Variables on LHS) are stored in insertion order (S, A, B...)
	}

	public void addStartVariable(Character start){
		this.startVariable = start;
	}

	public void addProductionRule(String production) throws InvalidProductionException {

		String lhs;
		String rhs;

		if (production.contains("->")) {
			//this is a valid Production Rule (in general)
			//in the TestData.txt file, there are 2 whitespace characters on either side of the '->' (rewrite symbol)
			lhs = production.substring(0, production.indexOf(" "));
			rhs = production.substring(0, production.lastIndexOf(" ")+1);
		} else {
			throw new InvalidProductionException(production, InvalidProductionException.NO_REWRITE);
		}

		if (lhs.length() > 1) {
			throw new InvalidProductionException(production, InvalidProductionException.NOT_SIMPLE);
		}

		Character lhsChar = lhs.charAt(0);

		if (lhsChar > 'Z' || lhsChar < 'A') {
			throw new InvalidProductionException(production, InvalidProductionException.INVALID_VARIABLE);
		}



	}





}
