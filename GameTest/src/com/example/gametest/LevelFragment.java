package com.example.gametest;

import java.util.Random;

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
	Character character = new Character(0,0);
	boolean update = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTargetFps(42);
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(12);
		lvlGen = new LevelDraw();
		buffer = new FloorBuffer(generateDevs());
		buffer.fillBuffer();
		addObject(character);

		run();
	}

	@Override
	public void onUpdate(long dt) {
		if (update) {
			float height = (this.getView().getHeight() * 2/3) - 45;
			character.y = -1*(buffer.getHeight(this.getView())) + height;
		}

	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.BLACK);
		//canvas.drawText("Hello Wordl", 100, 100, paint);
		lvlGen.view = this.getView();
		lvlGen.drawFromBuffer(buffer.getBuffer(), canvas);
		int width = this.getView().getWidth();
		character.x = (int)(width/4.0);


		buffer.update();

		update = true;
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent me) {
		Log.e("Action","Pressed");
		return true;
	}
	
	public FloorPoint[] generateDevs(){
		boolean dinges  = true;
		FloorPoint[] array = new FloorPoint[760];
		
		if(dinges){	
			for (int i = 0; i < 40; i++) {
				array[i] = new FloorPoint(0.0);
			}
			for (int i = 0; i < 360; i++) {
				array[i+40] = new FloorPoint(Math.sin(Math.toRadians(i)));
			}
			for (int i = 0; i < 360; i++) {
				array[i+400] = new FloorPoint(Math.sin(Math.toRadians(i)));
			}
		}
		else{
			Random random = new Random();
			random.setSeed(42);
			for(int i = 0; i < 400; i++){
				array[i] = new FloorPoint(random.nextFloat());
			}
		}
		return array;
	}
		
}
