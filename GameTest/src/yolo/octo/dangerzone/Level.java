package yolo.octo.dangerzone;


import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Random;

import nobleworks.libmpg.MP3Decoder;

import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;
import yolo.octo.dangerzone.core.GameFragment;
import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.FloorBuffer;
import yolo.octo.dangerzone.lvlgen.LevelDraw;
import yolo.octo.dangerzone.lvlgen.LevelGenerator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Level extends GameObject {

	public Paint paint;
	public Canvas canvas;

	private AudioTrack at;
	private Runnable mp3;
	private LevelDraw lvlDraw;
	private FloorBuffer buffer;
	private Character character = new Character(0,0);
	private Button jumpButton;
	private boolean update = false;
	private int speed = 4, bpm = 120;
	private long updateTime = 0;
	private double minTime = 1000/30;

	//Coin[] coin = new Coin[bpm];
	
	public Level(BeatDetector beatDet, long length, String path) {
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,158));
		paint.setTextSize(12);
		lvlDraw = new LevelDraw();
		Log.e("LvlGen", "Generating level");
		LevelGenerator lvlGen = new LevelGenerator(beatDet, length, speed);
		lvlGen.generateLevel();
		buffer = new FloorBuffer(lvlGen.level);
		buffer.fillBuffer();
		mp3 = playMp3(path);
		addObject(character);
		new Thread(mp3).start();
		
		
		/*
		for (int i = 0; i < bpm; i++) {
			coin[i] = new Coin(i*400, 200);
			coin[i].speed = speed + (speed/3);
			addObject(coin[i]);
		} */
	}
	
	protected void onAttach() {
		jumpButton = new Button(getParentFragment().getActivity(), 0, 0, 100, 100, Color.RED, "Jump");
		jumpButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent me) {
				if (me.getActionMasked() == MotionEvent.ACTION_DOWN && character.jumping == false) {
					character.jumping = true;
					character.direction = true;
					jumpButton.pressed = true;
				}
				if (me.getActionMasked() == MotionEvent.ACTION_UP) {
					jumpButton.pressed = false;
				}

				return true;
			}
		});
		addObject(jumpButton);
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
		character.x = (int)(canvas.getWidth()/4.0);
		character.addSprite(getParentFragment().getView());
	
		if (jumpButton != null)
			jumpButton.setPosition(75, canvas.getHeight() - 75);
		
		update = true;
	}
	
	private Runnable playMp3(final String path) {
		return new Runnable() {
			public void run() {
				try {
					MP3Decoder md = new MP3Decoder(path);
					
					int channels = AudioFormat.CHANNEL_OUT_DEFAULT;
					switch (md.getNumChannels()) {
						case 1: channels = AudioFormat.CHANNEL_OUT_MONO; break;
						case 2: channels = AudioFormat.CHANNEL_OUT_STEREO; break;
					}
					int bufferSize = AudioTrack.getMinBufferSize(md.getRate(),
							channels, AudioFormat.ENCODING_PCM_16BIT);
					short[] buffer = new short[bufferSize];
					ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(bufferSize * 2);
					// Audio data is little endian, so for correct bytes -> short conversion:
					nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
					at = new AudioTrack(AudioManager.STREAM_MUSIC,
							md.getRate(), channels,
							AudioFormat.ENCODING_PCM_16BIT, bufferSize,
							AudioTrack.MODE_STREAM);

					at.play();
					int readSamples = -1;
					while (readSamples != 0) {
						readSamples = md.readSamples(shortBuffer);
						shortBuffer.get(buffer, 0, readSamples);
						shortBuffer.position(0);
						while (at.getPlayState() != AudioTrack.PLAYSTATE_PLAYING);
						at.write(buffer, 0, readSamples);
						Log.v("playMp3", "Wrote " + readSamples + " samples");
					}
					/* TODO Stopping here leaves no guarantee everything has been
					 * played, but whatever */ 
					at.stop();
					AudioTrack temp = at;
					at = null;
					temp.release();
					Log.d("playMp3", "Done decoding!");
				} catch (Exception e) {
					Log.e("playMp3", "Oops!", e);
				}
			}
		};
	}
	
	public void onHalt(){
		if(at != null){
			this.at.pause();
		}
	}
	
	public void onRun(){
		if (at != null && at.getPlayState() == AudioTrack.PLAYSTATE_PAUSED){
			at.play();
		}
	}
}
