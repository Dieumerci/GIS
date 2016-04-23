import java.lang.reflect.Array;

/**
 * Hash table
 */
public class HashTable<T extends GISrecord> {

	private T[] hash = null;
	private int capacity = 0;
	private static int [] sizeList = null;
	private int size;
	private int sizeIndex;
	private int probeCount;

	/**
	 * Initializes a hash table
	 */
	@SuppressWarnings("unchecked")
	public HashTable(Class<T> type, int size) {

		hash = (T[]) Array.newInstance(type, size);

		//store all the array sizes in a static array
		sizeList = new int[] {1019, 2027, 4079, 8123, 16267, 32503, 65011, 130027, 260111, 
				520279, 1040387, 2080763, 4161539, 8323151, 16646323};
		size = 0;
		sizeIndex = 0;
		probeCount = 0;
		capacity = sizeList[sizeIndex];
	}

	/**
	 * Performs probing to resolve collisions
	 * @param count a variable for the quadratic function
	 * @return a new step size
	 */
	public double probe(int count) {
		return ((((count * count) + count)/2)) % capacity;
	}

	/**
	 * Performs probing to resolve collisions
	 * @param toHash the object that we want to hash
	 * @return hash value as long
	 */
	public long elfHash(Object toHash) {
		long hashValue = 0;
		if (toHash.getClass().equals(String.class)) {
			for (int Pos = 0; Pos < ((String)toHash).length(); Pos++) { // use all elements
				hashValue = (hashValue << 4) + ((String)toHash).charAt(Pos); // shift/mix
				long hiBits = hashValue & 0xF0; // get high nybble
				if (hiBits != 0)
					hashValue ^= hiBits >> 67; // xor high nybble with second nybble
				hashValue &= ~hiBits; // clear high nybble
			}
		}
		return hashValue;
	}

	/**
	 * Inserts an object into the hash table
	 * @param o the name and state of the object 
	 * @param t the object to insert into the hash table
	 */
	int previousProbeCount = 0;
	public void insert(Object o, T t) {
		probeCount = 0;
		if (o == null) {
			//do nothing
		}
		long home = (long) (elfHash(o) % capacity);
		int temp = 0;
		//if the index we are looking at is not holding an element
		//insert the object into the slot
		if (hash[(int) (((int)home + temp) % capacity)] == null) {
			hash[(int) (((int)home + temp) % capacity)] = t;
			size++;
		}
		else {
			//if there already exist an element in that index, use probing
			for (int i = 0;; i++) {

				//if the slot we are looking at is not holding an element
				//insert the object into the slot and break
				if (hash[(int) (((int)home + temp) % capacity)] == null) {
					hash[(int) (((int)home + temp) % capacity)] = t;
					size++;
					break;
				}
				//if the current slot holds an object with matching a feature name and state 
				//add the offset of the object t to the list of offsets
				else if (hash[((int)home + temp) % capacity].getfeatName().equals(t.getfeatName()) && 
						hash[((int)home + temp) % capacity].getStateAbbr().equals(t.getStateAbbr())) {
					hash[((int)home + temp) % capacity].addOffset(t.getOffset());
					break;
				}
				//else keep probing
				else {
					probeCount++;
					temp = (int) (probe(i) + (int)home);
				}
			}
		}
		
		//finds the maximum probe count
		previousProbeCount = Math.max(previousProbeCount, probeCount);
		probeCount = 0;
		
		//resize if the array is 70% full
		if ((((double)size)/((double)capacity)) >= .7) {
			reSize();
		}
	}

	/**
	 * Finds an object in the hash table
	 * @param o the name and state of the object t
	 * @param t the object we are finding
	 * @return return the object that we are looking for 
	 */
	public Object find (Object o, T t) {
		
		//return null if object is null
		if (o == null) {
			return null;
		}
		//go through each key in the hash table 
		//return the object if it's found
		long home = (long) (elfHash(o) % capacity);
		int temp = 0;
		for (int i = 0;; i++) {
			//return the object if we find it
			if (hash[((int)home + temp) % capacity].getNameAndState().equals(t.getNameAndState())) {
				return hash[((int)home + temp)%capacity];
			}
			//if the current slot is not null then probe
			if (hash[((int)home + temp) % capacity] != null) {
				temp = (int) (probe(i) + (int)home);
			}
			if (hash[((int)home + temp) % capacity] == null) {
				return null;
			}
		}
	}

	/**
	 * Resizes the hash table 
	 */
	@SuppressWarnings("unchecked")
	public void reSize() {	

		//create a temporary array to store all the items in hash
		T[] temp = hash;
		
		//create new capacity for the new array
		sizeIndex++;
		capacity = sizeList[sizeIndex];

		//create a whole new instance of hash 
		//reinsert all the elements from temp to hash
		hash = (T[]) Array.newInstance(GISrecord.class, capacity);
		size = 0;	
		for (T item: temp) {
			if (item != null) {
				insert(item.getNameAndState(), item);
			}
		}
	}

	/**
	 * Returns the capacity of the array (hash table)
	 * @return the capacity
	 */
	public int getLength() {
		return capacity;
	}

	/**
	 * Gets an object in the hash table
	 * @param i the index of the table
	 * @return the object we found
	 */
	public Object get(int i) {
		return hash[i];
	}

	/**
	 * Gets the current size of the table
	 * @return the size of the table
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets the previous number of times of probes
	 * @return the number of times of probes
	 */
	public int getProbe() {
		return previousProbeCount;
	}
}
