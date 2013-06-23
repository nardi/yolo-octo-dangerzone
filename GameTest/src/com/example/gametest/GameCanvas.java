package com.example.gametest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GameCanvas extends View {
	
	private Paint paint;
	private Canvas canvas;
	
	public GameCanvas(Context context) {
		super(context);
	}

	public GameCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameCanvas(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	{
		paint = new Paint();
		paint.setColor(Color.RED);
	}
	@Override
	public void onDraw(Canvas canvas){
		this.canvas = canvas;
		super.onDraw(canvas);
		
		canvas.drawText("Hello Wordl", 0, 0, paint);
		canvas.restore();
	}
	
	public Canvas getCanvas(){
		return this.canvas;
	}

}
