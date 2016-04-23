import java.io.IOException;

//On my honor:
//
//- I have not discussed the Java language code in my program with
//anyone other than my instructor or the teaching assistants
//assigned to this course.
//
//- I have not used Java language code obtained from another student,
//or any other unauthorized source, either modified or unmodified.
//
//- If any Java language code or documentation used in my program
//was obtained from another source, such as a text book or course
//notes, that has been clearly noted with a proper citation in
//the comments of my program.
//
//- I have not designed this program in such a way as to defeat or
//interfere with the normal operation of the Automated Grader.
//
//Pledge: On my honor, I have neither given nor received unauthorized
//aid on this assignment.
//
//Teresa Lin

/**
 * This is the main class which takes in three arguments
 */
public class GIS {
	
	/**
	 * Main: checks if the three arguments are valid
	 * then creates a controller object with the three input arguments
	 */
	public static void main (String[] args) throws IOException {

		Controller reader = null;

		//checks if there are three arguments
		if (args.length == 3) {
			reader = new Controller(args[0], args[1], args[2]);
		}
		//if there are less than three arguments, exit the program
		else {
			System.out.println("Does not have three arguments");
			System.exit(0);
		}
		
		//calls the controller
		reader.parseCommand();
	}
}
