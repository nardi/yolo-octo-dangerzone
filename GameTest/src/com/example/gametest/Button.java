package com.example.gametest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

public class Button extends GameObject {
	float x, y;
	Bitmap button1, button2;
	boolean pressed = false;
	
	public Button(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void addSprite(View v) {
		Context context = v.getContext();
	    Resources res = context.getResources();
	    try {
			this.button1 = BitmapFactory.decodeResource(res, R.drawable.button1);
			this.button2 = BitmapFactory.decodeResource(res, R.drawable.button2);
	    } catch (Exception e) {
	        Log.d("kak","Error is " + e);
	    } 
	}
	
	protected void onUpdate(long dt){
	}
	
	public void onDraw(Canvas canvas) {
		if(!pressed) 
			canvas.drawBitmap(button1, 200, 200, null);
		else
			canvas.drawBitmap(button2, 200, 200, null);
	}

}
