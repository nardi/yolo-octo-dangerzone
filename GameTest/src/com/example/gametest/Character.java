package com.example.gametest;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Character extends GameObject {
	private double height=0, speed=5.0;
	float x, y, groundY, radius = 40;
	Bitmap sprite;
	boolean jumping = false;
	boolean direction = false;
	long previous = 0, start = 0;
	
	public static final double gravity = 0.3;
	
	Paint character = new Paint(); {
		character.setColor(Color.rgb(33,201,50));
		character.setAntiAlias(true);
		character.setDither(true);
		character.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	
	public Character(float x, float y) {
		this.x = x;
		this.groundY = y;
		this.y = y;
	}
	
	public void addSprite(View v) {
		Context context = v.getContext();
	    Resources res = context.getResources();
	    try {
			Bitmap spriteBig = BitmapFactory.decodeResource(res, R.drawable.hashtag);
			this.sprite = getResizedBitmap(spriteBig, 100, 100);
	    } catch (Exception e) {
	        Log.d("kak","Error is " + e);
	    } 
	}
	
	protected void onUpdate(long dt){
		if (jumping) {
			updateY(dt);
		}
	}
	
	private void updateY(long dt) {		   
		y -= (dt/2) *speed;       
		speed -= (dt/10) * gravity;   
		if(y > groundY){
		    y=groundY;
		    speed=5.0; 
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
	
	public void onDraw(Canvas canvas) {
	//	canvas.drawCircle(x, y, radius, character);
		canvas.drawBitmap(sprite, x-50, y, null);
	}

}
