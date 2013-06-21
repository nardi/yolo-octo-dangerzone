package com.example.gametest;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

/*
 * Based on code from http://www.mysecretroom.com/www/programming-and-software/android-game-loops
 */

public abstract class GameFragment extends Fragment
	implements SurfaceHolder.Callback, View.OnTouchListener, BaseGameObject {
	private final static int MAX_FRAME_SKIPS = 5;

	private final GameFragment fragment = this;
	
	private GameThread thread = new GameThread();
	private boolean running = false;
	private SurfaceHolder surfaceHolder = null;
	/*
	 * surfaceCreated is true when the surface actually exists
	 * (for thread safety).
	 */
	private boolean surfaceCreated = false;
	/*
	 * paused is used to indicate a halt (pause) caused by the OS,
	 * which means the thread should be restarted automatically.
	 */
	private boolean paused = false;

	private int targetFps;
	private float updatePeriod;
	public boolean showStats = false;
	public boolean alwaysRecieveEvents = false;
	
	private List<BaseGameObject> autoObjects = new ArrayList<BaseGameObject>();
	
	private Paint statsPaint = new Paint();
	private long beginTime, timeDiff, sleepTime, updateTime,
		updateCount, drawCount, gameStartTime;

	{
		setTargetFps(60);
		
		statsPaint.setColor(Color.WHITE);
		statsPaint.setStyle(Style.FILL_AND_STROKE);
		statsPaint.setStrokeWidth(0);
		statsPaint.setTextSize(30);
	}
	
	public void setTargetFps(int fps) {
		targetFps = fps;
		updatePeriod = 1000f / targetFps;
	}
	
	public long getTotalTime() {
		if (gameStartTime == 0)
			return 0;
		return SystemClock.uptimeMillis() - gameStartTime;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public Paint getStatsPaint() {
		return statsPaint;
	}
	
	public void addObject(GameObject go) {
		autoObjects.add(go);
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SurfaceView surface = new SurfaceView(this.getActivity());
		surface.getHolder().addCallback(this);
		surface.setFocusable(true);
		surface.setOnTouchListener(thread);
		return surface;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
		
		Canvas c = holder.lockCanvas();
		thread.draw(c);
		holder.unlockCanvasAndPost(c);
		
		surfaceCreated = true;
		
		if (thread.waiting())
			thread.shouldWait(false);
	}
	
	protected void onRun() {}

	public synchronized void run() {
		if (!running) {
			onRun();
			running = true;
			if (thread.getState() != Thread.State.NEW) {
				thread = new GameThread();
				getView().setOnTouchListener(thread);
			}
			thread.start();
		}
	}
	
	protected void onHalt() {}
	
	public void postHalt() {
		onHalt();
		running = false;
	}
	
	public synchronized void halt() {
		if (running) {
			postHalt();
			while (true) {
				try {
					thread.join();
					return;
				} catch (InterruptedException e) { }
			}
		}
	}
	
	public void preUpdate(long dt) {}
	public void onUpdate(long dt) {}
	public void postUpdate(long dt) {}

	public void preDraw(Canvas canvas) {}
	public void onDraw(Canvas canvas) {}
	public void postDraw(Canvas canvas) {}
	
	public void onResize(int width, int height) {}
	
	@Override
	public boolean onTouch(View v, MotionEvent me) {
		return false;
	}
	
	private class GameThread extends Thread implements View.OnTouchListener {
		private boolean m_waiting = false;
		private boolean m_shouldWait = false;
		private Object waitLock = new Object();
		
		public boolean waiting() {
			synchronized (waitLock) { return m_waiting; }
		}
		
		public void shouldWait(boolean b) {
			synchronized (waitLock) { m_shouldWait = b; }
		}			
		
		long prevUpdate;
		
		private synchronized void update() {
			if (prevUpdate == 0)
				prevUpdate = SystemClock.uptimeMillis();
			long dt = SystemClock.uptimeMillis() - prevUpdate;
			preUpdate(dt);
			onUpdate(dt);
			for (BaseGameObject go : autoObjects)
				go.onUpdate(dt);
			postUpdate(dt);
			prevUpdate += dt;
		}
		
		private void drawStats(Canvas canvas) {
			canvas.drawText("Last update time: " + updateTime, 5, canvas.getHeight() - 75, statsPaint);
			canvas.drawText("Update count: " + updateCount, 5, canvas.getHeight() - 40, statsPaint);
			canvas.drawText("Skipped frames: " + (updateCount - drawCount), 5, canvas.getHeight() - 5, statsPaint);
		}

		@SuppressLint("WrongCall")
		private synchronized void draw(Canvas canvas) {
			preDraw(canvas);
			onDraw(canvas);
			for (BaseGameObject go : autoObjects)
				go.onDraw(canvas);
			postDraw(canvas);
			if (showStats)
				drawStats(canvas);
		}
		
		@Override
		public void run() {
			int framesSkipped;
			
			if (gameStartTime == 0)
				gameStartTime = SystemClock.uptimeMillis();
			
			while (running) {
				if (m_shouldWait) {
					m_waiting = true;
					while (m_shouldWait);
					m_waiting = false;
				}
				
				framesSkipped = 0;
				beginTime = SystemClock.uptimeMillis();
				update();
				
				if (surfaceCreated) {
					Canvas canvas = null;
					try {
						canvas = surfaceHolder.lockCanvas();
						synchronized (surfaceHolder) {
							if (canvas != null) {
								draw(canvas);
							}
						}
					} finally {
						if (canvas != null)
							surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}

				timeDiff = SystemClock.uptimeMillis() - beginTime;
				sleepTime = (long)(updatePeriod - timeDiff);
				
				if (sleepTime > 0)
					SystemClock.sleep(sleepTime);
				
				while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
					update();
					sleepTime += updatePeriod;
					framesSkipped++;
				}
				
				updateTime = (SystemClock.uptimeMillis() - beginTime) / (framesSkipped + 1);
				updateCount += framesSkipped + 1;
				drawCount++;
				
				Log.d("GameFragment", "Game time = " + getTotalTime());
			}
		}

		@Override
		public synchronized boolean onTouch(View v, MotionEvent me) {
			if (running || alwaysRecieveEvents)
				return fragment.onTouch(v, me);
			return false;
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		onResize(width, height);
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceCreated = false;
		/*
		 * To stop the thread from accessing the surface, you can either shut it down
		 * or block it after completing the current update-draw cycle.
		 * It seems to be about equal in performance.
		 */
		/* thread.shouldWait(true); 	// Block thread
		while (!thread.waiting()); */
		this.halt(); 					// Shut down thread
		surfaceHolder = null;
    }
	
	@Override
	public void onPause() {
		super.onPause();

		if (running) {
			this.halt();
			paused = true;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();

		if (paused) {
			this.run();
			paused = false;
		}
	}
}
