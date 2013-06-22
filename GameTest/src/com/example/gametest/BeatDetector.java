package com.example.gametest;

public interface BeatDetector {
	public boolean newSamples(double[] samples);
	public double estimateTempo();
	public Beat[] getBeats();
	public Section[] getSections();
}
