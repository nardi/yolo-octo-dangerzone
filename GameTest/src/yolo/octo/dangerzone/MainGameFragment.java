package yolo.octo.dangerzone;

import android.content.Intent;
import android.os.Bundle;
import yolo.octo.dangerzone.core.GameFragment;

public class MainGameFragment extends GameFragment {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setTargetFps(60);
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
