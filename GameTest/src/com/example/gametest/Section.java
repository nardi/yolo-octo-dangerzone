package com.example.gametest;

import java.util.ArrayList;
import java.util.List;

public class Section {
	public long startTime;
	public long endTime; // of length, kan ook
	public final List<Beat> beats = new ArrayList<Beat>();
	public double intensity;
	public double avgTempo;
	
	private int length;
	
	public Section(Beat startBeat, double startIntensity) {
		beats.add(startBeat);
		startTime = startBeat.startTime;
		endTime = startBeat.endTime;
		intensity = startIntensity;
		length = 1;
	}
	
	public void add(Beat beat, double newIntensity) {
		beats.add(beat);
		endTime = beat.endTime;
		length++;
		intensity = (intensity * (length - 1) + newIntensity) / length;
	}
	
	public void finish(Beat beat) {
		endTime = beat.startTime;
	}
}
