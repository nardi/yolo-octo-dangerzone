package com.example.gametest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class TestGameFragment extends GameFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTargetFps(60);
		this.showStats = true;
		this.alwaysRecieveEvents = true;

        this.run();
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
		SystemClock.sleep(5);
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
	}

	private void showPauseMenu() {
		Fragment frag = new PauseDialogFragment();
		Bundle args = new Bundle();
		args.putInt("gameId", R.id.testGameFragment);
		frag.setArguments(args);
		getActivity().getFragmentManager().beginTransaction()
			.add(frag, "pauseMenu").commit();
	}
	
	/*
	 * Called when you touch the game view.
	 */
	@Override
	public boolean onTouch(View v, MotionEvent me) {
		touching = me.getActionMasked() != MotionEvent.ACTION_UP;
		touchX = me.getX();
		touchY = me.getY();
		if (touchX < 50 && touchY < 50 && isRunning()) {
			postHalt();
			showPauseMenu();
		}
		return true;
	}
	
	/*
	 * Called whenever the game thread is started.
	 */
	@Override
	public void onRun() {
		getActivity().setTitle("Super Duper Game-o-Looper");
	}
	
	/*
	 * Called when the game thread is stopped.
	 */
	@Override
	public void onHalt() {
		getActivity().setTitle("Resting for a moment...");
	}
}
