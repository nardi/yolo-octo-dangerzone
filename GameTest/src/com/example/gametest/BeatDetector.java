package com.example.gametest;

import com.example.gametest.Section.BeatDetector;

/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */

public interface BeatDetector {
	public boolean newSamples(double[] samples);
	public double estimateTempo();
	public Beat[] getBeats();
	public Section[] getSections();

}

public class Detector implements BeatDetector{
	
	private CircularDoubleBuffer historyBuffer;
	private double[] tempBuffer;
	int writeEnd = 0;
	double instantEnergy = 0;
	double localEnergy = 0;
	
	// reference is om voor de eerste energies te kunnen bepalen of het beats
	// zijn, i.e. reference is historySize groot
	public BeatDetector(int historySize, int beatFactor, double[] reference) {
		tempBuffer = new double[historySize];//Gebruikt voor berekeningen op history
		historyBuffer = new CircularDoubleBuffer(historySize);
		writeEnd = historyBuffer.placeFrom(0, reference, 0, historySize);
	}
	
	public boolean newEnergy(double energy) {
		boolean isBeat = false;
		historyBuffer.getFrom(0, tempBuffer, 0, tempBuffer.length);
		double c = calcC(calcVariance(tempBuffer));
		if (instantEnergy > (c * localEnergy)) {
			isBeat = true;
		}
		return isBeat;
	}
	
	public boolean newSamples (double[] samples) {
		double instantEnergy = 0;
		for (int i = 0; i < samples.length; i++) {
			instantEnergy += (samples[i] * samples[i]);
		}
		this.instantEnergy = (instantEnergy * 1024) /samples.length;
	}
	
	private void updateHistory () {
		writeEnd = historyBuffer.setSingle(writeEnd, instantEnergy);
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
		this.localEnergy = localEnergy;
		// Krijg variantie total
		for (int i = 0; i < energyHistory.length; i++) {
			variance += ((energyHistory[i] - localEnergy) * (energyHistory[i] - localEnergy));
		}
		
		return (variance /energyHistory.length);
	}
	
	// Berekent de constante C
	private double calcC (double v) {
		double c = (-0.0025714 * v) + 1.5142857;
		return c;
	}
	
	public double estimateTempo() {
		
	}
	
	public Beat[] getBeats() {
		
	}
	
	public Section[] getSections () {

	}
}

public class Beat {
	double time;
	// intensity kan gebaseerd worden op amplitude, of later ook
	// op de frequentiebanden waar ze in voorkomen bijvoorbeeld
	double intensity;
}

public class Section {
	double startTime;
	Beat[] beats;
	double intensity;
	double avgTempo;
}
