package yolo.octo.dangerzone.lvlgen;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

public class LevelDraw {
	
	public GameCanvas gameCanvas;
	public View view;
	private Paint paint;
	private PointF old;
	private boolean init = true;
	private int y = 50;
	private int playerX;
	private float playerY;
	private Coin[] coin = new Coin[120];
	
	/* Constructor for a level drawer.
	 */
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
	
	/* drawText() is used to write text to the screen, either with the standard paint
	 * or a custom one.
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
	
	//public void fillFloor(PointF newp, Canvas canvas) {
	//	canvas.drawLine(newp.x, newp.y, newp.x, view.getHeight(), paint);
	//}
	
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
	 * Calls drawFloor with values from the ringbuffer.
	 */
	public void drawFromBuffer(PointF[] buffer, Canvas canvas){
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
			old.x = 150;
			playerX = (int) ((view.getWidth()/4.0 * 399.0) / view.getWidth());
			//Log.e("Navi", "" + playerX);
		}
		float translateY = 0;
		
		for (int i = 0; i < buffer.length; i++) {
			translate(buffer[i]);
			if (buffer[i].y < view.getHeight() * (1/6f)) {
				translateY = view.getHeight() * (1/6f) - buffer[i].y;
				Log.i("viewHeight", "" + view.getHeight());
				Log.i("y", "" + buffer[i].y);
				Log.i("translateY", "" + translateY);
			}
		}
		playerY = buffer[playerX].y;
		
		Path path = new Path();
		path.moveTo(buffer[0].x, buffer[0].y);
		
		//playerY = (float)(view.getHeight() * 2.0/3.0);
		
		for (int i = 1; i < buffer.length; i++) {
			path.lineTo(buffer[i].x, buffer[i].y);
			
			
			if (i != playerX) {
				//drawFloor(buf, canvas);
				//fillFloor(buf, canvas);
			}

		}
		Paint paint2 = new Paint();
		paint2.setColor(Color.BLUE);
		path.lineTo(view.getWidth(), view.getHeight());
		path.lineTo(0, view.getHeight());
		path.offset(0, translateY);
		canvas.drawPath(path, paint2);
		old.x = -1;
		old.y = -1;
	}
	
	public float getHeight() {
		return playerY;
	}
	
}
