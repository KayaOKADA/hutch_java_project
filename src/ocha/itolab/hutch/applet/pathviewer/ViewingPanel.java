
package ocha.itolab.hutch.applet.pathviewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ocha.itolab.hutch.core.data.*;
import ocha.itolab.hutch.core.tool.*;

public class ViewingPanel extends JPanel  {

	public JButton  fileOpenButton, viewResetButton, clusteringButton;
	public JRadioButton pathButton, populationButton, stopnessButton, directionButton;
	public JTextField numClusterField, numDivideField, alphaField;
	public JSlider clusteringRatioSlider, intensityRatioSlider;
	public Container container;
	
	/* Selective canvas */
	Canvas canvas;
	FileOpener fileOpener;
	DataSet ds;
	
	/* Cursor Sensor */
	boolean cursorSensorFlag = false;
	
	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	SliderListener sl = null;
	
	public ViewingPanel() {
		// super class init
		super();

		setSize(200, 800);

		
		//
		// ファイル入力および視点変更操作のパネル
		// （起動したとき右に出るパネルの設定）
		//
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(11,1));
		fileOpenButton = new JButton("JSON/CSV File Open");
		viewResetButton = new JButton("View Reset");
		clusteringButton = new JButton("Clustering");
		p1.add(fileOpenButton);
		p1.add(viewResetButton);
		p1.add(clusteringButton);

		JPanel pp1 = new JPanel();
		pp1.setLayout(new GridLayout(1,2));
		numClusterField = new JTextField("3");
		pp1.add(new JLabel("Num. cluster"));
		pp1.add(numClusterField);
		p1.add(pp1);
	
		JPanel pp2 = new JPanel();
		pp2.setLayout(new GridLayout(1,2));
		numDivideField = new JTextField("10");
		pp2.add(new JLabel("Num. divide"));
		pp2.add(numDivideField);
		p1.add(pp2);
		
		JPanel pp3 = new JPanel();
		JPanel ppp3 = new JPanel();
		pp3.setLayout(new GridLayout(2,1));
		ppp3.setLayout(new GridLayout(1,2));
		ppp3.add(new JLabel("Clustering ratio"));
		alphaField = new JTextField("0.5");
		ppp3.add(alphaField);
		clusteringRatioSlider = new JSlider(0, 100, 50);
		pp3.add(ppp3);
		pp3.add(clusteringRatioSlider);
		p1.add(pp3);
		
		JPanel pp4 = new JPanel();
		pp4.setLayout(new GridLayout(2,1));
		pp4.add(new JLabel("Intensity ratio"));
		intensityRatioSlider = new JSlider(0, 100, 50);
		pp4.add(intensityRatioSlider);
		p1.add(pp4);
		
		pathButton = new JRadioButton("Path");
		populationButton = new JRadioButton("Population");
		stopnessButton = new JRadioButton("Stopness");
		directionButton = new JRadioButton("Direction");
		p1.add(pathButton);
		p1.add(populationButton);
		p1.add(stopnessButton);
		p1.add(directionButton);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(pathButton);
		group1.add(populationButton);
		group1.add(stopnessButton);
		group1.add(directionButton);
		
		//
		// パネル群のレイアウト
		//
		this.setLayout(new GridLayout(2,1));
		this.add(p1);
		
		//
		// リスナーの追加
		//
		if (bl == null)
			bl = new ButtonListener();
		addButtonListener(bl);

		if (rbl == null)
			rbl = new RadioButtonListener();
		addRadioButtonListener(rbl);
		
		if (sl == null)
			sl = new SliderListener();
		addSliderListener(sl);
	}
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c) {
		canvas = (Canvas) c;
	}
	
	/**
	 * FileOpener をセットする
	 */
	public void setFileOpener(FileOpener fo) {
		fileOpener = fo;
	}

	
	
	/**
	 * Cursor Sensor の ON/OFF を指定するフラグを返す
	 * @return cursorSensorFlag
	 */
	public boolean getCursorSensorFlag() {
		return cursorSensorFlag;
	}
	
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		pathButton.addActionListener(actionListener);
		populationButton.addActionListener(actionListener);
		stopnessButton.addActionListener(actionListener);
		directionButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		fileOpenButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		clusteringButton.addActionListener(actionListener);
	}
	
	
	public void addSliderListener(ChangeListener changeListener) {
		clusteringRatioSlider.addChangeListener(changeListener);
		intensityRatioSlider.addChangeListener(changeListener);
	}

	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if (buttonPushed == pathButton) {
				canvas.setDisplayMode(canvas.DISPLAY_PATH);
			}
			if (buttonPushed == populationButton) {
				canvas.setDisplayMode(canvas.DISPLAY_POPULATION);
			}
			if (buttonPushed == stopnessButton) {
				canvas.setDisplayMode(canvas.DISPLAY_STOPNESS);
			}
			if (buttonPushed == directionButton) {
				canvas.setDisplayMode(canvas.DISPLAY_DIRECTION);
			}
			canvas.display();
		}
	}

	/**
	 * ボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton buttonPushed = (JButton) e.getSource();
			if (buttonPushed == fileOpenButton) {
				ds = fileOpener.getDataSet();
				ds.setDivision(Integer.parseInt(numDivideField.getText()));
				canvas.setDataSet(ds);
				canvas.display();
			}
			if (buttonPushed == viewResetButton) {				
				canvas.viewReset();
				canvas.display();
			}
			if (buttonPushed == clusteringButton) {
				int numd = Integer.parseInt(numDivideField.getText());
				int numc = Integer.parseInt(numClusterField.getText());
				if(ds != null) {
					ds.setDivision(numd);
					double alpha = Double.parseDouble(alphaField.getText());
					ClusteringInvoker ci = new ClusteringInvoker();
					ci.clustering(ds, numc, alpha);
				}
				canvas.setNumCluster(numc);
				canvas.viewReset();
				canvas.display();
			}
		}
	}

	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider sliderChanged = (JSlider) e.getSource();
			if (sliderChanged == clusteringRatioSlider) {
				double t = (double)clusteringRatioSlider.getValue() / 100.0;
				alphaField.setText(Double.toString(t));
			}
			if (sliderChanged == intensityRatioSlider) {
				double t = (double)intensityRatioSlider.getValue() / 100.0;
				canvas.setIntensityRatio(t);
				canvas.display();
			}
		}
	}
}
