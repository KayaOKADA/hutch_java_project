package ocha.itolab.hutch.core.data;

import java.nio.DoubleBuffer;
import java.util.*;

import ocha.itolab.hutch.applet.pathviewer.ViewingPanel;

import java.awt.Color;


public class BlockSet {
	ArrayList<OneBlock> blocklist = new ArrayList<OneBlock>();
	DataSet ds = null;
	static Statistics statistics[][] = new Statistics[24][72]; //本当は72はblocklist.size()<-staticじゃない
	
	public static int AGGREGATE_ALL = 1;
	public static int AGGREGATE_MALE = 2;
	public static int AGGREGATE_FEMALE = 3;
	public static int AGGREGATE_UNDER15 = 4;
	public static int AGGREGATE_BETWEEN1550 = 5;
	public static int AGGREGATE_OVER50 = 6;
	public static int AGGREGATE_GENDER_RATIO = 7;
	public static int AGGREGATE_INCREASE_DECREASE = 8;
	public static int AGGREGATE_STATISTICS = 9;
	public static int aggregate = AGGREGATE_ALL;
	
	double STOP_VELOCITY = 1.0e-6;
	
	int maxnum[] = new int[24];
	int minnum[] = new int[24];
	public static int maxnum_statistics = 0, minnum_statistics = 0;
	public static int maxnum_statistics_array[] = new int[72];
	public static int minnum_statistics_array[] = new int[72];
	public static int maxnum_statistics_hour[] = new int[24];
	public static int minnum_statistics_hour[] = new int[24];
	public static int maxnum_cid[] = new int[24];
	public static int minnum_cid[] = new int[24];
	int maxstop = 0, minstop = 0, maxpass = 0, minpass = 0;
	int maxnum_array[] = new int[72]; //本当はblocklist.size()
	int minnum_array[] = new int[72]; //本当はblocklist.size()
	int minhour = 0, maxhour = 24;
	String age = "", gender = "";
	double maxvel = 0.0, minratio = 0.0, maxratio = 0.0;
	int male_counter = 0;
	static double transparency = 0.5;
	static int looktime = 0;
	public static PathSegment segment[][];
	static double average = 0.0;
	static double average_id[] = new double[72];
	static double average_num = 0.0;
	static double variance = 0.0;
	static double transparency_a = 0.0;
	static double transparency_b = 0.0;
	static double transparency_c = 0.0;
	static double transparency_max = 0.0;
	static double sigma = 1.0;
	static double transparency_slide = 0.005;
	static int t_flag0 = 0;
	static int t_flag1 = 0;
	static int t_flag2 = 0;
	
	public BlockSet(DataSet ds) {
		this.ds = ds;
	}
	
