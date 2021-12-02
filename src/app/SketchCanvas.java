package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class SketchCanvas extends DisplayCanvas {
	
	private String brushTool; // "PEN" or "ERASER"
	private int brushSize;
	private String brushTip; // "CIRCLE", "SQUARE" or "TRIANGLE"
	private String currentEnvironment;
	
	private GeometryLogger logger;
	
	/**
	 * Default constructor for the SketchCanvas class
	 */
	public SketchCanvas() {
		// set up  default settings
		this.brushTool = "PEN";
		this.brushSize = 50;
		this.brushTip = "CIRCLE";
		this.currentEnvironment = "Tree";
		this.logger = new GeometryLogger();
		super.initColour = environmentColours[findIndex(currentEnvironment)];
		
		// add mouse listeners
		this.addMouseListener(new MouseAdapter() {
			int x1, y1;
			@Override
			public void mousePressed(MouseEvent e) {
				x1 = e.getX();
				y1 = e.getY();
				//System.out.println("Pressed: x=" + x1 + ", y=" + y1);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				int x2 = e.getX();
				int y2 = e.getY();
				//System.out.println("Released: x=" + x2 + ", y=" + y2);
				if(currentEnvironment.equals("Tree")) {
					logger.updateTrees(x1, y1, x2, y2, brushSize);
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				
				if(g2 != null) {
					switch(brushTip) {
						case "CIRCLE":
							g2.fillOval(x-brushSize/2, y-brushSize/2, brushSize, brushSize);
							break;
						case "SQUARE":
							g2.fillRect(x-brushSize/2, y-brushSize/2, brushSize, brushSize);
							break;
						case "TRIANGLE":
							int[] arrX = {x-brushSize/2, x, x+brushSize/2};
							int[] arrY = {y+brushSize/2, y-brushSize/2, y+brushSize/2}; 
							g2.fillPolygon(arrX, arrY, 3);
							break;
						default:
							System.out.println("ERROR: \"" + brushTip + "\" is an invalid brush tip.");
					}
					repaint();
					
				}
				
				//System.out.println("Dragged: x=" + x + ", y=" + y);
			}
		});
	}
	
	public String[] getEnvironmentLabels() {
		return environmentLabels;
	}
	
	public Color[] getEnvironmentColours() {
		return environmentColours;
	}
	
	public String getBrushTool() {
		return brushTool;
	}
	
	public void setBrushTool(String tool) {
		switch(tool) {
			case "PEN":
				this.brushTool = "PEN";
				g2.setPaint(environmentColours[findIndex(currentEnvironment)]);
				break;
			case "ERASER":
				this.brushTool = "ERASER";
				g2.setPaint(Color.WHITE);
				break;
			default:
				System.out.println("ERROR: Cannot set brush tool to " + tool);
		}
	}
	
	public int getBrushSize() {
		return brushSize;
	}
	
	public void setBrushSize(int size) {
		if(size > -1 && size < 101) { // brush size between 0-100
			this.brushSize = size;
		} else {
			System.out.println("ERROR: Cannot set brush size to " + size);
		}
	}
	
	public String getBrushTip() {
		return brushTip;
	}

	public void setBrushTip(String tip) {
		if(tip.equals("CIRCLE") || tip.equals("SQUARE") || tip.equals("TRIANGLE")) {
			this.brushTip = tip;
		} else {
			System.out.println("ERROR: Cannot set brush tip to " + tip);
		}
	}
	
	public String getCurrentEnvironment() {
		return currentEnvironment;
	}
	
	public void setCurrentEnvironment(String environment) {
		if(Arrays.stream(environmentLabels).anyMatch(environment::equals)) {
			this.currentEnvironment = environment;
			g2.setPaint(environmentColours[findIndex(currentEnvironment)]);
		} else {
			System.out.println("ERROR: Cannot set current environment to " + environment);
		}
	}
	
	public GeometryLogger getLogger() {
		return logger;
	}
	
	/**
	 * Finds the index of a given environment
	 * @param environment
	 * @return
	 */
	public int findIndex(String e) {
		int index;
		if(Arrays.stream(environmentLabels).anyMatch(e::equals)) { // if environment is valid
			index = Arrays.asList(environmentLabels).indexOf(e); // get valid index
		} else { // if environment is invalid
			index = 0; // return 0 (default environment)
		}
		return index;
	}
	
}
