package com.example.gametest;

import java.util.List;
/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */

public interface BeatDetector {
	public boolean newSamples(double[] samples);
	public void finishSong();
	public double estimateTempo();
	public List<Beat> getBeats();
	public List<Section> getSections();
}

