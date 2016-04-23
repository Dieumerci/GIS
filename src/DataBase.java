import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * This class writes to the database
 */
public class DataBase {
	
	RandomAccessFile dataBase;
	private long offset;
	
	/**
	 * Constructor: creates a new database file and writes the first line 
	 * @param file the name of the database file
	 */
	public DataBase(String file) throws IOException {
		dataBase = new RandomAccessFile(file, "rw");
		dataBase.writeBytes("FEATURE_ID|FEATURE_NAME|FEATURE_CLASS|STATE_ALPHA|"
				+ "STATE_NUMERIC|COUNTY_NAME|COUNTY_NUMERIC|PRIMARY_LAT_DMS|PRIM_LONG_DMS|"
				+ "PRIM_LAT_DEC|PRIM_LONG_DEC|SOURCE_LAT_DMS|SOURCE_LONG_DMS|SOURCE_LAT_DEC|"
				+ "SOURCE_LONG_DEC|ELEV_IN_M|ELEV_IN_FT|MAP_NAME|DATE_CREATED|DATE_EDITED\n");
	}
	
	/**
	 * Writes a string to the database, and before that, grabs the offset 
	 * @param str the string we want to write to the file
	 */
	public void write(String str) throws IOException {
		offset = dataBase.getFilePointer();
		dataBase.writeBytes(str + "\n");
	}
	
	/**
	 * Gets the current file pointer (offset) of the database
	 * @return the offset
	 */
	public long getOffset() throws IOException {
		return offset;
	}
	
	/**
	 * Gets a record in the database using an offset 
	 * @param offset the offset of the record
	 * @return the record with the matching offset
	 */
	public String getRecord(long offset) throws IOException {
		dataBase.seek(offset);
		return dataBase.readLine();
	}
	
	/**
	 * Gets the longitude of a record with a given offset
	 * @param offset the offset of the record
	 * @return the longitude of the record
	 */
	public String getLong(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[8];
	}
	
	/**
	 * Gets the latitude of a record with a given offset
	 * @param offset the offset of the record
	 * @return the latitude of the record
	 */
	public String getLat(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[7];
	}
	
	/**
	 * Gets the county name of a record with a given offset
	 * @param offset the offset of the record
	 * @return the country name of the record
	 */
	public String getCounty(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[5];
	}
	
	/**
	 * Gets the feature name of a record with a given offset
	 * @param offset the offset of the record
	 * @return the feature name of the record
	 */
	public String getFeatName(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[1];
	}
	
	/**
	 * Gets the state of a record with a given offset
	 * @param offset the offset of the record
	 * @return the state of the record
	 */
	public String getState(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[3];
	}
	
	/**
	 * Gets the feature ID of a record with a given offset
	 * @param offset the offset of the record
	 * @return the feature ID of the record
	 */
	public String getFeatureID(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[0];
	}
	
	/**
	 * Gets the feature cat of a record with a given offset
	 * @param offset the offset of the record
	 * @return the feature cat of the record
	 */
	public String getFeatureCat(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[2];
	}
	
	/**
	 * Gets the source longitude of a record with a given offset
	 * @param offset the offset of the record
	 * @return the source longitude of the record
	 */
	public String getSrcLong(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[11];
	}
	
	/**
	 * Gets the source latitude of a record with a given offset
	 * @param offset the offset of the record
	 * @return the source latitude of the record
	 */
	public String getSrcLati(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[12];
	}
	
	/**
	 * Gets the elevation of a record with a given offset
	 * @param offset the offset of the record
	 * @return the elevation of the record
	 */
	public String getElev(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[16];
	}
	
	/**
	 * Gets the date created of a record with a given offset
	 * @param offset the offset of the record
	 * @return the date created of the record
	 */
	public String getDateCreated(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[18];
	}
	
	/**
	 * Gets the date modified of a record with a given offset
	 * @param offset the offset of the record
	 * @return the date modified of the record
	 */
	public String getDateMod(long offset) throws IOException {
		dataBase.seek(offset);
		String[] array = dataBase.readLine().split("\\|");
		return array[19];
	}
}
