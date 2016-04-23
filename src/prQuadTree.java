// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the Curator System.
//
// Teresa Lin

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

// The test harness will belong to the following package; the quadtree
// implementation must belong to it as well.  In addition, the quadtree
// implementation must specify package access for the node types and tree
// members so that the test harness may have access to it.
//

public class prQuadTree< T extends Compare2D<? super T> > {

	abstract class prQuadNode { 
		public abstract boolean isLeaf();
		public abstract boolean isInternal();
	}

	public static final int BUCKET_SIZE = 4;

	class prQuadLeaf extends prQuadNode {

		Vector<T> Elements;

		public prQuadLeaf(T elem) {
			Elements = new Vector<T>();
			Elements.add(elem);
		}

		public boolean isLeaf() {
			return true;
		}

		public boolean isInternal() {
			return false;
		}
	}

	class prQuadInternal extends prQuadNode {
		prQuadNode NW, NE, SE, SW;

		public boolean isLeaf() {
			return false;
		}

		public boolean isInternal() {
			return true;
		}

		public prQuadInternal() { 
			NW = null;
			NE = null;
			SE = null;
			SW = null;
		}
	}

	prQuadNode root;
	long xMin, xMax, yMin, yMax;
	RandomAccessFile debugQuad;
	int size;

	// Initialize quadtree to empty state, representing the specified region.
	public prQuadTree(long xMin, long xMax, long yMin, long yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		
		root = null;
		size = 0;
	}

