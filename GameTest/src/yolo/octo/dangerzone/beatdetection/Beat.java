package yolo.octo.dangerzone.beatdetection;
/*
 * Beat objects are used to keep track of onset time, length and 
 * intensity of a beat.
 */

public class Beat {
	public long startTime;
	public long endTime;

	public float intensity;
	
	public long time() {
		return (startTime + endTime) / 2;
	}
	
	private int length;
	
	/* Beat constructor*/
	public Beat(long startTime, float startIntensity) {
		this.startTime = endTime = startTime;
		intensity = startIntensity;
		length = 1;
	}
	
	/* Used to increase the length and intensity of a beat*/
	public void add(long time, float newIntensity) {
		endTime = time;
		length++;
		intensity = (intensity * (length - 1) + newIntensity) / length;
	}
	
	/* Called to mark the end of a beat*/
	public void finish(long time) {
		endTime = time;
	}
}