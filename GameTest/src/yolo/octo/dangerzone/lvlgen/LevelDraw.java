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
	private Score score;
	private Paint paint;
	private Paint text;
	private PointF old;
	private boolean init = true;
	private int y = 50;
	private int playerX;
	private float playerY;
	private Coin[] coin = new Coin[120];
	
	/* Constructor for a level drawer.
	 */
	public LevelDraw(Score score) {
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,100));
		text = new Paint();
		text.setColor(Color.WHITE);
		text.setTextSize(40);
		old = new PointF(0 , 0);
		this.score = score;
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
	
	public void drawScore(int score, Canvas canvas) {
		canvas.drawText("Score: " + score, 10, 40, text);
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
		int height = Math.min(view.getWidth(), view.getHeight());
		
		if(view != null){
			dev.x = (float)((view.getWidth() / 399.0) * dev.x);
			
			dev.y = (float)((view.getHeight() * (2f/3f)) - (dev.y * (2f/7f) * height));
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
			playerX = (int)(399/4f);
		}
		float translateY = 0;
		
		drawScore(score.getScore(), canvas);
		
		for (int i = 0; i < buffer.length; i++) {
			translate(buffer[i]);
			if (buffer[i].y < view.getHeight() * (1/6f)) {
				float newTY = view.getHeight() * (1/6f) - buffer[i].y;
				if (newTY > translateY)
					translateY = newTY;
			}
		}
		playerY = buffer[playerX].y + translateY;
		
		Path path = new Path();
		path.moveTo(buffer[0].x, buffer[0].y);
		
		
		for (int i = 1; i < buffer.length; i++) {
			path.lineTo(buffer[i].x, buffer[i].y);
			
			
			if (i != playerX) {
				//drawFloor(buf, canvas);
				//fillFloor(buf, canvas);
			}

		}
		Paint paint2 = new Paint();
		paint2.setColor(Color.rgb(183, 219, 149));
		path.lineTo(view.getWidth(), view.getHeight());
		path.lineTo(0, view.getHeight());
		canvas.translate(0, translateY);
		canvas.drawPath(path, paint2);
		canvas.translate(0, -translateY);
		old.x = -1;
		old.y = -1;
	}
	
	public float getHeight() {
		return playerY;
	}
	
}
