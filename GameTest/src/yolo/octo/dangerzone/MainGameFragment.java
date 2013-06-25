package yolo.octo.dangerzone;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import yolo.octo.dangerzone.core.GameFragment;
import yolo.octo.dangerzone.lvlgen.FloorBuffer;
import yolo.octo.dangerzone.lvlgen.LevelDraw;

public class MainGameFragment extends GameFragment {
	Level level = null;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTargetFps(42);
        
        level = new Level();
		addObject(level);
		
		run();
	}
}
