package yolo.octo.dangerzone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import yolo.octo.dangerzone.core.GameFragment;
import yolo.octo.dangerzone.lvlgen.FloorBuffer;
import yolo.octo.dangerzone.lvlgen.LevelDraw;

public class MainGameFragment extends GameFragment {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTargetFps(30);
        //this.showStats = true;
        
		addObject(new Menu());
		
		run();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (childObjects.get(0) instanceof Menu) {
			Menu menu = (Menu)childObjects.get(0);
			menu.onActivityResult(requestCode, resultCode, data);
		}
	}
}
