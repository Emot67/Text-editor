package texteditor;

/**
 * An observer of changes in cursor location.
 * 
 * @author Tome Radman
 *
 */
public interface CursorObserver {

	/**
	 * An action to be taken upon cursor location change.
	 * @param loc Location of the cursor
	 */
	public void updateCursorLocation(Location loc);
}
