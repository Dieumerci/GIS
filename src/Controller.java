import java.io.File;
import java.io.IOException;

/**
 * This class reads the input file and passes each line to the command processor
 */
public class Controller {

	FileReader reader;
	World world; 
	
	String par1;
	String par2;
	String par3;
	
	File file1;
	File file2;
	File file3;
	
	CommandProcessor process;

	/**
	 * Constructor: creates and passes in all the files to the command processor 
	 * and creates a file reader object to read the input file
	 * @param par1 the database file
	 * @param par2 the script file
	 * @param par3 the log file
	 */
	public Controller(String par1, String par2, String par3) throws IOException {
		
		this.par1 = par1;
		this.par2 = par2;
		this.par3 = par3;
		
		file1 = new File(par1);
		file2 = new File(par2);
		file3 = new File(par3);
		
		if (file1.exists()) {
			file1.delete();
		}
		if (!file2.exists()) {
			System.exit(0);
		}
		if (file3.exists()) {
			file3.delete();
		}

		process = new CommandProcessor(par1, par2, par3);
		reader = new FileReader(par2);
	}

	/**
	 * Reads the input file line by line and passes them to the command processor
	 */
	public void parseCommand() throws IOException {
		
		//gets the number of lines of commands in the script file
		int length = reader.linesOfCommands();
		String command = null;
		
		//if the end of the file isn't reached yet
		while (length != 0) {
			//check if the current line is a command or not
			command = reader.readCommand();
			//process the command
			process.process(command);
			length--;
		}
	}
}
