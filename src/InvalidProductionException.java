import java.io.IOException;

/**
 *  An Exception class for Productions Rules that violate Simple-Grammar restrictions/constraints
 *  (i.e. they are not a valid Simple-Grammar Production Rule)
 *
 *  When attempting to add a Production Rule to the grammar in SimpleGrammar.addProductionRule(), if the Production Rule
 *  is not a valid Simple-Grammar Production Rule, then we don't want to add this Production Rule to the grammar, but
 *  rather inform the caller of addProductionRule() why the Production Rule that they are trying to add is not a
 *  valid Simple-Grammar Production Rule
 *
 *  So if a Production Rule is invalid, we throw an InvalidProductionException (an object of this class) with the invalid
 *  Production Rule, and the reason that it is invalid, so that the caller can know the reason and also display it
 */

public class InvalidProductionException extends IOException {

	String invalidProduction;   //The Production Rule that is an invalid simple-grammar Production Rule

	/*
		The type of InvalidProductionException - the reason why this Production Rule is an invalid simple-grammar Production
		-> it is assigned one of the constants below (they represent the types)
	 */
	String invalidType;


	/*
		These constants are the types of InvalidProductionException - each is a specific why a Production Rule is an invalid simple-grammar Production
	 */
	public static final String INVALID_PRODUCTION = "Production is not a valid Production Rule";
	public static final String NO_REWRITE = "Production doesn't contain the Rewrite (->) symbol";
	public static final String NOT_CONTEXT_FREE = "Production is not a valid Context-Free Grammar Production";
	public static final String NOT_SIMPLE = "Production is not a valid Simple-Grammar Production";
	public static final String INVALID_RHS_VARIABLE = "Production contains an invalid Variable on the RHS";
	public static final String INVALID_LHS_VARIABLE = "Production contains an invalid Variable on the LHS";
	public static final String INVALID_TERMINAL = "Production contains an invalid Terminal";


	/**
	 * Constructor for this  InvalidProductionException class
	 * @param production - the Production Rule that is invalid
	 * @param type - the type of InvalidProductionException - the specific reason why this production is invalid
	 */
	public  InvalidProductionException(String production, String type) {
		super(explanation(production, type));
		this.invalidProduction = production;
		this.invalidType = type;
	}



	public String getInvalidProduction() {
		return invalidProduction;
	}

	public String getInvalidType() {
		return invalidType;
	}

	/**
	 *
	 * @return a String representation of an Invalid Production, specifying the invalid production along with the reason why it is invalid
	 */
	@Override
	public String toString() {

		String type = invalidType;

		//If this PR is invalid because it contains an invalid variable on the LHS, then add this actual invalid variable to the reason why this PR is invalid
		if (type.equals(InvalidProductionException.INVALID_LHS_VARIABLE)) {
			type += " ('" + invalidProduction.charAt(0) + "')";
		}

		String toInsert = " '" + invalidProduction + "'"; //the actual invalid Production Rule that caused this exception to be thrown, as a string in quotes

		int index = type.indexOf(" "); //index of the first whitespace in the invalidType (i.e. the space after 'Production')

		//create a StringBuffer of the String type, so we can insert the actual invalid Production Rule after the word 'Production' in type

		StringBuffer result = new StringBuffer(type);

		result.insert(index, toInsert);
		String resultString = "InvalidProductionException: " + result.toString();

		return resultString;
	}



	/**
	 * This method is the same as toString() but we need a static method to pass to super() call
	 *  ->  we also take the production and type as parameters (the same parameters to the constructor) as the corresponding fields
	 *      have not been initialised yet when this method is to be called
	 *
	 * @return a String representation of an Invalid Production, specifying the invalid production along with the reason why it is invalid
	 */
	public static String explanation(String invalidProduction, String invalidType) {

		if (invalidType.equals(InvalidProductionException.INVALID_LHS_VARIABLE)) {
			invalidType += " ('" + invalidProduction.charAt(0) + "')";
		}

		String toInsert = " '" + invalidProduction + "'"; //the actual invalid Production Rule that caused this exception to be thrown, as a string in quotes

		int index = invalidType.indexOf(" "); //index of the first whitespace in the invalidType (i.ee the space after 'Production')

		StringBuffer result = new StringBuffer(invalidType);

		result.insert(index, toInsert);
		String resultString = "InvalidProductionException: " + result.toString();

		return resultString;
	}
}
