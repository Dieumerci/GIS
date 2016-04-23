import java.util.Vector;

public class Point implements Compare2D<Point> {

	private long xcoord;
	private long ycoord;
	private Vector<Long> offsetList;
	private int size;

	public Point() {
		xcoord = 0;
		ycoord = 0;
	}

	/**
	 * Constructor: creates a point object with no offset
	 */
	public Point(long x, long y) {
		xcoord = x;
		ycoord = y;
		offsetList = new Vector<Long>();
		size = 0;
	}

	/**
	 * Constructor: creates a point object with an offset
	 */
	public Point(long x, long y, long offset) {
		xcoord = x;
		ycoord = y;
		offsetList = new Vector<Long>();
		offsetList.add(offset);
		size = 1;
	}

	/**
	 * Returns an offset list 
	 * @return an offset list
	 */
	public Vector<Long> getOffsetList() {
		return offsetList;
	}
	
	/**
	 * Gets an offset in the offset list
	 * @param index
	 * @return the offset at index
	 */
	public long getOffsetAt(int index) {
		return offsetList.get(index);
	}

	/**
	 * Adds additional offset to the offset list
	 * @param offset the offset to add
	 * @return return true if added, false if the list already contains the offset
	 */
	public boolean addOffset(long offset) {
		if(offsetList.contains(offset)) {
			return false;
		}
		offsetList.add(offset);
		size++;
		return true;
	}

	/**
	 * Gets the x coordinate
	 * @return the x coordinate
	 */
	public long getX() {
		return xcoord;
	}
	
	/**
	 * Gets the y coordinate
	 * @return the y coordinate
	 */
	public long getY() {
		return ycoord;
	}

	/**
	 * Returns indicator of the direction to the user data object from the
	 * location (X, Y) specified by the parameters. The indicators are defined
	 * in the enumeration Direction, and are used as follows:
	 * 
	 * NE: vector from (X, Y) to user data object has a direction in the range
	 * [0, 90) degrees (relative to the positive horizontal axis
	 * 
	 * NW: same as above, but direction is in the range [90, 180)
	 * 
	 * SW: same as above, but direction is in the range [180, 270)
	 * 
	 * SE: same as above, but direction is in the range [270, 360)
	 * 
	 * NOQUADRANT: location of user object is equal to (X, Y)
	 */
	public Direction directionFrom(double X, double Y) { 

		if (((xcoord > X) && (ycoord >= Y)) || ((X == xcoord) && (Y == ycoord))) {
			return Direction.NE;
		} else if ((xcoord <= X) && (ycoord > Y)) {
			return Direction.NW;
		} else if ((xcoord < X) && (ycoord <= Y)) {
			return Direction.SW;
		} else if ((xcoord >= X) && (ycoord < Y)) {
			return Direction.SE;
		} else {
			return Direction.NOQUADRANT;
		}
	}

	/**
	 * Returns indicator of which quadrant of the rectangle specified by the
	 * parameters that user data object lies in. The indicators are defined in
	 * the enumeration Direction, and are used as follows, relative to the
	 * center of the rectangle:
	 * 
	 * NE: user data object lies in NE quadrant, including non-negative x-axis,
	 * but not the positive y-axis
	 * 
	 * NW: user data object lies in the NW quadrant, including the positive
	 * y-axis, but not the negative x-axis
	 * 
	 * SW: user data object lies in the SW quadrant, including the negative
	 * x-axis, but not the negative y-axis
	 * 
	 * SE: user data object lies in the SE quadrant, including the negative
	 * y-axis, but not the positive x-axis
	 * 
	 * NOQUADRANT: user data object lies outside the specified rectangle
	 */
	public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi) { 
		double xMid = (xLo + xHi) / 2;
		double yMid = (yLo + yHi) / 2;

		if (((xcoord > xMid) && (xcoord <= xHi) && (ycoord >= yMid) && (ycoord <= yHi))
				|| ((xcoord == xMid) && (ycoord == yMid))) {
			return Direction.NE;
		} 
		else if ((xcoord >= xLo) && (xcoord <= xMid) && (ycoord > yMid)
				&& (ycoord <= yHi)) {
			return Direction.NW;
		} 
		else if ((xcoord >= xLo) && (xcoord < xMid) && (ycoord >= yLo)
				&& (ycoord <= yMid)) {
			return Direction.SW;
		} 
		else if ((xcoord >= xMid) && (xcoord <= xHi) && (ycoord >= yLo)
				&& (ycoord < yMid)) {
			return Direction.SE;
		} 
		else {
			return Direction.NOQUADRANT;
		}
	}

	/**
	 * Returns true iff the user data object lies within or on the boundaries of
	 * the rectangle specified by the parameters.
	 */
	public boolean inBox(double xMin, double xMax, double yMin, double yMax) { 
		return (((xcoord >= xMin) && (xcoord <= xMax)) && ((ycoord >= yMin) && (ycoord <= yMax)));
	}

	/**
	 * Print method for the tree
	 */
	public String toString() {
		String out;
		out = "[(" + xcoord + ", " + ycoord + ")";
		for(long offset : offsetList) {
			out += ", " + Long.toString(offset);
		}
		out += "]";
		return out;
	}

	/**
	 * Overrides the user data object's inherited equals() method with an
	 * appropriate definition; it is necessary to place this in the interface
	 * that is used as a bound on the type parameter for the generic spatial
	 * structure, otherwise the compiler will bind to Object.equals(), which
	 * will almost certainly be inappropriate.
	 */
	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		if (!this.getClass().equals(o.getClass())) {
			return false;
		}

		// Get a reference of the appropriate type:
		Point obj = (Point) o;

		if ( (this.getX() == (obj.getX())) && ((this.getY()) == (obj.getY())) ) {
			return true;
		}

		else {
			return false;
		}
	}
	
	/**
	 * Gets the size of the offset list
	 * @return the size of the list 
	 */
	public int getSize() {
		return size;
	}
}
