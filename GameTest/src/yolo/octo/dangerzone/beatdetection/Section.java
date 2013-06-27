package yolo.octo.dangerzone.beatdetection;

import java.util.ArrayList;
import java.util.List;

/*
 * Sections are used to keep track of 
 * music sections in an MP3 file. 
 * Sections have a list of beats, a start time and an end time.
 */
public class Section {
	public long startTime;
	public long endTime;
	public final List<Beat> beats = new ArrayList<Beat>();
	public float intensity;
	public double avgTempo;
	
	private int length;
	/* Constructor*/
	public Section(Beat startBeat, float startIntensity) {
		beats.add(startBeat);
		startTime = startBeat.startTime;
		endTime = startBeat.endTime;
		intensity = startIntensity;
		length = 1;
	}
	
	/* Adds a beat to the list and updates the endtime of the section*/
	public void add(Beat beat, float newIntensity) {
		beats.add(beat);
		endTime = beat.endTime;
		length++;
		intensity = (intensity * (length - 1) + newIntensity) / length;
	}
	
	/* Sets the real endtime of the section*/
	public void finish(Beat beat) {
		endTime = beat.startTime;
	}
}
