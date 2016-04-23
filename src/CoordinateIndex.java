import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * This is a wrapper class for the prQuadTree
 */
public class CoordinateIndex {
	
	private prQuadTree<Point> tree;
	
	/**
	 * Constructor: initializes the tree
	 */
	public CoordinateIndex() {
		tree = null;
	}
	
	/**
	 * Creates a tree with the bounds
	 * @param xMin 
	 * @param xMax
	 * @param yMin
	 * @param yMax
	 */
	public void createTree(long xMin, long xMax, long yMin, long yMax) {
		tree = new prQuadTree<Point>(xMin, xMax, yMin, yMax);
	}
	
	/**
	 * Finds a point object in the tree and returns it
	 * @param point the point we want to search for
	 * @return the point we are looking for in the tree
	 */
	public Point find(Point point) {
		return tree.find(point);
	}
	
	/**
	 * Inserts a point object into the tree
	 * @param point the point we want to insert to the tree
	 */
	public boolean insert(Point point) {
		return tree.insert(point);
	}
		
	/**
	 * Searches for all the records in a given region
	 * @param midX
	 * @param midY
	 * @param halfX
	 * @param haldY
	 * @return a list of records that lie in the bounds
	 */
	public Vector<Point> findInRegion(long midX, long midY, long halfX, long halfY) {
		return tree.find(midX-halfX, midX + halfX, midY - halfY, midY + halfY);
	}
	
	/**
	 * Debug quad tree. 
	 * @param file to write debugged tree to. 
	 */
	public void debugQuadTree(RandomAccessFile log) {
		tree.printTree(log);
	}
}