	// Pre:   elem != null
	// Post:  If elem lies within the tree's region, and elem is not already 
	//        present in the tree, elem has been inserted into the tree.
	// Return true iff elem is inserted into the tree. 
	public boolean insert(T elem) {
		
		//if element is null return false
		if (elem == null) {
			return false;
		}
		//element exists and is in the quadrants
		if (elem.inBox(xMin, xMax, yMin, yMax)) {
			root = insertHelper (root, elem, xMin, xMax, yMin, yMax);
			return true;
		}
		//else element is not in bounds
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private prQuadNode insertHelper (prQuadNode node, T elem, double xLo, double xHi, double yLo, double yHi) {

		//if a node is empty with no leaf
		if (node == null) {
			return new prQuadLeaf(elem);
		}
		//if the quadrant is holding a leaf node
		else if (node.getClass().equals(prQuadLeaf.class)) {

			prQuadInternal inter = new prQuadInternal();
			prQuadLeaf leaf = (prQuadLeaf) node;

			//if the element to insert is already in the leaf, add the offset to the existing element
			for (T element: leaf.Elements) {
				if (element.equals(elem)) {
					((Point)element).addOffset(((Point)elem).getOffsetAt(0));
					return leaf;
				}
			}
			
			//if bucket size is not reached yet, insert into the leaf
			if (leaf.Elements.size() < BUCKET_SIZE) {
				leaf.Elements.add(elem);
				return leaf;
			}
			//if bucket size is reached
			else {	
				//insert the actual element into the newly created internal node
				insertHelper (inter, elem, xLo, xHi, yLo, yHi);

				//reinsert all the elements in the leaf
				for (T element: leaf.Elements) {
					insertHelper(inter, element, xLo, xHi, yLo, yHi);
				}
				return inter;
			}
		}
		//if the node is pointing to an internal node
		else {

			prQuadInternal inter = (prQuadInternal) node;

			double xMid = (xLo + xHi)/2;
			double yMid = (yLo + yHi)/2;

			Direction dir = elem.directionFrom(xMid, yMid);

			//if inserting into NW quadrant
			if (dir == Direction.NW) {
				inter.NW = insertHelper (inter.NW, elem, xLo, xMid, yMid, yHi);
			}
			//if inserting into NE quadrant
			else if (dir == Direction.NE || dir == Direction.NOQUADRANT) {
				inter.NE = insertHelper (inter.NE, elem, xMid, xHi, yMid, yHi);
			}
			//if inserting into SW quadrant
			else if (dir == Direction.SW) {
				inter.SW = insertHelper (inter.SW, elem, xLo, xMid, yLo, yMid);
			}
			//if inserting into SE quadrant
			else {
				inter.SE = insertHelper (inter.SE, elem, xMid, xHi, yLo, yMid);
			}
			return inter;
		}
	}

	@SuppressWarnings("unchecked")
	//counts the number of leaves in an internal node
	private int countLeaf(prQuadNode node) {
		int count = 0;
		prQuadInternal inter = (prQuadInternal) node;
		if (inter.NW != null && inter.NW.isLeaf()) {
			count++;
		}
		if (inter.NE != null && inter.NE.isLeaf()) {
			count++;
		}
		if (inter.SW != null && inter.SW.isLeaf()) {
			count++;
		}
		if (inter.SE != null && inter.SE.isLeaf()){
			count++;
		}
		return count;
	}

	// Pre:  elem != null
	// Post: If elem lies in the tree's region, and a matching element occurs
	//       in the tree, then that element has been removed.
	// Returns true iff a matching element has been removed from the tree.
	public boolean delete(T Elem) {
		//return false if element is null
		if (Elem == null) {
			return false;
		}
		//return false if cannot find element in tree
		if (find(Elem) == null) {
			return false;
		}
		//if element is in range
		if (Elem.inBox(xMin, xMax, yMin, yMax)) {
			root = deleteHelper (root, Elem, xMin, xMax, yMin, yMax);
			return true;
		}
		else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private prQuadNode deleteHelper (prQuadNode node, T elem, long xLo, long xHi, long yLo, long yHi) {
		//if node is not found
		if (node == null) {
			return null;
		}
		//if the node is a leaf
		if (node.isLeaf()) { 
			prQuadLeaf leaf = (prQuadLeaf) node;
			// check if the leaf contains the element
			if (leaf.Elements.contains(elem)) {
				return null;
			}
			return node;
		}
		//if node is an internal node
		else { 
			prQuadInternal inter = (prQuadInternal) node;
			long xMid = (xLo + xHi)/2; 
			long yMid = (yLo + yHi)/2; 
			
			//check which quadrant the element is in
			Direction dir = elem.inQuadrant( xLo, xHi, yLo, yHi );

			//if element is in NW quadrant
			if (dir == Direction.NW) { 
				inter.NW = deleteHelper(inter.NW, elem, xLo, xMid, yMid, yHi); 
			}
			//if element is in NE quadrant
			else if (dir == Direction.NE) { 
				inter.NE = deleteHelper(inter.NE, elem, xMid, xHi, yMid, yHi); 
			}
			//if element is in SW quadrant
			else if (dir == Direction.SW) { 
				inter.SW = deleteHelper(inter.SW, elem, xLo, xMid, yLo, yMid); 
			}
			//if element is in SE quadrant
			else if (dir == Direction.SE) {
				inter.SE = deleteHelper(inter.SE, elem, xMid, xHi, yLo, yMid); 
			}

			//check if there's more than one leaf
			if ((inter.NW != null && inter.NW.isInternal()) || (inter.NE != null && inter.NE.isInternal()) ||
					(inter.SW != null && inter.SW.isInternal()) ||(inter.SE != null && inter.SE.isInternal())) {
				return inter;
			}

			//cases where the internal node only points to a single leaf
			//if so, we want to replace the internal node with the lone leaf
			if (countLeaf(node) <= 1) {
				prQuadLeaf leaf = null;	
				if (inter.NW != null && inter.NW.isLeaf()) {
					leaf = (prQuadLeaf) inter.NW;
				}
				if (inter.NE != null && inter.NE.isLeaf()) {
					leaf = (prQuadLeaf) inter.NE;
				}
				if (inter.SW != null && inter.SW.isLeaf()) {
					leaf = (prQuadLeaf) inter.SW;
				}
				if (inter.SE != null && inter.SE.isLeaf()){
					leaf = (prQuadLeaf) inter.SE;
				}
				return leaf;
			}
			return inter;
		}
	}

	// Pre:  elem != null
	// Returns reference to an element x within the tree such that 
	// elem.equals(x)is true, provided such a matching element occurs within
	// the tree; returns null otherwise.
	public T find(T Elem) {
		if (Elem == null) {
			return null;
		}
		if (!Elem.inBox(xMin, xMax, yMin, yMax)) {
			return null;
		}
		return findHelper (root, Elem, xMin, xMax, yMin, yMax);
	}

	@SuppressWarnings("unchecked")
	private T findHelper (prQuadNode node, T elem, double xLo, double xHi, double yLo, double yHi) {

		//return null if element could not be found
		if (node == null) {
			return null;
		}
		//if the node is holding a leaf
		else if (node.getClass().equals(prQuadLeaf.class)) {
			prQuadLeaf leaf = (prQuadLeaf) node;
			//traverse the leaf and check if any elements match 
			for (int i = 0; i < leaf.Elements.size(); i++) {
				if (leaf.Elements.get(i).equals(elem)) {
					return leaf.Elements.get(i);
				}
			}
			return null;
		}
		//if is internal node
		else {
			prQuadInternal inter = (prQuadInternal) node;

			double xMid = (xLo + xHi)/2;
			double yMid = (yLo + yHi)/2;
			Direction dir = elem.directionFrom(xMid, yMid);

			//if the element is in the NW quadrant
			if (dir == Direction.NW) {
				return findHelper (inter.NW, elem, xLo, xMid, yMid, yHi);
			}
			//if the element is in the NE quadrant
			else if (dir == Direction.NE || dir == Direction.NOQUADRANT) {
				return findHelper (inter.NE, elem, xMid, xHi, yMid, yHi);
			}
			//if the element is in the SW quadrant
			else if (dir == Direction.SW) {
				return findHelper (inter.SW, elem, xLo, xMid, yLo, yMid);
			}
			//if the element is in the SE quadrant
			else if (dir == Direction.SE){
				return findHelper (inter.SE, elem, xMid, xHi, yLo, yMid);
			}
			else {

			}
			return null;
		}
	}

	// Pre:  xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is 
	//in the tree and x lies at coordinates within the defined rectangular 
	// region, including the boundary of the region.
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi) {
		
		//if root is null
		if (root == null) {
			return new Vector<T>();
		}
		return findVecHelper(root, xLo, xHi, yLo, yHi, xMin, xMax, yMin, yMax);      
	}

	//checks if the rectangle overlaps with the boundaries
	private boolean overlap(long xLo, long xHi, long yLo, long yHi, long xMin, long xMax, long yMin, long yMax) {
		return !((xHi < xMin) || (xLo > xMax) || (yHi < yMin) || (yLo > yMax));
	}

	@SuppressWarnings("unchecked")
	private Vector<T> findVecHelper(prQuadNode node, long xLo, long xHi, long yLo, long yHi, 
			long xLo2, long xHi2, long yLo2, long yHi2) {
		Vector<T> vList = new Vector<T>();
		//if node is null return the vector list
		if (node == null) {
			return vList;
		}
		//if leaf node, check the elements and add them into the vector list
		if (node.isLeaf()) {
			prQuadLeaf leaf = (prQuadLeaf) node;
			for (int i = 0; i < leaf.Elements.size(); i++) {
				//goes through all the elements, if it's in the box then add to vector
				if (leaf.Elements.get(i).inBox(xLo, xHi, yLo, yHi)) {
					vList.add(leaf.Elements.get(i));
					size++;
				}
				else {
					//do nothing
				}
			}
		}
		//if node is an internal node 
		else {
			prQuadInternal inter = (prQuadInternal) node;
			long xMid = (xLo2 + xHi2)/2;
			long yMid = (yLo2 + yHi2)/2;
			
			//checks if each quadrants are being overlapped 
			if (overlap(xLo, xHi, yLo, yHi, xLo2, xMid, yMid, yHi2)) {
				vList.addAll(findVecHelper(inter.NW, xLo, xHi, yLo, yHi, xLo2, xMid, yMid, yHi2));
			}
			if (overlap(xLo, xHi, yLo, yHi, xMid, xHi2, yMid, yHi2)) {
				vList.addAll(findVecHelper(inter.NE, xLo, xHi, yLo, yHi, xMid, xHi2, yMid, yHi2));
			}
			if (overlap(xLo, xHi, yLo, yHi, xLo2, xMid, yLo2, yMid)) {
				vList.addAll(findVecHelper(inter.SW, xLo, xHi, yLo, yHi, xLo2, xMid, yLo2, yMid));
			}
			if (overlap(xLo, xHi, yLo, yHi, xMid, xHi2, yLo2, yMid)) {
				vList.addAll(findVecHelper(inter.SE, xLo, xHi, yLo, yHi, xMid, xHi2, yLo2, yMid));
			}
		}
		return vList;
	}



	public void printTree(RandomAccessFile debugFile) {

		debugQuad = debugFile;
		try {
			printTreeHelper(this.root, "  ");
		} catch (IOException e1) {
			System.out.println("prQuadTree: Error - Failed to debug part of the tree!");
			e1.printStackTrace();
		}
	}

	/**
	 * Print method for the tree.
	 * @param sRoot
	 * @param Padding
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	public void printTreeHelper(prQuadNode sRoot, String Padding) throws IOException {

		// Check for empty leaf
		if (sRoot == null) {
			debugQuad.writeBytes(Padding + "*\n");
			return;
		}
		// Check for and process SW and SE subtrees
		if (sRoot.isInternal()) {
			prQuadInternal p = (prQuadInternal) sRoot;
			printTreeHelper(p.SW, Padding + " ");
			printTreeHelper(p.SE, Padding + " ");
		}
		// Display indentation padding for current node
		debugQuad.writeBytes(Padding);
		// Determine if at leaf or internal and display accordingly
		if (sRoot.isLeaf()) {
			prQuadLeaf p = (prQuadLeaf) sRoot;
			debugQuad.writeBytes(Padding + p.Elements + "\n");
		} else
			debugQuad.writeBytes(Padding + "@\n");

		// Check for and process NE and NW subtrees
		if (sRoot.isInternal()) {
			prQuadInternal p = (prQuadInternal) sRoot;
			printTreeHelper(p.NE, Padding + " ");
			printTreeHelper(p.NW, Padding + " ");
		}
	}
}

