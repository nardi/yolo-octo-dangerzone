package yolo.octo.dangerzone.lvlgen;

import java.util.List;

import android.util.Log;

import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;

public class LevelGenerator {
	public float[] level;
	private List<Beat> beats;
	private double lastIntens;
	//Indices per second
	private int speed;
	
	
	/* Constructor class - creates the level generator		
	 */
	public LevelGenerator(BeatDetector beatDet, long length, int speed) {
		this.speed = speed;
		level = new float[(int)(400 + (length / (1000 / (speed * 30))))];
		//beats = (Beat[]) (beatDet.getBeats().toArray()); //TODO retrieve beats here
		beats = beatDet.getBeats();
	}
	
	private int timeToIndex(long time) {
		return (int)(time / (1000 / (speed * 30)));
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel(){
		/*
		 * Interpoleren tussen 0 en eerste beat.
		 */
		Beat firstBeat = beats.get(0);
		float firstBeatIndex = timeToIndex(firstBeat.time());
		level[(int) firstBeatIndex] = firstBeat.intensity;
		for (int k = 0; k < firstBeatIndex; k++) {
			level[k] = level[(int) firstBeatIndex] * k / (float)firstBeatIndex;
		}
		
		/*
		 * Interpoleren tussen alle beats.
		 */
		for (int i = 0; i < beats.size() - 1; i++) {
			Beat beat1 = beats.get(i);
			int beatIndex1 = timeToIndex(beat1.time());
			Beat beat2 = beats.get(i + 1);
			int beatIndex2 = timeToIndex(beat2.time());
			
			/*if (beat2.intensity > beat1.intensity)
				level[beatIndex2] = level[beatIndex1] + beat2.intensity;
			else
				level[beatIndex2] = level[beatIndex1] - beat2.intensity; */
			
			level[beatIndex2] = beat2.intensity;
			
			for (int k = beatIndex1 + 1; k < beatIndex2; k++) {
				level[k] = (level[beatIndex2] - level[beatIndex1]) *
						(k - beatIndex1) / (float)(beatIndex2 - beatIndex1);
			}
		}
		
		/*
		 * Interpoleren tussen laatste beat en einde array.
		 */
		float lastBeatIndex = timeToIndex(beats.get(beats.size() - 1).time());
		for (int k = (int) (lastBeatIndex - 1); k < level.length; k++) {
			level[k] = level[(int) lastBeatIndex] * (1 - (k - lastBeatIndex) / (float)(level.length - 1 - lastBeatIndex));
		}	
	}
}
