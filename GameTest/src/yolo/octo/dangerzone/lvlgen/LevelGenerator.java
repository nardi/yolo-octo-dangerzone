package yolo.octo.dangerzone.lvlgen;

import java.io.Serializable;
import java.util.List;
import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.Section;


public class LevelGenerator implements Serializable {
	private float[] level;
	private transient BeatDetector bd;
	private int speed;
	private int preload;
	
	
	/* Constructor class - creates the level generator
	 */
	public LevelGenerator(BeatDetector bd, long length, int speed) {
		this.speed = speed;
		this.preload = 0;
		level = new float[(int)(this.preload + (length / (1000 / (speed * 30))))];
		this.bd = bd;
		
		generateLevel();
	}
	
	/* timeToIndex converts a given time to an index in the array, assuming 
	 * the game plays with 30 FPS. (It actually forces this) 
	 */
	private int timeToIndex(long time) {
		return (int)(preload + time / (1000 / (speed * 30)));
	}
	
	/* below are a few of our interpolation methods to fill the gaps between 
	 * beats.
	 */
	
	/* Interpolation method 1: Linear interpolation
	 * It draws straight lines between beats, resulting in jaggy peaks.
	 */
	private float linearInterpolation (float y1, float y2, float factor) {
		return y1 + ((y2 - y1) * factor);
	}

	/* Interpolation method 2: Cosine interpolation
	 * It fits a cosine function through the points, resulting in a more fluent
	 * level than you get with linear, with rounded hills and valleys. 
	 */
	private float cosineInterpolation(float y1, float y2, float factor) {
		float cosFactor = (float)((1 - Math.cos(factor * Math.PI)) / 2);
		return y1 * (1 - cosFactor) + y2 * cosFactor;
	}
	
	
	/* Interpolation method 3: Bad-ass interpolation
	 * Gives results similar to cosineInterpolation, but works faster
	 */
	private float badassInterpolation(float y1, float y2, float factor) {
		return y1 + factor * factor * (3 - 2 * factor) * (y2 - y1);
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel() {
		List<Beat> beats = bd.getBeats();
		List<Section> sections = bd.getSections();
		
		/*
		 * Interpolate between 0 and the first beat.
		 */
		Beat firstBeat = beats.get(0);
		int firstBeatIndex = timeToIndex(firstBeat.startTime);
		level[firstBeatIndex] = 0;
		for (int k = preload; k < firstBeatIndex; k++) {
			float factor = (k - preload) / (float)(firstBeatIndex - preload);
			level[k] = badassInterpolation(0, level[firstBeatIndex], factor);
		}
		
		/*
		 * Interpolate between the beats.
		 */
		for (int i = 0; i < beats.size() - 1; i++) {			
			
			Beat beat1 = beats.get(i);
			int beatIndex1 = timeToIndex(beat1.startTime);
			Beat beat2 = beats.get(i + 1);
			int beatIndex2 = timeToIndex(beat2.startTime);
			
			level[beatIndex2] = 0.7f * beat2.intensity * (i % 2 == 0 ? 1 : -1);
						
			for (int k = beatIndex1 + 1; k < beatIndex2; k++) {
				float factor = (k - beatIndex1) / (float)(beatIndex2 - beatIndex1);
				level[k] = badassInterpolation(level[beatIndex1], level[beatIndex2], factor);
			}
		}
		
		/*
		 * Interpolate between the final beat and the end of the array.
		 */
		int lastBeatIndex = timeToIndex(beats.get(beats.size() - 1).startTime);		
		for (int k = lastBeatIndex + 1; k < level.length; k++) {
			float factor = (k - lastBeatIndex) / (float)(level.length - 1 - lastBeatIndex);
			level[k] = badassInterpolation(level[lastBeatIndex], 0, factor);
		}
		
		if (!sections.isEmpty()) {
			Section firstSection = sections.get(0);
			int firstSectionIndex = timeToIndex(firstSection.startTime);
			float prevSectionIntensity = firstSection.intensity * firstSectionIndex / 400f;
			level[firstSectionIndex] += prevSectionIntensity;
			for (int k = preload; k < firstSectionIndex; k++) {
				float factor = (k - preload) / (float)(firstSectionIndex - preload);
				level[k] += badassInterpolation(0, prevSectionIntensity, factor);
			}
			
			for (int i = 0; i < sections.size() - 1; i++) {
				Section section1 = sections.get(i);
				int sectionIndex1 = timeToIndex(section1.startTime);
				Section section2 = sections.get(i + 1);
				int sectionIndex2 = timeToIndex(section2.startTime);
				
				float curSectionIntensity;				
				if (section1.intensity > section2.intensity) {
					curSectionIntensity = section2.intensity / 5;
				} else {
					curSectionIntensity = section2.intensity / 2;
				}
				level[sectionIndex2] += curSectionIntensity;
				
				for (int k = sectionIndex1 + 1; k < sectionIndex2; k++) {
					float factor = (k - sectionIndex1) / (float)(sectionIndex2 - sectionIndex1);
					level[k] += badassInterpolation(prevSectionIntensity, curSectionIntensity, factor);
				}
				
				prevSectionIntensity = curSectionIntensity;
			}
			
			Section lastSection = sections.get(sections.size() - 1);
			int lastSectionIndex = timeToIndex(lastSection.startTime);		
			for (int k = lastSectionIndex + 1; k < level.length; k++) {
				float factor = (k - lastSectionIndex) / (float)(level.length - 1 - lastSectionIndex);
				level[k] += badassInterpolation(prevSectionIntensity, 0, factor);
			}
		}
	}
	
	/* Returns the generated level
	 */
	public float[] getLevel(){
		return this.level;
	}
	
}
