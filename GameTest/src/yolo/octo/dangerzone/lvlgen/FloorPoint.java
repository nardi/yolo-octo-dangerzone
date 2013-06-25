/* Floorpoints zijn te punten waarmee het oppervlak van het level wordt gegenereerd.
 * Twee punten samen vormen een lijn.
 * 
 * Voorstel:
 * Elk punt start op een standaard y-coordinaat. Bijhet constructen van een 
 * punt kun je een afwijking meegeven tussen de 1 en -1, welke bepalen 
 * op welke hoogte het punt komt.
 */

package yolo.octo.dangerzone.lvlgen;

public class FloorPoint {
	private double deviation;
	
	/* Maak een punt aan met een bepaalde afwijking tussen -1 en 1. */
	FloorPoint(double deviation) {
		this.deviation = deviation;
	}
	
	
	/* Geeft de afwijking van het punt terug aan de ringbuffer. */
	public double getDev() {
		return this.deviation;
	}
}
