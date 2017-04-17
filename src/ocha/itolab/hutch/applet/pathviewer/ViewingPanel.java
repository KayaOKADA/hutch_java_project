
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
import javax.swing.border.LineBorder;

public class ViewingPanel extends JPanel  {

	public JButton  fileOpenButton, viewResetButton, aggregateButton, imageSaveButton, looktimeButton;
	public JRadioButton pathButton, populationButton;
	public JRadioButton maleButton, femaleButton, under15Button, b1550Button, over50Button, genderratioButton, allButton;
	public JRadioButton increase_decreaseButton, statisticsButton;
	public JTextField minHourField, maxHourField, imageFileField;
	public JSlider intensityRatioSlider,transparencySlider, looktimeSlider;
	public JSlider transparency_idSlider1, transparency_idSlider2;
	public JLabel transparencyLabel;
	public Container container;
	JTabbedPane pane = null;
	public static JPanel pck4 = new JPanel();

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
		p1.setLayout(new GridLayout(30,1));
		
		fileOpenButton = new JButton("JSON/CSV File Open");
		p1.add(fileOpenButton);
		
		viewResetButton = new JButton("View Reset");
		p1.add(viewResetButton);
		
		looktimeButton = new JButton("invisible/visible");
		p1.add(looktimeButton);
		
		JPanel pir = new JPanel();
		pir.setLayout(new GridLayout(2,1));
		JPanel pir2 = new JPanel();
		pir2.setLayout(new GridLayout(2,1));
		JPanel pir3 = new JPanel();
		pir3.setLayout(new GridLayout(2,1));
		JPanel pir4 = new JPanel();
		pir4.setLayout(new GridLayout(1,1));
		//pir.add(new JLabel("Intensity ratio"));
		//intensityRatioSlider = new JSlider(0, 100, 50);
		//pir.add(intensityRatioSlider);
		pir.add(new JLabel("Transparency"));
		transparencySlider = new JSlider(5, 40, 20);
		//transparencySlider.setMajorTickSpacing(10);
		//transparencySlider.setMajorTickSpacing(5);
		pir.add(transparencySlider);
		p1.add(pir);
		pir3.add(new JLabel("Transparency 増減"));
		transparency_idSlider1 = new JSlider(5, 250, 40);
		pir3.add(transparency_idSlider1);
		p1.add(pir3);
		transparency_idSlider2 = new JSlider(100, 600, 300);
		pir4.add(transparency_idSlider2);
		p1.add(pir4);
		pir2.add(new JLabel("Time"));
		looktimeSlider = new JSlider(0, 24, 0);
		pir2.add(looktimeSlider);
		p1.add(pir2);
		
		/*
		JPanel prb1 = new JPanel();
		prb1.setLayout(new GridLayout(1,2));
		JPanel prb2 = new JPanel();
		prb2.setLayout(new GridLayout(1,2));
		pathButton = new JRadioButton("Path");
		populationButton = new JRadioButton("Population");
		prb1.add(pathButton);
		prb1.add(populationButton);
		p1.add(prb1);
		p1.add(prb2);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(pathButton);
		group1.add(populationButton);
		
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
		*/
		
		JPanel pck1 = new JPanel();
		pck1.setLayout(new GridLayout(1,2));
		JPanel pck2 = new JPanel();
		pck2.setLayout(new GridLayout(1,3));
		JPanel pck3 = new JPanel();
		pck3.setLayout(new GridLayout(1,2));
		//JPanel pck4 = new JPanel();
		pck4.setLayout(new GridLayout(1,23));
		pck4.setPreferredSize(new Dimension(1,2));
		pck4.setBorder(new LineBorder(Color.LIGHT_GRAY, 2, true));
		JPanel pck5 = new JPanel();
		pck5.setLayout(new GridLayout(1,2));
		increase_decreaseButton = new JRadioButton("増減");
		statisticsButton = new JRadioButton("ALL");
		maleButton = new JRadioButton("Male");
		femaleButton = new JRadioButton("Female");
		under15Button = new JRadioButton("-15");
		b1550Button = new JRadioButton("15-50");
		over50Button = new JRadioButton("50-");
		allButton = new JRadioButton("ALL(hour)");
		genderratioButton = new JRadioButton("Gender ratio");
		Color colormap[] = new Color[24];
		for(int i=0;i<24;i++){
			colormap[i] = Color.WHITE;
		}
		TimePanel.panelInit(pck4, colormap);
		//Color color[] = BlockSet.setPanelcolormap();
		
		pck1.add(maleButton);
		pck1.add(femaleButton);
		pck2.add(under15Button);
		pck2.add(b1550Button);
		pck2.add(over50Button);
		pck3.add(allButton);
		pck3.add(genderratioButton);
		pck5.add(increase_decreaseButton);
		pck5.add(statisticsButton);
		
