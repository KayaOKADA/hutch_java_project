package ocha.itolab.hutch.core.data;

import java.awt.Dimension;
import java.awt.Color;
import javax.swing.JPanel;

public class TimePanel {
	static int time_num = 24;
	static JPanel[] timepanel = new JPanel[time_num];
	public static void panelInit(JPanel panel, Color[] colormap) {
		panel.removeAll();
		for(int i=0;i<time_num;i++){
			timepanel[i] = new JPanel();
			timepanel[i].setPreferredSize(new Dimension(1, 1));
			//timecolor[i] = BlockSet.setPanelcolormap(i);
			timepanel[i].setBackground(colormap[i]);
			panel.add(timepanel[i]);
		}
	}
}
