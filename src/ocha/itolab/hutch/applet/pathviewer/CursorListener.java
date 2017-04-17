
package ocha.itolab.hutch.applet.pathviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import com.jogamp.opengl.awt.GLCanvas;
//import javax.media.opengl.awt.GLCanvas;


public class CursorListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	Canvas canvas = null;
	GLCanvas glcanvas = null;

	FileOpener fileOpener = null;
	ViewingPanel  viewingPanel = null;
	int initX = 0, initY = 0, totalR = 0;
	long wheelCount = 0;
	
	/**
	 * Canvasをセットする
	 * @param c Canvas
	 */
	public void setCanvas(Object c, Object glc) {
		canvas = (Canvas) c;
		glcanvas = (GLCanvas) glc;
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
	}

	/**
	 * ViewingPanelをセットする
	 * @param v ViewingPanel
	 */
	public void setViewingPanel(ViewingPanel v) {
		viewingPanel = v;
	}
	
	/**
	 * FileOpener をセットする
	 */
	public void setFileOpener(FileOpener fo) {
		fileOpener = fo;
	}


	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * マウスのクリックを検出するリスナー
	 */
	public void mouseClicked(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
	
		
		int cX = e.getX();
		int cY = e.getY();
		canvas.pickObjects(cX, cY);
		canvas.display();
	}

	/**
	 * マウスボタンが押されたことを検出するリスナー
	 */
	public void mousePressed(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;

		totalR = 0;
		initX = e.getX();
		initY = e.getY();
		canvas.mousePressed(initX, initY);
	}

	/**
	 * マウスボタンが離されたことを検出するリスナー
	 */
	public void mouseReleased(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		
		canvas.mouseReleased();
		canvas.display();
	}

	/**
	 * マウスカーソルが動いたことを検出するリスナー
	 */
	public void mouseMoved(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		//if(viewingPanel.getCursorSensorFlag() == false) return;
		
		/*
		int cX = e.getX();
		int cY = e.getY();
		canvas.pickObjects(cX, cY);
		canvas.display();
		*/
	}

	/**
	 * マウスカーソルをドラッグしたことを検出するリスナー
	 */
	public void mouseDragged(MouseEvent e) {

		if(canvas == null) return;
		if(glcanvas == null) return;
		
		int cX = e.getX();
		int cY = e.getY();
		
		// 右ボタンの処理
		int m = e.getModifiers();
		if((m & MouseEvent.BUTTON3_MASK) != 0) {
			int dragMode = canvas.getDragMode();
			canvas.setDragMode(2); // SHIFT mode
			canvas.drag(initX, cX, initY, cY);
			canvas.setDragMode(dragMode);
		}
		else {
			canvas.drag(initX, cX, initY, cY);
		}
		canvas.display();
	}
	

	
	/**
	 * マウスホイールの動きを検出するリスナー
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(canvas == null) return;
		if(glcanvas == null) return;
		wheelCount++;

		canvas.mousePressed(initX, initY);
		int dragMode = canvas.getDragMode();
		canvas.setDragMode(1); // ZOOM mode
		int r = e.getWheelRotation();
		totalR -= (r * 20);
		canvas.drag(0, 0, 0, totalR);
		canvas.display();
		canvas.setDragMode(dragMode);
		WheelThread wt = new WheelThread(wheelCount);
		wt.start();
		
	}
	
	class WheelThread extends Thread {
		long count;
		WheelThread(long c) {
             this.count = c;
        }
 
         public void run() {
        	 try {
        		 Thread.sleep(500);
        	 } catch(Exception e) {
        	 	e.printStackTrace();
        	 }
        	 if(count != wheelCount) return;
        	 canvas.mouseReleased();
        	 canvas.display();
        	 totalR = 0;
         }
	}
}
