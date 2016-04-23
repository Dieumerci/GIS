import java.util.ArrayList;

/**
 * This class holds all the features of a GIS record 
 */
public class GISrecord extends Helper{

	private String featName;
	private String stateAbbr;
	private long offset;
	private long longitude;
	private long latitude;
	private String longitudeS;
	private String latitudeS;
	private String [] array;
	private ArrayList<Long> offsetList;
	private String countyName;
	private String record;
	private String featureCat;
	private String srcLong;
	private String srcLati;
	private String elevation;
	private String dateCreated;
	private String dateMod;
	private String featureID;

	/**
	 * Constructor: creates a GIS record with an offset with all the features
	 * @param line the record 
	 * @param offset the offset of the record
	 */
	public GISrecord(String line, long offset) {

		record = line;
		array = line.split("\\|");

		this.offset = offset;
		offsetList = new ArrayList<Long>();
		offsetList.add(offset);
		
		featureID = array[0];
		featName = array[1];
		featureCat = array[2];
		stateAbbr = array[3];
		countyName = array[5];
		latitudeS = array[7];
		longitudeS = array[8];
		latitude = convertDMS(array[7]);
		longitude = convertDMS(array[8]);
		srcLati = array[11];
		srcLong = array[12];
		elevation = array[16];
		dateCreated = array[18];
		
		try {
			dateMod = array[19];
		}
		catch(ArrayIndexOutOfBoundsException e) {
			
		}
	}

	/**
	 * Constructor: creates a GISrecord with all the features
	 * @param line the record 
	 */
	public GISrecord(String line) {

		array = line.split("\\t");

		featName = array[1];
		stateAbbr = array[2];
		offsetList = new ArrayList<Long>();
		offsetList.add(offset);
	}
	
	/**
	 * Returns the list of offsets in the record
	 * @return offsetList the list of offsets
	 */
	public ArrayList<Long> getOffsetList() {
		return offsetList;
	}
	
	/**
	 * Adds an offset to the list
	 * @param offset the offset we want to add
	 */
	public void addOffset(long offset) {
		offsetList.add(offset);
	}

	/**
	 * Calculates the size of the offset list
	 * @return the size of the offset list
	 */
	public int sizeOfList() {
		return offsetList.size();
	}

	public String getfeatName() {
		return featName;
	}

	public String getStateAbbr() {
		return stateAbbr;
	}

	public long getOffset() {
		return offset;
	}

	public long getOffsetAt(int n) {
		return offsetList.get(n);
	}

	public long getLong() {
		return longitude;
	}

	public long getLati() {
		return latitude;
	}

	public String getLongS() {
		return longitudeS;
	}

	public String getLatiS() {
		return latitudeS;
	}

	public String getNameAndState() {
		return featName.concat(stateAbbr);
	}
	
	public String getCounty() {
		return countyName;
	}
	
	public String getFeatureCat() {
		return featureCat;
	}
	
	public String getSrcLong() {
		return srcLong;
	}
	
	public String getSrcLati() {
		return srcLati;
	}
	
	public String getElev() {
		return elevation;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}
	
	public String getDateMod() {
		return dateMod;
	}
	
	public String getFeatureID() {
		return featureID;
	}
	
	public String toString() {
		return offset + ":  " + record + "\n";
	}
}
