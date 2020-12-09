import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class RunAssignment {

	public static void main(String[] args) {

		System.out.println("COMP314 Assignment 2 - Parsing simple-grammars\n\n");
		System.out.println("Developed by Group P:\n\tTalha Vawda\n\tDivya Soomaroo\n\tAdin Arumugam\n\tKhulekani Mfeka");
		System.out.println("====================\n\n");
		Scanner inputFile = null;

		try {
			inputFile= new Scanner(new File("TestData.txt"));	//A file to read the test data from the text file
		} catch (FileNotFoundException e) {
			System.out.println("'TestData.txt' not found in current directory");
			System.out.println("This application will now terminate.");
			e.printStackTrace();
		}


		/*
			Structure of the TestData.txt file:

				Production Rules for Grammar 1 (one Rule on each line)
				...
				/
				Test strings for Grammar 1 (one Rule on each line)
				...
				==
				Production Rules for Grammar 2
				......


			Make sure that the last line of the text file is "==" to signify the end of the last grammar


		 */


		while (inputFile.hasNextLine()){ //traversing through TestData file
			/*
				each iteration of this while loop is for a single grammar and its input strings
			 */

			System.out.println("=============================== Reading in a Grammar ===============================");

			String line = ""; //store the current line from the file


			/*
				Read in the grammar Production Rules
			*/

			SimpleGrammar g = new SimpleGrammar();


			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine();
			}

			boolean validGrammar = true;

			do {


				try {
					g.addProductionRule(line);
				} catch (InvalidProductionException e) {
					System.out.println(e.toString());
					//System.out.println("This application will now terminate.");
					validGrammar = false;
					//e.printStackTrace();
					//System.exit(1);
				}


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine();
				}

			} while (!line.equals("/"));

			if (validGrammar == true) {
				System.out.println("The Grammar read in is a valid Simple Grammar");
				System.out.println(g); //Display the grammar that was read in

			} else  {
				System.out.println("The Grammar read in is NOT a valid Simple Grammar");
			}



			/*
				Read in the test input strings for this grammar
			 */

			if (validGrammar == true) {
				System.out.println("================= Parsing the test input strings for this grammar =================");
			}


			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine();

			}

			do {

				//... (Code goes here)

				if (validGrammar == true) {
					g.parseString(line); //parseString() displays the output
					System.out.println("------------------------------------------------------------------------------------------");
				}


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine();
				}

			} while (!line.equals("=="));

			System.out.println("\n=============================================================================================================");
			System.out.println("=============================================================================================================\n\n");
		}


	}
}
