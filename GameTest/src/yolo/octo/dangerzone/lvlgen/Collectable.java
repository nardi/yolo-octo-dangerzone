package yolo.octo.dangerzone.lvlgen;

import yolo.octo.dangerzone.Character;
import yolo.octo.dangerzone.core.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Collectable extends GameObject {
	private int type;
	private float radius;
	private int reward;
	private int color;
	private float x, y;
	private float[] cPosition;
	private Paint paint;
	private static Score score;
	private static Character character;
	private View view;
	private LevelDraw lvlDraw;
	
	
	public Collectable(int type, int locIndex, LevelDraw lvlDraw) {
		this.type = type;
		this.radius = 30;
		this.view = lvlDraw.getView();
		this.lvlDraw = lvlDraw;
		
		
		//TODO: Zet locIndex (index in buffer) om in X.
		// locIndex is waarde in buffer van links naar rechts: 0 tot 399, 0 is links en 399 is rechts.
		if(view != null){
			this.x = (float)((view.getWidth() / 399.0) * locIndex);
		}
		//this.x =
		
		switch (type) {
			case 0:
				//create a coin on the floor, y is relative to the height of the floor.
				this.reward = 50;
				this.color = Color.YELLOW;
				this.y = 0;
				break;
				
			case 1 :
				//create a coin in the air, y is again relative to the floor.
				this.reward = 50;
				this.color = Color.YELLOW;
				this.y = 50;
				break;
			
			case 2 :
				//create an obstacle on the ground;
				this.reward = -30;
				this.color = Color.BLACK;
				this.y = 0;
				break;
			
			case 3 :
				//create an obstacle in the air
				this.reward = -30;
				this.color = Color.BLACK;
				this.y = 50;
				break; 
		}		
		
		this.paint = new Paint();
		paint.setColor(this.color);
		
	}
	
	public void onUpdate(long dt) {
		// TODO: Update x
		if(view != null){
			this.x -= (float)(view.getWidth() / 399.0);
		}
		
		checkColission();
	}
	
	public void onDraw(Canvas canvas) {
		//TODO: Set correct Y, based on actual level. 
		y = lvlDraw.getHeight();
		canvas.drawCircle(x, y, radius, this.paint);
	}
	
	
	public void checkColission() {
		if (character != null) {
			
			/* Get the position and radius of the character, and calculate the distance
			 * between them in order to check their collision.
			 */
			cPosition = character.getCharacterPos();
			float xDif = cPosition[0] - this.x;
			float yDif = cPosition[1] - this.y;
			double distance = (xDif * xDif) + (yDif * yDif);
			
			/* On collision, adjust the score.
			 */
			if (distance < (this.radius + cPosition[2]) * (this.radius + cPosition[2])) {
				score.addScore(this.reward);
				// TODO: Remove object
			}
		}		
	}
	
	public static void setCharacter(Character c) {
		character = c;
	}
	
	public static void setScore(Score s) {
		score = s;
	}
}
