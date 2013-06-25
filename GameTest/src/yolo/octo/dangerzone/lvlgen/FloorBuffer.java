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

public class FloorBuffer {
	private int index;
	private int bufferSize;
	private int pointCounter;
	private double[] buffer;
	private Point[] tempBuffer;
	private FloorPoint[] points;
	
	/* Initialiseer de waardes voor de buffer */
	FloorBuffer(FloorPoint[] points) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new double[bufferSize];
		tempBuffer = new Point[bufferSize];
		pointCounter = bufferSize;
		
		FillBuffer();
	}
	
	
	/* Initialiseer de buffer met de eerste >bufferSize< aantal waardes.
	 * Als er minder waardes dan dit zijn, wordt er een plat vlak gegenereerd.
	 */
	private void FillBuffer() {
		for (int i = 0; i < bufferSize; i++) {
			if (i < points.length) {
				buffer[i] = points[i].getDev();
			}
			
			else {
				buffer[i] = 0.0;
			}
		}
	}
	
	
	/* Vervangt het meest linker punt met het nieuwe, meest rechter punt. */
	public void Update() {
		if (pointCounter < points.length) {
			buffer[index] = points[pointCounter].getDev();
		}
		else {
			buffer[index] = 0.0;
		}
		
		index = (index + 1) % bufferSize;
		pointCounter++;
	}
	
	
	/* Geeft de buffer terug voor de teken klasse, in de volgorde van aller linker 
	 * punt op scherm naar aller rechter punt. 
	 */
	public Point[] getBuffer() {
		for (int i = index, j = 0; j < bufferSize; i++, j++) {
			i %= bufferSize;
			tempBuffer[j].y = (int) Math.round(buffer[i]);	
			tempBuffer[j].x = j;
		}
		
		return tempBuffer;
	}
}
