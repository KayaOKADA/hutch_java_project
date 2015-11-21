package ocha.itolab.hutch.core.data;

import java.util.*;
import java.awt.Color;

public class Grid {
	public static int NUMGX = 100;
	public static int NUMGY = 100;
	public static int NUMDIR = 8;
	
	static double xpos[];
	static double ypos[];
	static ArrayList<PathSegment> segments[]; 
	static Statistics statistics[];
	DataSet ds = null;
	int maxnum = 0, maxnumdir = 0;
	double maxstopness = 0.0;
	

	class Statistics {
		int num;
		int numdir[] = new int[NUMDIR];
		double stopness;
	}
	
	
	public Grid(DataSet ds) {
		this.ds = ds;
		double minmax[] = ds.getMinMax();
		
		xpos = new double[NUMGX + 1];
		ypos = new double[NUMGY + 1];
		for(int i = 0; i <= NUMGX; i++)
			xpos[i] = (double)i * (minmax[1] - minmax[0]) / (double)NUMGX + minmax[0];
		for(int i = 0; i <= NUMGY; i++)
			ypos[i] = (double)i * (minmax[3] - minmax[2]) / (double)NUMGY + minmax[2];
	
		aggregate();
	}
	
	
	public double getX(int id) {
		return xpos[id];
	}
	
	public double getY(int id) {
		return ypos[id];
	}
	
	public double[] getXarray() {
		return xpos;
	}
	
	public double[] getYarray() {
		return ypos;
	}
	
	public ArrayList getLocalSegments(int x, int y) {
		int cid = y * NUMGX + x;
		return segments[cid];
	}
	
	
	public Color getGridColorWithPopulation(int cid) {
		double value = (double)statistics[cid].num / (double)maxnum;
		double hue = (1.0 - value) * 160.0 / 240.0;
		double intensity = 0.5 + 0.5 * value;
		if(value < 1.0e-4) intensity = 0.2;
		Color color = Color.getHSBColor((float)hue, 1.0f, (float)intensity);
		return color;
	}
	
	
	public Color getGridColorWithStopness(int cid) {
		//double value1 = (double)statistics[cid].num / (double)maxnum;
		double value2 = statistics[cid].stopness / maxstopness;
		double hue = (1.0 - value2) * 160.0 / 240.0;
		double intensity = 0.5 + 0.5 * value2;
		if(value2 < 1.0e-4) intensity = 0.2;
		Color color = Color.getHSBColor((float)hue, 1.0f, (float)intensity);
		return color;
	}
	
	
	public Color getGridColorWithDirection(int cid, int dirid) {
		double value1 = (double)statistics[cid].numdir[dirid] / (double)maxnumdir;
		double value2 = (double)(dirid + 1) / (double)NUMDIR;
		double hue = (1.0 - value2) * 160.0 / 240.0;
		double intensity = 0.9 + 0.1 * value1;
		if(value1 < 1.0e-4) intensity = 0.2;
		Color color = Color.getHSBColor((float)hue, (float)intensity, (float)intensity);
		return color;
	}
	
	
	void aggregate() {
		double minmax[] = ds.getMinMax();
		
		// allocate an array corresponding to the set of rectangle subregions
		int sumrect = NUMGX * NUMGY;
		segments = new ArrayList[sumrect];
		statistics = new Statistics[sumrect];
		for(int i = 0; i < sumrect; i++) {
			segments[i] = new ArrayList<PathSegment>();
			statistics[i] = new Statistics();
		}
		
		
		
		// for each path
		for(int i = 0; i < ds.getNumLineString(); i++) {
			OneLineString ols = ds.getOneLineString(i);
			
			// for each pair of original vertices
			for(int j = 1; j < ols.getNumOriginalPosition(); j++) {
				double x0 = ols.getOriginalX(j - 1);
				double y0 = ols.getOriginalY(j - 1);
				double x1 = ols.getOriginalX(j);
				double y1 = ols.getOriginalY(j);
				double xc = (x0 + x1) * 0.5;
				double yc = (y0 + y1) * 0.5;
				
				// determine which rectangle the segment is enclosed
				int xid = (int)((xc - minmax[0]) / (minmax[1] - minmax[0]) * (double)NUMGX);
				if(xid < 0 || xid >= NUMGX) continue;
				int yid = (int)((yc - minmax[2]) / (minmax[3] - minmax[2]) * (double)NUMGY);
				if(yid < 0 || yid >= NUMGY) continue;
				int cid = yid * NUMGX + xid;
				
				//double dx = (minmax[1] - minmax[0]) / (double)NUMGX;
				//System.out.println("  xc=" + xc + " xid=" + xid + " range=(" + minmax[0] + "," + minmax[1] + ") gx0=" + (minmax[0] + xid * dx) + " gx1=" + (minmax[0] + (xid + 1) * dx));
				
				// allocate a segment
				PathSegment seg = new PathSegment();
				seg.ols = ols;
				seg.cx = xc;
				seg.cy = yc;
				seg.dx = x1 - x0;
				seg.dy = y1 - y0;
				seg.velocity = Math.sqrt(seg.dx * seg.dx + seg.dy * seg.dy);
				segments[cid].add(seg);
			}
		}
		
		
		// search for maximum velocity
		double maxvel = 0.0;
		for(int i = 0; i < sumrect; i++) {
			Statistics st = statistics[i];
			
			// for each segment in the rectangle
			for(int j = 0; j < segments[i].size(); j++) {
				PathSegment seg = segments[i].get(j);
				if(maxvel < seg.velocity) maxvel = seg.velocity;
			}
		}
		
		// for each rectangular region
		for(int i = 0; i < sumrect; i++) {
			Statistics st = statistics[i];
			
			// for each segment in the rectangle
			for(int j = 0; j < segments[i].size(); j++) {
				PathSegment seg = segments[i].get(j);
				double stopness = (maxvel - seg.velocity) / maxvel;
				stopness = Math.pow(stopness, 20.0);
				st.stopness += stopness;
				
				if(seg.velocity > 1.0e-2) {
					double anglesin = Math.asin(seg.dy / seg.velocity);
					double anglecos = Math.acos(seg.dx / seg.velocity);
					if(anglesin < 0) 
						anglecos = Math.PI * 2 - anglecos;
					int aid = (int)(NUMDIR * anglecos / (Math.PI * 2));
					if(aid < 0) aid = 0;
					if(aid >= NUMDIR) aid = NUMDIR - 1;
					st.numdir[aid] += 1;
				}
				
			}
			
			if(segments[i].size() > 0) {
				st.num = segments[i].size();
				if(maxnum < st.num) maxnum = st.num;
				if(maxstopness < st.stopness) maxstopness = st.stopness;
				for(int j = 0; j < 8; j++) {
					if(maxnumdir < st.numdir[j])
						maxnumdir = st.numdir[j];
				}
			}
			
		}
		
	}

	
	
	
}
