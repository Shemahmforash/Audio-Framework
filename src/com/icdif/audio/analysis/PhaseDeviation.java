package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * 
 * @author wanderer
 * 
 */
public class PhaseDeviation extends DetectionFunction {

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
	 * Calculate and sets the phase deviation
	 */
	public void calcPhaseDeviation() {
		RealImgFFT components = this.nextPhase();
		double[] phase = null;
		double[] previousPhase = null;
		double[] antePreviousPhase = null;

		do {

			/*
			 * get the phase from the components object
			 */
			phase = calcPhaseFromObject(components);

			double phaseDeviation = 0;

			/*
			 * iterate though the bins and sum the modulus of the phase Note:
			 * one has to start on the third bin, because this deviation
			 * compares three adjacent bins
			 */
			for (int i = 0; i < components.real.length; i++) {
				if (previousPhase==null && antePreviousPhase == null) {
					phaseDeviation += Math.sqrt(phase[i] * phase[i]);
				} else if (previousPhase != null && antePreviousPhase == null) {
					phaseDeviation += Math
							.sqrt((phase[i] - 2 * previousPhase[i])
									* (phase[i] - 2 * previousPhase[i]));
				} else {
					phaseDeviation += Math
							.sqrt((phase[i] - 2 * previousPhase[i] - antePreviousPhase[i])
									* (phase[i] - 2 * previousPhase[i] - antePreviousPhase[i]));
				}

			}

			/*
			 * Adds the phase deviation to the list
			 */
			PD.add((float) phaseDeviation / phase.length);
			
			if(previousPhase == null) {
				previousPhase = new double[phase.length];
			}
			if(antePreviousPhase == null) {
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
	 * Using the calcPhase method it calculates the phase array for all the
	 * frequencies in a bin of the output of the FFT
	 * 
	 * @param component
	 *            - an object containing the real and imaginary part of the FFT
	 *            transform.
	 * @return a double array containing the phase in each frequency for a
	 *         particular sample package
	 */
	private double[] calcPhaseFromObject(final RealImgFFT component) {
		double[] phase = new double[component.real.length];

		for (int i = 0; i < component.real.length; i++) {
			phase[i] = calcPhase(component.real[i], component.imaginary[i]);
		}

		return phase;

	}

	/**
	 * Calculates the phase as the arctan between the imaginary and real part of
	 * the FFT and taking into account the quadrant
	 * 
	 * @param real
	 * @param imag
	 * @return
	 */
	private double calcPhase(final float real, final float imag) {
		double phase;

		if (imag == 0 && real == 0) {
			phase = 0.0f;
		} else {
			phase = Math.atan(imag / real);
		}

		/*
		 * Now one must consider the quadrant
		 */
		if (real < 0.0 && imag == 0.0) {
			phase = Math.PI;
		} else if (real < 0.0 && imag == -0.0) {
			phase = -Math.PI;
		} else if (real < 0.0 && imag > 0.0) {
			phase += Math.PI;
		} else if (real < 0.0 && imag < 0.0) {
			phase += -Math.PI;
		}

		return phase;

	}

	public ArrayList<Float> getPD() {
		return PD;
	}

}
