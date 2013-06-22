package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class LevelDraw {
	
	public GameCanvas gameCanvas;
	public Canvas canvas;
	private Paint paint;
	Point old;
	int y = 50;
	
	public LevelDraw(GameCanvas gameCanvas) {
		this.gameCanvas = gameCanvas;
		this.canvas = gameCanvas.getCanvas();
		paint = new Paint();
		paint.setColor(Color.BLACK);
		old.x = 0;
		old.y = 0;
	}
	
	public void drawText(String toWrite, Point loc){
		canvas.drawText(toWrite, loc.x, loc.y, paint);
	}
	
	public void drawText(String toWrite, Point loc, Paint brush){
		canvas.drawText(toWrite, loc.x, loc.y, brush);
	}
	
	public void drawFloor(Point newp){
		paint.setStrokeWidth(25);
		canvas.drawLine(old.x, old.y, newp.x, newp.y, paint);
		old.x = newp.x;
		old.y = newp.y;
	}
	
	public void drawFloor(Point newp, Paint brush){
		canvas.drawLine(old.x, old.y, newp.x, newp.y, brush);
		old.x = newp.x;
		old.y = newp.y;
	}
	
	

}
