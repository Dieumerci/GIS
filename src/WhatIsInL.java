
/**
 * This class processes the what is in -l command
 */
public class WhatIsInL extends Helper{
	
	private String longitude;
	private String latitude;
	private String halfX;
	private String halfY;
	
	/**
	 * Constructor: takes in the command string, parses it, 
	 * and get the longitude, latitude, half height, and half width
	 */
	public WhatIsInL(String str) {
		String [] array = str.split("\\t");
		longitude = array[3];
		latitude = array[2];
		halfX = array[4];
		halfY = array[5];
	}
	
	/**
	 * Gets the longitude
	 * @return the longitude 
	 */
	public String getLongStr() {
		return longitude;
	}
	
	/**
	 * Gets the latitude
	 * @return the latitude 
	 */
	public String getLatiStr() {
		return latitude;
	}
	
	/**
	 * Gets the longitude
	 * @return the longitude in seconds
	 */
	public long getLong() {
		return convertDMS(longitude);
	}
	
	/**
	 * Gets the latitude
	 * @return the latitude in seconds
	 */
	public long getLati() {
		return convertDMS(latitude);
	}
	
	/**
	 * Gets the half width
	 * @return the half width
	 */
	public long getHalfX() {
		return Integer.parseInt(halfX);
	}

	/**
	 * Gets the half height
	 * @return the half height
	 */
	public long getHalfY() {
		return Integer.parseInt(halfY);
	}
}
