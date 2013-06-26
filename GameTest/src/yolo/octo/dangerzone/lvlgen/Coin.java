package yolo.octo.dangerzone.lvlgen;

import yolo.octo.dangerzone.core.GameObject;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Coin extends GameObject {
	public float x, y;
	public float radius = 40;
	public int speed = 0;
	private Score score;
	
	Paint coinPaint = new Paint(); {
		coinPaint.setColor(Color.rgb(212,175,55));
		coinPaint.setAntiAlias(true);
		coinPaint.setDither(true);
		coinPaint.setShadowLayer(2, 0, 0, Color.argb(0x42, 0, 0, 0));
	}
	
	public Coin(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void onUpdate(long dt) {
		this.x -= dt/speed;
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawCircle(x, y, radius, coinPaint);
	}

}
