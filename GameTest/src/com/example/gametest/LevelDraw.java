package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class LevelDraw {
	
	public GameCanvas gameCanvas;
	public Canvas canvas;
	private Paint paint;
	
	public LevelDraw(GameCanvas gameCanvas) {
		this.gameCanvas = gameCanvas;
		this.canvas = gameCanvas.canvas;
		paint = new Paint();
		paint.setColor(Color.BLACK);
	}
	
	public void drawText(String toWrite, Point loc){
		canvas.drawText(toWrite, loc.x, loc.y, paint);
	}
	
	public void drawText(String toWrite, Point loc, Paint brush){
		canvas.drawText(toWrite, loc.x, loc.y, brush);
	}

}
