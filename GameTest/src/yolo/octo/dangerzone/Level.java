package yolo.octo.dangerzone;


import java.util.Random;

import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;
import yolo.octo.dangerzone.core.GameFragment;
import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.FloorBuffer;
import yolo.octo.dangerzone.lvlgen.FloorPoint;
import yolo.octo.dangerzone.lvlgen.LevelDraw;
import yolo.octo.dangerzone.lvlgen.LevelGenerator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class Level extends GameObject {

	public Paint paint;
	public Canvas canvas;
	LevelDraw lvlDraw;
	FloorBuffer buffer;
	Character character = new Character(0,0);
	Button button = new Button(0,0);
	boolean update = false;
	int speed = 1, bpm = 120;
	//Coin[] coin = new Coin[bpm];
	
	public Level(BeatDetector beatDet, long length) {
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,158));
		paint.setTextSize(12);
		lvlDraw = new LevelDraw();
		LevelGenerator lvlGen = new LevelGenerator(beatDet, length);
		lvlGen.generateLevel();
		buffer = new FloorBuffer(lvlGen.level);
		buffer.fillBuffer();
		addObject(character);
		addObject(button);
		
		/*
		for (int i = 0; i < bpm; i++) {
			coin[i] = new Coin(i*400, 200);
			coin[i].speed = speed + (speed/3);
			addObject(coin[i]);
		} */
	}

	@Override
	public void onUpdate(long dt) {
		if (update && !character.jumping) {
			character.y = lvlDraw.getHeight() - 100;
			//float height = (this.getView().getHeight() * 2/3) - 45;
			//character.y = -1*(buffer.getHeight(this.getView())) + height;
		} else if (update) {
			character.groundY = lvlDraw.getHeight() - 100;
		}
		
		for (int i = 0; i < speed; i++) {
			buffer.update();
		}

	}
	
	@Override
	public void onDraw(Canvas canvas){
		canvas.drawColor(Color.rgb(124,139,198));
		lvlDraw.view = getParentFragment().getView();
		lvlDraw.drawFromBuffer(buffer.getBuffer(), canvas);
		int width = getParentFragment().getView().getWidth();
		character.x = (int)(width/4.0);
		character.addSprite(getParentFragment().getView());
		
		int height = getParentFragment().getView().getHeight();
		button.y = height - 150;
		button.addSprite(getParentFragment().getView());

		
		
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
