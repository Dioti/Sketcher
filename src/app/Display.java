package app;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Display extends JPanel {
	
	private SketchCanvas s;
	private RenderCanvas r;
	
	/**
	 * Constructor for the Display class
	 * Creates a sketch canvas and a render canvas
	 */
	public Display() {
		s = new SketchCanvas();
		this.add(s);
		
		r = new RenderCanvas();
		this.add(r);
	}
	
	/**
	 * Getter for the instantiated SketchCanvas
	 * @return the sketch canvas
	 */
	public SketchCanvas getSketchCanvas() {
		return s;
	}
	
	/**
	 * Getter for the instantiated RenderCanvas
	 * @return the render canvas
	 */
	public RenderCanvas getRenderCanvas() {
		return r;
	}

}
