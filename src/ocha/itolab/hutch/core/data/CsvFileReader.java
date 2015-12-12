package ocha.itolab.hutch.core.data;

import java.io.*;
import java.util.*;
import java.time.*;

public class CsvFileReader {
	static BufferedReader reader = null;
	static double TOO_LARGE = 1000.0;
	
	public static DataSet generateDataSet(String filepath) {
		DataSet ds = new DataSet();

		// open file
		open(filepath);
		
		// repeat line-by-line
		String ppid = "";
		OneLineString ols = null;
		boolean isValid = true;
		try {
			while(true) {
				
				// Read one line
				String line = reader.readLine();
				if(line == null) break;
				StringTokenizer token = new StringTokenizer(line, ",");
				String cid = token.nextToken();
				String pid = token.nextToken();
				String time = token.nextToken();
				double x = Double.parseDouble(token.nextToken());
				double y = Double.parseDouble(token.nextToken());
				y *= -1.0;
				if(x > TOO_LARGE || x < -TOO_LARGE || y > TOO_LARGE || y < -TOO_LARGE)
					isValid = false;

				// Allocate a new string
				if(ppid.compareTo(pid) != 0) {
					
					// Delete the invalid string
					if(isValid == false) 
						ds.linestrings.remove(ols);
					
					// Allocate the new one
					ols = ds.addOneLineString();
					isValid = true;
					if(ds.getNumLineString() % 10000 == 0)
						System.out.println("   ... reading CSV file " + ds.getNumLineString());
				}
				
				// Set position to the string
				ols.addOneOriginalPosition(x, y, Long.parseLong(time));
				ppid = pid;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		// close and return
		System.out.println("   ... complete to read CSV file");
		close();
		ds.postprocess();
		return ds;
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
