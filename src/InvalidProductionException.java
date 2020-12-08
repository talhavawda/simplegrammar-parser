import java.io.IOException;

public class InvalidProductionException extends IOException {

	String invalidProduction;
	String invalidType; //Reason why this Production is invalid, e.g. 'Rewrite symbol not found'

	public static final String NO_REWRITE = "Production doesn't contain the Rewrite (->) symbol";
	public static final String NOT_SIMPLE = "Production is not a Simple Production";
	public static final String INVALID_VARIABLE = "Production contains an invalid Variable";

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

	@Override
	public String toString() {

		String type = invalidType;

		if (type.equals(InvalidProductionException.INVALID_VARIABLE)) {
			type += " (" + invalidProduction.charAt(0) + ")";
		}

		String toInsert = " '" + invalidProduction + "'";

		int index = type.indexOf(" "); //index of the first whitespace in the invalidType (i.ee the space after 'Production')

		StringBuffer result = new StringBuffer(type);

		result.insert(index, toInsert);
		String resultString = "InvalidProductionException: " + result.toString();

		return resultString;
	}


	/*
		Same as toString() but we need a static method to pass to super() call
	 */
	public static String explanation(String invalidProduction, String invalidType) {

		if (invalidType.equals(InvalidProductionException.INVALID_VARIABLE)) {
			invalidType += " (" + invalidProduction.charAt(0) + ")";
		}

		String toInsert = " '" + invalidProduction + "'";

		int index = invalidType.indexOf(" "); //index of the first whitespace in the invalidType (i.ee the space after 'Production')

		StringBuffer result = new StringBuffer(invalidType);

		result.insert(index, toInsert);
		String resultString = "InvalidProductionException: " + result.toString();

		return resultString;
	}
}
