package yolo.octo.dangerzone.lvlgen;

import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.Character;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Coin extends GameObject {
	public float x, y;
	public float radius = 40;
	public int speed = 0;
	private static Score score;
	private static Character character;
	private float[] position;
	private boolean isTouched;
	
	Paint coinPaint = new Paint(); {
		coinPaint.setColor(Color.rgb(212,175,55));
		coinPaint.setAntiAlias(true);
		coinPaint.setDither(true);
		coinPaint.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	
	public Coin(int x, int y) {
		this.x = x;
		this.y = y;
		this.x = 150;
		this.y = 400;
	}
	
	public void onUpdate(long dt) {
		//this.x -= dt/speed;
		checkColission();
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawCircle(x, y, radius, coinPaint);
	}
	
	public void checkColission() {
		if (character != null) {
			
			
			/* # is X by Y. Iets in X*Y moet in coin vallen. 
			 * PosX + X >= coin.x 
			 */
			
			
			position = character.getCharacterPos();
			float xDif = position[0] - this.x;
			float yDif = position[1] - this.y;
			double distance = (xDif * xDif) + (yDif * yDif);
			//double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
			if (distance < (this.radius + position[2]) * (this.radius + position[2]) && !isTouched) {
				score.addScore(50);
				isTouched = true;
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
