
/**
 * This class processes the what is command
 */
public class WhatIs {
	
	private String featName;
	private String stateAbbr;
	
	/**
	 * Constructor: takes in the command string and get the feature name and state abbreviation
	 */
	public WhatIs(String str) {
		String[] array = str.split("\\t");
		featName = array[1];
		stateAbbr = array[2];
	}
	
	/**
	 * Gets the feature name
	 * @return the feature name
	 */
	public String getFeatName() {
		return featName;
	}
	
	/**
	 * Gets the state abbreviation
	 * @return the state abbreviation
	 */
	public String getState() {
		return stateAbbr;
	}
	
	public String getNameAndState() {
		return featName.concat(stateAbbr);
	}
}
