/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	 * The size of the window to check if a value in the detection function is a
	 * local maximum
	 */
	private int peakSelectionWindowSize = 3;

	/**
	 * If this class uses a peak selection window to find a local maximum or if
	 * it just considers all the values larger than the threshold.
	 */
	private boolean useLocalMaxAsPeakSelectionCondition = true;

	/**
	 * The constant to be multiplied by the "running average". If the value is
	 * bigger than MULTIPLYING_FACTOR * threshold, and is a local maximum then
	 * it's considered a peak
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
	 * > 0 in this array list is a peak
	 */
	private ArrayList<Float> peaks = new ArrayList<Float>();

	/**
	 * This array contains the onsets as time instants. I.e. the instants in
	 * seconds at which the onsets occur
	 */
	private ArrayList<Double> onsets = new ArrayList<Double>();

	/**
	 * Instantiates the class by passing the Detection Function that'll be used
	 * to calculate the threshold (by using this constructor one is assuming all
	 * the parameters to have their default values)
	 * 
	 * @param detectionFunction
	 */
	public PeakDetector(final ArrayList<Float> detectionFunction) {

		this.detectionFunction = detectionFunction;

	}

	/**
	 * Instantiates this class by passing the Detection Function and the
	 * parameters that'll be used to calculate the threshold and the peaks.
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

	public PeakDetector(final ArrayList<Float> detectionFunction,
			int thresholdWindowSize, float multiplier,
			boolean useLocalMaxAsPeakSelectionCondition) {
		super();
		this.detectionFunction = detectionFunction;
		this.thresholdWindowSize = thresholdWindowSize;
		this.multiplier = multiplier;
		this.useLocalMaxAsPeakSelectionCondition = useLocalMaxAsPeakSelectionCondition;
	}

	/**
	 * Instantiates this class by passing the Detection Function and the
	 * parameters that'll be used to calculate the threshold and the peaks.
	 * 
	 * @param detectionFunction
	 * @param thresholdWindowSize
	 *            by default it is 10 samples in each side
	 * @param multiplier
	 *            by default it is 1.6
	 * @param useLocalMaxAsPeakSelectionCondition
	 *            By default it is false
	 * @param peakSelectionWindowSize
	 *            By default it is 3
	 */
	public PeakDetector(final ArrayList<Float> detectionFunction,
			int thresholdWindowSize, float multiplier,
			boolean useLocalMaxAsPeakSelectionCondition,
			int peakSelectionWindowSize) {
		super();
		this.thresholdWindowSize = thresholdWindowSize;
		this.multiplier = multiplier;
		this.detectionFunction = detectionFunction;
		this.peakSelectionWindowSize = peakSelectionWindowSize;
		this.useLocalMaxAsPeakSelectionCondition = useLocalMaxAsPeakSelectionCondition;

	}

	/**
	 * Normalizes and makes a vertical shift so the mean of the function is
	 * zero.
	 */
	private void normalizeDetectionFunction() {
		float max = Collections.max(this.detectionFunction);

		float total = 0;

		// normalization
		for (int i = 0; i < detectionFunction.size(); i++) {
			detectionFunction.set(i, detectionFunction.get(i) / max);
			total += detectionFunction.get(i);
		}

		// now make a shift so that the average will be zero
		float meanvalue = total / detectionFunction.size();
		for (int i = 0; i < detectionFunction.size(); i++) {
			detectionFunction.set(i, detectionFunction.get(i) - meanvalue);
		}
	}

	private void normalizeDetectionFunction(String type) {
		// no type use traditional way
		if (type == "" || type == "traditional") {
			this.normalizeDetectionFunction();
		} else if (type == "Bello") {
			// normalized by subtracting the mean and dividing by the maximum
			// absolute deviation, and then low-pass filtered
			float meanvalue = this.findAverage(detectionFunction);
			float maxAbsDev = this.findMaxAbsoluteDeviation(detectionFunction);
			for (int i = 0; i < detectionFunction.size(); i++) {
				detectionFunction.set(i, (detectionFunction.get(i) - meanvalue)
						/ maxAbsDev);
			}

			// Simple IIR low pass filter
			detectionFunction = this.lowPassFilter(detectionFunction);

		}
	}

	/**
	 * Finds the maximum absolute deviation of an array
	 * 
	 * @param values
	 * @return
	 */
	private float findMaxAbsoluteDeviation(final ArrayList<Float> values) {

		ArrayList<Float> absoluteDeviation = new ArrayList<Float>();

		float mean = this.findAverage(values);
		for (int i = 0; i < values.size(); i++) {
			absoluteDeviation.set(i, Math.abs(values.get(i)));
		}

		float maxAbsDeviation = Collections.max(absoluteDeviation);

		return maxAbsDeviation;
	}

	/**
	 * Finds the average value of an array
	 * 
	 * @param values
	 * @return
	 */
	private float findAverage(final ArrayList<Float> values) {
		float total = 0;

		for (int i = 0; i < values.size(); i++) {
			total += values.get(i);
		}
		float meanvalue = total / detectionFunction.size();

		return meanvalue;
	}

	/**
	 * Finds the median value of an array
	 * 
	 * @param values
	 * @return
	 */
	public float findMedian(final ArrayList<Float> values) {

		Collections.sort(values);

		if (values.size() % 2 == 0) {
			return (values.get((values.size() / 2) - 1) + values.get(values
					.size() / 2)) / 2;
		} else {
			return values.get(values.size() / 2);
		}
	}

	/**
	 * Low pass filters (IIR) an array of values
	 * 
	 * @param input
	 * @return
	 */
	public ArrayList<Float> lowPassFilter(final ArrayList<Float> input) {
		double alpha = 0.15;
		ArrayList<Float> smoothed = new ArrayList<Float>();

		double x0 = 0;

		for (int i = 0; i < input.size(); i++) {
			smoothed.set(i, (float) (alpha * input.get(i) + (1.0 - alpha) * x0));
			x0 = smoothed.get(i);
		}

		return smoothed;
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

	private void calcThreshold(String type) {
		if (type == "" || type == "mean") {
			this.calcThreshold();
		} else if (type == "median") {
			for (int i = 0; i < detectionFunction.size(); i++) {
				// the window starts at 0 or at the current value -
				// THRESHOLD_WINDOW_SIZE
				int start = Math.max(0, i - thresholdWindowSize);

				// the same here, it ends at the last value, or at the current
				// value
				// + THRESHOLD_WINDOW_SIZE
				int end = Math.min(detectionFunction.size() - 1, i
						+ thresholdWindowSize);

				ArrayList<Float> tmpValues = new ArrayList<Float>();
				for (int j = start; j <= end; j++)
					tmpValues.add(detectionFunction.get(j));
				float median = this.findMedian(tmpValues);

				threshold.add(median * multiplier);
			}
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
			if (threshold.get(i) <= detectionFunction.get(i)) {

				/*
				 * if not using the condition of local maximum, then one needs
				 * just to consider the condition bigger than threshold
				 */
				if (!useLocalMaxAsPeakSelectionCondition) {
					filteredDetectionFunction.add(detectionFunction.get(i)
							- threshold.get(i));
				} else {
					/*
					 * the window starts at 0 or at the current index -
					 * peakSelectionWindowSize
					 */
					int start = Math.max(0, i - peakSelectionWindowSize);

					/*
					 * the same here, it ends at the last index, or at the
					 * current index + peakSelectionWindowSize
					 */
					int end = Math.min(detectionFunction.size() - 1, i
							+ peakSelectionWindowSize);

					/*
					 * checks the current value with the values around it (using
					 * a window of size peakSelectionWindowSize) to see if it is
					 * a local max. If it is, then it's added as a candidate for
					 * an onset, else, then zero is added in its turn
					 */
					boolean localMax = true;
					for (int j = start; j <= end; j++) {
						if (detectionFunction.get(i) < detectionFunction.get(j)) {
							localMax = false;
						}
					}

					/*
					 * if the current value is a maximum in the
					 * peakSelectionWindowSize window, then it is selected as a
					 * candidate for onset
					 */
					if (localMax)
						filteredDetectionFunction.add(detectionFunction.get(i)
								- threshold.get(i));
					else
						filteredDetectionFunction.add((float) 0);
				}

			} else
				filteredDetectionFunction.add((float) 0);
		}
	}

	/**
	 * Fills the array containing the peaks. Any value > 0 in this array is a
	 * peak. In order to calculate the peaks, it needs to calculate the
	 * threshold and the filtered Detection Function as intermediate steps.
	 */
	public void calcPeaks() {

		// first one normalizes the detection function and makes the mean to be
		// zero
		this.normalizeDetectionFunction();

		/*
		 * the threshold and filtered detectionFunction are needed in order to
		 * calculate the peaks
		 */
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
			/*
			 * in the filtered detection function, a peak is a value bigger than
			 * the next value
			 */
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
		for (int i = 0; i < this.peaks.size(); i++) {
			if (this.getPeaks().get(i) > 0) {
				/*
				 * converts the value to seconds by considering the window size
				 * and the sample rate
				 */
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
