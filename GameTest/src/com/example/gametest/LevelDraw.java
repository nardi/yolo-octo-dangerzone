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
	private int playerX;
	private float playerY;
	Character character = new Character (70, 50);
	Coin coin = new Coin(200, 200);
	
	public LevelDraw() {
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,100));
		old = new PointF(0 , 0);
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
			old.x = 150;
			playerX = (int) ((view.getWidth()/4.0 * 399.0) / view.getWidth());
			Log.e("Navi", "" + playerX);
		}
		
		paint.setStrokeWidth(10);
		
		if(old.x >= 0 && old.y > 0){
			canvas.drawLine(old.x, old.y, newp.x, newp.y, paint);
		}
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public void drawFloor(PointF newp, Paint brush, Canvas canvas){
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
			playerX = (int) ((view.getWidth()/4.0 * 399.0) / view.getWidth());
		}
		
		if(old.x >= 0 && old.y > 0){
			canvas.drawLine(old.x, old.y, newp.x, newp.y, brush);
		}
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public void fillFloor(PointF newp, Canvas canvas) {
		canvas.drawLine(newp.x, newp.y, newp.x, view.getHeight(), paint);
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
		playerY = translate(buffer[playerX]).y;
		
		//playerY = (float)(view.getHeight() * 2.0/3.0);
		for(int i = 0; i < buffer.length; i++){
			PointF buf = translate(buffer[i]);
			if (i != playerX) {
				drawFloor(buf, canvas);
				fillFloor(buf, canvas);
			}

		}
		old.x = -1;
		old.y = -1;
	}
	
	public float getHeight() {
		return playerY;
	}
	
}