    public static Color setPanelcolormap(int hour){
    	Color colormap = Color.WHITE;
    	double value;
    	double value1,value2;
    	if(aggregate == AGGREGATE_STATISTICS){
			if(maxnum_statistics_hour[hour] < 170){
				value = (double)(maxnum_statistics_hour[hour] - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
			}else{
				value = 1.0;
			}
			double hue = (1.0 - value) * 160.0 / 240.0;
			double intensity = 0.5 + 0.5 * value;
			colormap = Color.getHSBColor((float)hue, 1.0f, (float)intensity);
    	}else{
    		value1 = ((double)(statistics[hour][maxnum_cid[hour]].num - minnum_statistics_array[maxnum_cid[hour]]) / (double)(maxnum_statistics_array[maxnum_cid[hour]] - minnum_statistics_array[maxnum_cid[hour]]))-average_id[maxnum_cid[hour]];
    		value2 = ((double)(statistics[hour][minnum_cid[hour]].num - minnum_statistics_array[minnum_cid[hour]]) / (double)(maxnum_statistics_array[minnum_cid[hour]] - minnum_statistics_array[minnum_cid[hour]]))-average_id[minnum_cid[hour]];
    		double s1 = 0.0, s2 =0.0, s;
    		s1 = Math.abs(value1)/(1.0-average_id[maxnum_cid[hour]]);
    		s2 = Math.abs(value2)/(1.0-average_id[minnum_cid[hour]]);
    		if(s1>s2){
    			colormap = Color.getHSBColor(0.0f, (float)s1, 1.0f);
    		}else{
    			colormap = Color.getHSBColor(0.66f, (float)s2, 1.0f);
    		}
    	}
		return colormap;
    }
	
	class OneBlock {
		double positions[][];
	}
	
	class Statistics {
		int num = 0;
		int numstop;
		int male_num = 0;
		int female_num;
		ArrayList<PathSegment> segments = new ArrayList();
		ArrayList<PathSegment> male_segments = new ArrayList();
	}
	
	/**
	 * Add one block
	 */
	public void addOneBlock(double positions[][]) {
		OneBlock ob = new OneBlock();
		blocklist.add(ob);
		ob.positions = positions;
		/*System.out.println(positions[0][0]);
		System.out.println(positions[0][1]);
		System.out.println(positions[1][0]);
		System.out.println(positions[1][1]);*/
	}
	
	/**
	 * Get number of blocks
	 */
	public int getNumBlocks() {
		return blocklist.size();
	}

	/**
	 * Get one block
	 */
	public double[][] getOneBlock(int id) {
		OneBlock ob = blocklist.get(id);
		return ob.positions;
	}
	
	public void setTransparency(double t, int s) {
		if(s == 0){
			transparency_a = (double)t/10.0;
			t_flag0 = 1;
		}else if(s == 1){
			transparency_b = (double)t/10.0;
			t_flag1 = 1;
		}else if(s == 2){
			transparency_c = (double)t/10.0;
			t_flag2 = 1;
		}
    }
	
	public double getTransparency() {
		return transparency;
	}
	
	public void setLooktime(int t){
		looktime = t;
	}
	
	public int getLooktime() {
		return looktime;
	}

	int count = 0;
	public void aggregate() {
		for(int i = 0; i < blocklist.size(); i++){
			statistics[minhour][i] = new Statistics();
		}
		
		int i=0;
		for(i = 0; i < ds.getNumLineString(); i++) {
			OneLineString ols = ds.getOneLineString(i);
			boolean isNeeded = false;
			
			double x0 = ols.getOriginalX(0);
			double y0 = ols.getOriginalY(0);
			String age = ols.getOriginalAge(0);
			String gender = ols.getOriginalGender(0);
			int hour = ols.getOriginalHour(0);
			Date date = ols.getOriginalDate(0);
			//System.out.println(date);
			
			if(shouldDisplayed(x0, y0, hour, age, gender) == false)
				continue;
			isNeeded = true;
			
			int cid = specifyEnclosingBlock(x0, y0);
			ols.setBlockId(0, cid);
			
			//ここで超減ってる、1/3くらいになってる
			if(cid < 0) continue;
			count++;
			
			PathSegment seg = new PathSegment();
			seg.ols = ols;
			seg.cx = x0;
			seg.cy = y0;
			seg.age = age;
			seg.gender = gender;
			if(gender.equals("male")) statistics[minhour][cid].male_segments.add(seg);
			
			statistics[minhour][cid].segments.add(seg);
			ols.setDisplayFlag(isNeeded);
		}
		count=0;
		
		maxnum[minhour] = 0;
		minnum[minhour] = 1000000;
		for(int k = 0; k < blocklist.size(); k++) {
			Statistics st = statistics[minhour][k];
			if(st.segments.size() > 0) {
				st.num = st.segments.size();
				st.male_num = st.male_segments.size();
				if(minnum[minhour] > st.num) minnum[minhour] = st.num;
				if(maxnum[minhour] < st.num) maxnum[minhour] = st.num;
			}
		}
	}
	
	public void aggregate_all() {
		for(int j = 0; j < 24; j++){
			for(int i = 0; i < blocklist.size(); i++){
				statistics[j][i] = new Statistics();
			}
			maxnum_statistics_hour[j] = 0;
			minnum_statistics_hour[j] = 0;
		}
		setMinHour(0);
		setMaxHour(23);

		int i=0;
		for(i = 0; i < ds.getNumLineString(); i++) {
			OneLineString ols = ds.getOneLineString(i);
			boolean isNeeded = false;
			
			double x0 = ols.getOriginalX(0);
			double y0 = ols.getOriginalY(0);
			String age = ols.getOriginalAge(0);
			String gender = ols.getOriginalGender(0);
			int hour = ols.getOriginalHour(0);
			if(shouldDisplayed(x0, y0, hour, age, gender) == false)
				continue;
			isNeeded = true;
			
			int cid = specifyEnclosingBlock(x0, y0);
			ols.setBlockId(0, cid);
			
			//ここで超減ってる、1/3くらいになってる
			if(cid < 0) continue;
			count++;
			
			PathSegment seg = new PathSegment();
			seg.ols = ols;
			seg.cx = x0;
			seg.cy = y0;
			seg.age = age;
			seg.gender = gender;
			if(gender.equals("male")) statistics[hour][cid].male_segments.add(seg);
			
			statistics[hour][cid].segments.add(seg);
			ols.setDisplayFlag(isNeeded);
		}
		count=0;
		
		if((aggregate == AGGREGATE_STATISTICS) || (aggregate == AGGREGATE_INCREASE_DECREASE) || (aggregate == AGGREGATE_MALE) || (aggregate == AGGREGATE_FEMALE)){
			for(int h=0;h<24;h++){
				maxnum[h] = 0;
				minnum[h] = 1000000;
			}
			for(int h=0;h<72;h++){
				maxnum_statistics_array[h] = 0;
				minnum_statistics_array[h] = 1000000;
			}
			for(int l = 0; l < 24; l++){
				for(int k = 0; k < blocklist.size(); k++) {
					Statistics st = statistics[l][k];
					if(st.segments.size() > 0) {
						st.num = st.segments.size();
						st.male_num = st.male_segments.size();
						if(aggregate == AGGREGATE_MALE){
							if(minnum_statistics > st.male_num) minnum_statistics = st.male_num;
							if(st.male_num<170){//outlier対策
								if(maxnum_statistics < st.male_num) maxnum_statistics = st.male_num;
							}
						}else if(aggregate == AGGREGATE_FEMALE){
							if(minnum_statistics > (st.num-st.male_num)) minnum_statistics = (st.num-st.male_num);
							if((st.num-st.male_num)<170){//outlier対策
								if(maxnum_statistics < (st.num-st.male_num)) maxnum_statistics = (st.num-st.male_num);
							}
						}else{
							if(minnum_statistics > st.num) minnum_statistics = st.num;
							if(st.num<170){//outlier対策
								if(maxnum_statistics < st.num) maxnum_statistics = st.num;
							}
							if(minnum_statistics_array[k] > st.num) minnum_statistics_array[k] = st.num;
							if(maxnum_statistics_array[k] < st.num) maxnum_statistics_array[k] = st.num;
							if(minnum_statistics_hour[l] > st.num){
								minnum_statistics_hour[l] = st.num;
								minnum_cid[l] = k;
							}
							if(maxnum_statistics_hour[l] < st.num){
								maxnum_statistics_hour[l] = st.num;
								maxnum_cid[l] = k;
							}
							//System.out.println(st.num);
						}
					}
				}
			}
			if(aggregate == AGGREGATE_STATISTICS){
				calcDeviation(statistics, minnum_statistics, maxnum_statistics);
			}else if(aggregate == AGGREGATE_INCREASE_DECREASE){
				calcDeviation_in_de(statistics, minnum_statistics_array, maxnum_statistics_array);
			}else if((aggregate == AGGREGATE_MALE)||(aggregate == AGGREGATE_FEMALE)){
				calcDeviation_mf(statistics, minnum_statistics, maxnum_statistics);
			}
		}
		/*else if(aggregate == AGGREGATE_INCREASE_DECREASE){//間違えて店舗ごとの統計やってしまった
			for(int k=0;k<72;k++){
				maxnum_array[k] = 0;
				minnum_array[k] = 1000000;
			}
			
			for(int l = 0; l < 24; l++){
				for(int k = 0; k < blocklist.size(); k++) {
					Statistics st = statistics[l][k];
					if(st.segments.size() > 0) {
						st.num = st.segments.size();
						st.male_num = st.male_segments.size();
						if(minnum_array[k] > st.num) minnum_array[k] = st.num;
						if(maxnum_array[k] < st.num) maxnum_array[k] = st.num;
						if(minnum_statistics_array[l] > st.num) minnum_statistics_array[l] = st.num;
						if(maxnum_statistics_array[l] < st.num) maxnum_statistics_array[l] = st.num;
					}
				}
			}
			for(int l = 0; l < 72; l++){
				if(minnum_array[l]==1000000) minnum_array[l] = 1;
				if(maxnum_array[l]==0) maxnum_array[l] = 1;
				System.out.println("max:"+maxnum_array[l]);
				System.out.println("min:"+minnum_array[l]);
			}
		}*/
		
	}
	
	public Color getBlockColorWithPopulation(int cid) {
		double value = 0.0;
		double hue, intensity;
		Color color;
		
		value = (double)(statistics[minhour][cid].num - minnum[minhour]) / (double)(maxnum[minhour] - minnum[minhour]);
		if(aggregate == AGGREGATE_STATISTICS){
			if(statistics[minhour][cid].num >170){
				value = 1.0;
			}else{
				value = (double)(statistics[minhour][cid].num - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
			}
		}
		if(aggregate == AGGREGATE_MALE){
			value = (double)(statistics[minhour][cid].male_num - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
		}else if(aggregate == AGGREGATE_FEMALE){
			value = (double)((statistics[minhour][cid].num - statistics[minhour][cid].male_num) - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
		}
		if(aggregate ==  AGGREGATE_GENDER_RATIO){
			value = (double)statistics[minhour][cid].male_num/(double)statistics[minhour][cid].num;
		}
		if(aggregate == AGGREGATE_INCREASE_DECREASE){
			value = ((double)(statistics[minhour][cid].num - minnum_statistics_array[cid]) / (double)(maxnum_statistics_array[cid] - minnum_statistics_array[cid]))-average_id[cid];
		}
		
		//if(value < 1.0e-4) return Color.WHITE;
		if(value == 0.0) return Color.BLACK;
		
		if(aggregate ==  AGGREGATE_GENDER_RATIO){
			if(Double.isNaN(value)){
				color = Color.black;
			}else if(value<0.5){
				hue = 0;
				intensity = 0.1 + (0.9/0.5)*(0.5-value);
				color = Color.getHSBColor((float)hue, (float)intensity, 0.6f);
			}else{
				hue = 0.66;
				intensity = 0.1 + (0.9/0.5)*(value-0.5);
				color = Color.getHSBColor((float)hue, (float)intensity, 0.6f);
			}
		}else if(aggregate == AGGREGATE_INCREASE_DECREASE){
			double s = 0.0;
			if(value<0.0){
				s = Math.abs(value)/average_id[cid];
				if(s>=1.0){
					//color = Color.getHSBColor(0.15f, 1.0f, 1.0f);
					color = Color.BLUE;
				}else{
					color = Color.getHSBColor(0.66f, (float)s, 1.0f);
				}
			}else{
				s = Math.abs(value)/(1.0-average_id[cid]);
				if(s>1.0){
					s = 1.0;
				}
				color = Color.getHSBColor(0.0f, (float)s, 1.0f);
			}
		}else{
			hue = (1.0 - value) * 160.0 / 240.0;
			intensity = 0.5 + 0.5 * value;
			color = Color.getHSBColor((float)hue, 1.0f, (float)1.0f);
		}
		return color;
	}
	
	public double getTransparencyWithPopulation(int cid){
		double t = 1.0;
		double value = 0.0;
		double gamma = 0.5;
		t = Math.pow((value/1),(1/gamma));
		if(Double.isNaN(value)) return 0.0;
		
		if(aggregate ==  AGGREGATE_GENDER_RATIO){
			
			value = (double)statistics[minhour][cid].male_num/(double)statistics[minhour][cid].num;
			if(value>0.5) t = Math.pow(((value-0.5)/0.5),(1/gamma));
			else t = Math.pow(((value-0.5)/0.5),(1/gamma));
			
		}else if(aggregate == AGGREGATE_STATISTICS){
			value = (double)(statistics[minhour][cid].num - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
			t = Math.pow(value, transparency_a);
		}else if(aggregate == AGGREGATE_MALE){
			value = (double)(statistics[minhour][cid].male_num - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
			t = Math.pow(value, transparency_a);
		}else if(aggregate == AGGREGATE_FEMALE){
			value = (double)((statistics[minhour][cid].num - statistics[minhour][cid].male_num) - minnum_statistics) / (double)(maxnum_statistics - minnum_statistics);
			t = Math.pow(value, transparency_a);
		}else if(aggregate == AGGREGATE_INCREASE_DECREASE){
			double s =0.0;
			value = ((double)(statistics[minhour][cid].num - minnum_statistics_array[cid]) / (double)(maxnum_statistics_array[cid] - minnum_statistics_array[cid]))-average_id[cid];		
			if(value>0.0){
				s = Math.abs(value)/(1.0 - average_id[cid]);
				t = Math.pow(s, transparency_b);
			}else{
				s = Math.abs(value)/average_id[cid];
				t = Math.pow(s, transparency_c);
				
				if(t==1.0) System.out.println(s);
			}
		}else{
			value = (double)(statistics[minhour][cid].num - minnum[minhour]) / (double)(maxnum[minhour] - minnum[minhour]);
			t=Math.pow((value/0.5),(1/gamma))/4.0;
		}
		return t;
	}
	
	public void setMinHour(int h) {
		minhour = h;
	}
	
	public void setMaxHour(int h) {
		maxhour = h;
	}
	
	public void setAggregateFlag(int f) {
		aggregate = f;
		if(aggregate == AGGREGATE_STATISTICS){
			aggregate_all();
			Color colormap[] = new Color[24];
			for(int j=0;j<24;j++){
				colormap[j] = setPanelcolormap(j);
			}
			ViewingPanel.setTimepanel(colormap);
		}else if(aggregate == AGGREGATE_INCREASE_DECREASE){
			aggregate_all();
			Color colormap[] = new Color[24];
			for(int j=0;j<24;j++){
				colormap[j] = setPanelcolormap(j);
			}
			ViewingPanel.setTimepanel(colormap);
		}else{
			aggregate();
		}
	}
	
	
	public ArrayList getLocalSegments(int id) {
		return statistics[minhour][id].segments;
	}
	
	
	boolean shouldDisplayed(double x0, double y0, int hour, String age, String gender) {
		if(hour < minhour) return false;
		if(hour > maxhour) return false;
		if(aggregate == AGGREGATE_MALE){
			if(gender.equals("female")) return false;
		}
		if(aggregate == AGGREGATE_FEMALE){
			if(gender.equals("male")) return false;
		}
		if(aggregate == AGGREGATE_UNDER15){
			if(!(age.equals("0-15"))) return false;
		}
		if(aggregate == AGGREGATE_BETWEEN1550){
			if(!(age.equals("15-50"))) return false;
		}
		if(aggregate == AGGREGATE_OVER50){
			if(!(age.equals("50-"))) return false;
		}
		return true;
	}
	

	
	public int specifyEnclosingBlock(double xc, double yc) {
		
		for(int i = 0; i < blocklist.size(); i++) {
			double[][] pos = getOneBlock(i);
			
			boolean flag1 = false, flag2 = false;
			for(int j = 0; j < pos.length; j++) {
				int j2 = (j == 0) ? (pos.length - 1) : (j - 1);
				int ret = whichSide(xc, yc, pos[j], pos[j2]);
				if(ret > 0) flag1 = true;
				if(ret < 0) flag2 = true;
				if(flag1 == true && flag2 == true) break;
			}
			if(flag1 == false || flag2 == false)
				return i;
			
		}
		
		return -1;
	}
	
	public void calcDeviation(Statistics[][] st, int minnum, int maxnum){
		int l = 0, k = 0;
		int count = 0;
		double var_two = 0.0;
		double kari_num = 0;
		
		for(l = 0; l < 24; l++){
			if((l<=10) || (l>=19)){
				for(k = 0; k < blocklist.size(); k++) {
					average += (st[l][k].segments.size()-(double)minnum) / ((double)maxnum - (double)minnum);
					count++;
				}
			}
		}
		average = average / (double)count;
		for(l = 0; l < 23; l++){
			if((l<=10) || (l>=19)){
				for(k = 0; k < blocklist.size(); k++) {
					kari_num = (st[l][k].segments.size()-(double)minnum) / ((double)maxnum - (double)minnum);
					var_two += (kari_num - average)*(kari_num - average);
				}
			}
		}
		var_two = var_two / (double)count;
		variance = Math.sqrt(var_two);
		if(t_flag0==0){
			transparency_a = Math.log(transparency_slide)/Math.log(average + variance*sigma);
		}
		//transparency_max = Math.pow(transparency_a, (-1.0+2.0*(average + variance*sigma)));
	}
	
	public void calcDeviation_in_de(Statistics[][] st, int minnum[], int maxnum[]){
		int l = 0, k = 0;
		int count = 0;
		double var_two = 0.0;
		double kari_num = 0;
		
		for(k = 0; k < blocklist.size(); k++){
			average_id[k]=0;
		}
		
		for(k = 0; k < blocklist.size(); k++){
			for(l = 0; l < 24; l++){
				if((l<=10) || (l>=19)){
					average_id[k] += (st[l][k].segments.size()-(double)minnum[k]) / ((double)maxnum[k] - (double)minnum[k]);
				}
			}
			average_id[k] = average_id[k] / 16.0;
			
		}
		
		/*
		for(l = 0; l < 23; l++){
			if((l<=10) || (l>=19)){
				for(k = 0; k < blocklist.size(); k++) {
					kari_num = (st[l][k].segments.size()-(double)minnum) / ((double)maxnum - (double)minnum);
					var_two += (kari_num - average)*(kari_num - average);
				}
			}
		}
		var_two = var_two / (double)count;
		variance = Math.sqrt(var_two);*/
		if(t_flag1==0) transparency_b = 4.0;
		if(t_flag2==0) transparency_c = 30.0;
	}
	
	public void calcDeviation_mf(Statistics[][] st, int minnum, int maxnum){
		int l = 0, k = 0;
		int count = 0;
		double var_two = 0.0;
		double kari_num = 0;
		average = 0;
		
		for(l = 0; l < 24; l++){
			if((l<=10) || (l>=19)){
				for(k = 0; k < blocklist.size(); k++) {
					if(aggregate == AGGREGATE_MALE){
						average += (st[l][k].male_segments.size()-(double)minnum) / ((double)maxnum - (double)minnum);
						count++;
					}else{
						average += ((st[l][k].segments.size()-st[l][k].male_segments.size())-(double)minnum) / ((double)maxnum - (double)minnum);
						count++;
					}
				}
			}
		}
		average = average / (double)count;
		for(l = 0; l < 23; l++){
			if((l<=10) || (l>=19)){
				for(k = 0; k < blocklist.size(); k++) {
					if(aggregate == AGGREGATE_MALE){
						kari_num = (st[l][k].male_segments.size()-(double)minnum) / ((double)maxnum - (double)minnum);
						var_two += (kari_num - average)*(kari_num - average);
					}else{
						kari_num = ((st[l][k].segments.size()-st[l][k].male_segments.size())-(double)minnum) / ((double)maxnum - (double)minnum);
						var_two += (kari_num - average)*(kari_num - average);
					}
				}
			}
		}
		var_two = var_two / (double)count;
		variance = Math.sqrt(var_two);
		if(t_flag0==0){
			transparency_a = Math.log(transparency_slide)/Math.log(average + variance*sigma);
		}
	}
	
	int whichSide(double px, double py, double e1[], double e2[]) {
		double a = (e1[1] - py) * (e2[0] - px);
		double b = (e1[0] - px) * (e2[1] - py);
		if (a > b)
			return -1;
		if (a < b)
			return 1;
		
		return 0;
	}
}
