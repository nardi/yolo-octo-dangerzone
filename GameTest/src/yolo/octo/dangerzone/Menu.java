package yolo.octo.dangerzone;

import yolo.octo.dangerzone.core.GameObject;

public class Menu extends GameObject {
	public Menu() {	
		//dingen maken
	}

	public void wanneerGebruikerOpButtonDruktOfzo() {
		// verkrijg mp3 pad voor Level
		this.swapFor(new Level());
	}
}
