
package ocha.itolab.hutch.applet.pathviewer;

import com.jogamp.opengl.GL;
//import javax.media.opengl.GL2;
import com.jogamp.opengl.GL2;
import java.awt.Color;
import ocha.itolab.hutch.core.data.*;
import com.jogamp.opengl.util.gl2.GLUT;

public class BlockDrawer {
    static double intensityRatio = 1.0;
    static double transparency = 1.0;
    static int looktime = 12;
    static double thick = 0.2; 
    static int visible = 0;
    static int statistics_flag = 0;
	
    static void setIntensityRatio(double t) {
    	intensityRatio = t;
    }
    
    static void setVisible() {
    	if(visible == 1) visible = 0;
    	else if(visible == 0) visible = 1;
    }
    
    public static void setAggregatestatistics(int t){
    	statistics_flag = t;
    }
    
    static void drawBlock(GL2 gl2, DataSet ds, int displayMode) {
	paintBlock(gl2, ds, displayMode);
    }
	
    static void paintBlock(GL2 gl2, DataSet ds, int displayMode) {
    GLUT glut;
    glut = new GLUT();
	gl2.glColor3d(0.5, 0.5, 0.5);
	BlockSet block = ds.block;
	double Xrotate = 0.0;
	int blocktime = 0;
	
	gl2.glEnable(GL2.GL_BLEND);
	gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	Xrotate = Transformer.getViewRotateX();
	
	// for each rectangle
	int cid = 0;
		for(int k = 0; k < 24; k++){
			if((Xrotate%8 >= 2)&&(Xrotate%8<6)) blocktime = 23-k;
			else blocktime = k;
			if((blocktime>10) && (blocktime<19)) continue;
			if((visible == 1)&&(blocktime!=looktime)) continue;
			
			block.setMinHour(blocktime);
			block.setMaxHour(blocktime);
			if((statistics_flag == 1)&&(k==0)){
				block.aggregate_all();
				block.setMinHour(blocktime);
				block.setMaxHour(blocktime);
			}else if(statistics_flag == 0){
				block.aggregate();
			}
			for(int i = 0; i < block.getNumBlocks(); i++) {
			    double[][] bpos = block.getOneBlock(i);
						
			    Color color = block.getBlockColorWithPopulation(i);
			    transparency = block.getTransparencyWithPopulation(i);
				looktime = block.getLooktime();
			    //transparency = block.getTransparency();
			    if(color == Color.BLACK) continue;
		
			    double rr = intensityRatio * (double)color.getRed() / 255.0;
			    double gg = intensityRatio * (double)color.getGreen() / 255.0;
			    double bb = intensityRatio * (double)color.getBlue() / 255.0;
			    
			  //文字
			    double xx = 0.0, yy = 0.0;
		        for(int j = 0; j < 4; j++) {
		            xx += bpos[j][0];   yy += bpos[j][1];
		        }
		        xx *= 0.25;   yy *= 0.25;
		        gl2.glLineWidth(2.0f);
		        //String name = StringFileWriter.letters[i];
		        if(blocktime == looktime){
		        	String name = String.valueOf(i);
		        	gl2.glColor4d(1.0, 1.0, 1.0, 1.0);
		        	//gl2.glRasterPos3d(xx, yy, (float)looktime*(float)thick);
		        	//glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, name);
		        	
		        	gl2.glColor4d(1.0, 1.0, 1.0, 0.5);
		        	gl2.glBegin(GL2.GL_LINE_LOOP);
			        gl2.glVertex3d(bpos[0][0], bpos[0][1], (float)looktime*(float)thick);
			        gl2.glVertex3d(bpos[1][0], bpos[1][1], (float)looktime*(float)thick);
			        gl2.glVertex3d(bpos[2][0], bpos[2][1], (float)looktime*(float)thick);
			        gl2.glVertex3d(bpos[3][0], bpos[3][1], (float)looktime*(float)thick);
			        gl2.glEnd();
		        }
		        
			    
			    if(bb>1000){
			    }else{
			    	/*if((bb>0.4) &&(transparency<0.1)){
			    		continue;
			    	}else if(bb>0.4){
			    		gl2.glColor4d(rr, gg, bb, transparency);
			    	}else{
			    		gl2.glColor4d(rr, gg, bb,transparency);
			    	}*/
			    	gl2.glColor4d(rr, gg, bb,transparency);
				    double vertex[][] = {
				    		  { bpos[0][0], bpos[0][1], ((float)blocktime)*thick },
				    		  { bpos[1][0], bpos[1][1], ((float)blocktime)*thick },
				    		  { bpos[2][0], bpos[2][1], ((float)blocktime)*thick },
				    		  { bpos[3][0], bpos[3][1], ((float)blocktime)*thick },
				    		  { bpos[0][0], bpos[0][1], ((float)blocktime+1)*thick },
				    		  { bpos[1][0], bpos[1][1], ((float)blocktime+1)*thick },
				    		  { bpos[2][0], bpos[2][1], ((float)blocktime+1)*thick },
				    		  { bpos[3][0], bpos[3][1], ((float)blocktime+1)*thick }
				    		};
				    int face[][] = {//面の定義
				    		  { 0, 1, 2, 3 },
				    		  { 0, 1, 5, 4 },
				    		  { 0, 3, 7, 4 },
				    		  { 3, 2, 6, 7 },
				    		  { 2, 6, 5, 1 },
				    		  { 7, 6, 5, 4 }
				    		};
			        //塗りつぶしブロック
			        /*gl2.glBegin(GL2.GL_POLYGON);
				    gl2.glVertex3d(bpos[0][0], bpos[0][1], 0.0);
				    gl2.glVertex3d(bpos[1][0], bpos[1][1], 0.0);
				    gl2.glVertex3d(bpos[2][0], bpos[2][1], 0.0);
				    gl2.glVertex3d(bpos[3][0], bpos[3][1], 0.0);
				    gl2.glEnd();
				    //枠だけブロック
			        gl2.glBegin(GL2.GL_LINE_LOOP);
			        gl2.glVertex3d(bpos[0][0], bpos[0][1], ((float)k)*0.1);
			        gl2.glVertex3d(bpos[1][0], bpos[1][1], ((float)k)*0.1);
			        gl2.glVertex3d(bpos[2][0], bpos[2][1], ((float)k)*0.1);
			        gl2.glVertex3d(bpos[3][0], bpos[3][1], ((float)k)*0.1);
			        gl2.glEnd();
			        */
			       
			        //立方体
				    if(transparency > 0.02){
				        gl2.glBegin(GL2.GL_QUADS);
				        for (int p = 0; p < 6; ++p) {
				            for (int q = 0; q < 4; ++q) {
				              gl2.glVertex3dv(vertex[face[p][q]], 0);
				            }
				        }
				        gl2.glEnd();
				    }
			        
			        //ブロックまでの線をつける
			        //gl2.glColor4d(1.0, 1.0, 1.0, 1.0);
			        if(transparency > 0.1){
			        	gl2.glColor4d(rr, gg, bb, 1.0);
			        	double center_x = bpos[0][0] + (bpos[2][0]-bpos[0][0])/2;
			        	double center_y = bpos[3][1] + (bpos[0][1]-bpos[3][1])/2;
				        gl2.glBegin(GL2.GL_LINE_LOOP);
				    	gl2.glVertex3f((float)center_x, (float)center_y, 0.0f);
				    	gl2.glVertex3f((float)center_x, (float)center_y, (float)blocktime*(float)thick);
				    	gl2.glEnd();
			        }
			        //目盛をつける
			        gl2.glColor4d(1.0, 1.0, 1.0, 1.0);
			        gl2.glBegin(GL2.GL_LINE_LOOP);
			    	gl2.glVertex3f(12.0f, -10.0f, (float)blocktime*(float)thick);
			    	gl2.glVertex3f(12.5f, -10.0f, (float)blocktime*(float)thick);
			    	gl2.glEnd();
			    	
			    	gl2.glRasterPos3d(12.8f, -10.0f, (float)blocktime*(float)thick);
			    	String num = String.valueOf(blocktime);
			    	if(blocktime == looktime){
			    		glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, num);
			    	}else{
			    		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, num);
			    	}
			    }
			}	
		}
	gl2.glColor4d(1.0, 1.0, 1.0, 1.0);
	gl2.glBegin(GL2.GL_LINE_LOOP);
	//back
	gl2.glVertex3f(-5.0f, -10.0f, 0.0f);
	gl2.glVertex3f(-5.0f, 7.0f, 0.0f);
	gl2.glVertex3f(12.0f, 7.0f, 0.0f);
	gl2.glVertex3f(12.0f, -10.0f, 0.0f);
	gl2.glEnd();
	
	//under
	gl2.glBegin(GL2.GL_LINE_LOOP);
	gl2.glVertex3f(-5.0f, -10.0f, 0.0f);
	gl2.glVertex3f(-5.0f, -10.0f, 6.0f);
	gl2.glVertex3f(12.0f, -10.0f, 6.0f);
	gl2.glVertex3f(12.0f, -10.0f, 0.0f);
	gl2.glEnd();

	//flont
	gl2.glBegin(GL2.GL_LINE_LOOP);
	gl2.glVertex3f(-5.0f, -10.0f, 6.0f);
	gl2.glVertex3f(-5.0f, 7.0f, 6.0f);
	gl2.glVertex3f(12.0f, 7.0f, 6.0f);
	gl2.glVertex3f(12.0f, -10.0f, 6.0f);
	gl2.glEnd();
	
	//top
	gl2.glBegin(GL2.GL_LINE_LOOP);
	gl2.glVertex3f(-5.0f, 7.0f, 0.0f);
	gl2.glVertex3f(-5.0f, 7.0f, 6.0f);
	gl2.glVertex3f(12.0f, 7.0f, 6.0f);
	gl2.glVertex3f(12.0f, 7.0f, 0.0f);
	gl2.glEnd();
	
	//見ている面
	/*gl2.glDisable(GL2.GL_LIGHTING);
	gl2.glColor4d(1.0, 1.0, 1.0, 0.2);
	gl2.glBegin(GL2.GL_QUADS);
	gl2.glVertex3f(-5.0f, -4.0f, (float)looktime*(float)thick);
	gl2.glVertex3f(-5.0f, 13.0f, (float)looktime*(float)thick);
	gl2.glVertex3f(12.0f, 13.0f, (float)looktime*(float)thick);
	gl2.glVertex3f(12.0f, -4.0f, (float)looktime*(float)thick);
	gl2.glEnd();
	gl2.glEnable(GL2.GL_LIGHTING);
	gl2.glDisable(GL2.GL_BLEND);*/
	gl2.glDisable(GL2.GL_LIGHTING);
	Drawer.drawFloor((float)looktime*(float)thick);
	gl2.glDisable(GL2.GL_BLEND);
    }
}
