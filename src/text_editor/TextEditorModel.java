package texteditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A data model of the text editor. It storess text lines in a {@link List} of 
 * strings, cursor location as a {@link LocationRange} object and the current
 * selection as a {@link LocationRange} object. It also serves as a subject for 
 * {@link CursorObserver} and {@link TextObserver}.
 * 
 * @author Tome Radman
 *
 */
public class TextEditorModel {

	/**
	 * List of text lines currently in the model.
	 */
	private List<String> lines = new ArrayList<>();
	
	/**
	 * Current selection.
	 */
	private LocationRange selectionRange;
	
	/**
	 * Current cursor location.
	 */
	private Location cursorLocation;
	
	/**
	 * List of cursor observers.
	 */
	private List<CursorObserver> cursorObservers;
	
	/**
	 * List of text observers.
	 */
	private List<TextObserver> textObservers;
	
	/**
	 * A constructor which stores lines of the given text into
	 * the lines list. It also initializes cursor location and
	 * selection range.
	 * 
	 * @param text Initial text
	 */
	public TextEditorModel(String text) {		
		String[] lines = text.split("\n");
		for (String line : lines) {
			this.lines.add(line);
		}
		
		this.cursorLocation = new Location(0, 0);
		Location initStart = new Location(0, 0);
		Location initEnd = new Location(0, 0);
		
		this.selectionRange = new LocationRange(initStart, initEnd);
	}
	
	/**
	 * Returns the lines list.
	 * 
	 * @return Lines list
	 */
	public List<String> getLines() {
		return lines;
	}
	
	/**
	 * Returns the selection range.
	 * 
	 * @return Selection range
	 */
	public LocationRange getSelectionRange() {
		return selectionRange;
	}
	
	/**
	 * Sets the selection range
	 * 
	 * @param selectionRange New selection range
	 */
	public void setSelectionRange(LocationRange selectionRange) {
		this.selectionRange = Objects.requireNonNull(selectionRange);
	}

	/**
	 * Returns the cursor location-
	 * 
	 * @return Current cursor location
	 */
	public Location getCursorLocation() {
		return cursorLocation;
	}

	/**
	 * Returns the list of cursor observers.
	 * 
	 * @return Cursor observers
	 */
	public List<CursorObserver> getCursorObservers() {
		return cursorObservers;
	}



	/**
	 * An iteration for transparent traversal of stored text lines.
	 * 
	 * @author Tome Radman
	 *
	 */
	private class LinesIterator implements Iterator<String> {
		
		/** Index of the current element. */
		private int currIndex;
		
		/** Index of the last element. */
		private int endIndex;
		
		/**
		 * A constructor which sets the range from which the iteration
		 * is performed.
		 * 
		 * @param startIndex Index of the start element
		 * @param endIndex Idex of the end element
		 */
		public LinesIterator(int startIndex, int endIndex) {
			int lnSize = lines.size();
			if (startIndex < 0 || startIndex >= lnSize ||
				endIndex < 0 || endIndex > lnSize ||
				startIndex > endIndex) {
				throw new IndexOutOfBoundsException("Invalid iterator indexes.");
			}
			
			this.currIndex = startIndex; 
			this.endIndex = endIndex;
		}
		
		/**
		 * Returns true if there is a next element in interation.
		 */
		public boolean hasNext() {
			return currIndex < endIndex;
		}

		@Override
		public String next() {
			String next = lines.get(currIndex);
			++currIndex;
			return next;
		}
	}
	
	/**
	 * Returns an iterator which iterates through all lines of the text.
	 * 
	 * @return {@link LinesIterator} for iterating through all lines
	 */
	public Iterator<String> allLines() {
		return new LinesIterator(0, lines.size());
	}
	
	/**
	 * Returns an iteratior for iterating through lines in given range.
	 * 
	 * @param index1 Index of the start line
	 * @param index2 Inex of the end line
	 * @return
	 */
	public Iterator<String> linesRange(int index1, int index2) {
		return new LinesIterator(index1, index2);
	}
	
	
	/**
	 * Adds a cursor observer to the list of cursor observers.
	 * 
	 * @param observer A {@link CursorObserver} to be added
	 */
	public void addCursorObserver(CursorObserver observer) {
		if (cursorObservers == null) {
			cursorObservers = new ArrayList<>();
		}
		cursorObservers.add(observer);
	}
	
