import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class handles the import command
 */
public class Import extends Helper{

	private RandomAccessFile recordFile;
	private String line;
	private String file;
	BufferPool buffPool;

	/**
	 * Constructor: takes in the import command, parses it, and creates a random access file
	 * and creates a file reader object to read the input file
	 * @param str the command string
	 */
	public Import(String str) throws IOException {
		String[] array = str.split("\\t");
		file = array[1];
		recordFile = new RandomAccessFile (file, "r");
	}

	/**
	 * Parses each line to get the coordinates and return them as an array
	 * @return return the coordinates as an array
	 */
	int offsetCount = 0;
	public long[] getCoordinates() throws IOException {

		//initialize an array that holds the coordinates
		long[] array2= {0, 0};

		//while the end of line is not reached yet
		while ((line = recordFile.readLine()) != null) {

			if (line.contains("FEATURE")) {
			}
			else {
				String[] array = line.split("\\|");
				//convert longitude from DMS to seconds and store it into array2
				array2[0] = convertDMS(array[7]);
				//convert latitude from DMS to seconds and store it into array2
				array2[1] = convertDMS(array[8]);

				//return the array
				return array2;
			}
		}	
		return array2;
	}

	/**
	 * Gets the length of the file
	 * @return the length of the file
	 */
	int count = 0;
	public int length() throws IOException {
		while ((line = recordFile.readLine()) != null ) { 
			count++;
		}
		//reset the file pointer to point to the beginning of the file
		recordFile.seek(0);
		return count;
	}

	/**
	 * Gets the current line that we are trying to import
	 * @return line the current line we are importing
	 */
	public String getCurrentImportLine() {
		return line;
	}
}
