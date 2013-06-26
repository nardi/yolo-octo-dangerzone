package yolo.octo.dangerzone;

import yolo.octo.dangerzone.core.GameObject;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DrawFilter;
import android.graphics.LightingColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class Button extends GameObject {
	float x, y, width, height;
	Bitmap button1, button2;
	boolean pressed = false;
	RectF rect = new RectF();
	OnTouchListener onTouchListener;
	Paint paint = new Paint(); {
		paint.setAlpha(220);
	}
	Paint textPaint = new Paint(); {
		textPaint.set(paint);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(30);
		textPaint.setColor(Color.WHITE);
	}
	String text;
	
	public Button(Context context, float x, float y, float width, float height, int color, String text) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		updateRect();
		
		setColor(color);
		setText(text);

	    Resources res = context.getResources();
	    try {
			this.button1 = BitmapFactory.decodeResource(res, R.drawable.button1);
			this.button2 = BitmapFactory.decodeResource(res, R.drawable.button2);
	    } catch (Exception e) {
	        Log.e("Button", "Error is " + e);
	    }
	}
	
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
	
	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		updateRect();
	}
	
	protected void onDraw(Canvas canvas) {
		Bitmap button = pressed ? button2 : button1;	
		canvas.drawBitmap(button, null, rect, paint);
		canvas.drawText(text, x, y - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
	}

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
