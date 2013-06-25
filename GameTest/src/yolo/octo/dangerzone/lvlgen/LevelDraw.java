package yolo.octo.dangerzone.lvlgen;

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
	/*
	 * Following are the drawing functions for the level. Each function can 
	 * either use the standard paint or the given paint. The drawFloor functions
	 * save the point so each subsequent point will be drawn from the previous.
	 */
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
	
	public Point translateDeviation(Point dev){
		dev.y = 0; //TODO Translation formula here
		return dev;
	}
	
	/*
	 * THis is a raw version. I am trying to find a good translation method in order to evenly distribute the 400 x points over
	 * the screen and to translate the deviation.
	 */
	public void drawFromBuffer(Point[] buffer){
		for(int i = 0; i < buffer.length; i++){
			drawFloor(translateDeviation(buffer[i]));
		}
	}
	
	

}
