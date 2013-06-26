package yolo.octo.dangerzone.lvlgen;

import java.util.List;

import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;

public class LevelGenerator {
	public float[] level;
	private BeatDetector bd;
	//Indices per second
	private int speed;
	private int preload;
	
	/* Constructor class - creates the level generator
	 */
	public LevelGenerator(BeatDetector bd, long length, int speed, int preload) {
		this.speed = speed;
		this.preload = 99 + preload / (1000 / (speed * 30));
		level = new float[(int)(this.preload + (length / (1000 / (speed * 30))))];
		this.bd = bd;
	}
	
	private int timeToIndex(long time) {
		return (int)(preload + time / (1000 / (speed * 30)));
	}
	
	private float cosineInterpolation(float y1, float y2, float factor) {
		float cosFactor = (float)((1 - Math.cos(factor * Math.PI)) / 2);
		return y1 * (1 - cosFactor) + y2 * cosFactor;
	}
	
	private float badassInterpolation(float y1, float y2, float factor) {
		return y1 + factor * factor * (3 - 2 * factor) * (y2 - y1);
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel() {
		List<Beat> beats = bd.getBeats();
		double tempo = bd.estimateTempo();
		double beatSteps = 4 * (60000 / tempo) / (1000 / (speed * 30));
		
		/*
		 * Interpoleren tussen 0 en eerste beat.
		 */
		Beat firstBeat = beats.get(0);
		int firstBeatIndex = timeToIndex(firstBeat.startTime);
		level[firstBeatIndex] = 0;
		for (int k = preload; k < firstBeatIndex; k++) {
			float factor = (k - preload) / (float)(firstBeatIndex - preload);
			level[k] = badassInterpolation(0, level[firstBeatIndex], factor);
			level[k] += factor * -0.10f * (float)Math.cos(2 * Math.PI * (k - firstBeatIndex) / beatSteps);
		}
		
		/*
		 * Interpoleren tussen alle beats.
		 */
		for (int i = 0; i < beats.size() - 1; i++) {
			Beat beat1 = beats.get(i);
			int beatIndex1 = timeToIndex(beat1.startTime);
			Beat beat2 = beats.get(i + 1);
			int beatIndex2 = timeToIndex(beat2.startTime);
			
			/* if (beat2.intensity > beat1.intensity)
				level[beatIndex2] = level[beatIndex1] + beat2.intensity;
			else
				level[beatIndex2] = level[beatIndex1] - beat2.intensity; */
			
			level[beatIndex2] = 0.7f * beat2.intensity * (i % 2 == 0 ? 1 : -1);
			
			for (int k = beatIndex1 + 1; k < beatIndex2; k++) {
				float factor = (k - beatIndex1) / (float)(beatIndex2 - beatIndex1);
				level[k] = badassInterpolation(level[beatIndex1], level[beatIndex2], factor);
				level[k] += -0.10f * (float)Math.cos(2 * Math.PI * (k - firstBeatIndex) / beatSteps);
			}
		}
		
		/*
		 * Interpoleren tussen laatste beat en einde array.
		 */
		int lastBeatIndex = timeToIndex(beats.get(beats.size() - 1).startTime);		
		for (int k = (int) (lastBeatIndex - 1); k < level.length; k++) {
			float factor = (k - lastBeatIndex) / (float)(level.length - 1 - lastBeatIndex);
			level[k] = badassInterpolation(level[lastBeatIndex], 0, factor);
			level[k] += (1 - factor) * -0.15f * (float)Math.cos(2 * Math.PI * (k - firstBeatIndex) / beatSteps);
		}		
	}
}
