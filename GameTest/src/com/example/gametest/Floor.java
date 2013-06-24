package com.example.gametest;

public class Floor {
	/* TODO:
	 * De floor heeft een standaard hoogte en loopt van 0 tot getWidth.
	 * Dus heb ik getWidth nodig, en de std hoogte.
	 * Bereken het effect van de afwijking op de hoogte.
	 * Teken de lijnen.
	 * Houdt de hoogt eop positie X bij voor #yolo.
	 * meer?
	 */
	FloorBuffer buffer;
	double[] points;
	
	
	Floor(FloorBuffer buffer) {
		buffer = buffer;
	}
	
	public void DrawFloor() {
		//points = buffer.getBuffer();
		
		/* Voor elk punt, teken een lijn tussen dit punt en het volgende punt. */
		for (int i = 0; i + 1 < points.length; i++) {
			
		}
	}
}
