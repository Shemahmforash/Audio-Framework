/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;
import java.io.*;

/**
 * This class receives the Detection Function and calculates the Onsets. In
 * order to do this, first it calculates the threshold values, then it chooses
 * from the Detection Function the values bigger than the threshold and,
 * finally, it selects the peaks from this last array
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
	 * The Detection Function values that'll be used to calculate the threshold
	 */
	private ArrayList<Float> detectionFunction = new ArrayList<Float>();

	/**
	 * The threshold function - it's like a dynamic average
	 */
	private ArrayList<Float> threshold = new ArrayList<Float>();

	/**
	 * It contains the values of the Detection Function that are bigger than the
	 * threshold: threshold.get(i) <= detectionFunction.get(i)
	 */
	private ArrayList<Float> filteredDetectionFunction = new ArrayList<Float>();

	/**
	 * This array list contains the peaks, i.e., the values from the
	 * filteredDetectionFunction that are bigger than the next value. Any value
	 * > 0 in this arraylist is a peak
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
	 * @param detectionFunction
	 */
	public PeakDetector(final ArrayList<Float> detectionFunction) {

		this.detectionFunction = detectionFunction;

	}

	/**
	 * Instantiates this class by passing the spectral Flux and the parameters
	 * that'll be used to calculate the threshold and the peaks.
	 * 
	 * @param detectionFunction
	 * @param thresholdWindowSize
	 *            - by default it is 10 samples in each side
	 * @param multiplier
	 *            - by default it is 1.6
	 */
	public PeakDetector(final ArrayList<Float> detectionFunction,
			final int thresholdWindowSize, final float multiplier) {
		super();
		this.thresholdWindowSize = thresholdWindowSize;
		this.multiplier = multiplier;
		this.detectionFunction = detectionFunction;
	}

	/**
	 * For each value in the Detection Function, we calculate the average of the
	 * values around it. We use a "window" of radius THRESHOLD_WINDOW_SIZE
	 * around each value of the detection function.
	 */
	private void calcThreshold() {
		for (int i = 0; i < detectionFunction.size(); i++) {
			// the window starts at 0 or at the current value -
			// THRESHOLD_WINDOW_SIZE
			int start = Math.max(0, i - thresholdWindowSize);

			// the same here, it ends at the last value, or at the current value
			// + THRESHOLD_WINDOW_SIZE
			int end = Math.min(detectionFunction.size() - 1, i
					+ thresholdWindowSize);

			// we calculate the mean and multiply it by the factor
			float mean = 0;
			for (int j = start; j <= end; j++)
				mean += detectionFunction.get(j);
			mean /= (end - start);
			threshold.add(mean * multiplier);
		}
	}

	/**
	 * Creates a new array that contains only the detection function values that
	 * are bigger than the threshold. We don't save the detection function value
	 * in this case, we save the difference: detectionFunction - threshold,
	 * i.e., the "magnitude" of the peak.
	 */
	private void calcFilteredDetectionFunction() {
		for (int i = 0; i < threshold.size(); i++) {
			if (threshold.get(i) <= detectionFunction.get(i))
				filteredDetectionFunction.add(detectionFunction.get(i)
						- threshold.get(i));
			else
				filteredDetectionFunction.add((float) 0);
		}
	}

	/**
	 * Fills the array containing the peaks. Any value > 0 in this array is a
	 * peak. In order to calculate the peaks, it needs to calculate the
	 * threshold and the filtered Detection Function as intermediate steps.
	 */
	public void calcPeaks() {

		// the threshold and filtered detectionFunction are needed in order to
		// calculate the peaks
		this.calcThreshold();
		// System.out.println("threshold:");
		// System.out.println(this.threshold);
		this.calcFilteredDetectionFunction();
		// System.out.println("Filtered detection function:");
		// System.out.println(this.filteredDetectionFunction);

		/*
		 * TODO: maybe clean up the resulting peak list and remove any peaks
		 * that are to close together, say < 10ms
		 */
		for (int i = 0; i < filteredDetectionFunction.size() - 1; i++) {
			// in the filtered detection function, a peak is a value bigger than
			// the
			// next value
			if (filteredDetectionFunction.get(i) > filteredDetectionFunction
					.get(i + 1)) {
				peaks.add(filteredDetectionFunction.get(i));
				// peaks.add((float)1);
			}

			else
				peaks.add((float) 0);
		}
	}

	/**
	 * Gets an array containing the time instants (in seconds) of every onset
	 * 
	 * @param spectralWindowSize
	 *            the size of the spectral window, i.e., the hopsize
	 * @param sampleRate
	 *            the sample rate of the signal
	 * @return the time instances of every onset
	 */
	public ArrayList<Double> getPeaksAsInstantsInTime(
			final int spectralWindowSize, final int sampleRate) {
		/*
		 * TODO: Cuidado q os parametros a receber aqui podem ser diferentes
		 * para outras funções de detecção
		 */

		for (int i = 0; i < this.peaks.size(); i++) {
			if (this.getPeaks().get(i) > 0) {

				this.onsets
						.add((double) (i * (double) spectralWindowSize / ((double) sampleRate)));
			}
		}

		return this.onsets;

	}

	/**
	 * Prints the time instants onsets to a file
	 * 
	 * @param filename
	 *            the full path of the file where to write
	 */
	public void printOnsetsToFile(final String filename) {
		if (onsets.size() > 0) {
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
		} else {
			System.out.println("The onset array list must be calculated "
					+ "first by invoking the method CalcPeaks followed "
					+ "by the method getOnsetsAsInstantsInTime in order "
					+ "to be able to print the onsets to a file");

		}

	}

	/**
	 * @return the spectralFlux
	 */
	public ArrayList<Float> getDetectionFunction() {
		return detectionFunction;
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
		return filteredDetectionFunction;
	}

	/**
	 * An array containing the peaks and zeros
	 * 
	 * @return the peaks
	 */
	public ArrayList<Float> getPeaks() {
		return peaks;
	}

	/**
	 * Gets the onsets as instants in time
	 * 
	 * @return the array list of onsets
	 */
	public ArrayList<Double> getOnsets() {
		return onsets;
	}

}
