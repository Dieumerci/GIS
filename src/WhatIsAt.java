
/**
 * This class processes the what is at command
 */
public class WhatIsAt extends Helper{

	private String longi;
	private String lati;
	String[] array;
	
	/**
	 * Constructor: takes in the command string, parses it, and get the longitude and latitude
	 */
	public WhatIsAt(String str) {
		array = str.split("\\t");
		longi = array[2];
		lati = array[1];
	}
	
	/**
	 * Gets the longitude
	 * @return the longitude in seconds
	 */
	public long getLong() {
		return convertDMS(longi);
	}
	
	/**
	 * Gets the latitude
	 * @return the latitude in seconds
	 */
	public long getLati() {
		return convertDMS(lati);
	}
	
	/**
	 * Gets the longitude
	 * @return the longitude 
	 */
	public String getLongStr() {
		return longi;
	}
	
	/**
	 * Gets the latitude
	 * @return the latitude 
	 */
	public String getLatiStr() {
		return lati;
	}
}
