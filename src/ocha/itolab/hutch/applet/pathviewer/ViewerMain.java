package ocha.itolab.hutch.applet.pathviewer;

import java.awt.*;
import java.util.*;
//import javax.media.opengl.awt.GLCanvas;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import javax.swing.*;


public class ViewerMain extends JApplet {

	// GUI element
	Container windowContainer;
	Canvas canvas;
	CursorListener cl;
	FileOpener fileOpener;
	MenuBar menuBar;
	ViewingPanel viewingPanel;
	
	public static double INTERVAL_CONST = 30.0;
	
	/**
	 * applet を初期化し、各種データ構造を初期化する
	 */
	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}

	/**
	 * applet の各イベントの受付をスタートする
	 */
	public void start() {
	}

	/**
	 * applet の各イベントの受付をストップする
	 */
	public void stop() {
	}

	/**
	 * applet等を初期化する
	 */
	private void buildGUI() {

		// Canvas
		Color foregroundColor = Color.white;
		Color backgroundColor = Color.white;
		canvas = new Canvas(512, 512, foregroundColor, backgroundColor);
		canvas.requestFocus();
		GLCanvas glc = canvas.getGLCanvas();
		
		// FileOpener
		fileOpener = new FileOpener();
		fileOpener.setContainer(windowContainer);
		fileOpener.setCanvas(canvas);
		
		// ViewingPanel
		viewingPanel = new ViewingPanel();
		viewingPanel.setCanvas(canvas);
		viewingPanel.setFileOpener(fileOpener);
		
		// MenuBar
		menuBar = new MenuBar();
		menuBar.setCanvas(canvas);
		
		// CursorListener
		cl = new CursorListener();
		cl.setCanvas(canvas, glc);
		cl.setViewingPanel(viewingPanel);
		canvas.addCursorListener(cl);
		
		// CanvasとViewingPanelのレイアウト
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// ウィンドウ上のレイアウト
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);
		
	}

	/**
	 * main関数
	 * @param args 実行時の引数
	 */
	public static void main(String[] args) {
		Window window = new Window("Hutch", true, 800, 600, Color.white);
		ViewerMain v = new ViewerMain();

		v.init();
		window.getContentPane().add(v);
		window.setVisible(true);

		v.start(); 
	}

}
