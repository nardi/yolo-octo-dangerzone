package yolo.octo.dangerzone;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import nobleworks.libmpg.MP3Decoder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;
import yolo.octo.dangerzone.beatdetection.Section;
import yolo.octo.dangerzone.core.GameObject;

public class Menu extends GameObject {
	
	private BeatDetector bd;
	public long length;
	public int time, print;
	private String path;
	public boolean picking = false,
				   song = false,
				   ready = false;
	Button pickASong;
	Paint top,
		  bottom;
	Bitmap logo,
		   pnp;
	Resources res;
	RectF logoRect,
		  pnpRect;
	Level level;
	
	Context context;
	
	protected void onAttach() {	
		
		res = getParentFragment().getResources();
		
		top = new Paint();
		bottom = new Paint();
		
		logoRect = new RectF();
		pnpRect = new RectF();
		
		top.setColor(Color.rgb(183, 219, 149));
		bottom.setColor(Color.rgb(127, 139, 197));
		try {
			this.logo = BitmapFactory.decodeResource(res, R.drawable.logo);
			this.pnp = BitmapFactory.decodeResource(res, R.drawable.pnp);
		} catch (Exception e){
			Log.e("Menu", "Picca's falen!");
		}
		
		context = getParentFragment().getActivity().getApplicationContext();

		pickASong = new Button(getParentFragment().getActivity(), 0, 0, 300, 200, Color.RED, "Pick a Song");

		pickASong.setOnTouchListener(new OnTouchListener() {
				public boolean onTouch(View v, MotionEvent me) {
					if (me.getActionMasked() == MotionEvent.ACTION_DOWN && !song) {
						picking = true;
					}
					if (me.getActionMasked() == MotionEvent.ACTION_DOWN && ready) {
						swapFor(level);
					}
					if (me.getActionMasked() == MotionEvent.ACTION_UP) {
					}

					return true;
				}
			});
	
	addObject(pickASong);
	}
	
