/* De ringbuffer die bijhoudt welke punten er op dit moment op het scherm
 * staan
 * 
 * Dit is de meest basic vorm van de buffer.
 */

package com.example.gametest;

public class FloorBuffer {
	private int index;
	private int bufferSize;
	private double[] buffer;
	private FloorPoint[] points;
	
	/* Initialiseer de waardes voor de buffer */
	FloorBuffer(FloorPoint[] points) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new double[bufferSize];
	}
	
	
	/* Update de buffer met nieuwe waardes. Hier mee wordt het punt helemaal
	 * links weggegooid, en komt er rechts een nieuw punt. Als er geen punten
	 * meer zijn (einde level) wordt er een plat vlak getekend.
	 */
	public void UpdateBuffer() {
		for (int i = 0; i < bufferSize; i++) {
			if (points[i+index] != null) {
				buffer[i] = points[i+index].getDev();
			}
			
			else {
				buffer[i] = 0.0;
			}
		}
		
		index++;
	}
	
	/* Geeft de buffer terug voor de teken klasse. */
	public double[] getBuffer() {
		return buffer;
	}
}
