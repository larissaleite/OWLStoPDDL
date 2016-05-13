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

			//Create a file chooser, initialised with a path 
			fc = new JFileChooser("/tmp/workspace/OWLS2PDDL");

			//Create the open button.  
			openButton = new JButton("Choose OWL-S file to convert to PDDL");
			openButton.addActionListener(this);

			//For layout purposes, put the buttons in a separate panel
			JPanel buttonPanel = new JPanel(); //use FlowLayout
			buttonPanel.add(openButton);

			//Add the buttons and the log to this panel.
			add(buttonPanel, BorderLayout.PAGE_START);
			add(logScrollPane, BorderLayout.CENTER);
		}


		/**
		 * When clicked on <code>openButton</code> a file chooser is opened.
		 */
		JButton openButton;

		/**
		 * <code>log</code> holding the log entries.
		 */
		JTextArea log;

		/**
		 * <code>fc</code> file chooser object.
		 */
		JFileChooser fc;

		
		/**
		 * Handler to perform the open button action, when clicked.
		 */
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == openButton) {
				int returnVal = fc.showOpenDialog(Main.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					
					log.append("Opening: " + file.getName() + "\n\n");
					log.append("Parsing the owl-s files \n\n");

					try {
						new CreatePDDL(file.getAbsolutePath(), file.getName());
						log.append("File successfully converted to PDDL" + "\n");
					} catch (Exception ee) {
						log.append("There was an error when converting the owls file" + "\n");
						System.out.println("Error during converting the owl-s file! \n" + ee);
					}
				} else {
					log.append("Open command cancelled by user.\n");
				}
				log.setCaretPosition(log.getDocument().getLength());
			}
		}


		/**
		 * Creates the GUI and shows it. This method is only for the demonstration and
		 * to include the file-chooser.   
		 */
		private static void createAndShowGUI() {
			//Make sure we have nice window decorations.
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);

			//Create and set up the window.
			JFrame frame = new JFrame("FileChooserDemo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//Create and set up the content pane.
			JComponent newContentPane = new Main();
			newContentPane.setOpaque(true); //content panes must be opaque
			frame.setContentPane(newContentPane);

			//Display the window.
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
