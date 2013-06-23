package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

public class LevelDraw {
	
	public GameCanvas gameCanvas;
	//public Canvas canvas;
	public View view;
	private Paint paint;
	PointF old;
	int y = 50;
	
	public LevelDraw(View view) {
		//this.gameCanvas = gameCanvas;
		//this.canvas = gameCanvas.getCanvas();
		this.view = view;
		paint = new Paint();
		paint.setColor(Color.BLUE);
		old.x = 0;
		old.y = 0;
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
		paint.setStrokeWidth(25);
		canvas.drawLine(old.x, old.y, newp.x, newp.y, paint);
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public void drawFloor(PointF newp, Paint brush, Canvas canvas){
		canvas.drawLine(old.x, old.y, newp.x, newp.y, brush);
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public PointF translate(PointF dev){
		dev.x = (view.getWidth()/399 * dev.x);
		dev.y = ((view.getHeight() * 2/3) - (dev.y * 1/5));
		return dev;
	}
	
	/*
	 * This is a raw version. I am trying to find a good translation method in order to evenly distribute the 400 x points over
	 * the screen and to translate the deviation.
	 */
	public void drawFromBuffer(PointF[] buffer, Canvas canvas){
		for(int i = 0; i < buffer.length; i++){
			drawFloor(translate(buffer[i]), canvas);
		}
	}
	
	

}
