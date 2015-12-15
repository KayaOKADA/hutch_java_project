package ocha.itolab.hutch.datagen.dummy;

import java.io.*;

import com.sun.corba.se.impl.presentation.rmi.DynamicAccessPermission;
import com.sun.jmx.mbeanserver.DefaultMXBeanMappingFactory;

public class DummyCsvFileGenerator1 {
	static BufferedWriter writer;
	static String filename = "dummy1.csv";

	/**
	 * main
	 * @param args
	 */
	public static void main(String args[]) {
		open(filename);
		
		for(int i = 0; i < 1000; i++) {
			generateOneString(i);
		}

		close();
	}


	/**
	 * Generate one string
	 */
	static void generateOneString(int id) {
		int type = (int)(Math.random() * 4.0);
		double x = 0.0, y = 0.0, vx = 0.0, vy = 0.0, d = 0.0;
		
		if(type == 0) { // x=0
			x = 0.0;   y = Math.random() * 0.8 + 0.1;
			d = Math.random() * 0.1;
			vx = 1.0 * d;   vy = (Math.random() - 0.5) * d;
		}
		else if(type == 1) { // x=1
			x = 1.0;   y = Math.random() * 0.8 + 0.1;
			d = Math.random() * 0.1;
			vx = -1.0 * d;   vy = (Math.random() - 0.5) * d;
		}
		else if(type == 2) { // y=0
			y = 0.0;   x = Math.random() * 0.8 + 0.1;
			d = Math.random() * 0.1;
			vy = 1.0 * d;   vx = (Math.random() - 0.5) * d;
		}
		else if(type == 3) { // y=1
			y = 1.0;   x = Math.random() * 0.8 + 0.1;
			d = Math.random() * 0.1;
			vy = -1.0 * d;   vx = (Math.random() - 0.5) * d;
		}
		else return;
		
		// first position
		String line = "0," + id + ",-," + Double.toString(x) + "," + Double.toString(y);
		println(line);
		
		// for each
		for(int i = 0; i < 1000; i++) {
			x += vx;    y += vy;
			if(x < 0 || x > 1 || y < 0 || y > 1) break;
			
			line = "0," + id + ",-," + Double.toString(x) + "," + Double.toString(y);
			println(line);
			vx += (Math.random() - 0.5) * 0.1;
			vy += (Math.random() - 0.5) * 0.1;
		}
		
		
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
	

}