	/*
	 * Genakt van MultimediaAudio :)
	 * TODO Ehhhhh kan dit echt niet anders...
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			String path;
			if ("content".equalsIgnoreCase(uri.getScheme())) {
				/*
				 * Source:
				 * http://www.androidsnippets.com/get-file-path-of-gallery-image
				 */
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = getParentFragment().getActivity().managedQuery(uri, proj, // Which columns to
														// return
						null, // WHERE clause; which rows to return (all rows)
						null, // WHERE clause selection arguments (none)
						null); // Order-by clause (ascending by name)
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				path = cursor.getString(column_index);
			} else if ("file".equalsIgnoreCase(uri.getScheme())) {
				path = uri.getPath();
			} else {
				Log.e("Menu", "Something went wrong...");
				return;
			}
			this.path = path;
			new Thread(loadLevel(path)).start();
		}
	}
	
	private Runnable loadLevel(final String path) {
		return new Runnable() {
			public void run() {
				try {
					song = true;
					MediaPlayer elevator = MediaPlayer.create(context, R.raw.elevator);
					elevator.start();
					MP3Decoder md = new MP3Decoder(path);
					int fftBufferSize = 1024;
					int bufferSize = fftBufferSize * 100;
					ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(2 * bufferSize * md.getNumChannels());
					// Audio data is little endian, so for correct bytes -> short conversion:
					nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
					float[] audioData = new float[fftBufferSize / 2];
					
					bd = new FFTBeatDetector(md.getRate(), md.getNumChannels(), fftBufferSize / 2);
					
					int read = -1;
					while (read != 0) {
						int samplesLeft = read = md.readSamples(shortBuffer);
						
						while (samplesLeft > 0) {
							int i, j;
							for (i = 0, j = 0;
								 i < samplesLeft - 1 && i < fftBufferSize - 1;
								 i += 2, j++) {
								audioData[j] = ((shortBuffer.get() + shortBuffer.get()) / 2f) / Short.MAX_VALUE;
							}
							while (j < fftBufferSize / 2) {
								audioData[j++] = 0;
							}
							
							bd.newSamples(audioData);
							samplesLeft -= fftBufferSize;
						}
						
						shortBuffer.position(0);
					}
					bd.finishSong();
					
					for (Beat b : bd.getBeats())
						Log.i("bt", "Beat at " + b.startTime + ", intensity: " + b.intensity);
					for (Section s : bd.getSections())
						Log.i("bt", "Section from " + s.startTime + " to " + s.endTime + ", intensity: " + s.intensity);
					
					//Level level = new Level(bd);
					Log.e("Switching", "Switching to Level");
					
					/* Stop elevator, start elevator bell */
					elevator.stop();
					MediaPlayer bell = MediaPlayer.create(context, R.raw.elevator_bell);
					bell.start();
					
					length = 1000 * md.getLength() / md.getRate();
					level = new Level(bd, length, path);
					ready = true;
					

					
				} catch (Exception e) {
					Log.e("loadLevel", "Oops!", e);
				}
			}
		};
	}
	
	Paint paint = new Paint(); {
		paint.setColor(Color.RED);
		paint.setTextSize(40);
	};
	
	@Override
	public void onDraw(Canvas canvas){
		
		int height = this.getParentFragment().getView().getHeight();
		int width = this.getParentFragment().getView().getWidth();
			
		drawMenu(canvas, height, width);
		
		pickASong.setPosition(width / 2 , (height / 4) * 3);
		/* Set by the button in the Main menu*/
		if (picking) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		    intent.setType("audio/x-mp3");
		    Intent chooser = Intent.createChooser(intent, "Select soundfile");
		    getParentFragment().startActivityForResult(chooser, 1);
		    picking = false;
		}
		

		if (ready) {
			
			pickASong.setText("Start Playing");
		}
		else if (song) {
			switch(print){
				case 0:
					pickASong.setText("Loading   ");
					break;
				case 1:
					pickASong.setText("Loading.  ");
					break;
				case 2:
					pickASong.setText("Loading.. ");
					break;
				case 3:
					pickASong.setText("Loading...");
					break;
				default:
					pickASong.setText("Loading");
					break;
			}
		}
		
	}
	
	/* Draws the menu*/
	private void drawMenu (Canvas canvas, int height, int width) {
		canvas.drawRect(0, height / 2, width, height, top);
		canvas.drawRect(0, 0, width, height / 2, bottom);
		
		pnp = getResizedBitmap(pnp, height/5, height, width);
		canvas.drawBitmap(pnp, 0, (height/5 )*4, null);
		//pnpRect.set(0, height - (height / 5), width / 3, height);
		//canvas.drawBitmap(pnp, null, pnpRect, null);
		
		logo = getResizedBitmap(logo, height/2, height, width);
		float left = (width/2) - (logo.getWidth()/2);
		canvas.drawBitmap(logo, left, 0, null);
	}
	
	public Bitmap getResizedBitmap(Bitmap bm, double newHeight, int deviceHeight, int deviceWidth) {
		int bitmapHeight = bm.getHeight();
		int bitmapWidth = bm.getWidth();

		/* scale bitmap looking at a new height */
		int scaledHeight = (int)newHeight;
		int scaledWidth = (scaledHeight * bitmapWidth) / bitmapHeight; 
	
		try {
			 if (scaledWidth > deviceWidth)
			 scaledWidth = deviceWidth;
			 
			 bm = Bitmap.createScaledBitmap(bm, scaledWidth, scaledHeight, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		 
		return bm;
	}

	@Override
	public void onUpdate(long dt){
		time += dt;
		if(time > 750){
			print = (print + 1) % 4;
			time = time % 750;
		}
	}
	
	public void wanneerGebruikerOpButtonDruktOfzo() {
		// verkrijg mp3 pad voor Level
		this.swapFor(new Level(bd, length, path));
	}
}
