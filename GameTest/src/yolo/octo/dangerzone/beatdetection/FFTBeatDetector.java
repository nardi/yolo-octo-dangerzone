package yolo.octo.dangerzone.beatdetection;

import java.util.List;
import ddf.minim.analysis.FFT;

/*
 * FFTBeatDetector
 * An extension of the SimpleBeatDetector. 
 * Uses a Fast Fourier Transform on a single array of samples
 * to break it down into multiple frequency ranges.
 * Uses the low freqency range (from 80 to 230) to determine if there is a beat. 
 * Uses three frequency bands to find sections in the song.
 * 
 */
public class FFTBeatDetector implements BeatDetector{
	/* Has three simple beat detectors, one for each frequency range*/
	private SimpleBeatDetector lowFrequency;
	private SimpleBeatDetector highFrequency;
	private SimpleBeatDetector midFrequency;
	private FFT fft;
	
	/* Constructor*/
	public FFTBeatDetector(int sampleRate, int channels, int bufferSize) {
		fft = new FFT(bufferSize, sampleRate);
		fft.window(FFT.NONE);
		fft.noAverages();
		
		lowFrequency = new SimpleBeatDetector(sampleRate, channels);
		highFrequency = new SimpleBeatDetector(sampleRate, channels);
		midFrequency = new SimpleBeatDetector(sampleRate, channels);
	}
	
	@Override
	/* Uses the newEnergy function from the simple beat detector to fill the
	 * list of sections in each frequency range.
	 */
	public boolean newSamples(float[] samples) {
		fft.forward(samples);
		/* Only the low frequency range is used to find a beat*/
		boolean lowBeat = lowFrequency.newEnergy(fft.calcAvg(80, 230), 1024);
		highFrequency.newEnergy(fft.calcAvg(9000, 17000), 1024);
		midFrequency.newEnergy(fft.calcAvg(280, 2000), 1024);
		return lowBeat;
	}
	
	@Override
	/*
	 * Wraps up the song.
	 * Calculates a total intensity by adding the intensity of the beats found in the 
	 * high frequency range to the intensity in the low frequency range if both beats 
	 * occur at the same time.
	 * Beat intensities will be scaled so they fall between 0 and 1.
	 * 
	 * Then it uses the sections found in the middle frequency range and matches the 
	 * beats in the low frequency range with the sections in the middle frequency range.
	 */
	public void finishSong() {
		lowFrequency.finishSong();
		highFrequency.finishSong();
		List<Beat> lowBeats = lowFrequency.getBeats();
		List<Beat> highBeats = highFrequency.getBeats();
		int lbIndex = 0;
		for (Beat hb : highBeats) {
			for (; lbIndex < lowBeats.size(); lbIndex++) {
				Beat lb = lowBeats.get(lbIndex);
				if (lb.startTime > hb.startTime) {
					break;
				}
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
		
		float maxSectionIntensity = 0;
		lbIndex = 0;
		/* Looks for the best match in the low frequency range for the start and end
		 * of the section fills the section with the beats between the start and the end.
		 */
		for (Section s : midFrequency.getSections()) {
			int bestMatch = 0;
			long timeDiff = Long.MAX_VALUE;
			/* finds the start beat*/
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
			/* Finds the end beat*/
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
			/* Adds all beats from start to end to the section list*/
			s.beats.addAll(lowBeats.subList(startBeat, endBeat));
			if (s.intensity > maxSectionIntensity) {
				/* Sets the maxsection intensity*/
				maxSectionIntensity = (float) s.intensity;
			}
		}
		/* Scales the intensities in the sections to fall between 0 and 1*/
		for (Section s : midFrequency.getSections()) {
			s.intensity /= maxSectionIntensity;
		}
	}
	
	@Override
	/* See explanation at SimpleBeatDetector*/
	public double estimateTempo() {
		return lowFrequency.estimateTempo();
	}

	@Override
	/* See explanation at SimpleBeatDetector*/
	public List<Beat> getBeats() {
		return lowFrequency.getBeats();
	}

	@Override
	/* Only the middle frequency range is used for the section*/
	public List<Section> getSections() {
		return midFrequency.getSections();
	}
}
