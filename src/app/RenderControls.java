package app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RenderControls extends JPanel {

	RenderCanvas render;
	
	public RenderControls(RenderCanvas rc) {
		this.render = rc;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//this.setBorder(new EmptyBorder(10, 0, 0, 0));
		
		// create export button
		JPanel pane1 = new JPanel();
		pane1.setLayout(new BorderLayout());
		pane1.add(createExportPane(), BorderLayout.LINE_END);
		
		// create chaos slider
		JPanel pane2 = new JPanel();
		pane2.setLayout(new BorderLayout());
		pane2.add(createChaosPane());
		
		// create render options
		JPanel pane3 = new JPanel();
		pane3.setLayout(new BorderLayout());
		JPanel palette = createPalettePane();
		pane3.add(palette, BorderLayout.LINE_START);
		JPanel style = createStylePane();
		pane3.add(style);
		
		// add all to pane
		this.add(pane1);
		this.add(pane2);
		this.add(pane3);
	}
	
	private JPanel createExportPane() {
		JPanel exportPanel = new JPanel();
		
		JButton exportButton = new JButton("Save");
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				int result = fc.showSaveDialog(null);
				if (result == fc.APPROVE_OPTION) {
					File f = fc.getSelectedFile();
					System.out.println("Exporting: " + f.getAbsolutePath());
					render.exportImage(f);
				}
			}
		});

		try {
			Image exportIcon = ImageIO.read(getClass().getResource("/icons/floppy.png"));
			exportButton.setIcon(new ImageIcon(exportIcon));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		exportButton.setPreferredSize(new Dimension(exportButton.getPreferredSize().width, 
				exportButton.getPreferredSize().height + 5)); // pad button
		exportPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, exportPanel.getPreferredSize().height));
		
		exportPanel.add(exportButton);
		return exportPanel;
	}
	
	
	private JPanel createChaosPane() {
		JPanel chaosPanel = new JPanel();
		
		chaosPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Chaos"));
		JSlider randSlider = new JSlider(0, 10, 5);
		randSlider.addChangeListener(new ChangeListener() {
			public void stateChanged (ChangeEvent e) {
				int value = randSlider.getValue();
				render.setChaos(value);
				//System.out.println(value);
			}
		});
		randSlider.setPaintTicks(true);
		randSlider.setPaintLabels(true);
		randSlider.setMajorTickSpacing(5);
		randSlider.setMinorTickSpacing(1);
		
		chaosPanel.add(randSlider);
		return chaosPanel;
	}
	
	
	
	private JPanel createPalettePane() {
		JPanel palettePanel = new JPanel();
		palettePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Palette"));
		
		ButtonGroup palette = new ButtonGroup();
		ActionListener listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String palette = e.getActionCommand();
				render.setPalette(palette);
			}	
		};
		
		JRadioButton warm = new JRadioButton("Warm");
		JRadioButton cold = new JRadioButton("Cold");
		JRadioButton neutral = new JRadioButton("Neutral", true);
		warm.setActionCommand("WARM");
		cold.setActionCommand("COLD");
		neutral.setActionCommand("NEUTRAL");
		warm.addActionListener(listener);
		cold.addActionListener(listener);
		neutral.addActionListener(listener);
		
		palette.add(warm);
		palette.add(cold);
		palette.add(neutral);
		palettePanel.add(warm);
		palettePanel.add(cold);
		palettePanel.add(neutral);
		
		return palettePanel;
	}
	
	
	
	private JPanel createStylePane() {
		JPanel stylePanel = new JPanel();
		stylePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Style"));
		
		ButtonGroup style = new ButtonGroup();
		
		JRadioButton style1 = new JRadioButton("1", true);
		JRadioButton style2 = new JRadioButton("2");
		
		style.add(style1);
		style.add(style2);
		stylePanel.add(style1);
		stylePanel.add(style2);

		return stylePanel;
	}
	
}