	/**
	 * Removes a cursor observer from the list of cursor observers.
	 * 
	 * @param observer A {@link CursorObserver} to be removed
	 */
	public void removeCursorObserver(CursorObserver observer) {
		cursorObservers.remove(observer);
	}
	
	/**
	 * Notifies all attached cursor observers of the change in cursor location.
	 */
	private void notifyCursorObservers() {
		for (CursorObserver observer : cursorObservers) {
			observer.updateCursorLocation(cursorLocation);
		}
	}
	
	/**
	 * Adds a text observer to the list of text observers.
	 * 
	 * @param observer A {@link TextObserver} to be added
	 */
	public void addTextObserver(TextObserver observer) {
		if (textObservers == null) {
			textObservers = new ArrayList<>();
		}
		textObservers.add(observer);
	}
	
	/**
	 * Removes a text observer to the list of text observers.
	 * 
	 * @param observer A {@link TextObserver} to be removed
	 */
	public void removeTextObserver(TextObserver observer) {
		textObservers.remove(observer);
	}
	
	/**
	 * Notifies all attached text observers of the change in text.
	 */
	private void notifyTextObservers() {
		for (TextObserver observer : textObservers) {
			observer.updateText();
		}
	}
	
	/**
	 * Moves the cursor location to the left.
	 */
	public void moveCursorLeft() {
		if (!(cursorLocation.x == 0 && cursorLocation.y == 0)) {
			if (cursorLocation.x == 0 && cursorLocation.y > 0) {
				cursorLocation.x = lines.get(cursorLocation.y - 1).length();
				cursorLocation.y -= 1;
			} else {
				if (cursorLocation.x > 0) {
					cursorLocation.x -= 1;
				}
			}
			
			notifyCursorObservers();
		}
	}

	/**
	 * Moves the cursor location to the right.
	 */
	public void moveCursorRight() {
		if (!(cursorLocation.y == lines.size() - 1 &&
				cursorLocation.x >= lines.get(cursorLocation.y).length())) {
			
			if (cursorLocation.x == lines.get(cursorLocation.y).length() && 
				cursorLocation.y < lines.size() - 1) {
				cursorLocation.x = 0;
				cursorLocation.y += 1;
			} else {
				cursorLocation.x += 1;
			}
			
			notifyCursorObservers();
		}
	}
	
	/**
	 * Moves the cursor location up.
	 */
	public void moveCursorUp() {
		if (cursorLocation.y > 0) {
			
			String upLine = lines.get(cursorLocation.y - 1);
			if (cursorLocation.x > upLine.length()) {
				cursorLocation.x = upLine.length();
			}
			cursorLocation.y -= 1;
			
			notifyCursorObservers();
		}
	}
	
	/**
	 * Moves the cursor location down.
	 */
	public void moveCursorDown() {
		if (cursorLocation.y < lines.size() - 1) {
			
			String downLine = lines.get(cursorLocation.y + 1);
			if (cursorLocation.x > downLine.length()) {
				cursorLocation.x = downLine.length();
			}
			cursorLocation.y += 1;
			
			notifyCursorObservers();
		}
	}
	
	/**
	 * Deletes the character at location to the left of cursor location.
	 */
	public void deleteBefore() {
		if (!(cursorLocation.x == 0 && cursorLocation.y == 0)) {
			int cursX = cursorLocation.x;
			int cursY = cursorLocation.y;
			moveCursorLeft();

			if (cursX == 0) {
				String upLine = lines.get(cursY - 1);
				lines.set(cursY - 1, 
						upLine + lines.get(cursY));
				lines.remove(cursY);
				
			} else {
				String currLine = lines.get(cursY);
				if (cursX != currLine.length()) {
					lines.set(cursY, 
							currLine.substring(0, cursX - 1) + 
							currLine.substring(cursX, currLine.length()));
				} else {
					lines.set(cursY, 
							currLine.substring(0, cursX - 1));
				}
			}
			
			notifyTextObservers();
		}
	}
	
