package yolo.octo.dangerzone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.Score;

public class LevelComplete extends GameObject {

	public Paint paint;
	private Score score;
	private Character player;
	private Character octo;
	private Paint top,
				  bottom,
				  text;
	private int time;
	private Button nextText;
	private int textPart;
	private Menu menu;
	
	/* Constructor*/
	public LevelComplete (Score score) {
		this.score = score;
		textPart = 0;
		time = 0;
		top = new Paint();
		bottom = new Paint();
		text = new Paint();
		top.setColor(Color.rgb(127, 139, 197));
		bottom.setColor(Color.rgb(183, 219, 149));
		
	}
	
	protected void onAttach() {
		Context context = getParentFragment().getActivity();
		menu = new Menu();
		player = new Character(context, 0 , 0);
		octo = new Character(context, 0, 0, 20);
		text.setColor(Color.rgb(232, 118, 0));
		text.setTextSize(40);
		nextText = new Button(context, 0, 0, 100, 100, Color.RED, "Next");
		
		/* Makes the butten keeping track of the text skipping*/
		nextText.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent me) {
				if (me.getActionMasked() == MotionEvent.ACTION_DOWN) {
					nextText.pressed = true;
				}
				if (me.getActionMasked() == MotionEvent.ACTION_UP) {
				
					if (time > 20 && textPart < 4) {
						textPart++;
						time = 0;
					}
					else if (time > 20 && textPart > 3) {
						swapFor(menu);
					}
					nextText.pressed = false;
				}
	
				return true;
			}
		});
		
		addObject(nextText);
		addObject(player);
		addObject(octo);
	}
	
	public void onUpdate(long dt) {
		time++;
	}
	
	/* Draws the end screen*/
	public void onDraw (Canvas canvas) {
		
		int height = this.getParentFragment().getView().getHeight();
		int width = this.getParentFragment().getView().getWidth();
		nextText.setPosition(75, canvas.getHeight() - 75);
		canvas.drawRect(0, 0, width, (height / 3) * 2, top);
		canvas.drawRect(0, (height / 3) * 2, width, height, bottom);
		
		/* Sets the proper x and y coordinates of the player and octo*/
		player.x = width / 8;
		player.y = ((height / 3) * 2) - 100;
		octo.x = width - (width /4);
		octo.y = (height / 2) - 200;
		
		/* Keeps track of what part of the text we are at and draws it on the canvas*/
		switch(textPart) {
			case 0:
				canvas.drawText("Yolo...", width / 3, height /4, text);
				break;
			case 1:
				text.setTextSize(20);
				canvas.drawText("You have come a long way my dear friend...", width / 6, height /4, text);
				break;
			case 2:
				text.setTextSize(30);
				canvas.drawText("One day we will be reunited!", width / 6, height /4, text);
				
				break;
			case 3:
				canvas.drawText("Look for me in the next song...", width / 6, height /4, text);
				break;
			case 4:
				nextText.setText("Menu");
			default:
				break;
		}
	}
	
	public void onHalt() {
		
	}
	
	public void onRun () {}
}
