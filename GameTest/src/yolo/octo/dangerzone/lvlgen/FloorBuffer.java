/* De ringbuffer die bijhoudt welke punten er op dit moment op het scherm
 * staan
 * 
 * Dit is de meest basic vorm van de buffer.
 * 
 * NOTICES:
 * Maak EERST de punten aan, en daarna de buffer.
 */

package yolo.octo.dangerzone.lvlgen;

import java.util.Random;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.AudioTrack;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

public class FloorBuffer {
	private int index;
	private int bufferSize;

	private int pointCounter = 0;
	private int randomInt;
	private int offset;
	private float[] buffer;
	private PointF[] tempBuffer;
	private float[] points;
	private Random colGen = new Random();

	
	/* Initialiseer de waardes voor de buffer */
	public FloorBuffer(float[] points, int offset) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new float[bufferSize];
		tempBuffer = new PointF[bufferSize];
		this.offset = offset + bufferSize / 4 - 1;
		
		fillBuffer();
	}
	
	/* Initialiseer de buffer met de eerste >bufferSize< aantal waardes.
	 * Als er minder waardes dan dit zijn, wordt er een plat vlak gegenereerd.
	 */
	

	private void fillBuffer() {
		int toSkip = Math.min(bufferSize, offset);
		offset -= toSkip;
		for (int i = toSkip; i < bufferSize; i++) {
			if (pointCounter < points.length) {
				buffer[i] = points[pointCounter];
				pointCounter++;
				
				randomInt = colGen.nextInt(100);
				if (randomInt <= 3) {
					//TODO: Maak nieuwe collectable aan met types 0, 1, 2, of 3
					// Geef i mee!!!
				}
			}
			else {
				buffer[i] = 0;
			}
		}
	}
	
	
	/* Vervangt het meest linker punt met het nieuwe, meest rechter punt. */
	public void update() {

		if (offset > 0 || pointCounter >= points.length) {
			buffer[index] = 0;
			if (offset > 0)
				offset--;
			
			randomInt = colGen.nextInt(100);
			if (randomInt <= 3) {
				//TODO: Maak nieuwe collectable aan met types 0, 1, 2, of 3
				// Geef 399 mee!!! (Want hij moe trechts beginnen
			}

		}
		else {
			buffer[index] = points[pointCounter];
			pointCounter++;
		}
		
		index = (index + 1) % bufferSize;
	}
	
	public void update(int skip){
		for(int i = 0; i < skip; i++){
			update();
		}
	}
	
	/* Geeft de buffer terug voor de teken klasse, in de volgorde van aller linker 
	 * punt op scherm naar aller rechter punt. 
	 */
	public PointF[] getBuffer() {
		for (int i = index, j = 0; j < bufferSize; i++, j++) {
			i %= bufferSize;
			tempBuffer[j] = new PointF();
			tempBuffer[j].y = buffer[i];	
			tempBuffer[j].x = j;
		}
		
		return tempBuffer;
	}
	
	
	/* gets the height on the player's position
	 */
	public float getHeight(View v) {
		int width = v.getWidth();
		int location =(int) ((width/4.0 * 399.0) / width);
		float yValue = (float) (buffer[(index + location) % 400] * 100);
		return yValue;
	}
}
