/* Foreground objects is een superklasse voor alle objecten op de voorgrond.
 * Het bevat eigenschappen als de locatie die alle objecten op de voorgrond. 
 */

package com.example.gametest;

public class ForegroundObject {
	public int x;
	public int y;
	
	ForegroundObject(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	ForegroundObject(int[] x, int[] y) {
		for (int i = 0; i < x.length; i++) {
			new ForegroundObject(x[i], y[i]);
		}
	}
}
