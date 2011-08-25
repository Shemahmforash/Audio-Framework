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
public class SpectralDifference {

	/**
	 * The audio decoder in order to transform the audio data into an array of
	 * floats
	 */
	private final AudioDecoder decoder;

	/**
	 * the current sample array
	 */
	private float[] samples;

	/**
	 * The following samples
	 */
	private float[] nextSamples;

	/**
	 * Temporary array, to be used as an auxiliar
	 */
	private float[] tempSamples;

	/**
	 * the current sample, always modulo sample window size
	 */
	private int currentSample = 0;

	/**
	 * The hopSize (the margin of overlaping sample windows)
	 */
	private final int hopSize;

	/**
	 * The Fast Fourier transform
	 */
	private final FFT fft;

	/**
	 * The sample rate of the sound input
	 */
	private final int sampleRate;

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
	 * @param sampleRate
	 *            The sample rate of the audio
	 */
	public SpectralDifference(AudioDecoder decoder, final int sampleWindowSize,
			final int hopSize, final boolean isHamming, final int sampleRate) {

		if (decoder == null)
			throw new IllegalArgumentException("Decoder must be != null");

		if (sampleWindowSize <= 0)
			throw new IllegalArgumentException("Sample window size must be > 0");
		if (hopSize <= 0)
			throw new IllegalArgumentException("Hop size must be > 0");

		if (sampleWindowSize < hopSize)
			throw new IllegalArgumentException("Hop size must be <= sampleSize");

		// TODO: ACEITAR HOPSIZE NULO E, NESSE CASO, N USAR SOBREPOSIÇÃO DE
		// BANDAS

		// I set the parameters
		this.decoder = decoder;
		this.hopSize = hopSize;
		this.sampleRate = sampleRate;
		

		// I initiate the arrays of the samples, with the size of the sample
		// window
		this.samples = new float[sampleWindowSize];
		this.nextSamples = new float[sampleWindowSize];
		this.tempSamples = new float[sampleWindowSize];

		// instantiates the Fourier transform
		fft = new FFT(sampleWindowSize, sampleRate);

		// if true, we set the window type of the fft, to be Hamming
		// (hamming will basically smooth the samples).
		if (isHamming)
			fft.window(FFT.HAMMING);

		// catches the first two arrays of samples from the audio decoder
		decoder.readSamples(samples);
		decoder.readSamples(nextSamples);

		// calculates the spectralDifference
		this.calcspectralDifference();

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
			//if there are no more samples to read, it quits this method
			if (decoder.readSamples(nextSamples) == 0)
				return null;
			currentSample -= samples.length;
		}

		// copies from samples (starting in the currentSample) to
		// tempSamples(starting at 0), by an ammount of (samples.length -
		// currentSample)
		System.arraycopy(samples, currentSample, tempSamples, 0, samples.length
				- currentSample);

		// copies from nextSamples (starting at 0) to the tempSamples (starting
		// at samples.length - currentsample) by an ammount of currentSample
		System.arraycopy(nextSamples, 0, tempSamples, samples.length
				- currentSample, currentSample);
		

		// it performs the forward Fourier Transform in the tempSamples array
		// (i.e. an array containing the samples from the overlaping area of the
		// two windows)
		fft.forward(tempSamples);

		// it jumps by an ammount defined as the hopping Size
		currentSample += hopSize;

		// it returns the output of the fourier transform
		return fft.getSpectrum();
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
	 * Calculates and sets the Spectral Difference
	 * 
	 * @return an ArrayList of Floats containing the Spectral Difference
	 */
	private void calcspectralDifference() {

		float[] spectrum = this.nextSpectrum();
		float[] lastSpectrum = new float[spectrum.length];

		// while there are samples to read, we calculate the flux for each
		// window
		do {

			float flux = 0;
			for (int i = 0; i < spectrum.length; i++) {
				float value = (spectrum[i] - lastSpectrum[i]);
				// the negative values don't have interest
				flux += value < 0 ? 0 : value;
			}

			spectralDifference.add(flux);

			// the lastSpectrum in the following iteration is the
			// currentSpectrum of this iteration
			System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);

		} while ((spectrum = this.nextSpectrum()) != null);

	}

	/**
	 * Gets the Spectral Difference (a function that measures how quickly the
	 * power spectrum of a signal is changing)
	 * 
	 * @return the spectralDifference
	 */
	public ArrayList<Float> getSpectralDifference() {
		System.out.println("Total Spec Diff: " + spectralDifference.size());
		return spectralDifference;
	}	

}
