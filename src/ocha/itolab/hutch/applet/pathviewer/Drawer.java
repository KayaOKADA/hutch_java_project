package ocha.itolab.hutch.applet.pathviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.gl2.GLUgl2;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import ocha.itolab.hutch.core.data.DataSet;


public class Drawer implements GLEventListener {

	static private GL gl;
	static private GL2 gl2;
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
	int pickedBlockID = -1;
	
	int imageSize[] = new int[2];
	double datevalue = 0.0, interval = 0.0;
	boolean isMousePressed = false;
	double linewidth = 1.0, intensityRatio = 0.5, transparency = 0.5;
	static double imageShiftX = -0.08, imageShiftY = 0.33, imageScale = 1.3;
	int windowWidth, windowHeight;
	int numCluster = 0, displayMode = Canvas.DISPLAY_POPULATION;
	
	Transformer trans = null;
	DrawerUtility du = null;
	GLCanvas glcanvas;
	
	static DataSet ds;
	BufferedImage image;
	static Texture texture;
	
	String path = "";
	String filename = "";
	boolean saveImageFlag = false;
	
	
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
	
	public void setTransparency(double t) {
		transparency = t;
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
	
	public void setSaveImage(String name) {
		filename = name;
		saveImageFlag = true;
	}

	public void setImageShiftX(double t) {
		imageShiftX = t;
	}
	
	public void setImageShiftY(double t) {
		imageShiftY = t;
	}
	
	public void setImageScale(double t) {
		imageScale = t;
	}
	
	
	public void setCurrentDirectory(String path) {
		this.path = path;
		String inputfilename = path + "/floormap.png";
		System.out.println(inputfilename);
		
		try {
			image = ImageIO.read(new File(inputfilename));
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		// テクスチャ関係パラメータ
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		
		// Zバッファ法を有効にする
		gl.glEnable(GL.GL_RGBA);
		gl.glEnable(GL2.GL_DEPTH);
	    gl.glEnable(GL2.GL_DOUBLE);
	    gl.glEnable(GL.GL_DEPTH_TEST);
	    gl.glEnable(GL2.GL_NORMALIZE);
	    gl.glDisable(GL.GL_CULL_FACE);
	    //gl.glCullFace(GL.GL_BACK);
	    
	    gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 100.0), centerX, centerY, centerZ, 0.0, 1.0,0.0 );
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

		if(image != null && texture == null) {
			TextureData textureData = AWTTextureIO.newTextureData(gl.getGLProfile(), image, false);
			texture = TextureIO.newTexture(textureData);
		}
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// 視点位置を決定
		gl2.glLoadIdentity();
		//glu.gluLookAt( centerX, centerY, (centerZ + 20.0), centerX, centerY, centerZ, 0.0, 1.0, 0.0 );
		glu.gluLookAt( centerX, centerY, (centerZ + 100.0), centerX, centerY, centerZ, 0.0, 1.0, 0.0 );

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
		gl2.glEnable(GL2.GL_NORMALIZE);
		gl2.glEnable(GL2.GL_LIGHTING);
		float silver[] = {0.5f, 0.5f, 0.5f, 1.0f};
	    float lightpos[] = { -15.0f, -40.0f, 10.0f, 1.0f };
        gl2.glEnable(GL2.GL_LIGHT0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, lightpos, 0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, silver, 0);
		gl2.glEnable(GL2.GL_COLOR_MATERIAL);	
		//drawFloor();
		if(displayMode == Canvas.DISPLAY_PATH) {
			PathDrawer.setIntensityRatio(1.0);
			PathDrawer.drawPath(gl2, ds, numCluster);
		}
		else {
			if(pickedBlockID >= 0) {
				BlockDrawer.setIntensityRatio(intensityRatio);
				PathDrawer.setIntensityRatio(1.0 - intensityRatio);
				BlockDrawer.drawBlock(gl2, ds, displayMode);
				PathDrawer.drawPathLocal(gl2, ds, numCluster, pickedBlockID);
			}
			else {
				BlockDrawer.setIntensityRatio(1.0);
				BlockDrawer.drawBlock(gl2, ds, displayMode);
			}
		}
		
		if(saveImageFlag) {
			saveImage();
			saveImageFlag = false;
		}
		
		// 行列をポップ
		gl2.glPopMatrix();
		//ViewingPanel.setTimepanel();
	}



	
	public static void drawFloor(float z) {
		if(ds == null) return;
		if(texture == null) return;
		
		double minmax[] = ds.getMinMax();
		double diffx = (minmax[1] - minmax[0]) * 0.5;
		double diffy = (minmax[3] - minmax[2]) * 0.5;
		double centx = (minmax[0] + minmax[1]) * 0.5;
		double centy = (minmax[2] + minmax[3]) * 0.5;
		double minx = centx + ((minmax[0] - centx) + (imageShiftX * diffx)) * imageScale;
		double maxx = centx + ((minmax[1] - centx) + (imageShiftX * diffx)) * imageScale;
		double miny = centy + ((minmax[2] - centy) + (imageShiftY * diffy)) * imageScale;
		double maxy = centy + ((minmax[3] - centy) + (imageShiftY * diffy)) * imageScale;
	
		gl2.glColor4d(1.0, 1.0, 1.0, 0.5);
		// テクスチャ画像データの登録の開始
		gl.glEnable(GL.GL_TEXTURE_2D);

		// テクスチャ座標値の設定
		texture.enable(gl);
		texture.bind(gl);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2d(0.0, 1.0);
		gl2.glVertex3d(minx, miny, z);
		gl2.glTexCoord2d(1.0, 1.0);
		gl2.glVertex3d(maxx, miny, z);
		gl2.glTexCoord2d(1.0, 0.0);
		gl2.glVertex3d(maxx, maxy, z);
		gl2.glTexCoord2d(0.0, 0.0);
		gl2.glVertex3d(minx, maxy, z);
		gl2.glEnd();
		texture.disable(gl);

		// テクスチャ画像データの登録の終了
		gl.glDisable(GL.GL_TEXTURE_2D);
		
	}
	
	
	public void pickObjects(int px, int py) {
		if(ds == null) return;
		py = viewport.get(3) - py + 1;
		glu2.gluUnProject((double)px, (double)py, 0.0, modelview, projection, viewport, p1);
		pickedBlockID = ds.block.specifyEnclosingBlock(p1.get(0), p1.get(1));
	}
	
	
	
