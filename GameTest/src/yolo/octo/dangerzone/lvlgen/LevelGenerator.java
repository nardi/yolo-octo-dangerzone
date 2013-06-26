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
	private int ips = 30;
	
	
	/* Constructor class - creates the level generator		
	 */
	public LevelGenerator(BeatDetector beatDet, long length) {
		level = new float[(int)(400 + (length / (1000 / 30)))];
		//beats = (Beat[]) (beatDet.getBeats().toArray()); //TODO retrieve beats here
		beats = beatDet.getBeats();
	}
	
	private int timeToIndex(long time) {
		return (int)(time / (1000 / 30));
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel(){
		/*
		 * Interpoleren tussen 0 en eerste beat.
		 */
		Beat firstBeat = beats.get(0);
		int firstBeatIndex = timeToIndex(firstBeat.time());
		level[firstBeatIndex] = firstBeat.intensity;
		for (int k = 0; k < firstBeatIndex; k++) {
			level[k] = level[firstBeatIndex] * k / firstBeatIndex;
		}
		
		/*
		 * Interpoleren tussen alle beats.
		 */
		for (int i = 0; i < beats.size() - 1; i++) {
			Beat beat1 = beats.get(i);
			int beatIndex1 = timeToIndex(beat1.time());
			Beat beat2 = beats.get(i);
			int beatIndex2 = timeToIndex(beat2.time());
			
			if (beat2.intensity > beat1.intensity)
				level[beatIndex2] = level[beatIndex1] + beat2.intensity;
			else
				level[beatIndex2] = level[beatIndex1] - beat2.intensity;
			
			for (int k = beatIndex1 + 1; k < beatIndex2; k++) {
				level[k] = (level[beatIndex2] - level[beatIndex1]) *
						(k - beatIndex1) / (beatIndex2 - beatIndex1);
			}
		}
		
		/*
		 * Interpoleren tussen laatste beat en einde array.
		 */
		int lastBeatIndex = timeToIndex(beats.get(beats.size() - 1).time());
		for (int k = lastBeatIndex - 1; k < level.length; k++) {
			level[k] = level[lastBeatIndex] * (1 - (k - lastBeatIndex) / (level.length - 1 - lastBeatIndex));
		}
		
		if (42 == 42)
			return;
		
		int beatCounter = 0;
		level[0] = 0;
		for(int i = 1; i < level.length && beatCounter < beats.size();i++){
			
			/* If a beat is detected, its intensity is compared to the intensity
			 * of the previous beat. Based on this, the level will either go 
			 * up or down.
			 */
			
			if(beats.get(beatCounter).startTime / 33 == i){
				//XXX Hier is dus een beat.
				if(beats.get(beatCounter).intensity > lastIntens){
					makeInc(beats.get(beatCounter).intensity, i);
					lastIntens = beats.get(beatCounter).intensity;
					beatCounter++;
				}
				else{
					makeDec(beats.get(beatCounter).intensity, i);
					lastIntens = beats.get(beatCounter).intensity;
					beatCounter++;
				}
				
				// TODO: obstacle/coin generator
				
				
			}
			
			/* If it's not a beat, it's being regarded as a gap, which is to be filled
			 * later on.
			 */
			else{
				level[i] = -2;
			}
			//Als er geen beat is, iets anders alternatiefs doen (plat stuk oid)
				
			//XXX NIET VERGETEN i TE INCREMENTEN! Is nu incremented
		}
	}
	
	
	/* timeToIndex() converts a given time to an index in the level buffer.
	 */
	public int timeToIndex(int time){
		int index;
		
		//XXX Int of round?
		//XXX (int) (waarde +0.5) == round ;)
		index = time/1000 * ips;
		return index;
	}
}
