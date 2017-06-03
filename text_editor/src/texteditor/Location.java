package texteditor;

/**
 * A class which represents a location in the text editor.
 * 
 * @author Tome Radman
 *
 */
public class Location implements Comparable<Location>{

	/** X coordinate. */
	public int x;
	
	/** Y coordinate. */
	public int y;
	
	/**
	 * Constructor which sets the coordinates of the location.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public Location(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the coordinates of the location.
	 * 
	 * @param x X coordinate
	 * @param y Y coordinate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int compareTo(Location other) {
		if (this.y < other.y) {
			return -1;
		} else if (this.y > other.y) {
			return 1;
		} else {
			if (this.x < other.x) {
				return -1;
			} else if (this.x > other.x) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	/**
	 * Creates a new {@link Location} objects with the same coordinates
	 * as the calling object.
	 * 
	 * @return Copy of the location.
	 */
	public Location copy() {
		return new Location(this.x, this.y);
	}
	
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
