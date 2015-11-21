package ocha.itolab.hutch.applet.pathviewer;

import javax.media.opengl.GL2;

import ocha.itolab.hutch.core.data.*;


/**
 * 描画の視点操作（拡大縮小、回転、平行移動）のパラメータを管理するクラス
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
	 * 視点パラメータをリセットする
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
	 * マウスボタンが押されたモードを設定する
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
	 * マウスのドラッグ操作に応じてパラメータを制御する
	 * @param x マウスポインタのx座標値
	 * @param y マウスポインタのy座標値
	 * @param width 画面領域の幅
	 * @param height 画面領域の高さ
	 * @param dragMode ドラッグモード（1:ZOOM, 2:SHIFT, 3:ROTATE）
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
	 * 視点パラメータを初期化する
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
	 * DataSetをセットする
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
	 * 表示の拡大度を返す
	 * @return 表示の拡大度
	 */
	public double getViewScale() {
		return viewScale;
	}
	
	
	/**
	 * 表示の拡大度をセットする
	 * @param v 表示の拡大度
	 */
	public void setViewScale(double v) {
		viewScale = v;
	}
	

	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateX() {
		return Xrotate;
	}
	
	/**
	 * 表示の回転角度を返す
	 * @return 表示の回転角度
	 */
	public double getViewRotateY() {
		return Yrotate;
	}
	
	
	/**
	 * Treeのサイズ値を返す
	 * @return Treeのサイズ値
	 */
	public double getSize() {
		return size;
	}
	
	/**
	 * Treeのサイズ値をセットする
	 * @param t Treeのサイズ値
	 */
	public void setSize(double t) {
		size = t;
	}

	/**
	 * counterの中心座標値を返す
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 * @return 中心座標値
	 */
	public double getCenter(int i) {
		return center[i];
	}
	
	/**
	 * counterの中心座標値をセットする
	 * @param g 中心座標値
	 * @param i 座標軸(1:X, 2:Y, 3:Z)
	 */
	public void setCenter(double g, int i) {
		center[i] = g;
	}

	/**
	 * 視点の回転の行列値を返す
	 * @param i 行列中の要素の位置
	 * @return 行列値
	 */
	public double getViewRotate(int i) {
		return viewRotate[i];
	}
	
	/**
	 * 視点の回転の行列値をセットする
	 * @param v 行列値
	 * @param i 行列中の要素の位置
	 */
	public void setViewRotate(double v, int i) {
		viewRotate[i] = v;
	}

	/**
	 * 視点の平行移動量を返す
	 * @param i 座標軸 (0:X, 1:Y, 2:Z)
	 * @return 平行移動量
	 */
	public double getViewShift(int i) {
		return viewShift[i];
	}
	
	/**
	 * 視点の平行移動量をセットする
	 * @param v 平行移動量
	 * @param i 座標軸 (1:X, 2:Y, 3:Z)
	 */
	public void setViewShift(double v, int i) {
		viewShift[i] = v;
	}

}
