package com.example.gametest;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class LevelFragment extends GameFragment {

	public Paint paint;
	public Canvas canvas;
	LevelDraw lvlGen;
	FloorBuffer buffer;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTargetFps(42);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(12);
		lvlGen = new LevelDraw(this.getView());
		//buffer.FillBuffer();
	}

	@Override
	public void onUpdate(long dt) {
		;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		canvas.drawText("Hello Wordl", 100, 100, paint);
		lvlGen.drawFromBuffer(buffer.getBuffer(), canvas);
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent me) {
		Log.e("Action","Pressed");
		return true;
	}
}
