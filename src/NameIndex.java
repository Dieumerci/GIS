
/**
 * This is a wrapper class for the hash table
 */
public class NameIndex {

	@SuppressWarnings("rawtypes")
	HashTable hash;

	/**
	 * Constructor: initializes the hash table
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NameIndex() {
		hash = new HashTable(GISrecord.class, 1019);
	}

	/**
	 * Finds a GISrecord in the hash table and returns it
	 * @param o the record we are trying to find
	 * @return result the matching record
	 */
	public GISrecord find (GISrecord o) {
		@SuppressWarnings("unchecked")
		GISrecord result = ((GISrecord) hash.find(o.getNameAndState(), o));
		if (result != null) {
			if (result.getfeatName().equals(o.getfeatName()) &&
					result.getStateAbbr().equals(o.getStateAbbr()) &&
					result.getOffset() == o.getOffset()) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Finds a GIS record without knowing the offset
	 * @param o the GISrecord we are trying to search for
	 * @return the GIS record
	 */
	public GISrecord findNoOffset (GISrecord o) {
		@SuppressWarnings("unchecked")
		GISrecord result = ((GISrecord) hash.find(o.getNameAndState(), o));
		if (result != null) {
			if (result.getfeatName().equals(o.getfeatName()) &&
					result.getStateAbbr().equals(o.getStateAbbr())) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Adds a GISrecord into the hash table
	 * @param o a GISrecord
	 */
	@SuppressWarnings("unchecked")
	public void addObj(GISrecord o) {
		hash.insert(o.getNameAndState(), o);
	}

	/**
	 * Gets the capacity of the array (hash table)
	 * @return out whatever is in the current slot in the hash table
	 */

	public String toString() {
		String out = "";
		for(int x = 0; x < hash.getLength(); x++)
			if (hash.get(x) != null) {
				out += x + ":  [" + ((GISrecord)hash.get(x)).getfeatName() + ":"
						+ ((GISrecord)hash.get(x)).getStateAbbr() + ", " + 
						((GISrecord)hash.get(x)).getOffsetList() + "]\n";
			}
		return out;
	}

	/**
	 * Gets the maximum number of probes
	 * @return the probes
	 */
	public int getMaxProbe() {
		return hash.getProbe();
	}

}
