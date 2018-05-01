package texteditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.JComponent;

/**
 * A JComponent which represents a simple text editor. It uses an instance
 * of {@link TextEditorModel} for storing written text.
 * 
 * Suppported actions are:
 * -text insertion
 * -text selection using SHIFT + arrow keys
 * -text deletion using backspace or delete
 * -copy (CTRL+C), cut (CTRL+X) and paste (CTRL+V). 
 *  If SHIFT is down, text from the top of clipboard is removed.
 * 
 * @author Tome Radman
 *
 */
public class TextEditor extends JComponent implements CursorObserver, 
													TextObserver, ClipboardObserver {

	private static final long serialVersionUID = 1L;
	
	/** 
	 * The data model of the text editor. 
	 */
	private TextEditorModel model;
	
	/** 
	 * The clipboard of the editor, used for actions such as copy of paste. 
	 */
	private ClipboardStack clipboard;
	
	/**
	 * A {@link KeyAdapter} instance which specifies actions to be taken on
	 * specific key events.
	 */
	private KeyAdapter keyAdapter = new KeyAdapter() {
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				model.moveCursorLeft();
				setSelection(e);
			}
			else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				model.moveCursorRight();
				setSelection(e);
			}
			else if (e.getKeyCode() == KeyEvent.VK_UP) {
				model.moveCursorUp();
				setSelection(e);
			}
			else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				model.moveCursorDown();
				setSelection(e);
			}
			else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				LocationRange sel = model.getSelectionRange();
				if (sel.getStart().compareTo(sel.getEnd()) == 0) {
					model.deleteBefore();
				} else {
					model.deleteRange(model.getSelectionRange());
				}
				setSelection(e);
			} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				LocationRange sel = model.getSelectionRange();
				if (sel.getStart().compareTo(sel.getEnd()) == 0) {
					model.deleteAfter();
				} else {
					model.deleteRange(model.getSelectionRange());
				}
				setSelection(e);
			} else if (e.isControlDown()) {
				if (e.getKeyCode() == KeyEvent.VK_C || e.getKeyCode() == KeyEvent.VK_X) {
					LocationRange sel = model.getSelectionRange();
					if (sel.isDefined()) {
						String selText = getSelectedText(sel);
						clipboard.pushText(selText);
						if (e.getKeyCode() == KeyEvent.VK_X) {
							model.deleteRange(model.getSelectionRange());
						}
					}
				} else if (e.getKeyCode() == KeyEvent.VK_V) {
					if (!clipboard.isEmpty()) {
						String pasteText;
						if (e.isShiftDown()) {
							pasteText = clipboard.pop();
						} else {
							pasteText = clipboard.peek();
						}
						model.insert(pasteText);
					}
				} 
			} else if (!e.isActionKey()) {
				char c = e.getKeyChar();
				if (Character.isDefined(c)) {
					model.insert(e.getKeyChar());
				}
			}
			
		}
		
		/**
		 * Updates the selection range.
		 * @param e A {@link KeyEvent} instance
		 */
		private void setSelection(KeyEvent e) {
			Location cursor = model.getCursorLocation();
			Location cursorCpy = new Location(cursor.x, cursor.y);
			if (e.isShiftDown()) {
				LocationRange sel = model.getSelectionRange();
				Location start = sel.getStart();
				
				model.setSelectionRange(new LocationRange(start, cursorCpy));
			} else {
				model.setSelectionRange(new LocationRange(cursorCpy, cursorCpy));
			}
		}
		
		/**
		 * Returns the selected text.
		 * @param sel Range of the selected text
		 * @return selected text
		 */
		private String getSelectedText(LocationRange sel) {
			Location start = sel.getStart();
			Location end = sel.getEnd();
			StringBuilder sb = new StringBuilder();
			List<String> lines = model.getLines();
			
			if (start.compareTo(end) > 0) {
				Location pom = start;
				start = end;
				end = pom;
			}
			
			int linesSpan = end.y - start.y;
			String startLine = lines.get(start.y);
			if (linesSpan > 0) {
				sb.append(startLine.substring(start.x, startLine.length())).append('\n');
				
				for (int i = 1; i < linesSpan; ++i) {
					sb.append(lines.get(start.y + i)).append('\n');
				}
				
				String endLine = lines.get(end.y);
				sb.append(endLine.substring(0, end.x));
			} else {
				sb.append(startLine.substring(start.x, end.x));
			}
			System.out.println(sb.toString());
			return sb.toString();
			
		}

	};
	
	/**
	 * A constructor which sets the model and subscribes itself as
	 * the cursor, text and clipboard observer.
	 * 
	 * @param model A {@link TextEditorModel} instance
	 */
	public TextEditor(TextEditorModel model) {
		this.model = Objects.requireNonNull(model);
		model.addCursorObserver(this);
		model.addTextObserver(this);
		
		this.clipboard = new ClipboardStack();
		clipboard.addClipboardObserver(this);
		
		addKeyListener(keyAdapter);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		
		Font tr = new Font("Ubuntu", Font.BOLD, 14);
		g.setFont(tr);
		FontMetrics fm = g.getFontMetrics();
		
		drawSelection(g, fm);
		drawText(g, fm);
		drawCursor(g, fm);
				
	}
	
	/**
	 * Draws the text stored in the model using the given
	 * {@link Graphics} and {@link FontMetrics} objects.
	 * 
	 * @param g The {@link Graphics} object used for drawing
	 * @param fm The {@link FontMetrics} object used for getting font information
	 */
	private void drawText(Graphics g, FontMetrics fm) {
		g.setColor(Color.BLACK);
		
		String line = null;
		Iterator<String> linesIter = model.allLines();
		
		int lineCount = 0;
		while (linesIter.hasNext()) {
			line = linesIter.next();
			g.drawString(line, 0, (lineCount+1) * fm.getHeight());
			++lineCount;
		}
	}
	
	/**
	 * Draws the cursor of the text editor using the given
	 * {@link Graphics} and {@link FontMetrics} objects.
	 * 
	 * @param g The {@link Graphics} object used for drawing
	 * @param fm The {@link FontMetrics} object used for getting font information
	 */
	private void drawCursor(Graphics g, FontMetrics fm) {
		g.setColor(Color.BLACK);
		
		Location cursorLocation = model.getCursorLocation();
		String currLine = model.getLines().get(cursorLocation.y);
		int cursorY = (cursorLocation.y + 1) * fm.getHeight();
		int cursorX = fm.stringWidth(currLine.substring(0, cursorLocation.x));
		
		g.drawLine(cursorX, cursorY, cursorX, cursorY - fm.getAscent());

	}

	/**
	 * Draws the selection using given {@link Graphics} and
	 * {@link FontMetrics} object.
	 * 
	 * @param g {@link Graphics} object used for drawing
	 * @param fm {@link FontMetrics} used for getting font information
	 */
	private void drawSelection(Graphics g, FontMetrics fm) {
		g.setColor(Color.LIGHT_GRAY);
		LocationRange sel = model.getSelectionRange();
		Location start = sel.getStart();
		Location end = sel.getEnd();
		if (start.compareTo(end) > 0) {
			Location pom = start;
			start = end;
			end = pom;
		} 
		int linesSpan = end.y - start.y;
		if (linesSpan > 0) {
			int startPosition = fm.stringWidth(
					model.getLines().get(start.y).substring(0, start.x));
			g.fillRect(
					startPosition, 
					fm.getHeight()*start.y + fm.getDescent(), 
					getWidth() - start.x, 
					fm.getHeight());
			for (int i = 1; i < linesSpan; ++i) {
				g.fillRect(
						0, 
						fm.getHeight()*(start.y + i) + fm.getDescent(),
						getWidth(), 
						fm.getHeight());
			}
			
			int endPosition = fm.stringWidth(
					model.getLines().get(end.y).substring(0, end.x));
			g.fillRect(
					0, fm.getHeight()*end.y + fm.getDescent(), endPosition, fm.getHeight());
		} else {
			int startPosition = fm.stringWidth(
					model.getLines().get(start.y).substring(0, start.x));
			int selectionWidth = fm.stringWidth(
					model.getLines().get(start.y).substring(start.x, end.x));
			
			g.fillRect(startPosition, 
					fm.getHeight()*start.y + fm.getDescent(), 
					selectionWidth, 
					fm.getHeight());
			
		}
	}

	@Override
	public void updateCursorLocation(Location loc) {
		repaint();
	}
	
	@Override
	public void updateText() {
		repaint();
	}
	
	
	public KeyAdapter getKeyAdapter() {
		return keyAdapter;
	}

	@Override
	public void updateClipboard() {
		repaint();
	}
}
