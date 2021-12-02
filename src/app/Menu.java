package app;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class Menu extends JMenuBar {

	public Menu() {
		JMenu help = new JMenu("Help");
		MenuListener listener = new MenuListener();

		JMenuItem about = new JMenuItem("About");
		about.setActionCommand(about.getName());
		about.addActionListener(listener);

		JMenuItem credits = new JMenuItem("Credits");
		credits.setActionCommand(credits.getName());
		credits.addActionListener(listener);

		help.add(about);
		help.add(credits);
		this.add(help);
	}
	
	/**
	 * ActionListener for menu items that open a text popup when clicked
	 */
	class MenuListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String src = e.getActionCommand(); // gets the menu item called

			// setup window frame
			JFrame frame = new JFrame(src);
			frame.setResizable(false); // locks window
			frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
			
			// remove icon (set to 1x1 transparent pixel)
			Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
			frame.setIconImage(icon);

			// setup text container
			int padding = 15;
			JTextArea content = new JTextArea();
			content.setEditable(false);
			content.setHighlighter(null);
			content.setOpaque(false);
			content.setBorder(new EmptyBorder(padding, padding, padding, padding));

			// read text file
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream("./src/" + src + ".txt")));
				content.read(input, "reading");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			// pack and display
			frame.getContentPane().add(content);
			frame.pack();
			frame.setLocationRelativeTo(null); // center window
			frame.setVisible(true);
		}
	}
}
