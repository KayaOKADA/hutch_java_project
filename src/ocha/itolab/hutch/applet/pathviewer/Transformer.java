package ocha.itolab.hutch.applet.pathviewer;

import javax.media.opengl.GL2;

import ocha.itolab.hutch.core.data.*;


/**
 * �`��̎��_����i�g��k���A��]�A���s�ړ��j�̃p�����[�^���Ǘ�����N���X
 * @author itot
 */
public class Transformer {
	double viewShift[] = new double[3];
	double viewRotate[] = new double[16];
	double viewScale;
	double viewShiftBak[] = new double[3];
	double viewScaleBak;
	double Xrotate, Yrotate, XrotateBak, YrotateBak;

	double min[] = new double[3];
	double max[] = new double[3];
	double center[] = new double[3];
	double size;

	double shiftX, shiftZ;
	

	/**
	 * Constructor
	 */
	public Transformer() {
		setDefaultValue();
	}

	/**
	 * ���_�p�����[�^�����Z�b�g����
	 */
	public void viewReset() {
		for (int i = 0; i < 16; i++) {
			if (i % 5 == 0)
				viewRotate[i] = 1.0;
			else
				viewRotate[i] = 0.0;
		}
		viewScale = viewScaleBak = 1.0;
		viewShift[0] = viewShiftBak[0] = 0.0;
		viewShift[1] = viewShiftBak[1] = 0.0;
		viewShift[2] = viewShiftBak[2] = 0.0;
		Xrotate = XrotateBak = 0.0;
		Yrotate = YrotateBak = 0.0;

	}


	
	
	/**
	 * �}�E�X�{�^���������ꂽ���[�h��ݒ肷��
	 */
	public void mousePressed() {
		viewScaleBak = viewScale;
		viewShiftBak[0] = viewShift[0];
		viewShiftBak[1] = viewShift[1];
		viewShiftBak[2] = viewShift[2];
		XrotateBak = Xrotate;
		YrotateBak = Yrotate;
	}

	/**
	 * �}�E�X�̃h���b�O����ɉ����ăp�����[�^�𐧌䂷��
	 * @param x �}�E�X�|�C���^��x���W�l
	 * @param y �}�E�X�|�C���^��y���W�l
	 * @param width ��ʗ̈�̕�
	 * @param height ��ʗ̈�̍���
	 * @param dragMode �h���b�O���[�h�i1:ZOOM, 2:SHIFT, 3:ROTATE�j
	 */
	public void drag(int x, int y, int width, int height, int dragMode, Drawer d) {
		
		if(dragMode == 1) { // ZOOM
			
			if (y > 0) {
				viewScale =
					viewScaleBak * (1 + (double) (2 * y) / (double) height);
			} else {
				viewScale = viewScaleBak * (1 + (double) y / (double) height);
			}
		}
		
		if (dragMode == 2) { // SHIFT
			
			 float diffX = (float)x * 0.1f / width;
             float diffY = (-0.1f) * (float)y / height;
           
            viewShift[0] = viewShiftBak[0] + diffX / viewScale;
 			viewShift[1] = viewShiftBak[1] + diffY / viewScale;
		}
		
		if (dragMode == 3) { // ROTATE
			Xrotate = XrotateBak + (double) x * Math.PI / (double) width;
			Yrotate = YrotateBak + (double) y * Math.PI / (double) height;
			double cosX = Math.cos(Yrotate);
			double sinX = Math.sin(Yrotate);
			double cosY = Math.cos(Xrotate);
			double sinY = Math.sin(Xrotate);

			viewRotate[0] = cosY;
			viewRotate[1] = 0;
			viewRotate[2] = -sinY;
			viewRotate[4] = sinX * sinY;
			viewRotate[5] = cosX;
			viewRotate[6] = sinX * cosY;
			viewRotate[8] = cosX * sinY;
			viewRotate[9] = -sinX;
			viewRotate[10] = cosX * cosY;
		}

	}
	
	
	/**
	 * ���_�p�����[�^������������
	 */
	public void setDefaultValue() {

		viewRotate[0] = 0.8827;
		viewRotate[1] = 0.0;
		viewRotate[2] = -0.4699;
		viewRotate[3] = 0.0;
		viewRotate[4] = -0.2659;
		viewRotate[5] = 0.8244;
		viewRotate[6] = -0.4997;
		viewRotate[7] = 0.0;
		viewRotate[8] = 0.3873;
		viewRotate[9] = 0.5661;
		viewRotate[10] = 0.7277;
		viewRotate[11] = 0.0;
		viewRotate[12] = 0.0;
		viewRotate[13] = 0.0;
		viewRotate[14] = 0.0;
		viewRotate[15] = 1.0;

		viewShift[0] = 0.8819;
		viewShift[1] = -0.2209;
		viewShift[2] = 0.0;
		viewScale = 1.1417;
	}


