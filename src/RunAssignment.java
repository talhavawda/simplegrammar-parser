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

		while (inputFile.hasNextLine()){ //traversing through TestData file
			/*
				each iteration of this while loop is for a single grammar and its input strings
			 */

			String line = ""; //store the current line from the file


			/*
				Read in the grammar Production Rules
			*/

			SimpleGrammar g = new SimpleGrammar();


			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine();
			}

			do {

				try {
					g.addProductionRule(line);
				} catch (InvalidProductionException e) {
					System.out.println(e.toString());
					System.out.println("This application will now terminate.");
					e.printStackTrace();
					System.exit(1);
				}


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine();
				}

			} while (!line.equals("/"));



			/*
				Read in the test input strings for this grammar
			 */

			if (inputFile.hasNextLine()) {
				line = inputFile.nextLine();
			}

			do {

				//... (Code goes here)


				if (inputFile.hasNextLine()) {
					line = inputFile.nextLine();
				}

			} while (!line.equals("=="));


		}


	}
}
