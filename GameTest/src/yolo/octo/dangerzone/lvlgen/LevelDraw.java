/* The LevelDraw class is in charge of drawing and refreshing what's on the 
 * screen. It can retrieve the level from the FloorBuffer class, the character
 * and its state, obstacles and the score, and draw these on the screen.
 */

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
	
	
	/* drawScore() displays the players' score on the screen
	 */
	public void drawScore(int score, Canvas canvas) {
		canvas.drawText("Score: " + score, 10, 40, text);
	}
	
	
	/* The translate() function translates the values in the array to 
	 * coordinates on the screen. 
	 */
	public PointF translate(PointF dev){
		int height = Math.min(view.getWidth(), view.getHeight());
		
		/* The index is translated to a coordinate on the screen by multiplying the index
		 * with the width of the screen divided by the highest index.
		 * The deviation is translated by taking the line on height*(2/3) as base line
		 * and adding the deviation times (2/7), times the height variable.
		 */
		if(view != null){
			dev.x = (float)((view.getWidth() / 399.0) * dev.x);
			dev.y = (float)((view.getHeight() * (2f/3f)) - (dev.y * (2f/7f) * height));
			return dev;
		}
		
		Log.e("View", "View == null");
		return dev;
	}
	
	/* drawFromBuffer() draws a path using values retrieved from the ring-buffer.
	 */
	public void drawFromBuffer(PointF[] buffer, Canvas canvas){
		
		/* Initialises a few values ont he first draw
		 */
		if (init) {
			init = false;
			old.y = view.getHeight() * 2/3;
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
		
		/* Retrieves the Y-value of the player's location.
		 */
		playerY = buffer[playerX].y + translateY;
		
		/* Draw the paths between the points
		 */
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
	
	
	/* Returns the Y-value for the player, used with jumping and drawing the 
	 * character.
	 */
	public float getHeight() {
		return playerY;
	}
	
	public View getView(){
		return this.view;
	}
}
