import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
	Then main() method in this class must be used to run the assignment
 */

public class RunAssignment {

	/**
	 * Determining if each Grammar in the TestData text file is a valid Simple Grammar, and if it is, parsing each
	 * input test string for this Simple Grammar to determine whether it belongs to the language defined by this grammar
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("COMP314 Assignment 2 - Parsing simple-grammars");
		System.out.println("\tDetermining if each Grammar in the TestData text file is a valid Simple Grammar, and if it is, parsing each\n" +
				"\tinput test string for this Simple Grammar to determine whether it belongs to the language defined by this grammar\n\n");
		System.out.println("Developed by Group P:\n\tTalha Vawda\n\tDivya Soomaroo\n\tAdin Arumugam\n\tKhulekani Mfeka");
		System.out.println("====================\n\n");

		Scanner inputFile = null; //A Scanner to read the test data from the input text file

		try {
			inputFile= new Scanner(new File("TestData.txt"));	//Assign the file for TestData.txt to this Scanner so that we can use it to read from the input text file
		} catch (FileNotFoundException e) { //If "TestData.txt" cannot be located in the current directory
			System.out.println("'TestData.txt' not found in current directory");
			System.out.println("This application will now terminate.");
			e.printStackTrace();
		}


		/*

			The TestData.txt file contains the 2 grammars from the Assignment Specification Document, and also an
			additional 4 grammars

			The first 5 Grammars are Valid Simple-Grammars whilst the 6th (last) Grammar is not a valid Simple-Grammar
			Each grammar's corresponding input test strings contain both strings that belong and strings that don't belong
			 to the language that the grammar defines


			Structure of the TestData.txt file:

				Production Rules for Grammar 1 (one Rule on each line)
					i.e. S -> aA | b is written as two separate Rules:
						S -> aA
						S -> b
				...
				/
				Test strings for Grammar 1 (one string on each line) [Note: A blank/empty line represents the empty input string]
				...
				==
				Production Rules for Grammar 2
				......


			Make sure that the last line of the text file is "==" to signify the end of (the input strings for) the last grammar in the file


		 */


		while (inputFile.hasNextLine()){ //traversing through TestData file
			/*
				each iteration of this while loop is for a single grammar and its correponding input strings
			 */

			System.out.println("======================================== Reading in a Grammar ==============================================");

			String line = ""; //A String variable that is used to store the current line from the file


			/*
				Read in the Production Rules for this grammar

					The variable line represents a Production Rule from the text file
			*/

			SimpleGrammar g = new SimpleGrammar(); //Create a new SimpleGrammar object for this grammar


			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine(); //get the next line from the file -> The first Production Rule for this grammar
			}

			boolean validGrammar = true;    //Assume that at the beginning, the grammar is a Valid Simple-Grammar

			do {


				try {
					g.addProductionRule(line); //add this Production Rule to the grammar
				} catch (InvalidProductionException e) {
					/*
						If this Production Rule is not a valid simple-grammar Production Rule (i.e. this Production Rule
						violates the constraints of a simple-grammar), thus this grammar is not a valid simple-grammar

						Thus, using the InvalidProductionException class we created, display this
						invalid Production Rule and the reason why it is an invalid simple-grammar Production Rule

						Also set validGrammar to false so that we don't parse the test input strings for this grammar
							-> If a grammar is not a valid simple-grammar, then we should discard it (dont parse its
								input strings) and move on to the next grammar
					 */
					System.out.println(e.toString());
					validGrammar = false;
				}


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine(); //get the next line (Production Rule) from the file
				}

			} while (!line.equals("/")); //repeat until we have read in all the Production Rules for this grammar

			//If the grammar is a valid simple-grammar, then display this grammar
			if (validGrammar) { // i.e. if (validGrammar == true)
				System.out.println("The Grammar read in is a valid Simple Grammar");
				System.out.println(g); //Display the (valid) simple-grammar that was read in (printline() will call g.toString())

			} else  {
				System.out.println("The Grammar read in is NOT a valid Simple Grammar");
			}



			/*
				Read in the test input strings for this grammar

					The variable line represents a input test string from the text file

					If validGrammar is false then we don't parse the test input strings for this grammar
							-> If a grammar is not a valid simple-grammar, then we should discard it (dont parse its
								input strings) and move on to the next grammar
							-> However we cant use the 'continue;' statement to skip the remaining code below
							    and start the next iteration of this while loop to read in the next grammar as we still
							    have to traverse the input strings for the grammar till we reach the '==' in the file that
							    indicates that we have reached the end of the input strings for this grammar and that
							    the next grammar is next. So read in the remaining test strings for this grammar but
							    dont parse them (ignore them / do nothing)
			 */


			if (validGrammar) { // i.e. if (validGrammar == true)
				System.out.println("========================= Parsing the test input strings for this grammar =========================");
			}


			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine(); //get the next line (input test string) from the file -> this will be the first input test string for this grammar

			}

			do {

				//If the grammar is a valid simple-grammar, then parse thus input test string to determine if this string belongs to the language that this grammar defines
				if (validGrammar) {  // i.e. if (validGrammar == true)
					g.parseString(line); //parseString() displays the (left-most) derivation to the console (so we dont have to call print())
					System.out.println("------------------------------------------------------------------------------------------------------------");
				}


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine(); //get the next line (input test string) from the file
				}

			} while (!line.equals("==")); //repeat until we have read in all the input test strings for this grammar

			//Display a separator to show that we have reached the end of this grammar and that we are moving on to the next grammar
			System.out.println("\n=====================================================================================================================");
			System.out.println("=====================================================================================================================\n\n");
		}

		System.out.println("RunAssignment has Completed Successfully! \nThank You, and have a nice day!");

	}
}
