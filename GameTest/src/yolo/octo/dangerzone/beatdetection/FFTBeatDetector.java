package yolo.octo.dangerzone.beatdetection;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.analysis.FFT;

import android.util.Log;

public class FFTBeatDetector implements BeatDetector{
	private SimpleBeatDetector lowFrequency;
	private FFT fft;
	
	public FFTBeatDetector (int sampleRate, int channels, int bufferSize) {
		fft = new FFT(bufferSize / 2, 44100);
		fft.window(FFT.NONE);
		fft.noAverages();
		
		lowFrequency = new SimpleBeatDetector(sampleRate, channels);
	}
	
	@Override
	public boolean newSamples(float[] samples) {
		fft.forward(samples);
		//Log.i("detectTempo", " Energy :" + fft.calcAvg(100, 250));
		return lowFrequency.newEnergy(fft.calcAvg(80, 220), 1024);
	}
	
	private float findMax(float[] array) {
		float biggest = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > biggest)
				biggest = array[i];
		}
		return biggest;
	}
	
	@Override
	public void finishSong() {
		lowFrequency.finishSong();
		float maxBeatIntensity = 1;
		for (Beat b : lowFrequency.getBeats()) {
			if (b.intensity > maxBeatIntensity) {
				maxBeatIntensity = b.intensity;
			}
		}
		for (Beat b : lowFrequency.getBeats()) {
			b.intensity /= maxBeatIntensity;
		}
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
	
	private float calcAverage(float[] samples, int offset, int length) {
		float avg = 0;
		for (int i = offset; i < offset + length; i++) {
			avg += (samples[i] * samples[i]);
		}
		return avg / length;
	}
}
