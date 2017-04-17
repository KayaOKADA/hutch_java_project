package ocha.itolab.hutch.applet.pathviewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import ocha.itolab.hutch.core.data.*;
import ocha.itolab.hutch.core.tool.*;



public class ParameterPanel extends JPanel {

	public JButton clusteringButton, aggregateButton;	
	public JRadioButton kmeansButton, spectralButton;
	public JSlider clusteringRatioSlider, imageShiftXSlider, imageShiftYSlider, imageScaleSlider; 
	public JTextField numClusterField, numDivideField, alphaField;
	
	int method = ClusteringInvoker.KMEANS_CLUSTERING;
	
	/* Action listener */
	ButtonListener bl = null;
	RadioButtonListener rbl = null;
	SliderListener sl = null;
	
	DataSet ds;
	Canvas canvas;
	
	
	public ParameterPanel() {
		super();
		
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(15,1));
		
		kmeansButton = new JRadioButton("K-Means");
		spectralButton = new JRadioButton("Spectral");
		p1.add(kmeansButton);
		p1.add(spectralButton);
		ButtonGroup group2 = new ButtonGroup();
		group2.add(kmeansButton);
		group2.add(spectralButton);
	
		JPanel pnc = new JPanel();
		pnc.setLayout(new GridLayout(1,2));
		numClusterField = new JTextField("3");
		pnc.add(new JLabel("Num. cluster"));
		pnc.add(numClusterField);
		p1.add(pnc);
	
		JPanel pnd = new JPanel();
		pnd.setLayout(new GridLayout(1,2));
		numDivideField = new JTextField("10");
		pnd.add(new JLabel("Num. divide"));
		pnd.add(numDivideField);
		p1.add(pnd);
		
		JPanel pcr = new JPanel();
		JPanel pcr2 = new JPanel();
		pcr.setLayout(new GridLayout(2,1));
		pcr2.setLayout(new GridLayout(1,2));
		pcr2.add(new JLabel("Clustering ratio"));
		alphaField = new JTextField("0.5");
		pcr2.add(alphaField);
		clusteringRatioSlider = new JSlider(0, 100, 50);
		pcr.add(pcr2);
		pcr.add(clusteringRatioSlider);
		p1.add(pcr);
		
		clusteringButton = new JButton("Clustering");
		p1.add(clusteringButton);

		aggregateButton = new JButton("Aggregate again");
		p1.add(aggregateButton);

		JPanel ps1 = new JPanel();
		ps1.setLayout(new GridLayout(2,1));
		ps1.add(new JLabel("Image Shift X"));
		imageShiftXSlider = new JSlider(0, 100, 50);
		ps1.add(imageShiftXSlider);
		p1.add(ps1);
		
		JPanel ps2 = new JPanel();
		ps2.setLayout(new GridLayout(2,1));
		ps2.add(new JLabel("Image Shift Y"));
		imageShiftYSlider = new JSlider(0, 100, 50);
		ps2.add(imageShiftYSlider);
		p1.add(ps2);

		JPanel ps3 = new JPanel();
		ps3.setLayout(new GridLayout(2,1));
		ps3.add(new JLabel("Image Scale"));
		imageScaleSlider = new JSlider(0, 100, 50);
		ps3.add(imageScaleSlider);
		p1.add(ps3);

		this.add(p1);
	
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
	
	
	void setCanvas(Canvas c) {
		canvas = c;
	}
	
	void setDataSet(DataSet ds) {
		this.ds = ds;
		ds.setDivision(Integer.parseInt(numDivideField.getText()));
	}
	
	
	/**
	 * ラジオボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addRadioButtonListener(ActionListener actionListener) {
		kmeansButton.addActionListener(actionListener);
		spectralButton.addActionListener(actionListener);
	}
	
	
	/**
	 * ボタンのアクションの検出を設定する
	 * @param actionListener ActionListener
	 */
	public void addButtonListener(ActionListener actionListener) {
		clusteringButton.addActionListener(actionListener);
		aggregateButton.addActionListener(actionListener);
	}
	
	public void addSliderListener(ChangeListener changeListener) {
		clusteringRatioSlider.addChangeListener(changeListener);
		imageShiftXSlider.addChangeListener(changeListener);
		imageShiftYSlider.addChangeListener(changeListener);
		imageScaleSlider.addChangeListener(changeListener);
	}

	
	/**
	 * ラジオボタンのアクションを検知するActionListener
	 * @author itot
	 */
	class RadioButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JRadioButton buttonPushed = (JRadioButton) e.getSource();
			if (buttonPushed == kmeansButton) {
				method = ClusteringInvoker.KMEANS_CLUSTERING;
			}
			if (buttonPushed == spectralButton) {
				method = ClusteringInvoker.SPECTRAL_CLUSTERING;
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
			
			if (buttonPushed == clusteringButton) {
				int numd = Integer.parseInt(numDivideField.getText());
				int numc = Integer.parseInt(numClusterField.getText());
				if(ds != null) {
					ds.setDivision(numd);
					double alpha = Double.parseDouble(alphaField.getText());
					ClusteringInvoker ci = new ClusteringInvoker();
					ci.clustering(ds, numc, alpha, method);
				}
				canvas.setNumCluster(numc);
			}
			
			canvas.display();
		}
	}
	
	
	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider sliderChanged = (JSlider) e.getSource();
			if (sliderChanged == clusteringRatioSlider) {
				double t = (double)clusteringRatioSlider.getValue() / 100.0;
				alphaField.setText(Double.toString(t));
			}
			if (sliderChanged == imageShiftXSlider) {
				double t = (double)(imageShiftXSlider.getValue() - 50) / 50.0;
				canvas.setImageShiftX(t);
				canvas.display();
			}
			if (sliderChanged == imageShiftYSlider) {
				double t = (double)(imageShiftYSlider.getValue() - 50) / 50.0;
				canvas.setImageShiftY(t);
				canvas.display();
			}
			if (sliderChanged == imageScaleSlider) {
				double t = (double)imageScaleSlider.getValue() / 50.0;
				canvas.setImageScale(t);
				canvas.display();
			}
		}
	}
}
