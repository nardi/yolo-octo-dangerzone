package com.example.gametest;

import android.view.MotionEvent;
import android.view.View;

public interface Touchable {
	public boolean touch(View v, MotionEvent me);
}
