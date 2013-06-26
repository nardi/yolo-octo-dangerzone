package yolo.octo.dangerzone.lvlgen;

import java.util.List;

import android.util.Log;

import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;

public class LevelGenerator {
	public float[] level;
	private List<Beat> beats;
	//Indices per second
	private int speed;
	private int preload;
	
	/* Constructor class - creates the level generator
	 */
	public LevelGenerator(BeatDetector beatDet, long length, int speed, int preload) {
		this.speed = speed;
		this.preload = preload / (1000 / (speed * 30));
		level = new float[(int)(this.preload + (length / (1000 / (speed * 30))))];
		beats = beatDet.getBeats();
	}
	
	private int timeToIndex(long time) {
		return (int)(preload + time / (1000 / (speed * 30)));
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel(){
		/*
		 * Interpoleren tussen 0 en eerste beat.
		 */
		Beat firstBeat = beats.get(0);
		int firstBeatIndex = timeToIndex(firstBeat.startTime);
		level[firstBeatIndex] = firstBeat.intensity;
		float base = 0;
		float yDiff = level[firstBeatIndex];
		for (int k = preload; k < firstBeatIndex; k++) {
			float factor = (k - preload) / (float)(firstBeatIndex - preload);
			level[k] = base + yDiff * factor;
		}
		
		/*
		 * Interpoleren tussen alle beats.
		 */
		for (int i = 0; i < beats.size() - 1; i++) {
			Beat beat1 = beats.get(i);
			int beatIndex1 = timeToIndex(beat1.startTime);
			Beat beat2 = beats.get(i + 1);
			int beatIndex2 = timeToIndex(beat2.startTime);
			
			/*if (beat2.intensity > beat1.intensity)
				level[beatIndex2] = level[beatIndex1] + beat2.intensity;
			else
				level[beatIndex2] = level[beatIndex1] - beat2.intensity; */
			
			level[beatIndex2] = i % 2 == 0 ? beat2.intensity * 2 : beat2.intensity;
			
			base = level[beatIndex1];
			yDiff = level[beatIndex2] - base;
			for (int k = beatIndex1 + 1; k < beatIndex2; k++) {
				float factor = (k - beatIndex1) / (float)(beatIndex2 - beatIndex1);
				level[k] = base + yDiff * factor;
			}
		}
		
		/*
		 * Interpoleren tussen laatste beat en einde array.
		 */
		int lastBeatIndex = timeToIndex(beats.get(beats.size() - 1).startTime);
		base = level[lastBeatIndex];
		yDiff = 0 - level[lastBeatIndex];
		for (int k = (int) (lastBeatIndex - 1); k < level.length; k++) {
			float factor = (k - lastBeatIndex) / (float)(level.length - 1 - lastBeatIndex);
			level[k] = base + yDiff * factor;
		}	
	}
}
