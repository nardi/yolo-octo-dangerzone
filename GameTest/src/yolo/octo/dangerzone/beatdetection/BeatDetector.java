package yolo.octo.dangerzone.beatdetection;

import java.util.List;

/*
 * BeatDetector interface
 * BeatDetectors use a set of samples to find beats in a song. 
 * They also keep track of sections in a song.
 */

public interface BeatDetector {
	public boolean newSamples(float[] samples);
	public void finishSong();
	public double estimateTempo();
	public List<Beat> getBeats();
	public List<Section> getSections();
}