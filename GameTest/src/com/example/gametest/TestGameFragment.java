package com.example.gametest;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import edu.emory.mathcs.jtransforms.fft.FloatFFT_1D;

import nobleworks.libmpg.MP3Decoder;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class TestGameFragment extends GameFragment {
	private boolean jump;
	private int jumpHeight;
	private boolean direction;
	private boolean cantTouchThis;
	Coin coin = new Coin(400, 300);

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTargetFps(60);
		this.showStats = true;
		this.alwaysRecieveEvents = true;
		this.jump = false;
		this.jumpHeight = 0;
		this.direction = false;
		this.cantTouchThis = false;

        this.run();
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
				Cursor cursor = getActivity().managedQuery(uri, proj, // Which columns to
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
				Log.e("TestGameFragment", "Something went wrong..");
				return;
			}
			
			new Thread(fourierTransform(path)).start();
		}
	}
	
	AudioTrack at = null;
	
	/*
	 * Plays an mp3. Also writes PCM data to a file.
	 */
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
					byte[] fileBuffer = new byte[bufferSize * 2];
					ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(bufferSize * 2);
					// Audio data is little endian, so for correct bytes -> short conversion:
					nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
					String rawPath = path + ".raw";
					FileOutputStream out = new FileOutputStream(rawPath);
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
						nativeBuffer.get(fileBuffer, 0, readSamples * 2);
						nativeBuffer.position(0);
						while (at.getPlayState() != AudioTrack.PLAYSTATE_PLAYING);
						at.write(buffer, 0, readSamples);
						out.write(fileBuffer, 0, readSamples * 2);
						Log.v("playMp3", "Wrote " + readSamples + " samples");
					}
					/* TODO Stopping here leaves no guarantee everything has been
					 * played, but whatever */ 
					at.stop();
					out.close();
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

	long totalTime;
	boolean touching;
	float touchX, touchY;
	Paint touchCircle = new Paint(); {
		touchCircle.setColor(Color.rgb(80, 120, 200));
		touchCircle.setAntiAlias(true);
		touchCircle.setDither(true);
		touchCircle.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	RectF fullScreen;
	Paint text = new Paint(); {
		text.setColor(Color.WHITE);
		text.setTextSize(30);
	}
	DecimalFormat threeDecimals = new DecimalFormat("0.000");
	
	public Runnable fourierTransform (final String path) {
		return new Runnable() {
			public void run() {
				try {
					MP3Decoder md = new MP3Decoder(path);
					int bufferSize = 512;
					int bassArraySize = (int) (md.getLength() * 2 / bufferSize) + 1;
					double[] calcDatBass = new double[bassArraySize];
					DoubleFFT_1D fft = new DoubleFFT_1D(bufferSize);/*Nardi check dem sizes TODO*/
					double[] fft_out = new double[bufferSize];
					String fftPath = path + ".fft";
					FileOutputStream out = new FileOutputStream(fftPath);
					ByteBuffer nativeBuffer = ByteBuffer.allocateDirect(2 * bufferSize);
					// Audio data is little endian, so for correct bytes -> short conversion:
					nativeBuffer.order(ByteOrder.LITTLE_ENDIAN);
					ShortBuffer shortBuffer = nativeBuffer.asShortBuffer();
					
					int read = -1;
					int i = 0;
					int j = 0;
					double temp = 0;
					while (read != 0) {
						i = 0;
						read = md.readSamples(shortBuffer);
						while (i < read) {
							fft_out[i] = (double)shortBuffer.get()/Short.MAX_VALUE;
							i++;
						}
						shortBuffer.position(0);
						fft.realForward(fft_out);
						Log.v("FFT", "FFT op " + (44100/bufferSize) * 3+ "Hz: " + fft_out[3]);
						temp = Math.abs(fft_out[3]);
						if (temp > 0.1) {
							calcDatBass[j++] = 1;
						}
						else {
							calcDatBass[j++] = 0;
						}
					}
					DoubleFFT_1D bass = new DoubleFFT_1D(bassArraySize);			
					bass.realForward(calcDatBass);
					for (int k = 0; k < bassArraySize; k++) {/*TODO for loops etc*/
						Log.v("FFT", "FFT op " + ((44100.0/bufferSize)/bassArraySize) * k + "Hz: " + calcDatBass[k]);
					}
				}
				catch (Exception e){
					Log.e("Fourier Transform", "Borked!", e);
				}
			}
		};
	}
	
	/*
	 * Is called every time the draw surface gets a new size (i.e. when it is first
	 * initialized and when the screen is rotated).
	 */
	@Override
	public void onResize(int width, int height) {
		fullScreen = new RectF(0, 0, width, height);
		if (touchX == 0 && touchY == 0) {
			touchX = width / 2f;
			touchY = height / 2f;
		}
		touching = false;
	}
	
	/*
	 * Update the game state: dt is the elapsed time in milliseconds since
	 * the (start of the) last update.
	 */
	@Override
	public void onUpdate(long dt) {
		Log.d("TestGameFragment", "onUpdate: dt = " + dt);
		totalTime += dt;
		Log.d("TestGameFragment", "onUpdate: totalTime = " + totalTime);
		
		if (jump) {
			updateY(dt);
		}
	}

	/*
	 * Draw the game: only use this canvas! (threads and stuff)
	 */
	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRGB(100, 149, 237);
		
		if (!touching)
			canvas.saveLayerAlpha(fullScreen, 0x72, 0);
		canvas.drawCircle(touchX, touchY, 70, touchCircle);
		if (!touching)
			canvas.restore();
		
		if (coin != null) {
			if (checkCollisionCoin(touchX, touchY, coin)) {
				coin = null;
			} else
				coin.drawCoin(canvas);
		}
		
		if (at != null) {
			double seconds = (double)at.getPlaybackHeadPosition() / at.getPlaybackRate();
			canvas.drawText("Music position: " + threeDecimals.format(seconds) + "s",
					5, 30, getStatsPaint());
		}
	}
	
	private boolean checkCollisionCoin(float x, float y, Coin coin) {
		boolean collision;
		float xDif = x - coin.x;
		float yDif = y - coin.y;
		double distance = Math.pow(xDif, 2) + Math.pow(yDif, 2);
		if (distance < (70 + coin.radius) * (70 + coin.radius)) {
			collision = true;
		} else {
			collision = false;
		}
		return collision;
	}

	private void showPauseMenu() {
		Fragment frag = new PauseDialogFragment();
		Bundle args = new Bundle();
		args.putInt("gameId", R.id.testGameFragment);
		frag.setArguments(args);
		getActivity().getFragmentManager().beginTransaction()
			.add(frag, "pauseMenu").commit();
	}
	
	public void updateY(long time) {
		/* if direction == true, touchY goes up */
		if (direction) {
			if (jumpHeight >= 200) {
				direction = false;
			} else {
				touchY -= time; 
				jumpHeight += time;
			}
		}
		if (!direction) {
			touchY += time;
			jumpHeight -= time;
			if (jumpHeight <= 0) {
				jump = false;
				cantTouchThis = false;
			}
		}
		
	}
	
	/*
	 * Called when you touch the game view.
	 */
	@Override
	public boolean onTouch(View v, MotionEvent me) {

		//FIXME Conflict#1
		if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
			/*IK CLAIM RECHTSONDER VERDORIE!!!! ~Jordy
			 * Ringbuffer test
			 */
			if (me.getX() > (v.getWidth() - 150) && me.getY() > (v.getHeight() - 150) && isRunning()) {
				
				Log.e("JordyWasHere","Rechtsonder is van mij, bitsjes!");
				
			}
			
			
			if (me.getX() < 150 && me.getY() > v.getHeight() - 150 && jump == false) {
				jump = true;
				direction = true;
				cantTouchThis = true;
			}
			if (touchX < 150 && touchY < 150 && isRunning()) {
				//postHalt();
				//showPauseMenu();
				
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			    intent.setType("audio/x-mp3");
			    Intent chooser = Intent.createChooser(intent, "Select soundfile");
			    startActivityForResult(chooser,1);
			}
			
			//XXX Conflict#1
			/*
			 * Opens a new canvas to draw on when the user taps the upper right corner.
			 */
			/*
			if(me.getX() > this.getView().getWidth() - 150  && me.getY() < 150) {
	
				this.getActivity().setContentView(R.layout.level_layout);
			}
			*/

		}
		if (!cantTouchThis && me.getY() < v.getHeight() - 150) {
			touching = me.getActionMasked() != MotionEvent.ACTION_UP;
			touchX = me.getX();
			touchY = me.getY();
		}
		return true;
	}
	
	/*
	 * Called whenever the game thread is started.
	 */
	@Override
	public void onRun() {
		getActivity().setTitle("Super Duper Game-o-Looper");
		if (at != null && at.getPlayState() == AudioTrack.PLAYSTATE_PAUSED)
			at.play();
	}
	
	/*
	 * Called when the game thread is stopped.
	 * 
	 * TODO We moeten eigenlijk wel onderscheid maken tussen rotatie-stop (misschien
	 * wil je de muziek dan niet laten stoppen) en wanneer je bijv. het scherm uitzet
	 * (dan wel). 
	 */
	@Override
	public void onHalt() {
		getActivity().setTitle("Resting for a moment...");
		if (at != null)
			at.pause();
	}
}
