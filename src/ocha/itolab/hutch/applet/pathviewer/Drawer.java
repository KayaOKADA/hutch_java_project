package ocha.itolab.hutch.applet.pathviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.io.*;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.swing.event.DocumentListener;

import ocha.itolab.hutch.core.data.*;



public class Drawer implements GLEventListener {

	private GL gl;
	private GL2 gl2;
	private GLU glu;
	private GLUgl2 glu2;
	private GLUT glut;
	GLAutoDrawable glAD;

	private double angleX = 0.0f;
	private double angleY = 0.0f;
	private double shiftX = 0.0f;
	private double shiftY = 0.0f;
	private double scale = 1.0f;
	private double centerX, centerY, centerZ, size;

	DoubleBuffer modelview, projection, p1, p2, p3, p4;
	IntBuffer viewport;
	int pickX = -1, pickY = -1;
	
	int imageSize[] = new int[2];
	double datevalue = 0.0, interval = 0.0;
	boolean isMousePressed = false;
	double linewidth = 1.0, intensityRatio = 0.5;
	int windowWidth, windowHeight;
	int numCluster = 0, displayMode = Canvas.DISPLAY_PATH;
	
	Transformer trans = null;
	DrawerUtility du = null;
	GLCanvas glcanvas;
	
	DataSet ds;
	
	
	/**
	 * Constructor
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public Drawer(int width, int height, GLCanvas c) {
		
		glcanvas = c;
		imageSize[0] = width;
		imageSize[1] = height;
		du = new DrawerUtility(width, height);
		
		viewport = IntBuffer.allocate(4);
		modelview = DoubleBuffer.allocate(16);
		projection = DoubleBuffer.allocate(16);
		
		p1 = DoubleBuffer.allocate(3);
		p2 = DoubleBuffer.allocate(3);
		p3 = DoubleBuffer.allocate(3);
		p4 = DoubleBuffer.allocate(3);
		
		glcanvas.addGLEventListener(this);
	}

	/**
	 * Constructor
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public Drawer() {
		this(800, 600, null);
	}
		
		
	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * Transformerをセットする
	 * @param transformer 
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
		du.setTransformer(view);
	}

	/**
	 * 描画領域のサイズを設定する
	 * @param width 描画領域の幅
	 * @param height 描画領域の高さ
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du.setWindowSize(width, height);
	}

	
	/**
	 * アノテーション表示のON/OFF制御
	 * @param flag 表示するならtrue, 表示しないならfalse
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * マウスボタンのON/OFFを設定する
	 * @param isMousePressed マウスボタンが押されていればtrue
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * 線の太さをセットする
	 * @param lw 線の太さ（画素数）
	 */
	public void setLinewidth(double lw) {
		linewidth = lw;
		
	}

	public void setNumCluster(int numc) {
		numCluster = numc;
	}
	
	
	public void setIntensityRatio(double t) {
		intensityRatio = t;
	}
	
	public void setDataSet(DataSet ds) {
		this.ds = ds;
		trans.setDataSet(ds);
		centerX = trans.getCenter(0);
		centerY = trans.getCenter(1);
		size = trans.getSize();
	}
	
	
	public void setDisplayMode(int mode) {
		displayMode = mode;
	}
	
	
	/**
	 * ダミーメソッド
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
	


	/**
	 * 初期化
	 */
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL();
		gl2= drawable.getGL().getGL2();
		glu = new GLU();
		glu2 = new GLUgl2();
		glut = new GLUT();
		this.glAD = drawable;
	
		gl.glEnable(GL.GL_RGBA);
		gl.glEnable(GL2.GL_DEPTH);
		gl.glEnable(GL2.GL_DOUBLE);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl2.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// テクスチャ関係パラメータ
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
	}
	
	
	/**
	 * 再描画
	 */
	public void reshape(GLAutoDrawable drawable,
			int x, int y, int width, int height) {
		
		windowWidth = width;
		windowHeight = height;
	
		
		// ビューポートの定義
		gl2.glViewport(0, 0, width, height);
				
		// 投影変換行列の定義
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, 
				-height / 200.0, height / 200.0,
				-1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
	}
	


	/**
	 * 描画を実行する
	 */
	public void display(GLAutoDrawable drawable) {
		if(ds == null) return;
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// 視点位置を決定
		gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
					centerX, centerY, centerZ,
					0.0, 1.0, 0.0 );

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScale() * windowWidth / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// 行列をプッシュ
		gl2.glPushMatrix();
		
		// いったん原点方向に物体を動かす
		gl2.glTranslated(centerX, centerY, centerZ);
		
		// マウスの移動量に応じて回転
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0); 

		// マウスの移動量に応じて拡大縮小
		gl2.glScaled(scale, scale, 1.0);
			
		// マウスの移動量に応じて移動
		gl2.glTranslated((shiftX * 50.0), (shiftY * 50.0), 0.0);
		
		// 物体をもとの位置に戻す
		gl2.glTranslated(-centerX, -centerY, -centerZ);
		
		// 変換行列とビューポートの値を保存する
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);


		// 軌跡を表示する
		gl2.glDisable(GL2.GL_LIGHTING);
		if(displayMode == Canvas.DISPLAY_PATH) {
			PathDrawer.setIntensityRatio(1.0);
			PathDrawer.drawPath(gl2, ds, numCluster);
		}
		else {
			if(pickX >= 0 && pickY >= 0) {
				GridDrawer.setIntensityRatio(intensityRatio);
				PathDrawer.setIntensityRatio(1.0 - intensityRatio);
				GridDrawer.drawGrid(gl2, ds, displayMode);
				PathDrawer.drawPathLocal(gl2, ds, numCluster, pickX, pickY);
			}
			else {
				GridDrawer.setIntensityRatio(1.0);
				GridDrawer.drawGrid(gl2, ds, displayMode);
			}
		}
		
		// 行列をポップ
		gl2.glPopMatrix();
		
	}


	public void pickObjects(int px, int py) {
		if(ds == null) return;
		py = viewport.get(3) - py + 1;
		
		double[] xarray = ds.grid.getXarray();
		double[] yarray = ds.grid.getYarray();
		
		pickX = -1;
		for(int i = 0; i < xarray.length; i++) {
			glu2.gluProject(xarray[i], yarray[0], 0.0, modelview, projection, viewport, p1);
			double xx = p1.get(0);
			if(xx > (double)px) {
				pickX = i - 1;  break;
			}
		}
		
		pickY = -1;
		for(int i = 0; i < yarray.length; i++) {
			glu2.gluProject(xarray[0], yarray[i], 0.0, modelview, projection, viewport, p1);
			double yy = p1.get(1);
			if(yy > (double)py) {
				pickY = i - 1;  break;
			}
		}
		
		//System.out.println(px + " " + py + " " + pickX + " " + pickY);
	}
	
	
	
	/**
	 * 自動生成されたメソッド（そのままにしておく
	 */
	public void dispose(GLAutoDrawable arg0) {
		// 空欄のままにしておく
	}
}
