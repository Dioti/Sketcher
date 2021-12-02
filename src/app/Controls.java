package app;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

public class Controls extends JPanel {
	
	public Controls(Display d) {
		this.setLayout(new BorderLayout());
		
		SketchControls s = new SketchControls(d.getSketchCanvas(), d.getRenderCanvas());
		int sw = (int) Math.round(d.getSketchCanvas().getPreferredSize().getWidth());
		int sh = (int) Math.round(s.getPreferredSize().getHeight());
		s.setPreferredSize(new Dimension(sw, sh));
		this.add(s, BorderLayout.LINE_START);
		
		RenderControls r = new RenderControls(d.getRenderCanvas());
		int rw = (int) Math.round(d.getRenderCanvas().getPreferredSize().getWidth());
		int rh = (int) Math.round(r.getPreferredSize().getHeight());
		r.setPreferredSize(new Dimension(rw, rh));
		this.add(r, BorderLayout.LINE_END);
	}

}
