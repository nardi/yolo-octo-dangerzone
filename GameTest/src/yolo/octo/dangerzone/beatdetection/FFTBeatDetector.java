package yolo.octo.dangerzone.beatdetection;

import java.util.ArrayList;
import java.util.List;

import ddf.minim.analysis.FFT;

import android.util.Log;

public class FFTBeatDetector implements BeatDetector{
	private SimpleBeatDetector lowFrequency;
	private SimpleBeatDetector highFrequency;
	private SimpleBeatDetector midFrequency;
	private FFT fft;
	
	public FFTBeatDetector(int sampleRate, int channels, int bufferSize) {
		fft = new FFT(bufferSize, 44100);
		fft.window(FFT.NONE);
		fft.noAverages();
		
		lowFrequency = new SimpleBeatDetector(sampleRate, channels);
		highFrequency = new SimpleBeatDetector(sampleRate, channels);
		midFrequency = new SimpleBeatDetector(sampleRate, channels);
	}
	
	@Override
	public boolean newSamples(float[] samples) {
		fft.forward(samples);
		//Log.i("detectTempo", " Energy :" + fft.calcAvg(100, 250));
		boolean lowBeat = lowFrequency.newEnergy(fft.calcAvg(80, 220), 1024);
		highFrequency.newEnergy(fft.calcAvg(10000, 17000), 1024);
		midFrequency.newEnergy(fft.calcAvg(350, 1500), 1024);
		return lowBeat;
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
		highFrequency.finishSong();
		List<Beat> lowBeats = lowFrequency.getBeats();
		List<Beat> highBeats = highFrequency.getBeats();
		int lbIndex = 0;
		for (Beat hb : highBeats) {
			for (Beat lb = lowBeats.get(lbIndex);
				 lb.startTime >= hb.startTime;
				 lb = lowBeats.get(++lbIndex)) {
				// Als een hb binnen een lb valt, draagt deze bij aan de intensiteit
				if (lb.startTime <= hb.startTime
			     && lb.endTime > hb.startTime) {
					lb.intensity += hb.intensity / 2;
				}
			}
		}
		
		float maxBeatIntensity = 1;
		for (Beat b : lowBeats) {
			if (b.intensity > maxBeatIntensity) {
				maxBeatIntensity = b.intensity;
			}
		}
		for (Beat b : lowBeats) {
			b.intensity /= maxBeatIntensity;
		}
		
		lbIndex = 0;
		for (Section s : midFrequency.getSections()) {
			int bestMatch = 0;
			long timeDiff = Long.MAX_VALUE;
			for (; lbIndex < lowBeats.size(); lbIndex++) {
				Beat b = lowBeats.get(lbIndex);
				long newTimeDiff = Math.abs(s.startTime - b.startTime);
				if (newTimeDiff < timeDiff) {
					timeDiff = newTimeDiff;
					bestMatch = lbIndex;
				} else if (b.startTime > s.startTime) {
					break;
				}
			}
			int startBeat = bestMatch;
			timeDiff = Long.MAX_VALUE;
			for (; lbIndex < lowBeats.size(); lbIndex++) {
				Beat b = lowBeats.get(lbIndex);
				long newTimeDiff = Math.abs(s.endTime - b.endTime);
				if (newTimeDiff < timeDiff) {
					timeDiff = newTimeDiff;
					bestMatch = lbIndex;
				} else if (b.endTime > s.endTime) {
					break;
				}
			}
			int endBeat = bestMatch;
			s.beats.clear();
			s.beats.addAll(lowBeats.subList(startBeat, endBeat));
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
