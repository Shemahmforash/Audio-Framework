/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * This class is meant to do the calculations needed to obtain a spectral
 * difference (or spectral flux) function. The Spectral flux is a measure of how
 * quickly the power spectrum of a signal is changing, calculated by comparing
 * the power spectrum for one window against the power spectrum from the
 * previous window.
 * 
 * @author wanderer
 * 
 */
public class SpectralDifference extends DetectionFunction {

	private boolean useSquare = false;

	/**
	 * The spectral Flux (it'll be calculated with the constructor)
	 */
	private ArrayList<Float> spectralDifference = new ArrayList<Float>();

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
	public SpectralDifference(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming) {

		super(decoder, sampleWindowSize, hopSize, isHamming);

		// calculates and sets the spectralDifference
		this.calcspectralDifference();

	}

	/**
	 * Initiates this class, by supplying the parameters needed. And giving also
	 * the possibility to use the square of the difference in calculating the
	 * spectral flux.
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
	 * @param useSquare
	 *            Whether to use the square of the difference when calculating
	 *            the spectral flux.
	 */
	public SpectralDifference(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming, final boolean useSquare) {

		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.useSquare = useSquare;

		// calculates and sets the spectralDifference
		this.calcspectralDifference();

	}

	/**
	 * Calculates and sets the Spectral Difference
	 * 
	 * @return an ArrayList of Floats containing the Spectral Difference
	 */
	private void calcspectralDifference() {

		float[] spectrum = this.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];
		// System.out.println("Spectrum length = " + spectrum.length);

		// while there are samples to read, we calculate the flux for each
		// window
		do {

			float flux = 0;
			for (int i = 0; i < spectrum.length; i++) {
				float value = (spectrum[i] - lastSpectrum[i]);
				// the negative values don't have interest
				if(!useSquare) {
					flux += value < 0 ? 0 : value;
				} else {
					flux += value < 0 ? 0 : value * value;
				}				
			}

			spectralDifference.add(flux);

			// the lastSpectrum in the following iteration is the
			// currentSpectrum of this iteration
			System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);

		} while ((spectrum = this.nextSpectrum()) != null);
	}

	/**
	 * Deprecated, use getDetectionFunctio() instead
	 * 
	 * @return the spectralDifference
	 */
	public ArrayList<Float> getSpectralDifference() {
		// System.out.println("Total Spec Diff: " + spectralDifference.size());
		return spectralDifference;
	}

	@Override
	public ArrayList<Float> getDetectionFunction() {		
		return spectralDifference;
	}

}
