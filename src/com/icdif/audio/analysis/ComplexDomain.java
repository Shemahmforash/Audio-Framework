/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * A class that represents the ComplexDomain method of detecting onsets
 * 
 * @author wanderer
 * 
 */
public class ComplexDomain extends DetectionFunction {

	/**
	 * If uses or not Rectification (according to Dixon's
	 * "Onset Detection Revisited")
	 */
	private boolean isRectified = false;

	/**
	 * Complex Domain (CD) values (it'll be calculated with the constructor)
	 */
	private ArrayList<Float> CD = new ArrayList<Float>();

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
	public ComplexDomain(AudioDecoder decoder, int sampleWindowSize,
			int hopSize, boolean isHamming) {
		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.calcComplexDomain();
	}

	/**
	 * Initiates this class, by supplying the parameters needed and enabling it
	 * to behave like a Rectified Complex Domain
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
	 * @param isRectified
	 *            If it rectifies the Detection Function
	 */
	public ComplexDomain(final AudioDecoder decoder,
			final int sampleWindowSize, final int hopSize,
			final boolean isHamming, final boolean isRectified) {

		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.isRectified = isRectified;

		this.calcComplexDomain();

	}

	/**
	 * Calculates and sets the Complex Domain function values
	 */
	public void calcComplexDomain() {

		FFTComponents components = this.nextPhase();
		float[] spectrum = components.spectrum;
		float[] lastSpectrum = new float[spectrum.length];
		double[] phase = new double[components.real.length];
		double[] previousPhase = new double[components.real.length];
		double[] antePreviousPhase = new double[components.real.length];

		do {

			/*
			 * get the phase from the components object
			 */
			phase = calcPhaseFromComponents(components);

			float complex = 0;

			double targetValue = 0;

			/**
			 * prepare the data for the next iteration
			 */
			/*
			 * if (previousPhase == null) { previousPhase = new
			 * double[phase.length]; } if (antePreviousPhase == null) {
			 * antePreviousPhase = new double[phase.length]; }
			 */

			for (int i = 0; i < components.spectrum.length; i++) {
				/*targetValue = Math.abs(lastSpectrum[i])
								* Math.exp(2 * previousPhase[i]
										- antePreviousPhase[i]);*/
				//use the cosine to include just the real part of the complex exponential
				targetValue = Math.abs(lastSpectrum[i])
						* Math.cos(2 * previousPhase[i]
								- antePreviousPhase[i]);
				if (!isRectified) {

					complex += Math.abs(spectrum[i] - targetValue);
				} else {
					// when there is rectification, one only sums the values
					// when the current spectrum is bigger than the previous one
					if (spectrum[i] > lastSpectrum[i]) {
						complex += Math.abs(spectrum[i] - targetValue);
					}
				}
			}

			CD.add(complex);

			/**
			 * prepare the data for the next iteration
			 */
			// the lastSpectrum in the following iteration is the
			// currentSpectrum of this iteration
			System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);

			// the antepreviousphase in the next iteration is the previous phase
			// in this iteration
			if (previousPhase != null) {
				System.arraycopy(previousPhase, 0, antePreviousPhase, 0,
						previousPhase.length);
			}

			// the previous phase in the following iteration is the
			// current phase of this iteration
			System.arraycopy(phase, 0, previousPhase, 0, phase.length);

		} while ((components = this.nextPhase()) != null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.icdif.audio.analysis.DetectionFunction#getDetectionFunction()
	 */
	@Override
	public ArrayList<Float> getDetectionFunction() {
		return CD;
	}

}