	/**
	 * DataSet���Z�b�g����
	 */
	public void setDataSet(DataSet ds) {

		double minmax[] = ds.getMinMax();
		min[0] = minmax[0];    min[1] = minmax[2];
		max[0] = minmax[1];    max[1] = minmax[3];
		
		center[0] = (min[0] + max[0]) * 0.5;
		center[1] = (min[1] + max[1]) * 0.5;
		center[2] = (min[2] + max[2]) * 0.5;
		size = max[0] - min[0];
		double tmp = max[1] - min[1];
		if (size < tmp)
			size = tmp;
		size *= 0.5;

		//System.out.println("   center[" + treeCenter[0] + "," + treeCenter[1] + "," + treeCenter[2] + "] size=" + treeSize);
	}
	
	
	/**
	 * �\���̊g��x��Ԃ�
	 * @return �\���̊g��x
	 */
	public double getViewScale() {
		return viewScale;
	}
	
	
	/**
	 * �\���̊g��x���Z�b�g����
	 * @param v �\���̊g��x
	 */
	public void setViewScale(double v) {
		viewScale = v;
	}
	

	/**
	 * �\���̉�]�p�x��Ԃ�
	 * @return �\���̉�]�p�x
	 */
	public double getViewRotateX() {
		return Xrotate;
	}
	
	/**
	 * �\���̉�]�p�x��Ԃ�
	 * @return �\���̉�]�p�x
	 */
	public double getViewRotateY() {
		return Yrotate;
	}
	
	
	/**
	 * Tree�̃T�C�Y�l��Ԃ�
	 * @return Tree�̃T�C�Y�l
	 */
	public double getSize() {
		return size;
	}
	
	/**
	 * Tree�̃T�C�Y�l���Z�b�g����
	 * @param t Tree�̃T�C�Y�l
	 */
	public void setSize(double t) {
		size = t;
	}

	/**
	 * counter�̒��S���W�l��Ԃ�
	 * @param i ���W��(1:X, 2:Y, 3:Z)
	 * @return ���S���W�l
	 */
	public double getCenter(int i) {
		return center[i];
	}
	
	/**
	 * counter�̒��S���W�l���Z�b�g����
	 * @param g ���S���W�l
	 * @param i ���W��(1:X, 2:Y, 3:Z)
	 */
	public void setCenter(double g, int i) {
		center[i] = g;
	}

	/**
	 * ���_�̉�]�̍s��l��Ԃ�
	 * @param i �s�񒆂̗v�f�̈ʒu
	 * @return �s��l
	 */
	public double getViewRotate(int i) {
		return viewRotate[i];
	}
	
	/**
	 * ���_�̉�]�̍s��l���Z�b�g����
	 * @param v �s��l
	 * @param i �s�񒆂̗v�f�̈ʒu
	 */
	public void setViewRotate(double v, int i) {
		viewRotate[i] = v;
	}

	/**
	 * ���_�̕��s�ړ��ʂ�Ԃ�
	 * @param i ���W�� (0:X, 1:Y, 2:Z)
	 * @return ���s�ړ���
	 */
	public double getViewShift(int i) {
		return viewShift[i];
	}
	
	/**
	 * ���_�̕��s�ړ��ʂ��Z�b�g����
	 * @param v ���s�ړ���
	 * @param i ���W�� (1:X, 2:Y, 3:Z)
	 */
	public void setViewShift(double v, int i) {
		viewShift[i] = v;
	}

}
