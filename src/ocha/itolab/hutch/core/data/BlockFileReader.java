package ocha.itolab.hutch.core.data;

import java.io.*;
import java.util.*;

public class BlockFileReader {
	static BufferedReader reader;
	
	public static BlockSet readBlockFile(DataSet dataset, String filepath) {
		DataSet ds = dataset;
		BlockSet block = new BlockSet(ds);
		
		// open file
		open(filepath);
		
		// repeat for each line 
		int counter = 0;
		double pos[][] = null;
		try {
			while(true) {
				String line = reader.readLine();
				if(line == null) break;
				line = line.replace("[", " ");
				line = line.replace("]", " ");
				StringTokenizer token = new StringTokenizer(line);
				if(token == null || token.countTokens() < 2) continue;
				if(counter == 0) {
					pos = new double[4][];
					for(int i = 0; i < 4; i++)
						pos[i] = new double[2];
				}
				pos[counter][0] = Double.parseDouble(token.nextToken());
				pos[counter][1] = Double.parseDouble(token.nextToken());
				pos[counter][1] *= -1.0;
				counter++;
				if(counter >= 4) {
					block.addOneBlock(pos);
					counter = 0;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	
		close();
		block.aggregate();
		return block;
	}
	
	
	/**
	 * Open file
	 * @param filename
	 */
	static void open(String filename) {
		
		try {
			File file = new File(filename);
			reader = new BufferedReader(new FileReader(file));
			reader.ready();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * Close file
	 */
	static void close() {
		try {
			reader.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
