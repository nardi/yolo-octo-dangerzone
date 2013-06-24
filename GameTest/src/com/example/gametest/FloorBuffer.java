/* De ringbuffer die bijhoudt welke punten er op dit moment op het scherm
 * staan
 * 
 * Dit is de meest basic vorm van de buffer.
 * 
 * NOTICES:
 * Maak EERST de punten aan, en daarna de buffer.
 */

package com.example.gametest;

import android.graphics.Point;
import android.graphics.PointF;

public class FloorBuffer {
	private int index;
	private int bufferSize;
	private int pointCounter;
	private float[] buffer;
	private PointF[] tempBuffer;
	private FloorPoint[] points;
	
	/* Initialiseer de waardes voor de buffer */
	FloorBuffer(FloorPoint[] points) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new float[bufferSize];
		tempBuffer = new PointF[bufferSize];
		pointCounter = bufferSize;
		
		FillBuffer();
	}
	
	
	/* Initialiseer de buffer met de eerste >bufferSize< aantal waardes.
	 * Als er minder waardes dan dit zijn, wordt er een plat vlak gegenereerd.
	 */
	//XXX Deze was private, (?) maar heb ff public gemaakt voor testen
	private void FillBuffer() {
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
	public void Update() {
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
}
