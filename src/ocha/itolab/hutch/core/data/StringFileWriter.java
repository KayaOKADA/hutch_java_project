package ocha.itolab.hutch.core.data;

import java.io.*;

import ocha.itolab.hutch.applet.pathviewer.BlockDrawer;

public class StringFileWriter {
	static BufferedWriter writer;

	/**
	 * ファイルを出力する
	 */
	public static void write(DataSet ds, String path) {
		open(path + "/string.csv");
		
		// for each trajectory
		for(int i = 0; i < ds.getNumLineString(); i++) {
			OneLineString ols = ds.getOneLineString(i);
			String line = "";
			int hour =  ols.getOriginalHour(0);
            
            line += hour;
            line += ",";
            
			// for each step of the trajectory
			for(int j = 0; j < ols.getNumOriginalPosition(); j++) {
				int bid = ols.getBlockId(j);
				if(bid < 0 || bid >= letters.length) continue;
				line += letters[bid];
			}
			println(line);
		}
		
		close();
	}
	
	
	
	
	
	/**
	 * ファイルを開く
	 */
	static BufferedWriter open(String filename) {	
		try {
			 writer = new BufferedWriter(
			    		new FileWriter(new File(filename)));
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
		return writer;
	}
	
	/**
	 * ファイルを閉じる
	 */
	static void close() {
		
		try {
			writer.close();
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
	}
	

	/**
	 * 改行つきで出力する
	 */
	static void println(String word) {
		try {
			writer.write(word, 0, word.length());
			writer.flush();
			writer.newLine();
		} catch (Exception e) {
			System.err.println(e);
			return;
		}
	}	
	

	
	public static String letters[] = {
		"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
		"n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
		"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
		"N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
		"!", "#", "$", "%", "&", "'", "(", ")", "=", "-", "^", "~", "|",
		"@", "[", "]", "{", "}", ";", ":", ".", "/", "?", "_", 
	};
	
	
}