	void saveImage() {
		
		// RGBなら3, RGBAなら4
        int channelNum = 4;
        int allocSize = windowWidth * windowHeight * channelNum;
        ByteBuffer byteBuffer = ByteBuffer.allocate(allocSize);
        //gl2.glFlush();
        // 読み取るOpneGLのバッファを指定 GL_FRONT:フロントバッファ　GL_BACK:バックバッファ
        gl2.glReadBuffer( GL2.GL_BACK );
 
        // OpenGLで画面に描画されている内容をバッファに格納
        gl2.glReadPixels(0,             // 読み取る領域の左下隅のx座標
                0,                      // 読み取る領域の左下隅のy座標
                windowWidth,             // 読み取る領域の幅
                windowHeight,            // 読み取る領域の高さ
                GL2.GL_BGRA,            // 取得したい色情報の形式
                GL2.GL_UNSIGNED_BYTE,   // 読み取ったデータを保存する配列の型
                (Buffer) byteBuffer     // ビットマップのピクセルデータ（実際にはバイト配列）へのポインタ
        );
      
        // glReadBufferで取得したデータ(ByteBuffer)をDataBufferに変換する
        byte[] buff = byteBuffer.array();
    	BufferedImage imageBuffer =
				new BufferedImage(windowWidth, windowHeight, BufferedImage.TYPE_INT_RGB);
    	
    	for(int y = 0; y < windowHeight; y++){
    		for(int x = 0; x < windowWidth; x++){
    			
    			int offset = windowWidth * (windowHeight - y - 1) * channelNum;
    			// R
    			int rr = (int)buff[x * channelNum + offset + 2];
    			if(rr < 0) rr += 256;
    			// G
    			int gg = (int)buff[x * channelNum + offset + 1];
    			if(gg < 0) gg += 256;
    			// B
    			int bb = (int)buff[x * channelNum + offset + 0];
    			if(bb < 0) bb += 256;
    			
    			Color color = new Color(rr, gg, bb);
    			int value = color.getRGB();
    			imageBuffer.setRGB(x, y, value);
            }
        }
        
        try {
            ImageIO.write(imageBuffer, "png", new File(filename));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
	}
	
	/**
	 * 自動生成されたメソッド（そのままにしておく
	 */
	public void dispose(GLAutoDrawable arg0) {
		// 空欄のままにしておく
	}
}
