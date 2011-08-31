/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * @author wanderer
 * 
 */
public abstract class DetectionFunction {
	/**
	 * The audio decoder in order to transform the audio data into an array of
	 * floats
	 */
	protected AudioDecoder decoder;

	/**
	 * the current sample array
	 */
	private float[] samples;

	/**
	 * The following samples
	 */
	private float[] nextSamples;

	/**
	 * Temporary array, to be used as an auxiliary
	 */
	private float[] tempSamples;

	/**
	 * the current sample, always modulo sample window size
	 */
	protected int currentSample = 0;

	/**
	 * The hopSize (the margin of overlapping sample windows)
	 */
	private final int hopSize;

	/**
	 * The Fast Fourier transform
	 */
	private final FFT fft;

	protected class FFTComponents {
		public float[] real;

		public float[] imaginary;

		public float[] spectrum;
	}

	protected FFTComponents componentsFFT = new FFTComponents();

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
	public DetectionFunction(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming) {
		// TODO: ACEITAR HOPSIZE NULO E, NESSE CASO, N USAR SOBREPOSIÇÃO DE
		// BANDAS
		if (decoder == null)
			throw new IllegalArgumentException("Decoder must be != null");

		if (sampleWindowSize <= 0)
			throw new IllegalArgumentException("Sample window size must be > 0");
		if (hopSize <= 0)
			throw new IllegalArgumentException("Hop size must be > 0");

		if (sampleWindowSize < hopSize)
			throw new IllegalArgumentException("Hop size must be <= sampleSize");

		// I set the parameters
		this.decoder = decoder;
		this.hopSize = hopSize;

		// I initiate the arrays of the samples, with the size of the sample
		// window
		this.samples = new float[sampleWindowSize];
		this.nextSamples = new float[sampleWindowSize];
		this.tempSamples = new float[sampleWindowSize];

		// instantiates the Fourier transform
		fft = new FFT(sampleWindowSize, decoder.getSampleRate());

		// if true, we set the window type of the fft, to be Hamming
		// (hamming will basically smooth the samples).
		if (isHamming)
			fft.window(FFT.HAMMING);

		// catches the first two arrays of samples from the audio decoder
		decoder.readSamples(samples);
		decoder.readSamples(nextSamples);
	}

	/**
	 * Calculates the spectrum of the samples, by using the Fourier Transform
	 * and an hoping margin defined in the constructor, returning the spectrum
	 * or null, when there is no more data to read.
	 * 
	 * @return The next Spectrum or null
	 */
	public float[] nextSpectrum() {

		// when the currentSample is in the following window, it exchanges the
		// place of the following and the current window
		if (currentSample >= samples.length) {
			float[] tmp = nextSamples;
			nextSamples = samples;
			samples = tmp;
			// if there are no more samples to read, it quits this method
			if (decoder.readSamples(nextSamples) == 0)
				return null;
			currentSample -= samples.length;
		}

		// copies from samples (starting in the currentSample) to
		// tempSamples(starting at 0), by an amount of (samples.length -
		// currentSample)
		System.arraycopy(samples, currentSample, tempSamples, 0, samples.length
				- currentSample);

		// copies from nextSamples (starting at 0) to the tempSamples (starting
		// at samples.length - currentsample) by an amount of currentSample
		System.arraycopy(nextSamples, 0, tempSamples, samples.length
				- currentSample, currentSample);

		// it performs the forward Fourier Transform in the tempSamples array
		// (i.e. an array containing the samples from the overlapping area of
		// the
		// two windows)
		fft.forward(tempSamples);

		// it jumps by an amount defined as the hopping Size
		currentSample += hopSize;

		// it returns the output of the Fourier transform
		return fft.getSpectrum();
	}

	/**
	 * Calculates the Fourier Transform of the samples, by using the hoping
	 * margin defined in the constructor and returning the real and imaginary
	 * parts of the transform or null, when there is no more data to read.
	 * 
	 * @return The Real and Imaginary part of the FFT or null
	 */
	public FFTComponents nextPhase() {

		// when the currentSample is in the following window, it exchanges the
		// place of the following and the current window
		if (currentSample >= samples.length) {
			float[] tmp = nextSamples;
			nextSamples = samples;
			samples = tmp;
			// if there are no more samples to read, it quits this method
			if (decoder.readSamples(nextSamples) == 0)
				return null;
			currentSample -= samples.length;
		}

		// copies from samples (starting in the currentSample) to
		// tempSamples(starting at 0), by an amount of (samples.length -
		// currentSample)
		System.arraycopy(samples, currentSample, tempSamples, 0, samples.length
				- currentSample);

		// copies from nextSamples (starting at 0) to the tempSamples (starting
		// at samples.length - currentsample) by an amount of currentSample
		System.arraycopy(nextSamples, 0, tempSamples, samples.length
				- currentSample, currentSample);

		/**
		 * it performs the forward Fourier Transform in the tempSamples array
		 * (i.e. an array containing the samples from the overlapping area of
		 * the two windows)
		 */
		fft.forward(tempSamples);

		// it jumps by an amount defined as the hopping Size
		currentSample += hopSize;

		/**
		 * Sets the attributes of the inner class
		 */
		componentsFFT.imaginary = fft.getImaginaryPart();
		componentsFFT.real = fft.getRealPart();

		componentsFFT.spectrum = fft.getSpectrum();

		return componentsFFT;
	}

	/**
	 * Using the calcPhase method it calculates the phase array for all the
	 * frequencies in a bin of the output of the FFT
	 * 
	 * @param component
	 *            - an object containing the real and imaginary part of the FFT
	 *            transform.
	 * @return an array of doubles containing the phase in each frequency for a
	 *         particular sample frame
	 */
	protected double[] calcPhaseFromComponents(final FFTComponents component) {
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
	protected double calcPhase(final float real, final float imag) {
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

	/**
	 * Gets the instance of the FFT used for calculus
	 * 
	 * @return the FFT instance used for calculus
	 */
	public FFT getFft() {
		return fft;
	}

	/**
	 * Abstract class that returns the values of the detection function
	 * 
	 * @return the values of the detection function
	 */
	public abstract ArrayList<Float> getDetectionFunction();

}
