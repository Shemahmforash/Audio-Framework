package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * A class that detects the onset using the PhaseDeviation (PD) method
 * originally proposed by Bello et al and later refined by Simon Dixon by using
 * weighting (WPD)
 * 
 * @author wanderer
 * 
 */
public class PhaseDeviation extends DetectionFunction {

	/**
	 * Weather to use or not weighting in the phase deviation (According to
	 * Dixon's paper ONSET DETECTION REVISITED). If false - PhaseDeviation(PD),
	 * if true Weighted Phase Deviation (WPD)
	 */
	private boolean useWeighting = false;

	/**
	 * Weather to use Normalization in the weighted phase deviation method
	 * (According to Dixon's paper ONSET DETECTION REVISITED). If true one
	 * obtains the normalised weighted phase deviation method (NWPD). Note: Only
	 * possible if useWeighting = true;
	 */
	private boolean useNormalization = false;

	/**
	 * The Phase Deviation (PD) (it'll be calculated with the constructor)
	 */
	private ArrayList<Float> PD = new ArrayList<Float>();

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
	public PhaseDeviation(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming) {
		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.calcPhaseDeviation();

	}

	/**
	 * Initiates this class, by supplying the parameters needed and giving the
	 * chance to behave like a Weighted Phase Deviation method
	 * 
	 * @param decoder
	 *            The AudioDecoder that will decode the samples
	 * @param sampleWindowSize
	 *            The size of the window
	 * @param hopSizeThe
	 *            size of the overlap (it has to be minor than the sampleWindow)
	 * @param isHamming
	 *            If the samples are to be smoothed in the FFT by the use of the
	 *            Hamming Function
	 * @param useWeighting
	 *            Whether weighting of the phase deviation is used. If true, the
	 *            method is called Weighted Phase Deviation (WPD)
	 */
	public PhaseDeviation(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming,
			final boolean useWeighting) {

		super(decoder, sampleWindowSize, hopSize, isHamming);

		this.useWeighting = useWeighting;

		this.calcPhaseDeviation();

	}

	/**
	 * Initiates this class, by supplying the parameters needed and giving the
	 * chance to behave like a Normalised Weighted Phase Deviation method
	 * 
	 * @param decoder
	 *            The AudioDecoder that will decode the samples
	 * @param sampleWindowSize
	 *            The size of the window
	 * @param hopSizeThe
	 *            size of the overlap (it has to be minor than the sampleWindow)
	 * @param isHamming
	 *            If the samples are to be smoothed in the FFT by the use of the
	 *            Hamming Function
	 * @param useWeighting
	 *            Whether weighting of the phase deviation is used. If true, the
	 *            method is called Weighted Phase Deviation (WPD)
	 * @param useNormalization
	 *            Whether normalization of the weighted phase deviation if used.
	 *            If true, it's called Normalised Weighted Phase Deviation
	 *            (NWPD)
	 */
	public PhaseDeviation(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming,
			final boolean useWeighting, final boolean useNormalization) {

		super(decoder, sampleWindowSize, hopSize, isHamming);

		if (useWeighting == false) {
			System.out
					.println("Weighting needs to be true. Going back to the Phase Deviation method without weighting and without normalization.");
		} else {
			this.useWeighting = useWeighting;
			this.useNormalization = useNormalization;
		}

		this.calcPhaseDeviation();
	}

	/**
	 * Calculate and set the phase deviation
	 */
	public void calcPhaseDeviation() {
		FFTComponents components = this.nextPhase();
		double[] phase = null;
		double[] previousPhase = null;
		double[] antePreviousPhase = null;

		do {

			/*
			 * get the phase from the components object
			 */
			phase = calcPhaseFromComponents(components);

			double phaseDeviation = 0;

			/**
			 * used to normalize
			 */
			float totalSpectrum = 0;

			/*
			 * iterate though the bins and sum the modulus of the phase
			 * deviation. deltaPhi = Phi(n)-2Phi(n-1)+Phi(n-2) If using
			 * weighting, each deltaphi has to be multiplied by the
			 * correspondent spectrum magnitude. Note: One uses the formulas (8)
			 * and (9) of Bello "A tutorial on onset detection in music signals"
			 * and, for the weighting, the formula 2.4 in Simon Dixon
			 * "Onset Detection Revisited"
			 */
			/*
			 * TODO:Check if running only to spectrum.length and using the
			 * imaginary/real results only to this index is correct
			 */
			for (int i = 0; i < components.spectrum.length; i++) {
				if (useWeighting == false) {
					if (previousPhase == null && antePreviousPhase == null) {
						phaseDeviation += phase[i] > 0 ? phase[i] : (-1)
								* phase[i];
					} else if (previousPhase != null
							&& antePreviousPhase == null) {
						phaseDeviation += (phase[i] - 2 * previousPhase[i]) > 0 ? phase[i]
								- 2 * previousPhase[i]
								: (-1) * (phase[i] - 2 * previousPhase[i]);
					} else {
						phaseDeviation += (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i]) > 0 ? phase[i]
								- 2 * previousPhase[i] - antePreviousPhase[i]
								: (-1)
										* (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i]);
					}
				} else {
					if (previousPhase == null && antePreviousPhase == null) {
						phaseDeviation += (components.spectrum[i] * phase[i]) > 0 ? components.spectrum[i]
								* phase[i]
								: (-1) * components.spectrum[i] * phase[i];
					} else if (previousPhase != null
							&& antePreviousPhase == null) {

						phaseDeviation += (components.spectrum[i] * (phase[i] - 2 * previousPhase[i])) > 0 ? components.spectrum[i]
								* (phase[i] - 2 * previousPhase[i])
								: (-1) * components.spectrum[i]
										* (phase[i] - 2 * previousPhase[i]);
					} else {
						phaseDeviation += ( components.spectrum[i]
								* (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i])) > 0 ? components.spectrum[i]
										* (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i]) : (-1) * components.spectrum[i]
												* (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i]);
					}

				}
				if (useNormalization == true) {
					totalSpectrum += Math.sqrt(components.spectrum[i]
							* components.spectrum[i]);
				}

			}

			/*
			 * Adds the phase deviation to the list, dividing the result
			 * obtained with the number of bins or doing the normalization
			 */
			if (useNormalization == false) {
				PD.add((float) phaseDeviation / components.spectrum.length);
			} else {
				PD.add((float) (phaseDeviation / totalSpectrum));
			}

			/**
			 * prepare the data for the next iteration
			 */
			if (previousPhase == null) {
				previousPhase = new double[phase.length];
			}
			if (antePreviousPhase == null) {
				antePreviousPhase = new double[phase.length];
			}

			// the previous phase in the following iteration is the
			// current phase of this iteration
			System.arraycopy(phase, 0, previousPhase, 0, phase.length);

			// the antepreviousphase in the next iteration is the previous phase
			// in this iteration
			if (previousPhase != null) {
				System.arraycopy(previousPhase, 0, antePreviousPhase, 0,
						phase.length);
			}

		} while ((components = this.nextPhase()) != null);
	}

	/**
	 * deprecated, use getDetectionFunction() instead
	 * 
	 * @return
	 */
	public ArrayList<Float> getPD() {
		return PD;
	}

	@Override
	public ArrayList<Float> getDetectionFunction() {
		return PD;
	}

}
