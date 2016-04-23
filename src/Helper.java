
/**
 * This is a helper class to convert coordinates from DMS form to seconds
 */
public class Helper {
	
	/**
	 * Converts coordinates from DMS form to seconds
	 * @param str the string we want to convert
	 * @return seconds form
	 */
	public long convertDMS(String str) {

		String dir = null;
		String DS = null;
		String MM = null;
		String SS = null;

		//get the direction in the string
		dir = str.substring(str.length() - 1, str.length());

		//if we are dealing with longitude, get the substrings accordingly
		if (str.length() == 8) {
			DS = str.substring(0, 3);
			MM = str.substring(3, 5);
			SS = str.substring(5, 7);
		}
		//if we are dealing with latitude, get the substrings accordingly
		else {
			DS = str.substring(0, 2);
			MM = str.substring(2, 4);
			SS = str.substring(4, 6);
		}

		//equation to convert from DMS to seconds
		long seconds = (Long.parseLong(DS)*3600) + (60 * Long.parseLong(MM))  + ((long) Long.parseLong(SS));

		//if the direction is east or west, the value is negative
		if ((dir.equals("E")) || (dir.equals("W"))) {
			seconds = seconds * -1;
		}
		return seconds;
	}
}
