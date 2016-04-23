import java.io.IOException;

public class World extends Helper{

	@SuppressWarnings("unused")
	private String str;
	private String [] array;
	long SW;
	long SE;
	long NW;
	long NE;

	public World(String str) throws IOException {
		this.str = str;
		array = str.split("\\t");
		SW = convertDMS(array[1]);
		SE = convertDMS(array[2]);
		NE = convertDMS(array[3]);
		NW = convertDMS(array[4]);
	}

	/**
	 * Checks if the coordinates lie in the world boundaries
	 * @param longi   longitude
	 * @param lati    latitude
	 * @return returns true of they are in the boundaries, false otherwise
	 */
	public boolean isInBounds(long longi, long lati) {
		return ((SW < longi) && (longi < SE) && (NE < lati) && (lati < NW));
	}
}
