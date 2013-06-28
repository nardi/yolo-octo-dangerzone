package yolo.octo.dangerzone.lvlgen;

import yolo.octo.dangerzone.Character;
import yolo.octo.dangerzone.core.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Collectible extends GameObject {
	private float radius;
	private int reward;
	private int color;
	private int speed;
	private float x, y;
	private float[] cPosition;
	private Paint paint;
	private static Score score;
	private static Character character;
	private View view;
	private boolean rewarded;
	
	
	/* The constructor creates a Collectible object, which is either an obstacle
	 * or a coin.
	 */
	public Collectible(int type, int locIndex, LevelDraw lvlDraw, int speed) {
		this.radius = 30;
		this.view = lvlDraw.getView();
		this.speed = speed;
		
		
		/* Set the X-location, if the view is not null
		 */
		if(view != null){
			this.x = (float)view.getWidth();
			y = lvlDraw.getCollectibleHeight();
		}
		else{
			Log.e("Collectable","View NULL!");
			removeObject(this);
		}
		
		switch (type) {
			case 0:
				/* Create a coin on the floor, y is relative to the height of the floor.
				 */
				this.reward = 50;
				this.color = Color.YELLOW;
				this.y -= 1/40F * (float)view.getHeight();
				break;
				
			case 1 :
				/* Create a coin in the air, y is again relative to the floor.
				 */
				this.reward = 50;
				this.color = Color.YELLOW;
				this.y -= 1/5F * (float)view.getHeight();
				break;
			
			case 2 :
				/* Create an obstacle on the ground;
				 */
				this.reward = -30;
				this.color = Color.BLACK;
				this.y -= 1/40F * (float)view.getHeight();
				break;
			
			case 3 :
				/* Create an obstacle in the air.
				 */
				this.reward = -30;
				this.color = Color.BLACK;
				this.y -= 1/5F * (float)view.getHeight();
				break; 
		}		
		
		this.paint = new Paint();
		paint.setColor(this.color);
		
	}
	
	/* When update is called, move the collectible a bit to the left.
	 */
	public void onUpdate(long dt) {
		if(view != null){
			this.x -= speed * (float)(view.getWidth() / 399.0);
		}
	
		checkCollision();
	}
	
	/* When onDraw is called, the collectible is drawn on the screen. */
	public void onDraw(Canvas canvas) { 
		if(!rewarded){
			canvas.drawCircle(x, y, radius, this.paint);
		}
	}
	
	
	/* Check for collision with the player.
	 */
	public void checkCollision() {
		if (character != null) {
			
			/* Get the position and radius of the character, and calculate the distance
			 * between them in order to check their collision.
			 */
			cPosition = character.getCharacterPos();
			float xDif = cPosition[0] - this.x;
			float yDif = cPosition[1] - this.y;
			double distance = (xDif * xDif) + (yDif * yDif);
			
			/* On collision, adjust the score and remove the collectible.
			 */
			if (distance < (this.radius + cPosition[2]) * (this.radius + cPosition[2])) {
				if(!rewarded){
					score.addScore(this.reward);
					rewarded = true;
				}
				removeObject(this);
			}
		}		
	}
	
	
	/* Gives the Collectible-class access to the Character-class. 
	 */
	public static void setCharacter(Character c) {
		character = c;
	}
	
	/* Gives the Collectible-class access to the Score-class.
	 */
	public static void setScore(Score s) {
		score = s;
	}
}
