package ocha.itolab.hutch.core.data;

import java.util.*;

public class DataSet {
	ArrayList<OneLineString> linestrings = new ArrayList();
	double minmax[] = new double[4];
	public Grid grid;
	
	public OneLineString addOneLineString() {
		OneLineString ols = new OneLineString();
		linestrings.add(ols);
		return ols;
	}
	
	public int getNumLineString() {
		return linestrings.size();
	}
	
	public OneLineString getOneLineString(int id) {
		return (OneLineString)linestrings.get(id);
	}
	
	
	public void setDivision(int numdiv) {
		if(numdiv <= 1) return;
		for(OneLineString ols : linestrings) 
			ols.setDivision(numdiv);
	}

	
	public double[] getMinMax() {
		return minmax;
	}
	
	
	
	public void postprocess() {
		minmax[0] = minmax[2] =  1.0e+30;
		minmax[1] = minmax[3] = -1.0e+30;
		
		// for each linestring
		for(int i = 0; i < getNumLineString(); i++) {
			OneLineString ols = getOneLineString(i);
			
			// for each position
			for(int j = 0; j < ols.getNumOriginalPosition(); j++) {
				double x = ols.getOriginalX(j);
				double y = ols.getOriginalY(j);
				minmax[0] = (minmax[0] < x) ? minmax[0] : x;
				minmax[2] = (minmax[2] < y) ? minmax[2] : y;
				minmax[1] = (minmax[1] > x) ? minmax[1] : x;
				minmax[3] = (minmax[3] > y) ? minmax[3] : y;
				
			}
		}

		//System.out.println("   minmax x " + minmax[0] + "," + minmax[1] + " y " + minmax[2] + "," + minmax[3]);
		
		grid = new Grid(this);
		grid.aggregate();
	}
}
