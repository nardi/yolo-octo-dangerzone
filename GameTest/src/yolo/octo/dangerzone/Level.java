package yolo.octo.dangerzone;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Random;

import nobleworks.libmpg.MP3Decoder;

import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.Collectable;
import yolo.octo.dangerzone.lvlgen.FloorBuffer;
import yolo.octo.dangerzone.lvlgen.LevelDraw;
import yolo.octo.dangerzone.lvlgen.LevelGenerator;
import yolo.octo.dangerzone.lvlgen.Score;
import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.AudioTrack.OnPlaybackPositionUpdateListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Level extends GameObject {
	private static final int speed = 8;
	
	public Paint paint;
	//public Canvas canvas;
	private Score score;

	private AudioTrack at;
	private String path;
	private LevelDraw lvlDraw;
	private FloorBuffer buffer;
	private Character character;
	private Button jumpButton;
	private boolean update = false;
	private int bpm = 120;
	private long updateTime = 0;
	private long minTime = 33;
	private int preloadTime = 0;
	private long diff = 0;
	private long prevT = 0;
	private int fadeOut;
	private LevelGenerator lvlGen;
	private Random colGen = new Random();

	private LevelComplete end;
	//Coin[] coin = new Coin[bpm];
	
	public Level(BeatDetector beatDet, long length, String path) {
		this(new LevelGenerator(beatDet, length, speed), path);
		
		try{
			String savedPath = path.substring(path.lastIndexOf("/") + 1) + ".lvl";
			Log.e("OutPutStream", "Path: " + savedPath);
			FileOutputStream output = App.get().getApplicationContext().openFileOutput(savedPath, Context.MODE_PRIVATE);
			ObjectOutputStream lvlSaver = new ObjectOutputStream(output);
			lvlSaver.writeObject(lvlGen);
			lvlSaver.close();
		}catch(Exception ex){
			Log.e("OutPutStream", "Could not save level to a file because: ", ex);
		}
		
		/*
		for (int i = 0; i < bpm; i++) {
			coin[i] = new Coin(i*400, 200);
			coin[i].speed = speed + (speed/3);
			addObject(coin[i]);
		} */
	}
	
	public Level(LevelGenerator lvlGen, String path) {
		this.lvlGen = lvlGen;
		score = new Score();
		paint = new Paint();
		paint.setColor(Color.rgb(143,205,158));
		paint.setTextSize(12);
		lvlDraw = new LevelDraw(score);
		
		int preloadPoints = preloadTime / (1000 / (speed * 30));
		buffer = new FloorBuffer(lvlGen.getLevel(), preloadPoints);
		
		this.path = path;
	}

	protected void onAttach() {
		Context context = getParentFragment().getActivity();
		
		character = new Character(context, 0, 0);
		addObject(character);
		
		jumpButton = new Button(context, 0, 0, 100, 100, Color.RED, "Jump");
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
		new Thread(playMp3(path)).start();
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
		
		if(at != null && at.getPlayState() == AudioTrack.PLAYSTATE_STOPPED){
			AudioTrack temp = at;
			at = null;
			temp.release();
			fadeOut = 1;
		}
		
		if(fadeOut > 0){
			buffer.update(speed);
			if (fadeOut >= 200) {
				end = new LevelComplete(score);
				swapFor(end);
			}
			fadeOut++;
		}
		
	}
	
	private OnPlaybackPositionUpdateListener onAudioUpdate = new OnPlaybackPositionUpdateListener() {
		@Override
		public void onPeriodicNotification(AudioTrack arg0) {
			buffer.update(speed);
			int randomInt = colGen.nextInt(100);
			if (randomInt <= 3) {
				//TODO: Maak nieuwe collectable aan met types 0, 1, 2, of 3
				addObject(new Collectable(randomInt, 399, lvlDraw.getView()));
				// Geef 399 mee!!! (Want hij moe trechts beginnen
			}
		}
		@Override
		public void onMarkerReached(AudioTrack arg0) {
		}
	};
	
	@Override
	public void onDraw(Canvas canvas){
		
		canvas.drawColor(Color.rgb(124,139,198));
		lvlDraw.view = getParentFragment().getView();
		lvlDraw.drawFromBuffer(buffer.getBuffer(), canvas);
		character.x = (int)(canvas.getWidth()/4.0);
	
		if (jumpButton != null)
			jumpButton.setPosition(75, canvas.getHeight() - 75);
		
		update = true;
	}
	
	private Runnable playMp3(final String path) {
		final MP3Decoder md = new MP3Decoder(path);
		
		int afChannels = AudioFormat.CHANNEL_OUT_DEFAULT;
		switch (md.getNumChannels()) {
			case 1: afChannels = AudioFormat.CHANNEL_OUT_MONO; break;
			case 2: afChannels = AudioFormat.CHANNEL_OUT_STEREO; break;
		}
		final int bufferSize = AudioTrack.getMinBufferSize(md.getRate(),
				afChannels, AudioFormat.ENCODING_PCM_16BIT);
		final short[] buffer = new short[bufferSize];
		ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(bufferSize * 2);
		// Audio data is little endian, so for correct bytes -> short conversion:
		nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
		final ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				md.getRate(), afChannels,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize,
				AudioTrack.MODE_STREAM);
		int updatePeriod = md.getRate() / 30;
		// Dingen synchroon laten lopen met de muziek is lastig, dit lijkt te werken
		audioTrack.setPositionNotificationPeriod((int)(updatePeriod * 0.969));
		audioTrack.setPlaybackPositionUpdateListener(onAudioUpdate);
		return new Runnable() {
			public void run() {
				try {
					int preloadSamples = md.getRate() * md.getNumChannels() * preloadTime / 1000;
					short[] preloadBuffer = new short[preloadSamples];
					
					audioTrack.play();
					at = audioTrack;
					at.write(preloadBuffer, 0, preloadSamples);
					int readSamples = -1;
					while (readSamples != 0) {
						readSamples = md.readSamples(shortBuffer);
						shortBuffer.get(buffer, 0, readSamples);
						shortBuffer.position(0);
						while (at.getPlayState() != AudioTrack.PLAYSTATE_PLAYING);
						at.write(buffer, 0, readSamples);
						Log.v("playMp3", "Wrote " + readSamples + " samples");
					}
					/* Stopping here leaves no guarantee everything has been
					 * played, but whatever */ 
					at.stop();
					Log.d("playMp3", "Done decoding!");
				} catch (Exception e) {
					Log.e("playMp3", "Oops!", e);
				}
			}
		};
	}
	
	public void onHalt(){
		if(at != null){
			at.pause();
		}
	}
	
	public void onRun(){
		if (at != null && at.getPlayState() == AudioTrack.PLAYSTATE_PAUSED){
			at.play();
		}
	}
}
