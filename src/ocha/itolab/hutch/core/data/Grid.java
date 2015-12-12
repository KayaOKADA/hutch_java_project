package ocha.itolab.hutch.core.data;

import java.util.*;

import javax.print.attribute.standard.RequestingUserName;
import javax.xml.transform.OutputKeys;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.algorithms.implementations.IntegrityHmac;

import java.awt.Color;

public class Grid {
	public static int NUMGX = 100;
	public static int NUMGY = 100;
	public static int NUMDIR = 8;
	public static int AGGREGATE_ALL = 1;
	public static int AGGREGATE_STOP = 2;
	public static int AGGREGATE_PASS = 3;
	public static int AGGREGATE_RATIO = 4;
	
	double STOP_VELOCITY = 1.0e-6;
	
	static double xpos[];
	static double ypos[];
	static ArrayList<PathSegment> segments[]; 
	static Statistics statistics[];
	DataSet ds = null;
	int maxnum = 0, maxstop = 0, maxnumdir = 0, minhour = 0, maxhour = 24;
	double maxvel = 0.0;
	
	int aggregate = AGGREGATE_ALL;
	
	
	class Statistics {
		int num;
		int numstop;
		int numdir[] = new int[NUMDIR];
	}
	
	
	public Grid(DataSet ds) {
		this.ds = ds;
		init();
	}
	
	
	void init() {
		maxnum = 0; maxstop = 0; maxnumdir = 0;
				
		double minmax[] = ds.getMinMax();
		
		xpos = new double[NUMGX + 1];
		ypos = new double[NUMGY + 1];
		for(int i = 0; i <= NUMGX; i++)
			xpos[i] = (double)i * (minmax[1] - minmax[0]) / (double)NUMGX + minmax[0];
		for(int i = 0; i <= NUMGY; i++)
			ypos[i] = (double)i * (minmax[3] - minmax[2]) / (double)NUMGY + minmax[2];
	
		aggregate();
	}
	
	
	public void setGridResolution(int res) {
		NUMGX = NUMGY = res;
		init();
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
		double value = 0.0;
		
		if(aggregate == AGGREGATE_ALL)
			value = (double)statistics[cid].num / (double)maxnum;
		if(aggregate == AGGREGATE_STOP)
			value = (double)statistics[cid].numstop / (double)maxstop;
		if(aggregate == AGGREGATE_PASS)
			value = (double)(statistics[cid].num - statistics[cid].numstop) / (double)maxnum;
		if(aggregate == AGGREGATE_RATIO)
			value = (double)statistics[cid].numstop / (double)statistics[cid].num;
			
		if(value < 1.0e-4) return Color.BLACK;
		double hue = (1.0 - value) * 160.0 / 240.0;
		double intensity = 0.5 + 0.5 * value;
		Color color = Color.getHSBColor((float)hue, 1.0f, (float)intensity);
		return color;
	}
	
	
	/*
	public Color getGridColorWithStopness(int cid) {
		//double value1 = (double)statistics[cid].num / (double)maxnum;
		double value2 = statistics[cid].stopness / maxstopness;
		double hue = (1.0 - value2) * 160.0 / 240.0;
		double intensity = 0.5 + 0.5 * value2;
		if(value2 < 1.0e-4) return Color.BLACK;
		Color color = Color.getHSBColor((float)hue, 1.0f, (float)intensity);
		return color;
	}
	*/
	
	
	public Color getGridColorWithDirection(int cid, int dirid) {
		double value1 = (double)statistics[cid].numdir[dirid] / (double)maxnumdir;
		double value2 = (double)(dirid + 1) / (double)NUMDIR;
		double hue = (1.0 - value2) * 160.0 / 240.0;
		double intensity = 0.9 + 0.1 * value1;
		if(value1 < 1.0e-4) intensity = 0.2;
		Color color = Color.getHSBColor((float)hue, (float)intensity, (float)intensity);
		return color;
	}
	
	
	public void aggregate() {
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
				int hour = ols.getOriginalHour(j);
				
				if(shouldDisplayed(x0, y0, x1, y1, hour) == false)
					continue;
				
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
				if(seg.velocity < STOP_VELOCITY)
					(statistics[cid].numstop)++;
			}
		}
		
		
		// search for maximum velocity
		maxvel = 0.0;
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
				if(maxstop < statistics[i].numstop)
					maxstop = statistics[i].numstop;
				for(int j = 0; j < 8; j++) {
					if(maxnumdir < st.numdir[j])
						maxnumdir = st.numdir[j];
				}
			}
			
		}
		
	}

	
	public void setMinHour(int h) {
		minhour = h;
	}
	
	public void setMaxHour(int h) {
		maxhour = h;
	}
	
	public void setAggregateFlag(int f) {
		aggregate = f;
		aggregate();
	}
	
	
	boolean shouldDisplayed(double x0, double y0, double x1, double y1, int hour) {
		if(hour < minhour) return false;
		if(hour > maxhour) return false;
		if(aggregate == AGGREGATE_STOP) {
			double dist = (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
			dist = Math.sqrt(dist);
			if(dist > maxvel * 0.1) return false;
		}
		if(aggregate == AGGREGATE_PASS) {
			double dist = (x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1);
			dist = Math.sqrt(dist);
			if(dist < maxvel * 0.3) return false;
		}
		
		return true;
	}
	
	
}
