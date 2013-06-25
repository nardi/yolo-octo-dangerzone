package yolo.octo.dangerzone.lvlgen;

import java.util.List;

import yolo.octo.dangerzone.beatdetection.Beat;
import yolo.octo.dangerzone.beatdetection.BeatDetector;
import yolo.octo.dangerzone.beatdetection.FFTBeatDetector;

public class LevelGenerator {
	
	public float[] level;
	Beat[] beats;
	double lastIntens;
	//Indices per second
	int ips = 30;
	
	public LevelGenerator(BeatDetector beatDet, long length) {
		level = new float[(int) (400 + (length * 30))];
		beats = (Beat[]) (beatDet.getBeats().toArray()); //TODO retrieve beats here
	}
	
	public void generateLevel(){
		int beatCounter = 0;
		for(int i = 0; i < level.length;i++){
			//Als er een beat is, hier iets leuks doen

			if(beats[beatCounter].startTime /33 == i){
				//XXX Hier is dus een beat.
				if(beats[beatCounter].intensity > lastIntens){
					makeInc(beats[beatCounter].intensity, i);
				}
				else{
					makeDec(beats[beatCounter].intensity, i);
				}
			}
			else{
				level[i] = -2;
			}
			//Als er geen beat is, iets anders alternatiefs doen (plat stuk oid)
				
			//XXX NIET VERGETEN i TE INCREMENTEN! Is nu incremented
		}
		interpolate();
	}
	
	public int timeToIndex(int time){
		int index;
		
		//XXX Int of round?
		index = time/1000 * ips;
		return index;
	}
	
	public void makeInc(float intensity, int index){
		//Maak hier een functie die een helling genereert in de level array. Hiervoor moet de deviatie denk ik als -100 tot 100 oid 
		//worden aangegeven.
		level[index] = level[index - 1] + intensity;
	}
	
	public void makeDec(float intensity, int index){
		//Hetzelfde als makeInc, maar dan voor een helling omlaag, kan evt ook in 1 functie.
		level[index] = level[index - 1] - intensity;
	}
	/*
	 * Interpolation method.
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
