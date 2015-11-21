package ocha.itolab.hutch.applet.pathviewer;

import java.awt.Color;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import ocha.itolab.hutch.core.data.*;

public class PathDrawer {
	static double intensityRatio = 1.0;
	
	static void setIntensityRatio(double t) {
		intensityRatio = t;
	}
	
	
	/**
	 * ãOê’Çï\é¶Ç∑ÇÈ
	 */
	static void drawPath(GL2 gl2, DataSet ds, int numCluster) {
		gl2.glColor3d(0.5, 0.5, 0.5);
		
		// for each linestring
		for(int i = 0; i < ds.getNumLineString(); i++) {
			OneLineString ols = ds.getOneLineString(i);	
			drawOneLineString(gl2, ols, numCluster, 0.0);
		}
	
	}
	
	
	
	/**
	 * ã«èäÇí âﬂÇ∑ÇÈãOê’Çï\é¶Ç∑ÇÈ
	 */
	static void drawPathLocal(GL2 gl2, DataSet ds, int numCluster, int pickX, int pickY) {
		gl2.glColor3d(0.5, 0.5, 0.5);
		ArrayList localsegs = ds.grid.getLocalSegments(pickX, pickY);
		
		// for each linestring
		for(int i = 0; i < localsegs.size(); i++) {
			PathSegment ps = (PathSegment)localsegs.get(i);
			drawOneLineString(gl2, ps.ols, numCluster, 0.1);
		}
	
	}

	
	/**
	 * 1ñ{ÇÃãOê’Çï\é¶Ç∑ÇÈ
	 */
	static void drawOneLineString(GL2 gl2, OneLineString ols, int numCluster, double z) {
		
		if(numCluster > 1 && ols.getClusterId() >= 0) {
			float hue = (float)ols.getClusterId() / (float)numCluster;
			Color color = Color.getHSBColor(hue, 1.0f, (float)intensityRatio);
			double rr = (double)color.getRed() / 255.0;
			double gg = (double)color.getGreen() / 255.0;
			double bb = (double)color.getBlue() / 255.0;
			gl2.glColor3d(rr, gg, bb);
		}
	
		
		gl2.glBegin(GL2.GL_LINE_STRIP);
		for(int j = 0; j < ols.getNumOriginalPosition(); j++) {
			double x = ols.getOriginalX(j);
			double y = ols.getOriginalY(j);
			//System.out.println( "    " + i + "," + j + "," + x + "," + y);
			gl2.glVertex3d(x, y, z);
		}
		gl2.glEnd();
		
		
		
	}


}
