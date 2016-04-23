import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class reads the input file and passes each line to the command processor
 */
public class FileReader {
	
	String record;
	RandomAccessFile commands;
	
	/**
	 * Constructor: creates a new random access file to read the inputs
	 * @param str the name of the script file
	 */
	public FileReader(String str) throws FileNotFoundException {
		record = null;
		commands = new RandomAccessFile (str, "r");
	}

	/**
	 * Reads and interprets each record
	 * @return the record
	 */
	public String readCommand() throws IOException {
		//when there are still lines left to read
		while ((record = commands.readLine()) != null ) { 
			//if end of file is reached, stop execution
			if (record.contains("quit")) {
				break;
			}
			//if the record contains a semicolon, return the record
			if (record.contains(";")) {
				return record;
			}
			else {
				return record;
			}
		}	
		return "";
	}

	/**
	 * Gets the length of the file
	 * @return the length of the file
	 */
	public int linesOfCommands() throws IOException {
		int count = 0;
		//while the end of file is not reached yet
		while ((record = commands.readLine()) != null ) { 
			count++;
		}
		//reset the file pointer to point to the beginning of the file
		commands.seek(0);
		return count;
	}
}
