package com.example.gametest;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

public class FFTBeatDetector implements BeatDetector{
	private SimpleBeatDetector lowFrequency;
	private DoubleFFT_1D fft;
	
	public FFTBeatDetector (int sampleRate, int channels) {
		fft = new DoubleFFT_1D(1024);
		lowFrequency = new SimpleBeatDetector(sampleRate, channels);
	}
	
	@Override
	public boolean newSamples(double[] samples) {
		fft.realForward(samples);
		Log.i("detectTempo", " Energy :" + calcAverage(samples, 0, 6));
		return lowFrequency.newEnergy(calcAverage(samples, 0, 6), 1024);
	}
	
	private double findMax (double[] array) {
		double biggest = 0;
		for (int i = 0; i < array.length; i++) {
		}
		return biggest;
	}
	@Override
	/*Hier moet meer gebeuren*/
	public void finishSong() {
		lowFrequency.finishSong();
	}
	
	@Override
	public double estimateTempo() {
		return lowFrequency.estimateTempo();
	}

	@Override
	public List<Beat> getBeats() {
		return lowFrequency.getBeats();
	}

	@Override
	public List<Section> getSections() {
		return lowFrequency.getSections();
	}
	
	private double calcAverage (double[] samples, int offset, int length) {
		double avg = 0;
		for (int i = offset; i < offset + length; i++) {
			avg += (samples[i] * samples[i]);
		}
		return avg / length;
	}
}
