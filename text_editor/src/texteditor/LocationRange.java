package texteditor;

import java.util.Objects;

/**
 * A class which represents a range of locations in the
 * text editor by storing the start and end location.
 * 
 * @author Tome Radman
 *
 */
public class LocationRange {

	/** Start location. */
	private Location start;
	
	/** End location. */
	private Location end;
	
	/**
	 * A constructor which sets the start and end location.
	 * 
	 * @param start Start location
	 * @param end End location
	 */
	public LocationRange(Location start, Location end) {
		super();
		this.start = Objects.requireNonNull(start);
		this.end = Objects.requireNonNull(end);
	}

	/**
	 * Returns the start location.
	 * 
	 * @return Start location
	 */
	public Location getStart() {
		return start;
	}

	/**
	 * Returns the end location.
	 * 
	 * @return End location
	 */
	public Location getEnd() {
		return end;
	}
	
	/**
	 * Checks whether there is a difference between start and end location.
	 * 
	 * @return True if start and end location are different, false otherwise
	 */
	public boolean isDefined() {
		return start.compareTo(end) != 0;
	}
	
	@Override
	public String toString() {
		return  start + "->" + end;
	}
}
