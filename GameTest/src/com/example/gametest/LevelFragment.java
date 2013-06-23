package com.example.gametest;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LevelFragment extends GameFragment {

	public Paint paint;
	public Canvas canvas;
	
	public LevelFragment() {
		
	}
	
	{
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(12);
	}

	@Override
	public void onUpdate(long dt) {
		;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		canvas.drawText("Hello Wordl", 100, 100, paint);
	}
	
	public boolean onTouch(){
		Log.e("Action","Pressed");
		return true;
	}
}
