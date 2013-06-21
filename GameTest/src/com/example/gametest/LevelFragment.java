package com.example.gametest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class LevelFragment extends GameFragment {

	public Paint paint;
	public Canvas canvas;
	
	public LevelFragment() {
		
	}
	
	{
		paint = new Paint();
		paint.setColor(Color.RED);
	}

	@Override
	public void onUpdate(long dt) {
		;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		this.canvas = canvas;
		
		canvas.drawText("Hello Wordl", 0, 0, paint);
		canvas.restore();
	}
	
	public boolean onTouch(){
		
	}
}
