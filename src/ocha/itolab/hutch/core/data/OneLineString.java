package ocha.itolab.hutch.core.data;

import java.util.*;

public class OneLineString {
	ArrayList<Position> original = new ArrayList();
	ArrayList<Position> divided = new ArrayList();
	int clusterId = -1;
	
	class Position {
		double x, y;
	}
	
	
	
	public void setClusterId(int id) {
		clusterId = id;
	}
	
	public int getClusterId() {
		return clusterId;
	}
	
	public void addOneOriginalPosition(double x, double y) {
		Position p = new Position();
		p.x = x;
		p.y = y;
		original.add(p);
	}
	
	public int getNumOriginalPosition() {
		return original.size();
	}
	
	public double getOriginalX(int id) {
		Position p = (Position)original.get(id);
		return p.x;
	}
	
	public double getOriginalY(int id) {
		Position p = (Position)original.get(id);
		return p.y;
	}
	
	public int getNumDividedPosition() {
		return divided.size();
	}
	
	public double getDividedX(int id) {
		Position p = (Position)divided.get(id);
		return p.x;
	}
	
	public double getDividedY(int id) {
		Position p = (Position)divided.get(id);
		return p.y;
	}
	
	
	public void setDivision(int numdiv) {
		if(numdiv <= 1) return;
		divided.clear();
		double interval = (double)(original.size() - 1) / (double)(numdiv - 1);
		
		double current = 0.0;
		for(int i = 0; i < numdiv; i++) {
			int id1 = (int)current;
			if(id1 < 0) id1 = 0;
			if(id1 >= original.size() - 1) id1 = original.size() - 2;
			int id2 = id1 + 1;
			double ratio = current - (double)id1;
			Position opos1 = original.get(id1);
			Position opos2 = original.get(id2);
			Position dpos = new Position();
			dpos.x = (1.0 - ratio) * opos1.x + ratio * opos2.x;
			dpos.y = (1.0 - ratio) * opos1.y + ratio * opos2.y;
			divided.add(dpos);
			current += interval;
		}
		
	}
	
	
}
