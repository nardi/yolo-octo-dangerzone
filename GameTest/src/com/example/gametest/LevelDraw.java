package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

public class LevelDraw {
	
	
	public GameCanvas gameCanvas;
	//public Canvas canvas;
	public View view;
	private Paint paint;
	PointF old;
	int y = 50;
	private boolean init = true;
	
	public LevelDraw() {
		//this.gameCanvas = gameCanvas;
		//this.canvas = gameCanvas.getCanvas();
		//this.view = view;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		old = new PointF(0 , 0);
		//standardHeight = view.getHeight() * 2/3;
		//old.y = standardHeight;
		//old.x = 0;
		//old.y = 300;
	}
	/*
	 * Following are the drawing functions for the level. Each function can 
	 * either use the standard paint or the given paint. The drawFloor functions
	 * save the point so each subsequent point will be drawn from the previous.
	 */
	public void drawText(String toWrite, PointF loc, Canvas canvas){
		canvas.drawText(toWrite, loc.x, loc.y, paint);
	}
	
	public void drawText(String toWrite, PointF loc, Paint brush, Canvas canvas){
		canvas.drawText(toWrite, loc.x, loc.y, brush);
	}
	
	public void drawFloor(PointF newp, Canvas canvas){
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
		}
		
		paint.setStrokeWidth(10);
		canvas.drawLine(old.x, old.y, newp.x, newp.y, paint);
		//Log.e("Draw", "From X: " + old.x + " to " + newp.x);
		//Log.e("Draw", "From Y: " + old.y + " to " + newp.y);
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public void drawFloor(PointF newp, Paint brush, Canvas canvas){
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
		}
		
		canvas.drawLine(old.x, old.y, newp.x, newp.y, brush);
		old.x = newp.x;
		old.y = newp.y;
		
		
	}
	
	
	
	/* XXX: DRAW TEST
	 */
	public void drawFloorTest(PointF newp, Canvas canvas, int index){
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
		}
		
		if (old.x == 0) {
			Log.e("XNull", "Old x was 0 at: " + index);
		}
		if (newp.x == 0) {
			Log.e("XNull", "New x was 0 at: " + index);
		}
		paint.setStrokeWidth(10);
		canvas.drawLine(old.x, old.y, newp.x, newp.y, paint);
		old.x = newp.x;
		old.y = newp.y;
		
		
	}
	
	
	public PointF translate(PointF dev){
		if(view != null){
			dev.x = (float)((view.getWidth() /399.0) * dev.x);
			
			dev.y = (float)((view.getHeight() * (2.0/3.0)) - (dev.y * (1.0/5.0) * view.getHeight()));
			return dev;
		}
		Log.e("View", "View == null");
		return dev;
	}
	
	/*
	 * This is a raw version. I am trying to find a good translation method in order to evenly distribute the 400 x points over
	 * the screen and to translate the deviation.	 */
	public void drawFromBuffer(PointF[] buffer, Canvas canvas){
		Log.e("Draw start", ">>>>>>>><<<<<<<<");
		for(int i = 0; i < buffer.length; i++){
			drawFloorTest(translate(buffer[i]), canvas, i);
		}
		Log.e("Draw stop", "---------------------");
	}
	
	
}
