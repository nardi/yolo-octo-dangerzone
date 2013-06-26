package yolo.octo.dangerzone.lvlgen;

import java.util.List;

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
		level = new float[(int) (400 + (length * 30))];
		//beats = (Beat[]) (beatDet.getBeats().toArray()); //TODO retrieve beats here
		beats = beatDet.getBeats();
	}
	
	/* generateLevel() will generate the level using the beat-data 
	 * from the audio analysis.
	 */
	public void generateLevel(){
		int beatCounter = 0;
		level[0] = 0;
		for(int i = 1; i < level.length;i++){
			
			/* If a beat is detected, its intensity is compared to the intensity
			 * of the previous beat. Based on this, the level will either go 
			 * up or down.
			 */
			if(beats.get(beatCounter).startTime /33 == i){
				//XXX Hier is dus een beat.
				if(beats.get(beatCounter).intensity > lastIntens){
					makeInc(beats.get(beatCounter).intensity, i);
				}
				else{
					makeDec(beats.get(beatCounter).intensity, i);
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
		interpolate();
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
	
	/* makeInc() generates a hill when the previous intensity was lower than
	 * the current.
	 */
	public void makeInc(float intensity, int index){
		//Maak hier een functie die een helling genereert in de level array. Hiervoor moet de deviatie denk ik als -100 tot 100 oid 
		//worden aangegeven.
		level[index] = level[index - 1] + intensity;
	}
	
	/* makeDec() generates a valley when the previous intensity was lower than
	 * the current.
	 */
	public void makeDec(float intensity, int index){
		//Hetzelfde als makeInc, maar dan voor een helling omlaag, kan evt ook in 1 functie.
		level[index] = level[index - 1] - intensity;
	}
	
	
	/* interpolate() fills the gaps made in generateLevel by interpolating 
	 * between the beats' intensity values
	 */
	public void interpolate(){
		for(int i = 0; i < level.length;){
			int q = i;
			/*
			 * Found a gap between two beats.
			 */
			if(level[i] == -2){
				/*
				 * Finding the end of the gap.
				 */
				while(level[q] == -2){
					q++;
				}
				/*
				 * Add the average value to the deviation of the beginning of the gap.
				 */
				for(int p = i; p < q; p++){
					level[p] = level[p - 1];
					level[p] += (level[i - 1] + level[q]) / q;
				}
				i += q - i;
			}
			/*
			 * If no gap is found just increment i.
			 */
			else{
				i++;
			}
		}
	}

}
