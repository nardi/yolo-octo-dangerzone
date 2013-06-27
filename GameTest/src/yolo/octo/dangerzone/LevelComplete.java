package yolo.octo.dangerzone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import yolo.octo.dangerzone.core.GameObject;
import yolo.octo.dangerzone.lvlgen.Score;

public class LevelComplete extends GameObject {

	public Paint paint;
	public Canvas canvas;
	private Score score;
	private Character player;
	private Character octo;
	
	public LevelComplete (Score score) {
		this.score = score;
		
	}
	
	protected void onAttach() {
		Context context = getParentFragment().getActivity();
		player = new Character(context, 0, 0);
		//octo = new Character(context, );
	}
	
	public void onUpdate(long dt) {
		
	}
	
	public void onDraw (Canvas canvas) {
		
	}
	
	public void onHalt() {
		
	}
	
	public void onRun () {}
}
