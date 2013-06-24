package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class Character extends GameObject {
	float x, y;
	float radius = 40;
	
	Paint character = new Paint(); {
		character.setColor(Color.rgb(33,201,50));
		character.setAntiAlias(true);
		character.setDither(true);
		character.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	
	public Character(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	protected void onUpdate(long dt){
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawCircle(x, y, radius, character);
	}

}
