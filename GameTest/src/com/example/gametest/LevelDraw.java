package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class LevelDraw {
	
	public GameCanvas gameCanvas;
	public Canvas canvas;
	private Paint paint;
	Point x;
	
	public LevelDraw(GameCanvas gameCanvas) {
		this.gameCanvas = gameCanvas;
		this.canvas = gameCanvas.getCanvas();
		paint = new Paint();
		paint.setColor(Color.BLACK);
		x.x = 0;
		x.y = 0;
	}
	
	public void drawText(String toWrite, Point loc){
		canvas.drawText(toWrite, loc.x, loc.y, paint);
	}
	
	public void drawText(String toWrite, Point loc, Paint brush){
		canvas.drawText(toWrite, loc.x, loc.y, brush);
	}
	
	public void drawFloor(Point y){
		paint.setStrokeWidth(25);
		canvas.drawLine(x.x, x.y, y.x, y.y, paint);
		x.x = y.x;
		x.y = y.y;
	}
	
	public void drawFloor(Point y, Paint brush){
		canvas.drawLine(x.x, x.y, y.x, y.y, brush);
		x.x = y.x;
		x.y = y.y;
	}

}
