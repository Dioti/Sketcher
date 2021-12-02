package app;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class SketcherViewer {
	
	public static void main(String[] args) {
		
		// set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
		
        // set up window
        JFrame app = new JFrame("Sketcher");
        app.setResizable(false);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel padded = new JPanel();
        padded.setLayout(new BoxLayout(padded, BoxLayout.Y_AXIS));
        padded.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // add components
        Display d = new Display();
        padded.add(d, BorderLayout.PAGE_START);
        
        Controls c = new Controls(d);
        padded.add(c);
        
		// pack and display
        app.add(padded);
		app.setJMenuBar(new Menu());
		app.pack();
		app.setLocationRelativeTo(null); // center window
		app.getContentPane().requestFocusInWindow(); // disable autofocus on buttons
		app.setVisible(true);

	}
}
