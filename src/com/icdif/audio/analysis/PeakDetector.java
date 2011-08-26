/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;
import java.io.*;

/**
 * This class receives the SpectralDifference and calculates the Peaks. In order
 * to do this, first it calculates the threshold values, then it chooses from
 * the SpectralDifference the values bigger than the threshold and, finally, it
 * selects the peaks from this last array
 * 
 * @author wanderer
 * 
 */
public class PeakDetector {

	/**
	 * The number of values (for each side) for which we calculate the mean
	 */
	private int thresholdWindowSize = 10;

	/**
	 * The constant to be multiplied by the "average". If the value is bigger
	 * than MULTIPLYING_FACTOR * threshold, then it's considered a peak
	 */
	private float multiplier = 1.6f;

	/**
	 * The spectral Flux that'll be used to calculate the threshold
	 */
	private ArrayList<Float> spectralFlux = new ArrayList<Float>();

	/**
	 * The threshold function - it's like a dynamic average
	 */
	private ArrayList<Float> threshold = new ArrayList<Float>();

	/**
	 * It contains the values of the SpectralFlux that are bigger than the
	 * threshold: threshold.get(i) <= spectralFlux.get(i)
	 */
	private ArrayList<Float> filteredSpectralFlux = new ArrayList<Float>();

	/**
	 * This array list contains the peaks, i.e., the values from the
	 * filteredSpectralFlux that are bigger than the next value. Any value > 0
	 * in this arraylist is a peak
	 */
	private ArrayList<Float> peaks = new ArrayList<Float>();

	/**
	 * This array contains the onsets as time instants. I.e. the instants in
	 * seconds at which the onsets occur
	 */
	private ArrayList<Double> onsets = new ArrayList<Double>();

	/**
	 * Instantiates the class by passing the spectral Flux that'll be used to
	 * calculate the threshold
	 * 
	 * @param spectralFlux
	 */
	public PeakDetector(final ArrayList<Float> spectralFlux) {

		this.spectralFlux = spectralFlux;

	}

	/**
	 * Instantiates this class by passing the spectral Flux and the parameters
	 * that'll be used to calculate the threshold and the peaks.
	 * 
	 * @param spectralFlux
	 * @param thresholdWindowSize
	 * @param multiplier
	 */
	public PeakDetector(final ArrayList<Float> spectralFlux,
			final int thresholdWindowSize, final float multiplier) {
		super();
		this.thresholdWindowSize = thresholdWindowSize;
		this.multiplier = multiplier;
		this.spectralFlux = spectralFlux;
	}

	/**
	 * For each value in the spectral flux, we calculate the average of the
	 * values around it. We use a "window" of radius THRESHOLD_WINDOW_SIZE
	 * around each flux value.
	 */
	private void calcThreshold() {
		for (int i = 0; i < spectralFlux.size(); i++) {
			// the window starts at 0 or at the current value -
			// THRESHOLD_WINDOW_SIZE
			int start = Math.max(0, i - thresholdWindowSize);

			// the same here, it ends at the last value, or at the current value
			// + THRESHOLD_WINDOW_SIZE
			int end = Math
					.min(spectralFlux.size() - 1, i + thresholdWindowSize);

			// we calculate the mean and multiply it by the factor
			float mean = 0;
			for (int j = start; j <= end; j++)
				mean += spectralFlux.get(j);
			mean /= (end - start);
			threshold.add(mean * multiplier);
		}
	}

	/**
	 * Creates a new array that contains only the spectral flux values that are
	 * bigger than the threshold. We don't save the spectral flux value in this
	 * case, we save the difference: spectral flux - threshold, i.e., the
	 * "magnitude" of the peak.
	 */
	private void calcFilteredSpectralFlux() {
		for (int i = 0; i < threshold.size(); i++) {
			if (threshold.get(i) <= spectralFlux.get(i))
				filteredSpectralFlux
						.add(spectralFlux.get(i) - threshold.get(i));
			else
				filteredSpectralFlux.add((float) 0);
		}
	}

	/**
	 * Fills the array containing the peaks. Any value > 0 in this array is a
	 * peak. In order to calculate the peaks, it also needs to calculate the
	 * threshold and the filtered Spectral Flux as intermediate steps.
	 */
	public void calcPeaks() {

		// the threshold and filtered spectral flux are needed in order to
		// calculate the peaks
		this.calcThreshold();
		this.calcFilteredSpectralFlux();

		/*
		 * TODO: maybe clean up the resulting peak list and remove any peaks
		 * that are to close together, say < 10ms
		 */
		for (int i = 0; i < filteredSpectralFlux.size() - 1; i++) {
			// in the filtered spectral flux, a peak is a value bigger than the
			// next value
			if (filteredSpectralFlux.get(i) > filteredSpectralFlux.get(i + 1)) {
				peaks.add(filteredSpectralFlux.get(i));
				// peaks.add((float)1);
			}

			else
				peaks.add((float) 0);
		}
	}
	
	/**
	 * Gets an array containing the time instants (in seconds) of every onset
	 * @param spectralWindowSize the size of the spectral window, i.e., the hopsize
	 * @param sampleRate the sample rate of the signal
	 * @return the time instances of every onset
	 */
	public ArrayList<Double> getPeaksAsInstantsInTime(
			final int spectralWindowSize, final int sampleRate) {

		for (int i = 0; i < this.peaks.size(); i++) {
			if (this.getPeaks().get(i) > 0) {
				
				this.onsets
						.add((double) (i *  (double) spectralWindowSize / ((double) sampleRate/* * 2 */)));
			}

			// System.out.println("onset(" + i + ") = " + (double)(i *
			// 1024.0/44100.0));
		}

		return this.onsets;

	}

	/**
	 * Prints the time instants onsets to a file
	 * @param filename
	 */
	public void printOnsetsToFile(final String filename) {
		try {
			FileWriter outFile = new FileWriter(filename);
			PrintWriter out = new PrintWriter(outFile);

			for (int i = 0; i < this.onsets.size(); i++) {
				out.println(onsets.get(i));
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return the spectralFlux
	 */
	public ArrayList<Float> getSpectralFlux() {
		return spectralFlux;
	}

	/**
	 * @return the threshold
	 */
	public ArrayList<Float> getThreshold() {
		return threshold;
	}

	/**
	 * @return the filteredSpectralFlux
	 */
	public ArrayList<Float> getFilteredSpectralFlux() {
		return filteredSpectralFlux;
	}

	/**
	 * @return the peaks
	 */
	public ArrayList<Float> getPeaks() {
		return peaks;
	}

}
