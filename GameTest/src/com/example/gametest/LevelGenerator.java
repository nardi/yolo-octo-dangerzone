package com.example.gametest;

import java.util.List;

public class LevelGenerator {
	
	float[] level;
	Beat[] beats;
	//Indices per second
	int ips = 30;
	
	public LevelGenerator(int length) {
		level = new float[length * 30];
		beats = FFTBeatDetector.getBeats().toArray(); //TODO retrieve beats here
		
		
	}
	
	public void generateLevel(){
		int beatCounter = 0;
		for(int i = 0; i < level.length;){
			//Als er een beat is, hier iets leuks doen
			if()
			//Als er geen beat is, iets anders alternatiefs doen (plat stuk oid)
				
			//XXX NIET VERGETEN i TE INCREMENTEN!
		}
	}
	
	public int timeToIndex(int time){
		int index;
		
		//XXX Int of round?
		index = time/1000 * ips;
		return index;
	}

}
