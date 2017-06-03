package texteditor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * A class which represents a clipboard of the text editor. The 
 * clipboard is implemented using a stack on which the text segments
 * are stored. This class enables the text editor to utilize copy, cut and
 * paste commands.
 * 
 * @author Tome Radman
 *
 */
public class ClipboardStack {

	/** 
	 * The stack which represents the clipboard.
	 * */
	private Deque<String> texts;
	
	/**
	 * A list of clipboard observers.
	 */
	private List<ClipboardObserver> observers;
	
	/**
	 * A constructor which initializes the stack.
	 */
	public ClipboardStack() {
		texts = new ArrayDeque<>();
		observers = new ArrayList<>();
	}
	
	/**
	 * Pushes the given text onto the clipboard.
	 * 
	 * @param text Text to be pused onto the clipboard
	 */
	public void pushText(String text) {
		if (text == null) {
			throw new NullPointerException("Given text is null.");
		}
		texts.push(text);
		notifyObservers();
	}
	
	/**
	 * Removes and returns the top element of the clipboard stack.
	 * 
	 * @return String from the top of the clipboard stack
	 */
	public String pop() {
		notifyObservers();
		return texts.pop();		
	}
	
	/**
	 * eturns the top element of the clipboard stack without removing
	 * it.
	 * 
	 * @return String from the top of the clipboard stack
	 */
	public String peek() {
		return texts.peek();
	}
	
	/**
	 * Checks whether the clipboard stack is empty.
	 * 
	 * @return True if the clipboard is empty, false otherwise
	 */
	public boolean isEmpty() {
		return texts.isEmpty();
	}
	
	/**
	 * Clears the clipboard stack
	 * 
	 */
	public void clear() {
		texts.clear();
		notifyObservers();
	}
	
	/**
	 * Adds the given {@link ClipboardObserver} to the list of 
	 * observers.
	 * 
	 * @param observer A {@link ClipboardObserver} to be added
	 */
	public void addClipboardObserver(ClipboardObserver observer) {
		Objects.requireNonNull(observer, "Given clipboard observer must not be null.");
		observers.add(observer);
		
	}
	
	/**
	 * Removes the given {@link ClipboardObserver} from the list of
	 * clipboard observers, if it exists in the list.
	 * 
	 * @param observer {@link ClipboardObserver} to be removed
	 */
	public void removeClipboardObserver(ClipboardObserver observer) {
		Objects.requireNonNull(observer, "Given clipboard observer must not be null.");
		observers.remove(observer);
		
	}
	
	/**
	 * Notifies attached observers of the change in the clipboard.
	 */
	public void notifyObservers() {
		for (ClipboardObserver observer : observers) {
			observer.updateClipboard();
		}
	}
}
