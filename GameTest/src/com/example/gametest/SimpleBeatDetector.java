package com.example.gametest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Deze onthoudt "energies" en kan zo van nieuwe energies bepalen of deze
 * een beat zijn of niet.
 * 
 * Dit kan trouwens ook een interface worden, maken we een SimpleBeatDetector en
 * later een FFTBeatDetector
 */
public class SimpleBeatDetector implements BeatDetector {
	private static final int LOWEST_BPM = 60;
	private static final int HIGHEST_BPM = 200;
	
	private CircularDoubleBuffer historyBuffer;
	private double[] tempBuffer;
	private int historyPosition = 0;
	//private double localEnergy = 0;

	private boolean wasBeat = false;
	private List<Beat> beats = new ArrayList<Beat>();
	private Beat currentBeat = null;
	private int currentBeatLength = 0;

	private int sampleRate, channels;
	private long sampleCounter = 0;
	
	private List<Section> sections = new ArrayList<Section>();
	private Section currentSection = null;
	
	/*
	 * reference is om voor de eerste energies te kunnen bepalen of het beats
	 * zijn, i.e. reference is historySize groot
	 * 
	 * XXX reference wordt op dit moment niet gebruikt, is een beetje lastig goed te doen
	 */
	public SimpleBeatDetector (int sampleRate, int channels) {
		this.sampleRate = sampleRate;
		this.channels = channels;
		historyBuffer = new CircularDoubleBuffer(sampleRate / 1024);
		tempBuffer = new double[historyBuffer.getLength()]; //Gebruikt voor berekeningen op history
		//historyPosition = historyBuffer.placeFrom(0, reference, 0, reference.length);
	}
	
	public boolean newSamples(double[] samples) {
		return newEnergy(calcAverage(samples), samples.length); 
	}
	
	public boolean newEnergy(double instantEnergy, int fromSamples) {
		historyBuffer.getFrom(0, tempBuffer, 0, tempBuffer.length);
		historyPosition = historyBuffer.placeFrom(historyPosition, instantEnergy);

		double avgEnergy = calcAverage(tempBuffer);
		double c = calcC(calcVariance(tempBuffer, avgEnergy));
		
		boolean isBeat = instantEnergy > c * avgEnergy;
		long time = (1000 * sampleCounter / channels) / sampleRate;

		if (currentBeat != null) {
			if (isBeat) {
				currentBeat.add(time, instantEnergy);
			} else {
				currentBeat.finish(time);
				beats.add(currentBeat);
				if (currentSection == null
				 || avgEnergy / currentSection.intensity > c
				 || currentSection.intensity / avgEnergy > c) {
					if (currentSection != null) {
						currentSection.finish(currentBeat);
						sections.add(currentSection);
					}
					currentSection = new Section(currentBeat, avgEnergy);
				} else {
					currentSection.add(currentBeat, avgEnergy);
				}
				currentBeat = null;
			}
		}
		
		/*
		 * Het is alleen een nieuwe beat als ervoor een niet-beat geweest is
		 */
		boolean temp = isBeat;
		isBeat = isBeat && !wasBeat;
		wasBeat = temp;
		
		if (!beats.isEmpty() && isBeat) {
			Beat lastBeat = beats.get(beats.size() - 1);
			long beatTime = time - lastBeat.startTime;
			isBeat = 60000 / beatTime <= HIGHEST_BPM;
		}
		
		if (isBeat)
			currentBeat = new Beat(time, instantEnergy);
		
		sampleCounter += fromSamples;
		return isBeat;
	}
	
	public void finishSong() {
		if (currentBeat != null) {
			beats.add(currentBeat);
			if (currentSection != null)
				currentSection.finish(currentBeat);
			currentBeat = null;
		}
		if (currentSection != null) {
			sections.add(currentSection);
			currentSection = null;
		}
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
		int numBeatTimes = 0;
		long[] beatTimes = new long[beats.size() - 1];
		//long totalBeatTime = 0;
		//double avgBeatTime;
		double medianBeatTime;

		for (int i = 0; i < beats.size() - 1; i++) {
			long time = beats.get(i + 1).time() - beats.get(i).time();
			if (60000 / time >= LOWEST_BPM) {
				//totalBeatTime += time;
				beatTimes[numBeatTimes] = time;
				numBeatTimes++;
			}
		}
		//avgBeatTime = ((double)totalBeatTime / numBeatTimes);
		
		Arrays.sort(beatTimes);
		medianBeatTime = beatTimes[beatTimes.length - numBeatTimes / 2 - 1];
		if (beatTimes.length % 2 == 0) {
	      double prev = beatTimes[beatTimes.length - numBeatTimes / 2];
	      medianBeatTime = (prev + medianBeatTime) / 2;
	    }
		
		// Mediaan lijkt beste resultaat te geven
		return 60000 / medianBeatTime;
	}
	
	public List<Beat> getBeats() {
		return beats;
	}
	
	public List<Section> getSections () {
		return sections;
	}
}
