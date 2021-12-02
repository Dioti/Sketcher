package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DisplayCanvas extends JPanel {

	public static int WIDTH = 500;
	public static int HEIGHT = 400;
	
	public static String[] environmentLabels = {
			"Tree",
			"Grass",
			"Flowers",
			"Dirt",
			"Sky",
			"Sun"};
	public static Color[] environmentColours = {
			new Color(26,122,21),
			new Color(165,237,76),
			new Color(255,181,253),
			new Color(122,89,58),
			new Color(146,247,227),
			new Color(255,255,0)};
	
	public Image display;
	public Graphics2D g2;
	
	public Color initColour;
	
	/**
	 * Constructor for the DisplayCanvas class
	 * Sets the preferred size of the canvas
	 */
	public DisplayCanvas() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
	}
	
	/**
	 * Override for paintComponent method
	 * Draws the current canvas display
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (display == null) {
			display = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) display.getGraphics();
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			if(initColour != null) {
				g2.setPaint(initColour);
			} else {
				g2.setPaint(Color.BLACK);
			}
			clear();
		}
		g.drawImage(display, 0, 0, null);
	}
	
	/**
	 * Clears the canvas display
	 */
	public void clear() {
		Color old = g2.getColor();
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, WIDTH, HEIGHT);
		g2.setColor(old);
		repaint();
	}
	
	/**
	 * Gets the canvas display as an Image
	 * @return
	 */
	public Image getDisplay() {
		return display;
	}
	
	/**
	 * Imports an image to the canvas
	 * @param f
	 */
	public void importImage(File f) {
		try {
			Image imported = ImageIO.read(f);
			System.out.println("imported: "+ imported.getClass());
			Graphics g = display.getGraphics();
			g.drawImage(imported, 0, 0, null);
			repaint();
			g.dispose(); // to save resources
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Exports an image from the canvas
	 * @param f
	 */
	public void exportImage(File f) {
		// make a copy of the canvas
		BufferedImage bi = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		try {
			g.drawImage(display, 0, 0, null);
			ImageIO.write(bi, "png", f);
			g.dispose(); // to save resources
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void update(Image img) {
		this.display = img;
		System.out.println("Updated with Image");
	}
	
}
