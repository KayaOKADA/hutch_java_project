package ocha.itolab.hutch.core.data;

import java.util.*;

import javax.print.attribute.standard.RequestingUserName;

public class OneLineString2 {
	ArrayList<Position> original = new ArrayList();
	ArrayList<Position> divided = new ArrayList();
	int clusterId = -1;
	boolean isNeeded = true;
	
	class Position {
		double x, y;
		Date date;
		int hour;
		int blockId = -1;
	}
	
	
	
	public void setClusterId(int id) {
		clusterId = id;
	}
	
	public int getClusterId() {
		return clusterId;
	}
	
	public void setDisplayFlag(boolean f) {
		isNeeded = f;
	}
	
	public boolean getDisplayFlag() {
		return isNeeded;
	}
	
	public void addOneOriginalPosition(double x, double y, long time) {
		Position p = new Position();
		p.x = x;
		p.y = y;
		Date date = new Date();
		date.setTime(time);
		p.date = date;
		StringTokenizer token = new StringTokenizer(date.toString());
		token.nextToken();
		token.nextToken();
		token.nextToken();
		StringTokenizer token2 = new StringTokenizer(token.nextToken(), ":");
		p.hour = Integer.parseInt(token2.nextToken());
		//System.out.println(p.hour);
		
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
	
	public Date getOriginalDate(int id) {
		Position p = (Position)original.get(id);
		return p.date;
	}
	
	public int getOriginalHour(int id) {
		Position p = (Position)original.get(id);
		return p.hour;
	}
	
	
	public void setBlockId(int id, int bid) {
		Position p = (Position)original.get(id);
		p.blockId = bid;
	}
	
	public int getBlockId(int id) {
		Position p = (Position)original.get(id);
		return p.blockId;
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