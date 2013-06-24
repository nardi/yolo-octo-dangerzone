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
	Button button = new Button(0,0);
	boolean update = false;
	int speed = 3;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTargetFps(42);
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,158));
		paint.setTextSize(12);
		lvlGen = new LevelDraw();
		buffer = new FloorBuffer(generateDevs());
		buffer.fillBuffer();
		addObject(character);
		addObject(button);

		run();
	}

	@Override
	public void onUpdate(long dt) {
		if (update && !character.jumping) {
			character.y = lvlGen.getHeight() - 100;
			//float height = (this.getView().getHeight() * 2/3) - 45;
			//character.y = -1*(buffer.getHeight(this.getView())) + height;
		} else if (update) {
			character.groundY = lvlGen.getHeight() - 100;
		}
		

	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.rgb(124,139,198));
		lvlGen.view = this.getView();
		lvlGen.drawFromBuffer(buffer.getBuffer(), canvas);
		int width = this.getView().getWidth();
		character.x = (int)(width/4.0);
		character.addSprite(this.getView());
		
		int height = this.getView().getHeight();
		button.y = height - 150;
		button.addSprite(this.getView());

		for (int i = 0; i < speed; i++) {
			buffer.update();
		}
		
		update = true;
	}
	
	@Override
	protected boolean onTouch(View v, MotionEvent me) {
		if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
			if (me.getX() < 150 && me.getY() > v.getHeight() - 150 && character.jumping == false) {
				character.jumping = true;
				character.direction = true;
				button.pressed = true;
			}
		}
		if (me.getActionMasked()==MotionEvent.ACTION_UP) {
			button.pressed = false;
		}
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
