/* De ringbuffer die bijhoudt welke punten er op dit moment op het scherm
 * staan
 * 
 * Dit is de meest basic vorm van de buffer.
 * 
 * NOTICES:
 * Maak EERST de punten aan, en daarna de buffer.
 */

package yolo.octo.dangerzone.lvlgen;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

public class FloorBuffer {
	private int index;
	private int bufferSize;
	private int pointCounter;
	private float[] buffer;
	private PointF[] tempBuffer;
	private FloorPoint[] points;
	
	/* Initialiseer de waardes voor de buffer */
	public FloorBuffer(FloorPoint[] points) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new float[bufferSize];
		tempBuffer = new PointF[bufferSize];
		pointCounter = bufferSize;
		
		fillBuffer();
	}
	
	
	/* Initialiseer de buffer met de eerste >bufferSize< aantal waardes.
	 * Als er minder waardes dan dit zijn, wordt er een plat vlak gegenereerd.
	 */
	//XXX Deze was private, (?) maar heb ff public gemaakt voor testen
	public void fillBuffer() {
		for (int i = 0; i < bufferSize; i++) {
			if (i < points.length) {
				buffer[i] = points[i].getDev();
			}
			
			else {
				buffer[i] = 0;
			}
		}
	}
	
	
	/* Vervangt het meest linker punt met het nieuwe, meest rechter punt. */
	public void update() {
		if (pointCounter < points.length) {
			buffer[index] = points[pointCounter].getDev();
		}
		else {
			buffer[index] = 0;
		}
		
		index = (index + 1) % bufferSize;
		pointCounter++;
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