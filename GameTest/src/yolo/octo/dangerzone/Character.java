package yolo.octo.dangerzone;

import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.Collectible;
import yolo.octo.dangerzone.Character;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class Character extends GameObject {
	private double speed=5.0;
	public float x, y, groundY, radius = 50;
	private Bitmap sprite;
	public boolean jumping = false;
	public boolean direction = false;
	public static final double gravity = 0.3;
	
	Paint character = new Paint(); {
		character.setColor(Color.rgb(33,201,50));
		character.setAntiAlias(true);
		character.setDither(true);
		character.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	
	/* Constructor */
	public Character(Context context, float x, float y) {
		this.x = x;
		this.groundY = y;
		this.y = y;
		
	    Resources res = context.getResources();
	    try {
			Bitmap spriteBig = BitmapFactory.decodeResource(res, R.drawable.hashtag);
			this.sprite = getResizedBitmap(spriteBig, 100, 100);
	    } catch (Exception e) {
	        Log.d("kak","Error is " + e);
	    } 
	    
	    Collectible.setCharacter(this);
	}
	
	/* Constructor*/
	public Character(Context context, float x, float y, int floatOffset) {

		this.x = x;
		this.groundY = y;
		this.y = y + floatOffset;
		
	    Resources res = context.getResources();
	    try {
			Bitmap spriteBig = BitmapFactory.decodeResource(res, R.drawable.octo);
			this.sprite = getResizedBitmap(spriteBig, 100, 100);
	    } catch (Exception e) {
	        Log.d("kak","Error is " + e);
	    } 	
	}
	
	/* The general update method */
	protected void onUpdate(long dt){
		if (jumping) {
			updateY(dt);
		}
	}
	
	/* If the character is in 'jump' mode, updateY changes the vertical position
	 * depending on the direction and speed.
	 */
	private void updateY(long dt) {
		y -= (dt/5f) * speed;       
		speed -= (dt/17f) * gravity;
		if(y > groundY){
		    y = groundY;
		    speed = 5.0; 
		    jumping = false;
		}      
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix(); 
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		 
		return resizedBitmap;
	}
	
	/* General onDraw method, draws the character.
	 */
	public void onDraw(Canvas canvas) {
		canvas.drawBitmap(sprite, x-50, y, null);
	}
	
	/* getCharacterPos returns the x coordinatem y coordinate and radius of the character
	 */
	public float[] getCharacterPos() {
		float[] pos = {x, y , radius};
		return pos;
	}
}
