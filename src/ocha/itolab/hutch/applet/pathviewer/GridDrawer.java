package ocha.itolab.hutch.applet.pathviewer;

import javax.media.opengl.GL2;
import java.awt.Color;
import ocha.itolab.hutch.core.data.*;

public class GridDrawer {
	static double intensityRatio = 1.0;
	
	static void setIntensityRatio(double t) {
		intensityRatio = t;
	}
	
	/**
	 * グリッドを表示する
	 */
	static void drawGrid(GL2 gl2, DataSet ds, int displayMode) {
		
		if(displayMode == Canvas.DISPLAY_DIRECTION)
			drawGridDirection(gl2, ds);
		
		else 
			paintGrid(gl2, ds, displayMode);
	
	}
		
		
	
	static void paintGrid(GL2 gl2, DataSet ds, int displayMode) {
		gl2.glColor3d(0.5, 0.5, 0.5);
		Grid grid = ds.grid;
	
		
		// for each rectangle
		int cid = 0;
		for(int i = 0; i < grid.NUMGY; i++) {
			double gy0 = grid.getY(i);
			double gy1 = grid.getY(i + 1);
			for(int j = 0; j < grid.NUMGX; j++, cid++) {
				double gx0 = grid.getX(j);
				double gx1 = grid.getX(j + 1);
				
				Color color = null;
				if(displayMode == Canvas.DISPLAY_POPULATION)
					color = grid.getGridColorWithPopulation(cid);
				if(displayMode == Canvas.DISPLAY_STOPNESS)
					color = grid.getGridColorWithStopness(cid);

				double rr = intensityRatio * (double)color.getRed() / 255.0;
				double gg = intensityRatio * (double)color.getGreen() / 255.0;
				double bb = intensityRatio * (double)color.getBlue() / 255.0;
				gl2.glColor3d(rr, gg, bb);
				gl2.glBegin(GL2.GL_POLYGON);
				gl2.glVertex3d(gx0, gy0, 0.0);
				gl2.glVertex3d(gx0, gy1, 0.0);
				gl2.glVertex3d(gx1, gy1, 0.0);
				gl2.glVertex3d(gx1, gy0, 0.0);
				gl2.glEnd();
			}
		}	

	}


	static void drawGridDirection(GL2 gl2, DataSet ds) {
		gl2.glColor3d(0.5, 0.5, 0.5);
		Grid grid = ds.grid;
	
		// for each rectangle
		int cid = 0;
		for(int i = 0; i < grid.NUMGY; i++) {
			double gy0 = grid.getY(i);
			double gy1 = grid.getY(i + 1);
			double gyc = (gy0 + gy1) * 0.5;
			for(int j = 0; j < grid.NUMGX; j++, cid++) {
				double gx0 = grid.getX(j);
				double gx1 = grid.getX(j + 1);
				double gxc = (gx0 + gx1) * 0.5;
		
				// for each direction
				for(int k = 0; k < Grid.NUMDIR; k++) {
					
					// set the color
					Color color = grid.getGridColorWithDirection(cid, k);
					double rr = (double)color.getRed() / 255.0;
					double gg = (double)color.getGreen() / 255.0;
					double bb = (double)color.getBlue() / 255.0;
					gl2.glColor3d(rr, gg, bb);
					
					// calculate the segment vertex position
					double length = (gxc - gx0) * 0.8;
					double angle = 2.0 * Math.PI * ((double)k + 0.5) / (double)Grid.NUMDIR;
					double ddx = Math.cos(angle);
					double ddy = Math.sin(angle);
					double gx2 = gxc + ddx * length;
					double gy2 = gyc + ddy * length;
					
					// draw a segment
					gl2.glBegin(GL2.GL_LINE_STRIP);
					gl2.glVertex3d(gxc, gyc, 0.0);
					gl2.glVertex3d(gx2, gy2, 0.0);
					gl2.glEnd();
				}
				
			}
		}
		
	}


}
