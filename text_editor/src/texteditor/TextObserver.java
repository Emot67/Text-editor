package texteditor;

/**
 * An interface which represents an observer changes in the 
 * text model.
 * 
 * @author Tome Radman
 *
 */
public interface TextObserver {

	/**
	 * Action which is performed upon text change.
	 */
	public void updateText();
}
