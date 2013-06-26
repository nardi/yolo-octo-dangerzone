package yolo.octo.dangerzone;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import nobleworks.libmpg.MP3Decoder;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;
import yolo.octo.dangerzone.beatdetection.Section;
import yolo.octo.dangerzone.core.GameObject;

public class Menu extends GameObject {
	
	private BeatDetector bd;
	public long length;
	public int time, print;
	
	protected void onAttach() {	
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
	    intent.setType("audio/x-mp3");
	    Intent chooser = Intent.createChooser(intent, "Select soundfile");
	    getParentFragment().startActivityForResult(chooser, 1);
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
			
			new Thread(loadLevel(path)).start();
		}
	}
	
	private Runnable loadLevel(final String path) {
		return new Runnable() {
			public void run() {
				try {
					MP3Decoder md = new MP3Decoder(path);
					int fftBufferSize = 1024;
					int bufferSize = fftBufferSize * 100;
					ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(2 * fftBufferSize * md.getNumChannels());
					// Audio data is little endian, so for correct bytes -> short conversion:
					nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
					float[] audioData = new float[fftBufferSize];
					
					bd = new FFTBeatDetector(md.getRate(), md.getNumChannels(), md.getNumChannels() * fftBufferSize);
					
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
							while (j < fftBufferSize) {
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
					length  = md.getLength()/ md.getRate();
					swapFor(new Level(bd, length));
				} catch (Exception e) {
					Log.e("loadLevel", "Oops!", e);
				}
			}
		};
	}
	
	@Override
	public void onDraw(Canvas canvas){
		Paint paint = new Paint();
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.RED);
		paint.setTextSize(25);
		int height = this.getParentFragment().getView().getHeight() / 2;
		int width = this.getParentFragment().getView().getWidth() / 2;
		Log.e("Draw", "Drawing");
		switch(print){
			case 0:
				canvas.drawText("Loading", width, height, paint);
			case 1:
				canvas.drawText("Loading.", width, height, paint);
			case 2:
				canvas.drawText("Loading..", width, height, paint);
			case 3:
				canvas.drawText("Loading...", width, height, paint);
		}
	}
	
	@Override
	public void onUpdate(long dt){
		if(time > 750){
			print++;
			if(print > 3){
				Log.e("update", "Resetting print");
				print = 0;
			}
			time = 0;
		}
		else{
			time += dt;
		}
	}
	

	public void wanneerGebruikerOpButtonDruktOfzo() {
		// verkrijg mp3 pad voor Level
		this.swapFor(new Level(bd, length));
	}
}