	/**
	 * Deletes the character at location to the right of cursor location.
	 */
	public void deleteAfter() {
		if (!(cursorLocation.y >= lines.size() - 1 &&
				cursorLocation.x >= lines.get(cursorLocation.y).length())) {
			
			String currLine = lines.get(cursorLocation.y);
			
			if (cursorLocation.x == currLine.length()) {
				lines.set(cursorLocation.y,
						currLine + lines.get(cursorLocation.y + 1));
				lines.remove(cursorLocation.y + 1);
					
			} else {
				lines.set(cursorLocation.y, 
						currLine.substring(0, cursorLocation.x) + 
						currLine.substring(cursorLocation.x + 1));
			}
			
			notifyTextObservers();
		}
	}
	
	/**
	 * Deletes the text in the given range.
	 * 
	 * @param range Range of the text to be deleted
	 */
	public void deleteRange(LocationRange range) {		
		Location start = range.getStart();
		Location end = range.getEnd();
		
		if (start.compareTo(end) > 0) {
			Location pom = start;
			start = end;
			end = pom;
		}
		String startLine = lines.get(start.y);
		String endLine = lines.get(end.y);
		
		lines.set(start.y, 
				startLine.substring(0, start.x) +
				endLine.substring(end.x, endLine.length()));
		
		for (int i = start.y + 1; i <= end.y; ++i) {
			lines.remove(start.y + 1);
		}

		cursorLocation = start;
		Location selRange = new Location(start.x, start.y);
		setSelectionRange(new LocationRange(selRange, selRange));

		notifyTextObservers();
	}
	
	/**
	 * Inserts the given character at cursor location.
	 * 
	 * @param c Character to be inserted
	 */
	public void insert(char c) {
		if (selectionRange.isDefined()) {
			deleteRange(selectionRange);
		}
		String currLine = lines.get(cursorLocation.y);
		String back = currLine.substring(0, cursorLocation.x);
		String front = currLine.substring(cursorLocation.x, currLine.length());
		
		if (c == 10) {
			lines.set(cursorLocation.y, back);
			lines.add(cursorLocation.y + 1, front);
			cursorLocation.setLocation(0, cursorLocation.y + 1);
		} else {
			lines.set(cursorLocation.y, back + c + front);
			cursorLocation.setLocation(cursorLocation.x + 1, cursorLocation.y);
		}
		
		Location cursAfter = new Location(cursorLocation.x, cursorLocation.y);
		setSelectionRange(new LocationRange(cursAfter, cursAfter));
		System.out.println(selectionRange);
		
		notifyTextObservers();
	}
	
	/**
	 * Inserts the given text.
	 * 
	 * @param s Text to be inserted
	 */
	public void insert(String s) {
		if (selectionRange.isDefined()) {
			deleteRange(selectionRange);
		}
		
		String[] sLines = s.split("\n");
		System.out.println(sLines.length);
		int newLinesCount = sLines.length;
		
		String currLine = lines.get(cursorLocation.y);
		String back = currLine.substring(0, cursorLocation.x);
		String front = currLine.substring(cursorLocation.x, currLine.length());

		lines.set(cursorLocation.y, back + sLines[0]);
		for (int i = 1; i < sLines.length; ++i) {
			lines.add(cursorLocation.y + i, sLines[i]);
		}
		
		String endLine = lines.get(cursorLocation.y + newLinesCount - 1);
		lines.set(cursorLocation.y + newLinesCount - 1, endLine + front);
		
		cursorLocation.setLocation(
				lines.get(cursorLocation.y + newLinesCount - 1).length() - front.length()
				,cursorLocation.y + newLinesCount - 1);
		
		Location cursAfter = new Location(cursorLocation.x, cursorLocation.y);
		setSelectionRange(new LocationRange(cursAfter, cursAfter));

		notifyTextObservers();
	}
}
