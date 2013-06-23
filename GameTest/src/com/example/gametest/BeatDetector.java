package com.example.gametest;
/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */

public interface BeatDetector {
	public boolean newSamples(double[] samples);
	public double estimateTempo();
	public Beat[] getBeats();
	public Section[] getSections();

}

