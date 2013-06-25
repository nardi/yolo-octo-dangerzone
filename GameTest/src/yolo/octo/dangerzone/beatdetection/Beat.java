package yolo.octo.dangerzone.beatdetection;

public class Beat {
	public long startTime;
	public long endTime;
	// intensity kan gebaseerd worden op amplitude, of later ook
	// op de frequentiebanden waar ze in voorkomen bijvoorbeeld
	public float intensity;
	
	public long time() {
		return (startTime + endTime) / 2;
	}
	
	private int length;
	
	public Beat(long startTime, float startIntensity) {
		this.startTime = endTime = startTime;
		intensity = startIntensity;
		length = 1;
	}
	
	public void add(long time, float newIntensity) {
		endTime = time;
		length++;
		intensity = (intensity * (length - 1) + newIntensity) / length;
	}
	
	public void finish(long time) {
		endTime = time;
	}
}