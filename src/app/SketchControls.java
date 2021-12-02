package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SketchControls extends JPanel {
	
	private static Border selected = new MatteBorder(0, 0, 5, 0, Color.BLACK);
	private static Border unselected = new MatteBorder(0, 0, 5, 0, Color.LIGHT_GRAY);
	
	private SketchCanvas sc;
	private RenderCanvas rc;
	private Color defaultColour; // colour of window background
	
	/**
	 * Constructor for SketchControls class
	 * @param canvas the canvas instance that is being controlled
	 */
	public SketchControls(SketchCanvas sc, RenderCanvas rc) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.defaultColour = this.getBackground();
		this.sc = sc;
		this.rc = rc;
		
		// create the first row of options (canvas settings)
		JPanel pane1 = new JPanel();
		pane1.setLayout(new BorderLayout());
		pane1.add(createCanvasPane(), BorderLayout.LINE_START); // canvas settings
		pane1.add(createRenderPane(), BorderLayout.LINE_END); // render button
		
		// create the second row of options (brush settings)
		JPanel pane2 = new JPanel();
		pane2.setLayout(new BorderLayout());
		pane2.add(createToolPane(), BorderLayout.LINE_START);
		pane2.add(createTipPane());
		pane2.add(createSizePane(), BorderLayout.LINE_END);
		
		// create the third row of options (environment settings)
		JPanel pane3 = new JPanel();
		pane3.setLayout(new BorderLayout());
		pane3.add(createEnvironmentsPane());
		
		// add all to pane
		this.add(pane1);
		this.add(pane2);
		this.add(pane3);
	}

	/**
	 * Create canvas settings allowing the user to import, export and clear the canvas
	 * 
	 * @return the panel containing the buttons
	 */
	private JPanel createCanvasPane() {
		JPanel canvasPane = new JPanel();
		canvasPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Canvas"));

		JButton importButton = new JButton("Import");
		importButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int result = fc.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					System.out.println("Importing: " + f.getAbsolutePath());
					sc.importImage(f);
					String txtPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".") + 1) + "txt";
					sc.getLogger().importGeom(new File(txtPath));
				}
			}
		});

		JButton exportButton = new JButton("Export");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int result = fc.showSaveDialog(null);
				if (result == fc.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					System.out.println("Exporting: " + f.getAbsolutePath());
					sc.exportImage(f);
					String txtPath = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(".") + 1) + "txt";
					sc.getLogger().exportGeom(new File(txtPath));
				}
			}
		});
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sc.clear();
				sc.getLogger().clear();
			}
		});

		try {
			Image importIcon = ImageIO.read(getClass().getResource("/icons/download.png"));
			Image exportIcon = ImageIO.read(getClass().getResource("/icons/upload.png"));
			Image clearIcon = ImageIO.read(getClass().getResource("/icons/bin.png"));
			importButton.setIcon(new ImageIcon(importIcon));
			exportButton.setIcon(new ImageIcon(exportIcon));
			clearButton.setIcon(new ImageIcon(clearIcon));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		canvasPane.add(importButton);
		canvasPane.add(exportButton);
		canvasPane.add(clearButton);
		return canvasPane;
	}
	
	/**
	 * Create a render button
	 * @return the panel containing the button
	 */
	private JPanel createRenderPane() {
		JPanel renderPane = new JPanel();
		
		JButton renderButton = new JButton("Render");
		renderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rc.updateRender(sc.getLogger(), sc.getDisplay());
			}
		});
		
		try {
			Image renderIcon = ImageIO.read(getClass().getResource("/icons/arrow.png"));
			renderButton.setIcon(new ImageIcon(renderIcon));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		renderPane.setLayout(new FlowLayout(FlowLayout.CENTER, 1, renderPane.getPreferredSize().height));
		
		renderPane.add(renderButton);
		return renderPane;
	}
	
	/**
	 * Create canvas tools allowing the user to switch between pen and eraser
	 * canvas
	 * 
	 * @return a panel containing the tool buttons
	 */
	private JPanel createToolPane() {
		JPanel toolPane = new JPanel();
		toolPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
		JPanel p = new JPanel(); // pad
		ButtonGroup tools = new ButtonGroup();
		ToolListener listener = new ToolListener();

		JRadioButton penTool = new JRadioButton("", true);
		penTool.setBackground(Color.LIGHT_GRAY);
		penTool.setActionCommand("pen");
		penTool.addActionListener(listener);

		JRadioButton eraseTool = new JRadioButton();
		eraseTool.setActionCommand("eraser");
		eraseTool.addActionListener(listener);

		try {
			Image penIcon = ImageIO.read(getClass().getResource("/icons/pencil.png"));
			Image eraserIcon = ImageIO.read(getClass().getResource("/icons/eraser.png"));
			penTool.setIcon(new ImageIcon(penIcon));
			eraseTool.setIcon(new ImageIcon(eraserIcon));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		tools.add(penTool);
		tools.add(eraseTool);
		p.add(penTool);
		p.add(eraseTool);
		toolPane.add(p);
		return toolPane;
	}
	
	/**
	 * Create settings for the brush tip
	 * 
	 * @return the panel containing the tip buttons
	 */
	private JPanel createTipPane() {
		JPanel tipPane = new JPanel();
		tipPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Brush Tip"));
		JPanel p = new JPanel(); // pad
		ButtonGroup tips = new ButtonGroup();
		BrushTipListener listener = new BrushTipListener();

		JRadioButton circleTip = new JRadioButton("", true);
		circleTip.setBackground(Color.LIGHT_GRAY);
		circleTip.setActionCommand("CIRCLE");
		circleTip.addActionListener(listener);

		JRadioButton squareTip = new JRadioButton();
		squareTip.setActionCommand("SQUARE");
		squareTip.addActionListener(listener);

		JRadioButton triangleTip = new JRadioButton();
		triangleTip.setActionCommand("TRIANGLE");
		triangleTip.addActionListener(listener);

		try {
			Image circleIcon = ImageIO.read(getClass().getResource("/icons/circle.png"));
			Image squareIcon = ImageIO.read(getClass().getResource("/icons/square.png"));
			Image triangleIcon = ImageIO.read(getClass().getResource("/icons/triangle.png"));
			circleTip.setIcon(new ImageIcon(circleIcon));
			squareTip.setIcon(new ImageIcon(squareIcon));
			triangleTip.setIcon(new ImageIcon(triangleIcon));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		tips.add(circleTip);
		tips.add(squareTip);
		tips.add(triangleTip);
		p.add(circleTip);
		p.add(squareTip);
		p.add(triangleTip);
		tipPane.add(p);
		return tipPane;
	}
	
	/**
	 * Create a slider for the brush size
	 * 
	 * @return the panel containing the slider
	 */
	private JPanel createSizePane() {
		JPanel sizePanel = new JPanel();
		sizePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Brush Size"));

		JSlider brushSize = new JSlider(0, 100, 50);
		brushSize.setPaintTicks(true);
		brushSize.setPaintLabels(true);
		brushSize.setMajorTickSpacing(50);
		brushSize.setMinorTickSpacing(5);
		brushSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = brushSize.getValue();
				sc.setBrushSize(value);
				//System.out.println(value);
			}
		});

		sizePanel.add(brushSize);
		return sizePanel;
	}
	
	/**
	 * Create different brushes for each environment type
	 * @return a panel containing the environment types
	 */
	private JPanel createEnvironmentsPane() {
		JPanel environPane = new JPanel();
		environPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Environments"));
		ButtonGroup environments = new ButtonGroup();
		EnvironmentsListener listener = new EnvironmentsListener();
		int buttonSize = 50;
		
		ArrayList<JRadioButton> envButtons = new ArrayList<JRadioButton>();
		for (int i = 0; i < sc.getEnvironmentLabels().length; i++) { // for each environment
			JPanel p = new JPanel(new GridBagLayout());
			p.setPreferredSize(new Dimension(buttonSize, buttonSize));
			p.setBackground(sc.getEnvironmentColours()[i]);
			p.setBorder(unselected);
			
			JRadioButton b = new JRadioButton(sc.getEnvironmentLabels()[i]);
			b.setActionCommand(sc.getEnvironmentLabels()[i]);
			b.addActionListener(listener);
			b.setOpaque(false);
			b.setFocusPainted(false);
			
			// hide jRadioButton circle
			Image icon = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
			b.setIcon(new ImageIcon(icon));
			
			// select first button
			if(i == 0) {
				b.setSelected(true);
				p.setBorder(selected);
			}
			
			p.add(b);
			envButtons.add(b);
			environments.add(b); // add to buttongroup
			environPane.add(p); // add to pane
		}
		
		return environPane;
	}
	
	/**
	 * ActionListener for tool settings
	 */
	class ToolListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateButtonBG(e); // update button background

			// set tool
			String tool = e.getActionCommand();
			switch (tool) {
			case "pen":
				sc.setBrushTool("PEN");
				break;
			case "eraser":
				sc.setBrushTool("ERASER");
				break;
			default:
				System.out.println("ERROR: Invalid tool!");
			}
			System.out.println("[tool: " + tool + "]");
		}
	}
	
	/**
	 * ActionListener for brush tip settings
	 */
	class BrushTipListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			updateButtonBG(e); // update button background

			// set brush tip
			String tip = e.getActionCommand();
			sc.setBrushTip(tip);
			System.out.println("[brush_tip: " + tip + "]");
		}
	}
	
	/**
	 * ActionListener for environment type settings
	 */
	class EnvironmentsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JRadioButton source = (JRadioButton) e.getSource(); // get the button the event was called on
			ButtonGroup group = ((DefaultButtonModel) source.getModel()).getGroup(); // get rest of buttongroup
			Enumeration elements = group.getElements();
			while (elements.hasMoreElements()) { // iterate through each button in buttongroup
				AbstractButton b = (AbstractButton) elements.nextElement();
				JPanel p = (JPanel) b.getParent(); // get the button's container
				if (b.isSelected()) {
					p.setBorder(selected);
				} else {
					p.setBorder(unselected);
				}
			}

			String env = e.getActionCommand();
			sc.setCurrentEnvironment(env);
			System.out.println("[environment: " + env + "]");
		}
	}
	
	/**
	 * Updates the background colour of a JRadioButton's button group
	 * 
	 * @param e: the action event called on a JRadioButton
	 */
	public void updateButtonBG(ActionEvent e) {
		JRadioButton source = (JRadioButton) e.getSource(); // get the button the event was called on
		ButtonGroup group = ((DefaultButtonModel) source.getModel()).getGroup(); // get rest of buttongroup
		Enumeration elements = group.getElements();
		while (elements.hasMoreElements()) { // iterate through each button in buttongroup
			AbstractButton b = (AbstractButton) elements.nextElement();
			if (b.isSelected()) {
				b.setBackground(Color.LIGHT_GRAY);
			} else {
				b.setBackground(defaultColour);
			}
		}
	}


}
