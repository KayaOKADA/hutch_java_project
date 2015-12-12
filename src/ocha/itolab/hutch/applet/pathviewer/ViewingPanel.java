
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

	public JButton  fileOpenButton, viewResetButton, aggregateButton, imageSaveButton;
	public JRadioButton pathButton, populationButton, directionButton;
	public JRadioButton allButton, stopButton, passButton, ratioButton;
	public JTextField minHourField, maxHourField, imageFileField;
	public JSlider intensityRatioSlider;
	public Container container;
	JTabbedPane pane = null;

	/* Selective canvas */
	Canvas canvas;
	FileOpener fileOpener;
	ParameterPanel ppanel;
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
		p1.setLayout(new GridLayout(20,1));
		
		fileOpenButton = new JButton("JSON/CSV File Open");
		p1.add(fileOpenButton);
		
		viewResetButton = new JButton("View Reset");
		p1.add(viewResetButton);
		
		JPanel pir = new JPanel();
		pir.setLayout(new GridLayout(2,1));
		pir.add(new JLabel("Intensity ratio"));
		intensityRatioSlider = new JSlider(0, 100, 50);
		pir.add(intensityRatioSlider);
		p1.add(pir);
		
		JPanel prb1 = new JPanel();
		prb1.setLayout(new GridLayout(1,2));
		JPanel prb2 = new JPanel();
		prb2.setLayout(new GridLayout(1,2));
		pathButton = new JRadioButton("Path");
		populationButton = new JRadioButton("Population");
		directionButton = new JRadioButton("Direction");
		prb1.add(pathButton);
		prb1.add(populationButton);
		prb2.add(directionButton);
		p1.add(prb1);
		p1.add(prb2);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(pathButton);
		group1.add(populationButton);
		group1.add(directionButton);
		
		JPanel pminh = new JPanel();
		pminh.setLayout(new GridLayout(1,2));
		minHourField = new JTextField("0");
		pminh.add(new JLabel("Min. hour"));
		pminh.add(minHourField);
		p1.add(pminh);
		
		JPanel pmaxh = new JPanel();
		pmaxh.setLayout(new GridLayout(1,2));
		maxHourField = new JTextField("24");
		pmaxh.add(new JLabel("Max. hour"));
		pmaxh.add(maxHourField);
		p1.add(pmaxh);
		
		JPanel pck1 = new JPanel();
		pck1.setLayout(new GridLayout(1,2));
		JPanel pck2 = new JPanel();
		pck2.setLayout(new GridLayout(1,2));
		allButton = new JRadioButton("All");
		stopButton = new JRadioButton("Stop");
		passButton = new JRadioButton("Pass");
		ratioButton = new JRadioButton("Ratio");
		pck1.add(allButton);
		pck1.add(stopButton);
		pck2.add(passButton);
		pck2.add(ratioButton);
		p1.add(pck1);
		p1.add(pck2);
		ButtonGroup group2= new ButtonGroup();
		group2.add(allButton);
		group2.add(stopButton);
		group2.add(passButton);
		group2.add(ratioButton);
		
		aggregateButton = new JButton("Aggregate again");
		p1.add(aggregateButton);
		

		
		JPanel pif = new JPanel();
		pif.setLayout(new GridLayout(1,2));
		imageFileField = new JTextField("image.png");
		pif.add(new JLabel("Image filename"));
		pif.add(imageFileField);
		p1.add(pif);
		
		imageSaveButton = new JButton("Image Save");
		p1.add(imageSaveButton);
		
		//
		// パネル群のレイアウト
		//
		pane = new JTabbedPane();
		pane.add(p1);
		pane.setTabComponentAt(0, new JLabel("Main"));
		this.add(pane);
		
		ppanel = new ParameterPanel();
		pane.add(ppanel);
		pane.setTabComponentAt(1, new JLabel("Parameters"));
		pane.setMnemonicAt(1, KeyEvent.VK_0);
		
		
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
		ppanel.setCanvas(canvas);
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
		directionButton.addActionListener(actionListener);
		allButton.addActionListener(actionListener);
		stopButton.addActionListener(actionListener);
		passButton.addActionListener(actionListener);
		ratioButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		fileOpenButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		aggregateButton.addActionListener(actionListener);
		imageSaveButton.addActionListener(actionListener);
	}
	
	
	public void addSliderListener(ChangeListener changeListener) {
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
			if (buttonPushed == directionButton) {
				canvas.setDisplayMode(canvas.DISPLAY_DIRECTION);
			}
			if (buttonPushed == allButton) {
				if(ds != null)
					ds.grid.setAggregateFlag(Grid.AGGREGATE_ALL);
			}
			if (buttonPushed == stopButton) {
				if(ds != null)
					ds.grid.setAggregateFlag(Grid.AGGREGATE_STOP);
			}
			if (buttonPushed == passButton) {
				if(ds != null)
					ds.grid.setAggregateFlag(Grid.AGGREGATE_PASS);
			}
			if (buttonPushed == ratioButton) {
				if(ds != null)
					ds.grid.setAggregateFlag(Grid.AGGREGATE_RATIO);
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
				ppanel.setDataSet(ds);
				canvas.setDataSet(ds);
				canvas.display();
			}
			if (buttonPushed == viewResetButton) {				
				canvas.viewReset();
				canvas.display();
			}
			if (buttonPushed == aggregateButton) {
				int min = Integer.parseInt(minHourField.getText());
				ds.grid.setMinHour(min);
				int max = Integer.parseInt(maxHourField.getText());
				ds.grid.setMaxHour(max);
				ds.grid.aggregate();
				canvas.display();
			}
			if(buttonPushed == imageSaveButton) {
				canvas.saveImageFile(imageFileField.getText());
				canvas.display();
			}
		}
	}

	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider sliderChanged = (JSlider) e.getSource();
			if (sliderChanged == intensityRatioSlider) {
				double t = (double)intensityRatioSlider.getValue() / 100.0;
				canvas.setIntensityRatio(t);
				canvas.display();
			}
		}
	}
}
