package ocha.itolab.hutch.core.tool;

import ocha.itolab.hutch.core.data.*;
import java.io.*;

import com.sun.beans.editors.IntegerEditor;
import com.sun.scenario.effect.impl.prism.ps.PPSBlend_ADDPeer;

public class ClusteringInvoker {
	public static int KMEANS_CLUSTERING = 1;
	public static int SPECTRAL_CLUSTERING = 2;
	
	String pythonpath = "C:/tools/Anaconda3/python.exe";
	String pycodedir = "C:/itot/projects/InfoVis/Hutch/Hutch/Python/";
	
	double data[][];
	
	
	/**
	 * Invoke a clustering process
	 */
	public void clustering(DataSet ds, int numc, double alpha, int method) {
		String pyname = "clusterng.Kmeans.py ";
		if(method == SPECTRAL_CLUSTERING)
			pyname = "clustering.Spectral.py ";
		
		// Setup a matrix and write to a temporary file
		preprocess(ds, numc, alpha);
		
		// Invoke the clustering
		try {
			/*
			ProcessBuilder pb = new ProcessBuilder(
					pythonpath, (pycodedir + "clustering.py")
			);
			Process p = pb.start();
			*/
			String command = pythonpath + " " + pycodedir + pyname + pycodedir;
			System.out.println(command);
			Runtime runtime = Runtime.getRuntime();
			Process p = runtime.exec(command);
			int ret = p.waitFor();
			p.destroy();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// Set labels to the linestrings
		postprocess(ds);
		
	}
	
	
	
	/**
	 * Setup the matrix for input of clustering process
	 */
	void preprocess(DataSet ds, int numc, double alpha) {
	
		int numpos = ds.getOneLineString(0).getNumDividedPosition();
		int numline = ds.getNumLineString();
		int numdim = numpos * 4 - 2;
		
		// allocate the matrix
		data = new double[numline][];
		for(int i = 0; i < numline; i++)
			data[i] = new double[numdim];
		
		// for each linestring
		for (int i = 0; i < numline; i++) {
			OneLineString ols = ds.getOneLineString(i);
			
			// for each position
			for(int j = 0; j < numpos; j++) {
				double x = ols.getDividedX(j);
				double y = ols.getDividedY(j);
				double v1 = x * alpha;
				double v2 = y * alpha;
				data[i][j] = v1;
				data[i][j + numpos] = v2;
						
				if(j < numpos - 1) {
					double x2 = ols.getDividedX(j + 1);
					double y2 = ols.getDividedY(j + 1);
					double v3 = (x2 - x) * (1.0 - alpha);
					double v4 = (y2 - y) * (1.0 - alpha);
					data[i][j + numpos * 2] = v3;
					data[i][j + numpos * 3 - 1] = v4;
				}
			}
		}
		
		// write to a temporary file
		try {
			
			// Open the file
			File file = new File(pycodedir + "clusteringInput.csv");
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			
			// Write the number of clusters
			String word	= Integer.toString(numc) + ","
			     + Integer.toString(numline) + "," + Integer.toString(numdim);
			writer.write(word, 0, word.length());
			writer.flush(); writer.newLine();
			
			// Write the matrix value
			for(int i = 0; i < data.length; i++) {
				word = Double.toString(data[i][0]);
				for(int j = 1; j < data[i].length; j++)
					word += ("," + Double.toString(data[i][j]));
				writer.write(word, 0, word.length());
				writer.flush(); writer.newLine();
			}
			
			// Close the file
			writer.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Set labels to linestrings
	 */
	void postprocess(DataSet ds) {
		
		// read from a temporary file
		try {
			
			// Open the file
			File file = new File(pycodedir + "clusteringOutput.csv");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			reader.ready();
			
			// Repeat for each linestring
			int id = 0;
			while(true) {
				String line = reader.readLine();
				if(line == null) break;
				int cid = Integer.parseInt(line);
				OneLineString ols = ds.getOneLineString(id++);
				ols.setClusterId(cid);
			}
			
			// Close the file
			reader.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
		
}
