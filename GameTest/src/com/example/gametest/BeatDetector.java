package com.example.gametest;

/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */
public class BeatDetector {

	// reference is om voor de eerste energies te kunnen bepalen of het beats
	// zijn, i.e. reference is historySize groot
	public BeatDetector(int historySize, int beatFactor, double[] reference) {
		...
		historyBuffer.placeFrom(0, reference, 0, historySize);
	}
	
	public boolean newEnergy(double energy) {
		...
		return isBeat;
	}
	
	public double setEnergy (double[] samples) {
		double localEnergy = 0;
		for (int i = 0; i < samples.length; i++) {
			localEnergy += (samples[i] * samples[i]);
		}
		return (localEnergy / samples.length);
	}
	
	// Berekent de afwijking van de locale energy met zijn history
	private double calcVariance (double[] energyHistory, double localEnergy) {
		double variance = 0;
		for (int i = 0; i < energyHistory.length; i++) {
			variance += ((energyHistory[i] - localEnergy) * (energyHistory[i] - localEnergy));
		}
		return (variance/energyHistory.length);
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
	
	public Section[] getSections {
		
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
