package app;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class RenderCanvas extends DisplayCanvas {
	
	public static String[] paletteLabels = {"WARM", "COLD", "NEUTRAL"};
	public static String[] styleLabels = {"Style 1", "Style 2"};
	
	private int chaos;
	private String currentPalette;
	private String currentStyle;
	
	private GeometryLogger logger;
	private Image img;
	private Mat src;
	
	private Mat sky;
	
	public RenderCanvas() {
		this.chaos = 5;
		this.currentPalette = "NEUTRAL";
		this.currentStyle = "Style 1";
		
		// load OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public void updateRender(GeometryLogger logger, Image sketch) {
		// fetch logger and src image
		this.logger = logger;
		this.img = sketch;
		src = imageToMat(img);
		
		clear();
		
		generateSkies();
		generateSuns();
		//generateMounts();
		generateDirt();
		generateGrass();
		generateTrees();
		generateGrass();
		generateFlowers();
		
		repaint();
	}
	
	public void setChaos(int c) {
		this.chaos = c;
	}
	
	public int getChaos() {
		return chaos;
	}
	
	public void setPalette(String str) {
		this.currentPalette = str;
	}
	
	public String getPalette() {
		return currentPalette;
	}
	
	public void getStyle(String str) {
		this.currentStyle = str;
	}
	
	public String setStyle(String str) {
		return currentStyle;
	}
	
	/*public void generateMounts() {
		Point start = new Point(0, 200);
		Point end = new Point(400, 200);
		
		
		int width = (int) Math.ceil(Math.hypot(start.x-end.x, start.y-end.y)); // width of the mountain range
		int height = 100; // max height of peaks in current range
		int density = 10; // density of peaks
		double[] peaks = NoiseGenerator.generateNoise(width,density);
		
		int[] x = new int[width];
		int[] y = new int[width];
		for(int i = 0; i < width; i++) {
			//int p = (int)((peaks[i] + 1) * 127.5);
			int p = (int)((peaks[i] + 1) * (height));
			System.out.println(p);
			x[i] = i;
			y[i] = p;
		}
		//x[width] = x[0];
		//y[width] = y[0];
		
		Polygon m = new Polygon(x, y, width);
		
		g2.setColor(Color.BLACK);
		g2.draw(m);
	}*/
	
	public void generateFlowers() {
		// filter for flowers
		Mat m = filter(src, "Flowers");
		
		// get contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // get convex hull
        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        
        // get flowers regions
        ArrayList<Polygon> regions = new ArrayList<Polygon>();
        for(int i = 0; i < hullList.size(); i++) { // for each detected contour (region)
        	// convert the contour into a list of points
        	List<Point> list = new ArrayList<>();
        	Converters.Mat_to_vector_Point(hullList.get(i), list);
        	
        	// convert list of points into polygon
        	Polygon region = new Polygon();
            for(int j = 0; j < list.size(); j++) {
            	//System.out.println(list.get(j).toString());
            	int x = (int) Math.floor(list.get(j).x);
            	int y = (int) Math.floor(list.get(j).y);
            	region.addPoint(x, y);
            }
            
            // add polygon into regions list
            regions.add(region);
            
            // calculate density of flowers
            double area = Imgproc.contourArea(hullList.get(i));
            System.out.println(area);
            int density_mod = 5;
            int numOfPoints = (int) Math.round((area / 2000) * density_mod);
            
            // get random points (brute force)
        	int x1 = region.getBounds().x;
        	int x2 = region.getBounds().x + region.getBounds().width;
        	int y1 = region.getBounds().y;
        	int y2 = region.getBounds().y + region.getBounds().height;
        	int count = 0;
        	while(count < numOfPoints) {
        		int x = (int) ((Math.random() * (x2 - x1)) + x1);
        		int y = (int) ((Math.random() * (y2 - y1)) + y1);
        		if(region.contains(x, y)) { // if point is within region
        			drawFlower(x, y);
        			count++;
        		}
        	}
        }
	}
	
	public void drawFlower(int x, int y) {
		int size = (int) ((Math.random() * (20 - 5)) + 5);
		
		// get random petal colour
		int[] rgb = new int[3];
		for(int i = 0; i < 3; i++) {
			rgb[i] = (int) ((Math.random() * (255 - 50)) + 50);
		}
		
		// draw petals
		int pSize = (int) Math.round(size * 0.5); // size of flower petal
		g2.setPaint(new Color(rgb[0], rgb[1], rgb[2])); // set to petal colour
		g2.fillOval(x, y, pSize, pSize);
		g2.fillOval(x-(pSize), y, pSize, pSize);
		g2.fillOval(x, y-(pSize), pSize, pSize);
		g2.fillOval(x-(pSize), y-(pSize), pSize, pSize);
		
		// draw petal pattern
		int pRad = (int) Math.round(pSize * 0.8); // petal radius
		int[] px = {x-pRad, x, x+pRad, x+(pRad/2), x+pRad, x, x-pRad, x-(pRad/2), x-pRad};
		int[] py = {y-pRad, y-(pRad/2), y-pRad, y, y+pRad, y+(pRad/2), y+pRad, y, y-pRad};
		Polygon p = new Polygon(px, py, 9);
		g2.setPaint(new Color(255,255,255,175));
		g2.fillPolygon(p);
		
		// draw flower center
		int cSize = (int) Math.round(size * 0.5); // size of flower center
		g2.setPaint(new Color(255, 220, 46));
		g2.fillOval(x-cSize/2, y-cSize/2, cSize, cSize);
	}
	
	public void generateGrass() {
		// filter for grass
		Mat m = filter(src, "Grass");
		
		// get contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // get convex hull
        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        
        // get grass regions
        ArrayList<Polygon> regions = new ArrayList<Polygon>();
        for(int i = 0; i < hullList.size(); i++) { // for each detected contour (region)
        	// convert the contour into a list of points
        	List<Point> list = new ArrayList<>();
        	Converters.Mat_to_vector_Point(hullList.get(i), list);
        	
        	// convert list of points into polygon
        	Polygon region = new Polygon();
            for(int j = 0; j < list.size(); j++) {
            	//System.out.println(list.get(j).toString());
            	int x = (int) Math.floor(list.get(j).x);
            	int y = (int) Math.floor(list.get(j).y);
            	region.addPoint(x, y);
            }
            
            // add polygon into regions list
            regions.add(region);
            
            // calculate density of grass
            double area = Imgproc.contourArea(hullList.get(i));
            int density_mod = 5;
            int numOfPoints = (int) Math.round((area / 100) * density_mod);
            
            // get random points (brute force)
        	int x1 = region.getBounds().x;
        	int x2 = region.getBounds().x + region.getBounds().width;
        	int y1 = region.getBounds().y;
        	int y2 = region.getBounds().y + region.getBounds().height;
        	int count = 0;
        	while(count < numOfPoints) {
        		int x = (int) ((Math.random() * (x2 - x1)) + x1);
        		int y = (int) ((Math.random() * (y2 - y1)) + y1);
        		if(region.contains(x, y)) { // if point is within region
        			drawGrass(x, y);
        			count++;
        		}
        	}
        }
	}
	
	public void drawGrass(int x, int y) {
		int width = (int) ((Math.random() * (10 - 1)) + 1);
		int height = (int) ((Math.random() * (30 - 5)) + 5);
		int[] defCol = {61, 125, 34}; // default grass colour
		int[] newCol = new int[3];
		for(int i = 0; i < 3; i++) {
			newCol[i] = (int) Math.round(defCol[i] + (Math.random() * (20 - (-20))) + (-20));
			if(newCol[i] > 255) {
				newCol[i] = 255;
			} else if(newCol[i] < 0) {
				newCol[i] = 0;
			}
		}
		int a = (int) ((Math.random() * (255 - 100)) + 100);
		
		Color c = new Color(newCol[0], newCol[1], newCol[2], a);
		g2.setColor(c);
		int[] tx = {x-width/2, x, x+width/2};
		int[] ty = {y+height/2, y-height/2, y+height/2}; 
		g2.fillPolygon(tx, ty, 3);
	}
	
	public void generateDirt() {
		// filter for dirt
		Mat m = filter(src, "Dirt");
		
		// get contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        
        // get convex hull
        List<MatOfPoint> hullList = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            MatOfInt hull = new MatOfInt();
            Imgproc.convexHull(contour, hull);
            Point[] contourArray = contour.toArray();
            Point[] hullPoints = new Point[hull.rows()];
            List<Integer> hullContourIdxList = hull.toList();
            for (int i = 0; i < hullContourIdxList.size(); i++) {
                hullPoints[i] = contourArray[hullContourIdxList.get(i)];
            }
            hullList.add(new MatOfPoint(hullPoints));
        }
        
        // get dirt regions
        ArrayList<Polygon> regions = new ArrayList<Polygon>();
        for(int i = 0; i < hullList.size(); i++) { // for each detected contour (region)
        	// convert the contour into a list of points
        	List<Point> list = new ArrayList<>();
        	Converters.Mat_to_vector_Point(hullList.get(i), list);
        	
        	// convert list of points into polygon
        	Polygon region = new Polygon();
            for(int j = 0; j < list.size(); j++) {
            	//System.out.println(list.get(j).toString());
            	int x = (int) Math.ceil(list.get(j).x);
            	int y = (int) Math.ceil(list.get(j).y);
            	region.addPoint(x, y);
            }
            
            // add polygon into regions list
            regions.add(region);
        }
        
        // draw each dirt region
        for(int i = 0; i < regions.size(); i++) {
        	drawDirt(regions.get(i));
        }
	}
	
	public void drawDirt(Polygon region) {
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("Dirt_01_100.png"));
		} catch (IOException e) {
		}
		TexturePaint tp = new TexturePaint(img, new Rectangle(0, 0, 100, 100));
		g2.setPaint(tp);
		g2.fill(region);
	}
	
	public void generateSkies() {
		// filter for sky
		Mat m = filter(src, "Sky");
		
		// get contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        
        if(contours.size() != 0) {
        
        // get the y value of sky regions (horizon)
        /*MatOfPoint2f[] contoursPoly  = new MatOfPoint2f[contours.size()];
        int[] horizons = new int[contours.size()];
        for (int i = 0; i < contours.size(); i++) {
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 3, true);
            Rect r = Imgproc.boundingRect(new MatOfPoint(contoursPoly[i].toArray()));
            horizons[i] = (int) Math.round(r.br().y);
            System.out.println("y=" + horizons[i]);
        }
        
        // get max y value
        int lowestHorizon = Arrays.stream(horizons).max().getAsInt();*/
        int lowestHorizon = HEIGHT;
		
        // generate texture
		double[][] noise = NoiseGenerator.generateNoise(WIDTH, lowestHorizon, 48);
		BufferedImage img = new BufferedImage(WIDTH, lowestHorizon, BufferedImage.TYPE_BYTE_GRAY);
	    for (int x = 0; x < WIDTH; x++) {
	        for (int y = 0; y < lowestHorizon; y++) {
	        	int rgb = 0x010101 * (int)((noise[x][y] + 1) * 127.5); // black + noise value
	            img.setRGB(x, y, rgb);
	        }
	    }
        
	    // apply colour model
	    
	    Color c = null;
	    switch(currentPalette) {
		    case "WARM":
		    	c = new Color(245, 167, 66);
		    	break;
		    case "NEUTRAL":
		    	c = new Color(173, 224, 223);
		    	break;
		    case "COLD":
		    	c = new Color(95, 108, 156);
		    	break;
		    default:
		    	System.out.println("Colour palette: " + currentPalette + " not recognised!");
	    }
	    BufferedImage mapped = createGradientImage(img, Color.white, c);
	    
	    // draw sky
	    Graphics g = display.getGraphics();
    	g.drawImage(mapped, 0, 0, null);
    	g.dispose();
    	
        /*BufferedImage originalImage = null;
        BufferedImage imageWithGradient = null;
		try {
			originalImage = convertToARGB(ImageIO.read(new File("Dirt_01_100.png")));
			imageWithGradient = convertToARGB(originalImage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
		Graphics2D g = imageWithGradient.createGraphics();
        g2.drawImage(originalImage, 0, 0, null);

        Point2D center = new Point2D.Float(50, 50);
        int radius = 100;
        float fractions[] = { 0.0f, 1.0f };
        Color colors[] = { new Color(0,0,0,255), new Color(0,0,0,0) };
        RadialGradientPaint paint = 
            new RadialGradientPaint(center, radius, fractions, colors);
        g.setPaint(paint);

        g.setComposite(AlphaComposite.DstOut);
        g.fillOval(50 - radius, 50 - radius, radius * 2, radius * 2);
        g.dispose();
    	*/
        }
	}
	
    private static BufferedImage convertToARGB(BufferedImage image)
    {
        BufferedImage newImage =
            new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
	
	public BufferedImage createGradientImage(BufferedImage bi, Color ...colors) {
        byte[] r = new byte[256];
        byte[] g = new byte[256];
        byte[] b = new byte[256];
        
        final int fade = (256 / (colors.length - 1));
        
        // (generate all 256 RGB values by
        // fading between the colors supplied)
        for (int i = 0; i < 256; ++i) {
            Color c0 = colors[(i / fade)];
            Color c1 = colors[(i / fade) + 1];

            float amt = (i % fade) / ((float) fade);

            r[i] = getChannel(amt, c0.getRed(),   c1.getRed());
            g[i] = getChannel(amt, c0.getGreen(), c1.getGreen());
            b[i] = getChannel(amt, c0.getBlue(),  c1.getBlue());
        }
        
		return new BufferedImage(
	            new IndexColorModel(8, 256, r, g, b),
	            bi.getRaster(), false, null);
	}
	
	public byte getChannel(float amt, int ch0, int ch1) {
        return (byte)
            ((ch0 * (1 - amt)) + (ch1 * amt));
    }
	
	public void generateSuns() {
		// filter for sun
		Mat m = filter(src, "Sun");
		
		// get canny output
		Mat canny = new Mat();
		int threshold = 100;
		Imgproc.Canny(m, canny, threshold, threshold * 2);
		
		// get list of contours
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		// get sun geometry data
        //ArrayList<double[]> suns = new ArrayList<double[]>();
        ArrayList<int[]> suns = new ArrayList<int[]>();
		
		for (int i = 0; i < contours.size(); i++) {
			// find approximation of suns to +/-3 accuracy
            MatOfPoint2f contourPoly = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contourPoly, 3, true);
            
            // find bounding box of suns
            Rect boundRect = Imgproc.boundingRect(new MatOfPoint(contourPoly.toArray()));
            
            // get geometric data
            Point center = new Point();
            float[] radius = new float[1];
            Imgproc.minEnclosingCircle(contourPoly, center, radius);
            //suns.add(new double[] {center.x, center.y, radius[0]});
            int[] sun = {(int) Math.round(center.x), (int) Math.round(center.y), (int) Math.round(radius[0] * 2)};
            suns.add(sun);
		}
            
		for (int[] sun: suns) {
			drawSun(sun[0], sun[1], sun[2]);
		}
	}
	
	public void drawSun(int centerX, int centerY, int diameter) {
		int rings = (int) Math.ceil(Math.random() * (chaos * 0.5));
		
		// calculate ring size
		double[] size = new double[rings+1];
		size[0] = diameter;
		double offset = size[0] * 1/3;
		for(int i = 1; i < rings + 1; i++) {
			size[i] = offset + size[i-1];
			offset = offset * 1/2;
		}
		
		// draw circles
		int band = (int) Math.ceil(255 / (rings + 1));
		Color c = new Color(255, 249, 163);
		int alpha = 0;
		for(int i = size.length - 1; i > -1; i--) {
			//System.out.println(size[i]);
			int x = (int) Math.round(centerX - size[i]/2);
			int y = (int) Math.round(centerY - size[i]/2);
			int sz = (int) Math.round(size[i]);
			alpha += band;
			if(alpha > 255) { // if out of range
				alpha = 255; // set to max alpha
			}
			g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			g2.fillOval(x, y, sz, sz);
		}
		
	}
	
	public void generateTrees() {
		ArrayList<int[]> trees = logger.getTrees();
		for (int[] trunk: trees) {
			int d = (int) Math.round(Math.random() * (chaos * 2));
			double dist = Math.hypot(trunk[0]-trunk[2], trunk[1]-trunk[3]);
			double thickness = trunk[4];
			int sz = (int) Math.round(((dist*0.5) + (thickness*0.5)) / 10);
			//System.out.println("sz=" + sz);
			if(trunk[1] > trunk[3]) {
				drawTree(trunk[0], trunk[1], -90, sz);
			} else {
				drawTree(trunk[2], trunk[3], -90, sz);
			}
			//System.out.println(trunk[0] + " " + trunk[1] + " " + trunk[2] + " " + trunk[3]);
		}
	}
	
	public void drawTree(int x1, int y1, double angle, int depth) {
		// stop drawing
		if (depth == 0) {
            return;
		}
		
		// generate random branch length
		double length = Math.random() * (chaos) + depth/5;
		
		// calculate coordinates for end of branch
		int x2 = (int) (x1 + (Math.cos(Math.toRadians(angle)) * depth * length));
		int y2 = (int) (y1 + (Math.sin(Math.toRadians(angle)) * depth * length));
		
		// generate random angle difference for left and right branch
		double diffL = (Math.random() * (chaos * 4)) + (chaos*3);
		double diffR = (Math.random() * (chaos * 4)) + (chaos*3);
		
		// set branch colour
		int a = (int) Math.round(Math.random() * 255); // random opacity
		if(depth > 5) {
		    g2.setColor(new Color(89, 68, 46, 255)); // brown
		} else if (depth == 5) {
			g2.setColor(new Color(94, 138, 76, 240)); // green
		} else {
			int shade = -1; // dark
			if(Math.round(Math.random()) == 1) {
				shade = 2; // light
			}
			
			int r = g2.getColor().getRed() + (3 * shade);
			int g = g2.getColor().getGreen() + (2 * shade);
			int b = g2.getColor().getBlue() + (3 * shade);
			if (r > 255 || r < 0) {r = g2.getColor().getRed();}
			if (g > 255 || g < 0) {g = g2.getColor().getGreen();}
			if (b > 255 || b < 0) {b = g2.getColor().getBlue();}
			g2.setColor(new Color(r,g,b,a));
		}
		
		// set branch thickness
		double thickness = depth * 1.1;
		g2.setStroke(new BasicStroke(Math.round(thickness), 1, 1));
		
		// draw current branch and offshoots
		g2.drawLine(x1, y1, x2, y2); // tree trunk
	    drawTree(x2, y2, angle - diffL, depth - 1); // left branch
	    drawTree(x2, y2, angle + diffR, depth - 1); // right branch
	}
	
	/**
	 * Converts an Image class (Java AWT) to a Mat class (OpenCV)
	 * 
	 * @param img: the Java AWT image to be converted
	 * @return the converted Mat image
	 */
	public Mat imageToMat(Image img) {
		// Image -> BufferedImage
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		// BufferedImage -> Mat
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);

		return mat;
	}

	/**
	 * Converts a Mat class (OpenCV) to an Image class (Java AWT)
	 * 
	 * @param mat: the Mat image to be converted
	 * @return the converted Image
	 */
	public Image matToImage(Mat mat) {
		return HighGui.toBufferedImage(mat);
	}
	
	/**
	 * Filters for a given environment
	 * 
	 * @param str: the environment label
	 * @return the filtered image
	 */
	public Mat filter(Mat src, String str) {
		Mat mask = new Mat();
		Scalar c = colorToScalar(environmentColours[findIndex(str)]);
		Core.inRange(src, c, c, mask);
		return mask;
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
	
	/**
	 * Converts a Color class to a Scalar class (OpenCV)
	 * 
	 * @param c: the colour class, represented using RGB
	 * @return the scalar class, represented using BGR
	 */
	public Scalar colorToScalar(Color c) {
		return new Scalar(c.getBlue(), c.getGreen(), c.getRed());
	}
	
	
}
