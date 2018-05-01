package texteditor;

/**
 * An observer of changes in the clipboard.
 * 
 * @author tome
 *
 */
public interface ClipboardObserver {

	/**
	 * An action to be taken upon clipboard change.
	 */
	public void updateClipboard();
}
