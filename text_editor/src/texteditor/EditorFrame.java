package texteditor;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * A {@link JFrame} which displays a {@link TextEditor}.
 * 
 * @author Tome Radman
 *
 */
public class EditorFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * A constructor which creates the {@link TextEditorModel} and {@link TextEditor} 
	 * objects and puts the editor into the frame.
	 */
	public EditorFrame() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocation(0, 0);
		setSize(400, 350);
		
		TextEditorModel model = new TextEditorModel(
				"This is the first line.\nThis is the second line.\n:)"
				);
		
		JComponent editor = new TextEditor(model);
		editor.setFocusable(true);
		this.getContentPane().add(editor);
		editor.requestFocusInWindow();
		
	}
	
	/**
	 * Runs the text editor.
	 * 
	 * @param args Command line arguments: not used.
	 */
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			new EditorFrame().setVisible(true);
		});
	}
}
