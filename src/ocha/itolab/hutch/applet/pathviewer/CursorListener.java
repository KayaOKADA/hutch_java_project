
package ocha.itolab.hutch.applet.pathviewer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import javax.media.opengl.awt.GLCanvas;


public class CursorListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	Canvas canvas = null;
	GLCanvas glcanvas = null;

	FileOpener fileOpener = null;
	ViewingPanel  viewingPanel = null;
	int initX = 0, initY = 0, totalR = 0;
	long wheelCount = 0;
	
	/**
	 * Canvas���Z�b�g����
	 * @param c Canvas
	 */
	public void setCanvas(Object c, Object glc) {
		canvas = (Canvas) c;
		glcanvas = (GLCanvas) glc;
		glcanvas.addMouseListener(this);
		glcanvas.addMouseMotionListener(this);
	}

	/**
	 * ViewingPanel���Z�b�g����
	 * @param v ViewingPanel
	 */
	public void setViewingPanel(ViewingPanel v) {
		viewingPanel = v;
	}
	
	/**
	 * FileOpener ���Z�b�g����
	 */
	public void setFileOpener(FileOpener fo) {
		fileOpener = fo;
	}


	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * �}�E�X�̃N���b�N�����o���郊�X�i�[
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
	 * �}�E�X�{�^���������ꂽ���Ƃ����o���郊�X�i�[
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
	 * �}�E�X�{�^���������ꂽ���Ƃ����o���郊�X�i�[
	 */
	public void mouseReleased(MouseEvent e) {
		
		if(canvas == null) return;
		if(glcanvas == null) return;
		
		canvas.mouseReleased();
		canvas.display();
	}

	/**
	 * �}�E�X�J�[�\�������������Ƃ����o���郊�X�i�[
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
	 * �}�E�X�J�[�\�����h���b�O�������Ƃ����o���郊�X�i�[
	 */
	public void mouseDragged(MouseEvent e) {

		if(canvas == null) return;
		if(glcanvas == null) return;
		
		int cX = e.getX();
		int cY = e.getY();
		
		// �E�{�^���̏���
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
	 * �}�E�X�z�C�[���̓��������o���郊�X�i�[
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
