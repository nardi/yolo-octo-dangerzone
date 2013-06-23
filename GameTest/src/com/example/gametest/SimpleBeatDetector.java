package com.example.gametest;

import java.util.ArrayList;
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
	private double localEnergy = 0;
	private boolean wasBeat = false;
	private List<Beat> beats = new ArrayList<Beat>();
	
	/*
	 * reference is om voor de eerste energies te kunnen bepalen of het beats
	 * zijn, i.e. reference is historySize groot
	 */
	public SimpleBeatDetector (int historySize, int beatFactor, double[] reference) {
		tempBuffer = new double[historySize]; //Gebruikt voor berekeningen op history
		historyBuffer = new CircularDoubleBuffer(historySize);
		historyPosition = historyBuffer.placeFrom(0, reference, 0, historySize);
	}
	
	public boolean newSamples (double[] samples) {
		double instantEnergy = 0;
		for (int i = 0; i < samples.length; i++) {
			instantEnergy += (samples[i] * samples[i]);
		}
		instantEnergy = (instantEnergy * 1024) / samples.length;
		
		historyBuffer.getFrom(0, tempBuffer, 0, tempBuffer.length);
		historyPosition = historyBuffer.placeFrom(historyPosition, instantEnergy);

		double c = calcC(calcVariance(tempBuffer));
		
		boolean isBeat = instantEnergy > (c * localEnergy);

		/*
		 * Het is alleen een beat als ervoor een niet-beat geweest is
		 */
		boolean temp = isBeat;
		isBeat = isBeat && wasBeat;
		wasBeat = temp;
		return isBeat;
	}
	
	// Berekent de afwijking van de locale energy met zijn history
	private double calcVariance (double[] energyHistory) {
		double variance = 0;
		double localEnergy = 0;
		
		// Krijg local energy total
		for (int i = 0; i < energyHistory.length; i++) {
			localEnergy += (energyHistory[i] * energyHistory[i]);
		}
		
		localEnergy /= energyHistory.length;

		// Krijg variantie total
		for (int i = 0; i < energyHistory.length; i++) {
			variance += ((energyHistory[i] - localEnergy) * (energyHistory[i] - localEnergy));
		}
		
		return variance / energyHistory.length;
	}
	
	// Berekent de intensiteitsfactor C die bepaalt of een bepaald energieniveau een beat is of niet
	private double calcC (double v) {
		return (-0.0025714 * v) + 1.5142857;
	}
	
	public double estimateTempo() {
		long beatTime = 0;
		for (int i = 0; i < beats.size() - 1; i++) {
			beatTime += beats.get(i + 1).time - beats.get(i).time;
		}
		return beatTime / beats.size();
	}
	
	public List<Beat> getBeats() {
		return beats;
	}
	
	public List<Section> getSections () {
		return null;
	}
}
