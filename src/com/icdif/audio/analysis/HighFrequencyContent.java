/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * A class that represents the High Frequency Content (HFC) detection function
 * 
 * @author wanderer
 * 
 */
public class HighFrequencyContent extends DetectionFunction {

	/**
	 * An enum that gives the available weighting types for the HFC
	 * 
	 * @author wanderer
	 * 
	 */
	private enum weightingType {
		linear, square
	}

	/**
	 * By default, the weighting is linear
	 */
	private weightingType weighting = weightingType.linear;

	/**
	 * The High Frequency Content function (it'll be calculated with the
	 * constructor)
	 */
	private ArrayList<Float> HFC = new ArrayList<Float>();

	/**
	 * Initiates this class, by supplying the parameters needed
	 * 
	 * @param decoder
	 *            The AudioDecoder that will decode the samples
	 * @param sampleWindowSize
	 *            The size of the window
	 * @param hopSize
	 *            The size of the overlap (it has to be minor than the
	 *            sampleWindow)
	 * @param isHamming
	 *            If the samples are to be smoothed in the FFT by the use of the
	 *            Hamming Function
	 */
	public HighFrequencyContent(AudioDecoder decoder, int sampleWindowSize,
			int hopSize, boolean isHamming) {
		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.calcHighFrequencyContent();
	}

	/**
	 * Initiates this class, by supplying the parameters needed
	 * 
	 * @param decoder
	 *            The AudioDecoder that will decode the samples
	 * @param sampleWindowSize
	 *            The size of the window
	 * @param hopSize
	 *            The size of the overlap (it has to be minor than the
	 *            sampleWindow)
	 * @param isHamming
	 *            If the samples are to be smoothed in the FFT by the use of the
	 *            Hamming Function
	 * @param weigthing
	 *            Defines the weighting (linear, square) used to calculate the
	 *            HFC function
	 */
	public HighFrequencyContent(AudioDecoder decoder, int sampleWindowSize,
			int hopSize, boolean isHamming, weightingType weigthing) {
		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.weighting = weigthing;

		this.calcHighFrequencyContent();

	}

	/**
	 * Calculates and sets the HighFrequencyContent detection function
	 */
	public void calcHighFrequencyContent() {

		float[] spectrum = this.nextSpectrum();

		do {

			float energ = 0;

			/**
			 * runs all the bins in the frame and sums the values calculated in it
			 */
			for (int i = 0; i < spectrum.length; i++) {
				float value = spectrum[i] * spectrum[i];

				switch (weighting) {
				case linear:
					energ = energ + i * value;
					break;
				case square:
					energ = energ + i * i * value;
				default:// by default one uses linear weighting
					energ = energ + i * value;
					break;
				}
			}
			//before adding the value to the HFC function, one must divided it by the number of bins
			this.HFC.add(energ/spectrum.length);

		} while ((spectrum = this.nextSpectrum()) != null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.icdif.audio.analysis.DetectionFunction#getDetectionFunction()
	 */
	@Override
	public ArrayList<Float> getDetectionFunction() {
		return HFC;
	}

}
