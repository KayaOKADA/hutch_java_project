
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
	 * Container ���Z�b�g����
	 * @param c Component
	 */
	public void setContainer(Component c) {
		windowContainer = c;
	}
	
	
	/**
	 * Canvas ���Z�b�g����
	 * @param c Canvas
	 */
	public void setCanvas(Canvas c) {
		canvas = c;
	}
	

	
	/**
	 * �t�@�C���_�C�A���O�ɃC�x���g���������Ƃ��ɁA�Ή�����t�@�C������肷��
	 * @return �t�@�C��
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
			return ds;
		} else if (selected == JFileChooser.CANCEL_OPTION) { // cancel selected
			return null;
		} 
		
		return null;
	}
	

}
