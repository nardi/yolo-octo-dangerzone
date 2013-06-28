package yolo.octo.dangerzone;

import yolo.octo.dangerzone.core.GameObject;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/*
 * Button
 * Has a text field, color, position and size.
 * Used in the game and in the menu. 
 */
public class Button extends GameObject {
	private float x, y, width, height;
	public boolean pressed = false;
	private Bitmap button1, button2;
	private RectF rect = new RectF();
	private OnTouchListener onTouchListener;
	
	private Paint paint = new Paint(); {
		paint.setAlpha(220);
	}
	
	private Paint textPaint = new Paint(); {
		textPaint.set(paint);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(30);
		textPaint.setColor(Color.WHITE);
	}
	private String text;
	
	/* Constructor*/
	public Button(Context context, float x, float y, float width, float height, int color, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		updateRect();
		
		setColor(color);
		setText(text);

	    Resources res = context.getResources();
	    /* Tries using the button pictures*/
	    try {
			this.button1 = BitmapFactory.decodeResource(res, R.drawable.button1);
			this.button2 = BitmapFactory.decodeResource(res, R.drawable.button2);
	    } catch (Exception e) {
	        Log.e("Button", "Error is " + e);
	    }
	}
	/* Used to update the size and location of the button (only changes on a rotation)*/
	private void updateRect() {
		rect.left = x - width / 2;
		rect.right = x + width / 2;
		rect.top = y - height / 2;
		rect.bottom = y + height / 2;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setColor(int color) {
		paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateRect();
	}
	
	/* Used to set the size*/
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		updateRect();
	}
	
	/* Draws the button on the canvas*/
	protected void onDraw(Canvas canvas) {
		Bitmap button = pressed ? button2 : button1;	
		canvas.drawBitmap(button, null, rect, paint);
		canvas.drawText(text, x, y - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
	}
	
	/* Functions to handle the touch*/
	public void setOnTouchListener(OnTouchListener otl) {
		onTouchListener = otl;
	}
	
	protected boolean onTouch(View v, MotionEvent me) {
		if (rect.contains(me.getX(), me.getY())) {
			return onTouchListener.onTouch(v, me);
		}
		return false;
	}
}
