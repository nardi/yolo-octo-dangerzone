package com.example.gametest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */
public class SimpleBeatDetector implements BeatDetector {
	private CircularDoubleBuffer historyBuffer;
	private double[] tempBuffer;
	private int historyPosition = 0;
	//private double localEnergy = 0;
	private boolean wasBeat = false;
	private List<Beat> beats = new ArrayList<Beat>();
	private int sampleRate, channels;
	private long sampleCounter = 0;
	
	/*
	 * reference is om voor de eerste energies te kunnen bepalen of het beats
	 * zijn, i.e. reference is historySize groot
	 */
	public SimpleBeatDetector (int sampleRate, int channels, double[] reference) {
		this.sampleRate = sampleRate;
		this.channels = channels;
		historyBuffer = new CircularDoubleBuffer(sampleRate / 1024);
		tempBuffer = new double[historyBuffer.getLength()]; //Gebruikt voor berekeningen op history
		//historyPosition = historyBuffer.placeFrom(0, reference, 0, reference.length);
	}
	
	public boolean newSamples (double[] samples) {
		double instantEnergy = calcAverage(samples);
		
		historyBuffer.getFrom(0, tempBuffer, 0, tempBuffer.length);
		historyPosition = historyBuffer.placeFrom(historyPosition, instantEnergy);

		double avgEnergy = calcAverage(tempBuffer);
		double c = calcC(calcVariance(tempBuffer, avgEnergy));
		
		boolean isBeat = instantEnergy > c * avgEnergy;

		/*
		 * Het is alleen een beat als ervoor een niet-beat geweest is
		 */
		boolean temp = isBeat;
		isBeat = isBeat && !wasBeat;
		wasBeat = temp;
		
		if (isBeat) {
			Beat beat = new Beat();
			beat.time = (1000 * sampleCounter / channels) / sampleRate;
			beat.intensity = 1;
			beats.add(beat);
		}
		
		sampleCounter += samples.length;
		return isBeat;
	}
	
	// Berekent de afwijking van de locale energy met zijn history
	private double calcAverage (double[] samples) {
		double avg = 0;
		for (int i = 0; i < samples.length; i++) {
			avg += (samples[i] * samples[i]);
		}
		return avg / samples.length;
	}
	
	// Berekent de afwijking van de locale energy met zijn history
	private double calcVariance (double[] energyHistory, double avgEnergy) {
		double variance = 0;
		//localEnergy = 0;
		
		// Krijg local energy total
		/* for (int i = 0; i < energyHistory.length; i++) {
			localEnergy += (energyHistory[i] * energyHistory[i]);
		}
		
		localEnergy /= energyHistory.length; */

		// Krijg variantie total
		for (int i = 0; i < energyHistory.length; i++) {
			variance += ((energyHistory[i] - avgEnergy) * (energyHistory[i] - avgEnergy));
		}
		
		return variance / energyHistory.length;
	}
	
	// Berekent de intensiteitsfactor C die bepaalt of een bepaald energieniveau een beat is of niet
	private double calcC (double v) {
		return (-0.0025714 * v) + 1.5142857;
	}
	
	public double estimateTempo() {
		long[] beatTimes = new long[beats.size() - 1];
		long totalBeatTime = 0;
		double avgBeatTime;
		double beatTime;

		for (int i = 0; i < beats.size() - 1; i++) {
			long time = beats.get(i + 1).time - beats.get(i).time;
			totalBeatTime += time;
			beatTimes[i] = time;
		}
		avgBeatTime = ((double)totalBeatTime / (beats.size() - 1));
		
		Arrays.sort(beatTimes);
		double median = beatTimes[beatTimes.length / 2];
		if (beatTimes.length % 2 == 0) {
	      double prev = beatTimes[beatTimes.length / 2 - 1];
	      median = (prev + median) / 2;
	    }
		beatTime = median;
		
		// Gemiddelde van mediaan en gemiddelde, want waarom ook niet
		return 60000 / ((beatTime + avgBeatTime) / 2);
	}
	
	public List<Beat> getBeats() {
		return beats;
	}
	
	public List<Section> getSections () {
		return null;
	}
}
