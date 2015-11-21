package ocha.itolab.hutch.applet.pathviewer;

import java.awt.*;
import javax.media.opengl.awt.GLCanvas;
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
	 * applet �����������A�e��f�[�^�\��������������
	 */
	public void init() {
		setSize(new Dimension(1000,800));
		buildGUI();
	}

	/**
	 * applet �̊e�C�x���g�̎�t���X�^�[�g����
	 */
	public void start() {
	}

	/**
	 * applet �̊e�C�x���g�̎�t���X�g�b�v����
	 */
	public void stop() {
	}

	/**
	 * applet��������������
	 */
	private void buildGUI() {

		// Canvas
		canvas = new Canvas(512, 512);
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
		
		// Canvas��ViewingPanel�̃��C�A�E�g
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(glc, BorderLayout.CENTER);
		mainPanel.add(viewingPanel, BorderLayout.WEST);

		// �E�B���h�E��̃��C�A�E�g
		windowContainer = this.getContentPane();
		windowContainer.setLayout(new BorderLayout());
		windowContainer.add(mainPanel, BorderLayout.CENTER);
		windowContainer.add(menuBar, BorderLayout.NORTH);
		
	}

	/**
	 * main�֐�
	 * @param args ���s���̈���
	 */
	public static void main(String[] args) {
		Window window = new Window("HumanTrackViewer", true, 800, 600, Color.lightGray);
		ViewerMain v = new ViewerMain();

		v.init();
		window.getContentPane().add(v);
		window.setVisible(true);

		v.start(); 
	}

}
