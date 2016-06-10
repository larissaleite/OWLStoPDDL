package it4bi.ufrt.xwt.OWLStoPDDL;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Main extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	/**
	 * This class opens a standard file-requester to select an owls-file
	 */
	public Main() {
		super(new BorderLayout());

		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		// Create a file chooser, initialized with a path
		fc = new JFileChooser("/tmp/workspace/OWLS2PDDL");

		// Create the open button.
		//openButton = new JButton("Choose OWL-S file to convert to PDDL");
		openButton1 = new JButton("Choose first OWL-S file");
		openButton1.addActionListener(this);
		
		openButton2 = new JButton("Choose second OWL-S file");
		openButton2.addActionListener(this);

		// For layout purposes, put the buttons in a separate panel
		JPanel buttonPanel = new JPanel(); // use FlowLayout
		buttonPanel.add(openButton1);		
		buttonPanel.add(openButton2);
		
		// Add the buttons and the log to this panel.
		add(buttonPanel, BorderLayout.PAGE_START);
		add(logScrollPane, BorderLayout.CENTER);
	}

	/**
	 * When clicked on <code>openButton</code> a file chooser is opened.
	 */
	JButton openButton1;
	
	/**
	 * When clicked on <code>openButton</code> a file chooser is opened.
	 */
	JButton openButton2;

	/**
	 * <code>log</code> holding the log entries.
	 */
	JTextArea log;

	/**
	 * <code>fc</code> file chooser object.
	 */
	JFileChooser fc;
	
	File file1;
	
	File file2;

	/**
	 * Handler to perform the open button action, when clicked.
	 */
	public void actionPerformed(ActionEvent e) {
		int returnVal;
		
		if (e.getSource() == openButton1) {
			returnVal = fc.showOpenDialog(Main.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file1 = fc.getSelectedFile();

				log.append("Opening: " + file1.getName() + "\n\n");

				try {
					
				} catch (Exception ee) {
					log.append("There was an error when reading the owls file" + "\n");
					System.out.println("Error during reading the owl-s file! \n" + ee);
				}
			} else {
				log.append("Open command cancelled by user.\n");
			}
			
			log.setCaretPosition(log.getDocument().getLength());

		} else if (e.getSource() == openButton2) {
			returnVal = fc.showOpenDialog(Main.this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file2 = fc.getSelectedFile();
				
				log.append("Opening: " + file2.getName() + "\n\n");
				
				try {
					
				} catch (Exception ee) {
					log.append("There was an error when reading the owls file" + "\n");
					System.out.println("Error during reading the owl-s file! \n" + ee);
				} 
			} else {
				log.append("Open command cancelled by user.\n");
			}
			
			log.setCaretPosition(log.getDocument().getLength());
		}
		
		if (file1 != null && file2 != null) {
			System.out.println("creating extractor " + file1.getAbsolutePath() + "  --- "+ file2.getAbsolutePath());
			new ConceptsExtractor(file1.getAbsolutePath(), file2.getAbsolutePath());
		}
	}

	/**
	 * Creates the GUI and shows it. This method is only for the demonstration
	 * and to include the file-chooser.
	 */
	private static void createAndShowGUI() {
		// Make sure we have nice window decorations.
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		// Create and set up the window.
		JFrame frame = new JFrame("FileChooserDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		JComponent newContentPane = new Main();
		newContentPane.setOpaque(true); // content panes must be opaque
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Creating and showing this application's GUI.
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
} // End of Document
