

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

public class NoiseViewer {
	
	public static void main(String[] args) {
		
		int width = 500;
		int height = 400;
		double feature_size = 100;
		
		JFrame frame = new JFrame("NoiseViewer");

		long seed = System.currentTimeMillis();
		OpenSimplexNoise simplex = new OpenSimplexNoise(seed);
		OpenSimplex2F simplex2 = new OpenSimplex2F(seed);
		double[][] noise = new double[width][height];
		int octaves = 3;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				/*double[] n = new double[octaves];
				int amp = 1;
				int freq = 1;
				int maxVal = 0;
				for(int o = 0; o < octaves; o++) { // persistence = 0.5
					n[o] = amp * simplex.eval(x/(FEATURE_SIZE/freq), y/(FEATURE_SIZE/freq), 0.0);
					maxVal += amp;
					freq *= 2;
					amp *= 0.5;
				}
				double sum = Arrays.stream(n).sum();
				noise[y][x] = sum / maxVal;
				*/
				
				double n1 = 1 * simplex.eval(x/(feature_size/1), y/(feature_size/1), 0.0);
				double n2 = 0.5 * simplex.eval(x/(feature_size/2), y/(feature_size/2), 0.0);
				double n3 = 0.25 * simplex.eval(x/(feature_size/4), y/(feature_size/4), 0.0);
				double n = n1 + n2 + n3;
				noise[x][y] = n / (1 + 0.5 + 0.25);
				
				//noise[y][x] = simplex2.noise2(x, y);
			}
		}
		
		
		// draw image
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	        	int rgb = 0x010101 * (int)((noise[x][y] + 1) * 127.5);
	            img.setRGB(x, y, rgb);
	        }
	    }
		
		frame.getContentPane().add(new JLabel(new ImageIcon(img)));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	static public double octaveNoise(double x, double y, double z, int octaves, double persistence) {
	    double total = 0;
	    double frequency = 1;
	    double amplitude = 1;
	    double maxValue = 0;  // Used for normalizing result to 0.0 - 1.0
	    for(int i=0;i<octaves;i++) {
	        total += ImprovedNoise.noise(x * frequency, y * frequency, z * frequency) * amplitude;
	        
	        maxValue += amplitude;
	        
	        amplitude *= persistence;
	        frequency *= 2;
	    }
	    
	    return total/maxValue;
	}
	
	static public double[] merge(double[] ...arrays ) {
	    int size = 0;
	    for ( double[] a: arrays )
	        size += a.length;

	    	double[] res = new double[size];

	        int destPos = 0;
	        for ( int i = 0; i < arrays.length; i++ ) {
	            if ( i > 0 ) destPos += arrays[i-1].length;
	            int length = arrays[i].length;
	            System.arraycopy(arrays[i], 0, res, destPos, length);
	        }

	        return res;
	}

}
