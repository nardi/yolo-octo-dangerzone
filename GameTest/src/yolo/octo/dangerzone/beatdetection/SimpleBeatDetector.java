package yolo.octo.dangerzone.beatdetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yolo.octo.dangerzone.core.CircularFloatBuffer;

/*
 * SimpleBeatDetector 
 * Attempts to find the beats in a given set of samples.
 * Can also estimate if there beat when given the magnitude
 * of an arbitrary frequency in a set of samples (used in FFTBeatDetector).
 * Construct a list of the beats found. 
 * 
 * Beats are found by comparing the energy of a sample with the average of the
 * previous energies measured. If the energy in a sample is higher than the average
 * of the history we have found a beat.
 */
public class SimpleBeatDetector implements BeatDetector {
	private static final int LOWEST_BPM = 60;
	private static final int HIGHEST_BPM = 200;
	
	private CircularFloatBuffer historyBuffer;
	private float[] tempBuffer;
	private int historyPosition = 0;
	
	private boolean wasBeat = false;
	private List<Beat> beats = new ArrayList<Beat>();
	private Beat currentBeat = null;

	private int sampleRate, channels;
	private long sampleCounter = 0;
	
	private List<Section> sections = new ArrayList<Section>();
	private Section currentSection = null;
	
	/* Constructor*/
	public SimpleBeatDetector (int sampleRate, int channels) {
		this.sampleRate = sampleRate;
		this.channels = channels;
		historyBuffer = new CircularFloatBuffer(sampleRate / 1024);
		tempBuffer = new float[historyBuffer.getLength()]; //Gebruikt voor berekeningen op history
	}
	
	/* Calculates the average energy over the new samples 
	 * returns true if the energy is bigger than the average of the
	 * history buffer. 
	 */
	public boolean newSamples(float[] samples) {
		return newEnergy(calcAverage(samples), samples.length); 
	}
	
	/* Test if there is a beat*/
	public boolean newEnergy(float instantEnergy, int fromSamples) {
		historyBuffer.getFrom(0, tempBuffer, 0, tempBuffer.length);
		historyPosition = historyBuffer.placeFrom(historyPosition, instantEnergy);
		
		float avgEnergy = calcAverage(tempBuffer);
		float c = calcC(calcVariance(tempBuffer, avgEnergy));
		float doubleC = c * c;
		
		boolean isBeat = instantEnergy > Math.sqrt(c) * avgEnergy;
		long time = (1000 * sampleCounter / channels) / sampleRate;
		
		/* If we found a beat we will check if we already had one
		 * If so, we will elongate the current beat, else we will 
		 * make a new one.
		 */
		if (currentBeat != null) {
			if (isBeat) {
				currentBeat.add(time, instantEnergy);
			} else {
				currentBeat.finish(time);
				beats.add(currentBeat);
				/* If the intensity matches the intensity of 
				  * the current section this beat will be added to the section.
				  * else we will make a new section.
				  */
				if (currentSection == null
				 || avgEnergy / currentSection.intensity > doubleC
				 || currentSection.intensity / avgEnergy > doubleC) {
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
		
		/* We can only find new beats if the previous samples didn't have a beat.
		 */
		boolean temp = isBeat;
		isBeat = isBeat && !wasBeat;
		wasBeat = temp;
		
		/* Elongates the previous beat if we already had a previous beat
		 */
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
	
	/*Finishes the song
	 * Ends the current beat if it doesn't have an end yet.
	 * Ends the current section if it doesn't have an end yet.
	 */
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
	
	/* Calculates the average energy over an array of samples*/
	private float calcAverage (float[] samples) {
		float avg = 0;
		for (int i = 0; i < samples.length; i++) {
			avg += Math.abs(samples[i]);
		}
		return avg / samples.length;
	}
	
	/* Calculates the variance of the energy in the energy history with the average energy in the history*/
	private float calcVariance (float[] energyHistory, float avgEnergy) {
		float variance = 0;
		
		for (int i = 0; i < energyHistory.length; i++) {
			variance += ((energyHistory[i] - avgEnergy) * (energyHistory[i] - avgEnergy));
		}
		
		return variance / energyHistory.length;
	}
	
	/* Used to calculate the constant value*/
	private float calcC (float v) {
		return -0.0025714f * v + 1.5142857f;
	}
	
	/* Walks through the list of beats and 
	 * returns the average length of all beats.
	 */
	public double estimateTempo() {
		int numBeatTimes = 0;
		long[] beatTimes = new long[beats.size() - 1];
		long totalBeatTime = 0;
		double avgBeatTime;
		float medianBeatTime;

		for (int i = 0; i < beats.size() - 1; i++) {
			long time = beats.get(i + 1).time() - beats.get(i).time();
			if (60000 / time >= LOWEST_BPM) {
				beatTimes[numBeatTimes] = time;
				numBeatTimes++;
			}
		}
		avgBeatTime = ((double)totalBeatTime / numBeatTimes);
		
		/* Also calculates the median, just in case*/
		Arrays.sort(beatTimes);
		medianBeatTime = beatTimes[beatTimes.length - numBeatTimes / 2 - 1];
		if (beatTimes.length % 2 == 0) {
	      double prev = beatTimes[beatTimes.length - numBeatTimes / 2];
	      medianBeatTime = (float) ((prev + medianBeatTime) / 2);
	    }
		
		return 60000 / avgBeatTime;
	}
	
	public List<Beat> getBeats() {
		return beats;
	}
	
	public List<Section> getSections () {
		return sections;
	}
}
