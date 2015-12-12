package ocha.itolab.hutch.applet.pathviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
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

import jogamp.nativewindow.x11.awt.X11AWTGraphicsConfigurationFactory;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
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
	double imageShiftX = 0.0, imageShiftY = 0.0, imageScale = 1.0;
	int windowWidth, windowHeight;
	int numCluster = 0, displayMode = Canvas.DISPLAY_PATH;
	
	Transformer trans = null;
	DrawerUtility du = null;
	GLCanvas glcanvas;
	
	DataSet ds;
	BufferedImage image;
	Texture texture;
	
	String path = "";
	String filename = "";
	boolean saveImageFlag = false;
	
	
	/**
	 * Constructor
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
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
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public Drawer() {
		this(800, 600, null);
	}
		
		
	public GLAutoDrawable getGLAutoDrawable() {
		return glAD;
	}

	/**
	 * Transformer���Z�b�g����
	 * @param transformer 
	 */
	public void setTransformer(Transformer view) {
		this.trans = view;
		du.setTransformer(view);
	}

	/**
	 * �`��̈�̃T�C�Y��ݒ肷��
	 * @param width �`��̈�̕�
	 * @param height �`��̈�̍���
	 */
	public void setWindowSize(int width, int height) {
		imageSize[0] = width;
		imageSize[1] = height;
		du.setWindowSize(width, height);
	}

	
	/**
	 * �A�m�e�[�V�����\����ON/OFF����
	 * @param flag �\������Ȃ�true, �\�����Ȃ��Ȃ�false
	 */
	public void setAnnotationSwitch(boolean flag) {
		
	}
	
	/**
	 * �}�E�X�{�^����ON/OFF��ݒ肷��
	 * @param isMousePressed �}�E�X�{�^����������Ă����true
	 */
	public void setMousePressSwitch(boolean isMousePressed) {
		this.isMousePressed = isMousePressed;
	}

	/**
	 * ���̑������Z�b�g����
	 * @param lw ���̑����i��f���j
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
	 * �_�~�[���\�b�h
	 */
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}
	


	/**
	 * ������
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
		
		// �e�N�X�`���֌W�p�����[�^
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl2.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_DECAL);
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
	}
	
	
	/**
	 * �ĕ`��
	 */
	public void reshape(GLAutoDrawable drawable,
			int x, int y, int width, int height) {
		
		windowWidth = width;
		windowHeight = height;
	
		
		// �r���[�|�[�g�̒�`
		gl2.glViewport(0, 0, width, height);
				
		// ���e�ϊ��s��̒�`
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(-width / 200.0, width / 200.0, 
				-height / 200.0, height / 200.0,
				-1000.0, 1000.0);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		
	}
	


	/**
	 * �`������s����
	 */
	public void display(GLAutoDrawable drawable) {
		if(ds == null) return;

		if(image != null && texture == null) {
			TextureData textureData = AWTTextureIO.newTextureData(gl.getGLProfile(), image, false);
			texture = TextureIO.newTexture(textureData);
		}
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// ���_�ʒu������
		gl2.glLoadIdentity();
		glu.gluLookAt( centerX, centerY, (centerZ + 20.0),
					centerX, centerY, centerZ,
					0.0, 1.0, 0.0 );

		shiftX = trans.getViewShift(0);
		shiftY = trans.getViewShift(1);
		scale = trans.getViewScale() * windowWidth / (size * 300.0);
		angleX = trans.getViewRotateY() * 45.0;
		angleY = trans.getViewRotateX() * 45.0;

		// �s����v�b�V��
		gl2.glPushMatrix();
		
		// �������񌴓_�����ɕ��̂𓮂���
		gl2.glTranslated(centerX, centerY, centerZ);
		
		// �}�E�X�̈ړ��ʂɉ����ĉ�]
		gl2.glRotated(angleX, 1.0, 0.0, 0.0);
		gl2.glRotated(angleY, 0.0, 1.0, 0.0); 

		// �}�E�X�̈ړ��ʂɉ����Ċg��k��
		gl2.glScaled(scale, scale, 1.0);
			
		// �}�E�X�̈ړ��ʂɉ����Ĉړ�
		gl2.glTranslated((shiftX * 50.0), (shiftY * 50.0), 0.0);
		
		// ���̂����Ƃ̈ʒu�ɖ߂�
		gl2.glTranslated(-centerX, -centerY, -centerZ);
		
		// �ϊ��s��ƃr���[�|�[�g�̒l��ۑ�����
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport);
		gl2.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelview);
		gl2.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projection);


		// �O�Ղ�\������
		gl2.glDisable(GL2.GL_LIGHTING);
		drawFloor();
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
		
		if(saveImageFlag) {
			saveImage();
			saveImageFlag = false;
		}
		
		// �s����|�b�v
		gl2.glPopMatrix();
		
	}


	
	void drawFloor() {
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
	
		
		// �e�N�X�`���摜�f�[�^�̓o�^�̊J�n
		gl.glEnable(GL.GL_TEXTURE_2D);

		// �e�N�X�`�����W�l�̐ݒ�
		texture.enable(gl);
		texture.bind(gl);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2d(0.0, 1.0);
		gl2.glVertex3d(minx, miny, -0.1);
		gl2.glTexCoord2d(1.0, 1.0);
		gl2.glVertex3d(maxx, miny, -0.1);
		gl2.glTexCoord2d(1.0, 0.0);
		gl2.glVertex3d(maxx, maxy, -0.1);
		gl2.glTexCoord2d(0.0, 0.0);
		gl2.glVertex3d(minx, maxy, -0.1);
		gl2.glEnd();
		texture.disable(gl);

		// �e�N�X�`���摜�f�[�^�̓o�^�̏I��
		gl.glDisable(GL.GL_TEXTURE_2D);
		
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
	
	
	
	void saveImage() {
		
		// RGB�Ȃ�3, RGBA�Ȃ�4
        int channelNum = 4;
        int allocSize = windowWidth * windowHeight * channelNum;
        ByteBuffer byteBuffer = ByteBuffer.allocate(allocSize);
        //gl2.glFlush();
        // �ǂݎ��OpneGL�̃o�b�t�@���w�� GL_FRONT:�t�����g�o�b�t�@�@GL_BACK:�o�b�N�o�b�t�@
        gl2.glReadBuffer( GL2.GL_BACK );
 
        // OpenGL�ŉ�ʂɕ`�悳��Ă�����e���o�b�t�@�Ɋi�[
        gl2.glReadPixels(0,             // �ǂݎ��̈�̍�������x���W
                0,                      // �ǂݎ��̈�̍�������y���W
                windowWidth,             // �ǂݎ��̈�̕�
                windowHeight,            // �ǂݎ��̈�̍���
                GL2.GL_BGRA,            // �擾�������F���̌`��
                GL2.GL_UNSIGNED_BYTE,   // �ǂݎ�����f�[�^��ۑ�����z��̌^
                (Buffer) byteBuffer     // �r�b�g�}�b�v�̃s�N�Z���f�[�^�i���ۂɂ̓o�C�g�z��j�ւ̃|�C���^
        );
      
        // glReadBuffer�Ŏ擾�����f�[�^(ByteBuffer)��DataBuffer�ɕϊ�����
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
	 * �����������ꂽ���\�b�h�i���̂܂܂ɂ��Ă���
	 */
	public void dispose(GLAutoDrawable arg0) {
		// �󗓂̂܂܂ɂ��Ă���
	}
}
