package app;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GeometryLogger {
	
	ArrayList<int[]> trees;
	
	public GeometryLogger() {
		this.trees = new ArrayList<int[]>();
		// ...
	}
	
	public void updateTrees(int x1, int y1, int x2, int y2, int thickness) {
		trees.add(new int[] {x1, y1, x2, y2, thickness});
		//System.out.println("Added TREE: point1=(" + x1 + "," + y1 + "), point2=(" + x2 + "," + y2 + "), thickness=" + thickness);
	}
	
	public ArrayList<int[]> getTrees() {
		return this.trees;
	}
	
	public void clear() {
		// clear the log of all environments
		trees.clear();
		// ...
	}
	
	public void exportGeom(File f) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(f));
		    for (int i = 0; i < trees.size(); i++) {
		    	String str = "";
		    	for (int j = 0; j < trees.get(i).length; j++) {
		    		str += " " + trees.get(i)[j];
		    	}
		    	bw.write("TREE" + str);
		    	bw.newLine();
		    }
		    bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void importGeom(File f) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(f));
			clear(); // clear current geometry data
			String line;
			while ((line = br.readLine()) != null) {
				// parse lines
				String[] data = line.split(" ");
				switch(data[0]) {
				case "TREE":
					updateTrees(Integer.parseInt(data[1]), Integer.parseInt(data[2]), 
							Integer.parseInt(data[3]), Integer.parseInt(data[4]), 
							Integer.parseInt(data[5]));
					break;
				default:
					System.out.println("Environment[" + data[0] + "] not recognised.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	

}
