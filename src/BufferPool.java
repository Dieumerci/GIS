import java.io.RandomAccessFile;
import java.util.LinkedList;

/**
 * This class is used as a buffer pool when of recently used GISRecord objects to minimize disk access
 */
public class BufferPool {
	
	private int maxPoolSize = 10;
	private LinkedList<GISrecord> bufferPool;
	@SuppressWarnings("unused")
	private LinkedList<RandomAccessFile> fileList;

	/**
	 * Constructor: creates an empty BufferPool linked list and initializes the random access file
	 * @param fileName
	 */
	public BufferPool() {
		bufferPool = new LinkedList<GISrecord>();
	}
	
	/**
	 * Adds record into the buffer pool
	 * @param record the record we want to add to the buffer pool
	 */
	public void addRecord(GISrecord record) {
		
		if (record != null) {
			//if the buffer pool is full
			//remove the last record in the list before inserting
			if (bufferPool.size() == maxPoolSize) {
				bufferPool.removeLast();
				bufferPool.addFirst(record);
			}
			//if the buffer pool is not full and the record we want to insert already exist
			//we want to reinsert the matching record to the top of the list
			//else we just add to the list normally
			else {
				if (bufferPool.contains(record)) {
					bufferPool.remove(record);
					bufferPool.addFirst(record);
				}
				else {
					bufferPool.addFirst(record);
				}
			}
		}	
	}
	
	/**
	 * Finds a GISrRecord based using an offset
	 * @param offset the offset we want to use to find the GIS record
	 * @return gisRecord the record we are searching for
	 */
	public GISrecord findRecord(long offset) {
		//if the buffer pool is not empty, we want to search through each record
		//if the offset of the record matches the offset we input, return that record
		if(!bufferPool.isEmpty()) {
			for(GISrecord gisRecord : bufferPool) {
				if(gisRecord.getOffset() == offset) {
					bufferPool.remove(gisRecord);
					bufferPool.addFirst(gisRecord);
					return gisRecord;
				}
			}
		}
		return null;
	}
	
	/**
	 * Prints the contents of the buffer pool
	 */
	public String toString() {
		String out;
		//if the buffer pool is empty
		if(bufferPool.size() == 0)
			out = "BufferPool does not contain any GIS-Records\n";
		//else if it's not empty, we want to print out every single record
		else {
			out = "MRU\n";
			for(GISrecord gisRecord : bufferPool)
				out += gisRecord.toString();
			out += "LRU\n";
		}
		return out;
	}
}