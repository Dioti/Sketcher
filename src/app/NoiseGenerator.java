package app;

import java.awt.image.BufferedImage;

public class NoiseGenerator {
	
	public static double[] generateNoise(int width, double feature_size) {
		long seed = System.currentTimeMillis();
		System.out.println("seed=" + seed);
		OpenSimplexNoise simplex = new OpenSimplexNoise(seed);
		double[] noise = new double[width];
		
		for (int i = 0; i < width; i++) {
			double n1 = 1 * simplex.eval(i/(feature_size/1), 0.0, 0.0);
			double n2 = 0.5 * simplex.eval(i/(feature_size/2), 0.0, 0.0);
			double n3 = 0.25 * simplex.eval(i/(feature_size/4), 0.0, 0.0);
			double n = n1 + n2 + n3;
			noise[i] = n / (1 + 0.5 + 0.25);
			//noise[i] = simplex.eval(i/feature_size, 0.0, 0.0);
		}
		
		return noise;
	}
	
	public static double[][] generateNoise(int width, int height, double feature_size) {
		long seed = System.currentTimeMillis();
		System.out.println("seed=" + seed);
		OpenSimplexNoise simplex = new OpenSimplexNoise(seed);
		double[][] noise = new double[width][height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double n1 = 1 * simplex.eval(x/(feature_size/1), y/(feature_size/1), 0.0);
				double n2 = 0.5 * simplex.eval(x/(feature_size/2), y/(feature_size/2), 0.0);
				double n3 = 0.25 * simplex.eval(x/(feature_size/4), y/(feature_size/4), 0.0);
				double n = n1 + n2 + n3;
				noise[x][y] = n / (1 + 0.5 + 0.25);
			}
		}
		
		return noise;
	}

}