		p1.add(pck4);
		p1.add(pck5);
		p1.add(pck1);
		//p1.add(pck2);
		//p1.add(pck3);
		ButtonGroup group2= new ButtonGroup();
		group2.add(maleButton);
		group2.add(femaleButton);
		group2.add(under15Button);
		group2.add(b1550Button);
		group2.add(over50Button);
		group2.add(allButton);
		group2.add(genderratioButton);
		group2.add(statisticsButton);
		group2.add(increase_decreaseButton);
		
		aggregateButton = new JButton("Aggregate again");
		/*
		p1.add(aggregateButton);
		 */
		
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
		
		//ParameterPanel
		/*
		ppanel = new ParameterPanel();
		pane.add(ppanel);
		pane.setTabComponentAt(1, new JLabel("Parameters"));
		pane.setMnemonicAt(1, KeyEvent.VK_0);*/
		
		
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
		//ppanel.setCanvas(canvas);
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
	
	public static void setTimepanel(Color[] colormap){
		TimePanel.panelInit(pck4, colormap);
	}
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		//pathButton.addActionListener(actionListener);
		//populationButton.addActionListener(actionListener);
		maleButton.addActionListener(actionListener);
		femaleButton.addActionListener(actionListener);
		under15Button.addActionListener(actionListener);
		b1550Button.addActionListener(actionListener);
		over50Button.addActionListener(actionListener);
		allButton.addActionListener(actionListener);
		genderratioButton.addActionListener(actionListener);
		increase_decreaseButton.addActionListener(actionListener);
		statisticsButton.addActionListener(actionListener);
	}

	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		fileOpenButton.addActionListener(actionListener);
		viewResetButton.addActionListener(actionListener);
		looktimeButton.addActionListener(actionListener);
		aggregateButton.addActionListener(actionListener);
		imageSaveButton.addActionListener(actionListener);
	}
	
	
	public void addSliderListener(ChangeListener changeListener) {
		//intensityRatioSlider.addChangeListener(changeListener);
		transparencySlider.addChangeListener(changeListener);
		transparency_idSlider1.addChangeListener(changeListener);
		transparency_idSlider2.addChangeListener(changeListener);
		looktimeSlider.addChangeListener(changeListener);
	}

	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if (buttonPushed == populationButton) {
				canvas.setDisplayMode(canvas.DISPLAY_POPULATION);
			}
			if (buttonPushed == maleButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_MALE);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == femaleButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_FEMALE);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == under15Button) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_UNDER15);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == b1550Button) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_BETWEEN1550);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == over50Button) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_OVER50);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == allButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_ALL);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == genderratioButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_GENDER_RATIO);
					BlockDrawer.setAggregatestatistics(0);
				}
			}
			if (buttonPushed == increase_decreaseButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_INCREASE_DECREASE);
					BlockDrawer.setAggregatestatistics(1);
				}
			}
			if (buttonPushed == statisticsButton) {
				if(ds != null){
					ds.block.setAggregateFlag(BlockSet.AGGREGATE_STATISTICS);
					BlockDrawer.setAggregatestatistics(1);
				}
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
				//ppanel.setDataSet(ds);
				canvas.setDataSet(ds);
				canvas.display();
			}
			if (buttonPushed == viewResetButton) {				
				canvas.viewReset();
				canvas.display();
			}
			if (buttonPushed == looktimeButton) {				
				BlockDrawer.setVisible();
				canvas.display();
			}
			if (buttonPushed == aggregateButton) {
				/*int min = Integer.parseInt(minHourField.getText());
				ds.block.setMinHour(min);
				int max = Integer.parseInt(maxHourField.getText());
				ds.block.setMaxHour(max);*/
				ds.block.aggregate();
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
			/*if (sliderChanged == intensityRatioSlider) {
				double t = (double)intensityRatioSlider.getValue() / 100.0;
				canvas.setIntensityRatio(t);
				canvas.display();
			}*/
			if (sliderChanged == transparencySlider) {
				double t = (double)transparencySlider.getValue();
				ds.block.setTransparency(t, 0);
				canvas.display();
				//transparencyLabel.setText("value : " + transparencySlider.getValue());
			}
			if (sliderChanged == transparency_idSlider1) {
				double t = (double)transparency_idSlider1.getValue();
				ds.block.setTransparency(t, 1);
				canvas.display();
				//transparencyLabel.setText("value : " + transparencySlider.getValue());
			}
			if (sliderChanged == transparency_idSlider2) {
				double t = (double)transparency_idSlider2.getValue();
				ds.block.setTransparency(t, 2);
				canvas.display();
				//transparencyLabel.setText("value : " + transparencySlider.getValue());
			}
			if (sliderChanged == looktimeSlider) {
				int t = (int)looktimeSlider.getValue();
				ds.block.setLooktime(t);
				canvas.display();
				//transparencyLabel.setText("value : " + transparencySlider.getValue());
			}
		}
	}
}
