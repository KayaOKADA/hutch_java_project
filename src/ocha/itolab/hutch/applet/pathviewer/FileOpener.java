
package ocha.itolab.hutch.applet.pathviewer;

import java.io.*;
import java.awt.*;
import javax.swing.*;
import ocha.itolab.hutch.core.data.*;

public class FileOpener {

	File currentDirectory, inputFile, outputFile;
	Component windowContainer;
	Canvas canvas;
	JsonFileReader jfr = null;
	
	private JTextArea textArea = new JTextArea();

	
	/**
	 * Container をセットする
	 * @param c Component
	 */
	public void setContainer(Component c) {
		windowContainer = c;
	}
	
	
	/**
	 * Canvas をセットする
	 * @param c Canvas
	 */
	public void setCanvas(Canvas c) {
		canvas = c;
	}
	

	public File getCurrentDirectory() {
		return currentDirectory;
	}
	
	
	
	/**
	 * ファイルダイアログにイベントがあったときに、対応するファイルを特定する
	 * @return ファイル
	 */
	public DataSet getDataSet() {
		JFileChooser fileChooser = new JFileChooser(currentDirectory);
		int selected = fileChooser.showOpenDialog(windowContainer);
		if (selected == JFileChooser.APPROVE_OPTION) { // open selected
			currentDirectory = fileChooser.getCurrentDirectory();
			File file = fileChooser.getSelectedFile();
			String filepath = file.getAbsolutePath();
			DataSet ds = null;
			if(filepath.endsWith("json"))
				ds = JsonFileReader.generateDataSet(filepath);
			if(filepath.endsWith("csv"))
				ds = CsvFileReader.generateDataSet(filepath);
			canvas.setCurrentDirectory(currentDirectory.getAbsolutePath());
			String blockpath = currentDirectory.getAbsolutePath() + "/blocks.txt";
			ds.block = BlockFileReader.readBlockFile(ds, blockpath);
			StringFileWriter.write(ds, currentDirectory.getAbsolutePath());
			return ds;
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		} 
		
		return null;
	}
	
	
}
