/**
 * 
 */
package com.icdif.audio.io;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * This class implements the AudioDecoder Interface and is responsible for
 * decoding the audio data from a wave file, i.e. transforming the bytes in pcm
 * format to arrays of float that we can easily analyze and plot.
 * 
 * @author wanderer
 * 
 */
public class WavDecoder implements AudioDecoder {

	/**
	 * The maximum value allowed for a short
	 */
	private final float MAX_SHORT_VALUE = Short.MAX_VALUE;

	/**
	 * The stream from where we read
	 */
	private final EndianDataInputStream inputStream;

	/**
	 * The number of channels in the original data
	 */
	private final int channels;

	/**
	 * The sample rate in Hertz
	 */
	private float sampleRate;

	/**
	 * Constructor that initializes the stream from where we read the pcm data.
	 * It also reads the header of this stream and checks if it really is valid.
	 * 
	 * @param stream
	 *            The Wave file as a stream
	 * @throws Exception
	 *             When it's not possible to read from the file
	 */
	public WavDecoder(InputStream stream) throws Exception {

		// checks if the stream is valid
		if (stream == null)
			throw new IllegalArgumentException("Input stream must not be null");

		inputStream = new EndianDataInputStream(new BufferedInputStream(stream,
				1024 * 1024));

		if (!inputStream.readStringFrom4Byte().equals("RIFF"))
			throw new IllegalArgumentException("It's not a wav");

		inputStream.readIntLittleEndian();

		if (!inputStream.readStringFrom4Byte().equals("WAVE"))
			throw new IllegalArgumentException("expected WAVE tag");

		if (!inputStream.readStringFrom4Byte().equals("fmt "))
			throw new IllegalArgumentException("expected fmt tag");

		// TODO: adicionar suporte para outro bit-rate
		// checks it it's 16-bit
		if (inputStream.readIntLittleEndian() != 16)
			throw new IllegalArgumentException(
					"expected wave chunk size to be 16");

		if (inputStream.readShortLittleEndian() != 1)
			throw new IllegalArgumentException("expected format to be 1");

		channels = inputStream.readShortLittleEndian();
		sampleRate = inputStream.readIntLittleEndian();

		// TODO: suporte para outros sampling rates
		/*if (sampleRate != 44100)
			throw new IllegalArgumentException("Not 44100 sampling rate");*/

		inputStream.readIntLittleEndian();
		inputStream.readShortLittleEndian();
		int format = inputStream.readShortLittleEndian();

		if (format != 16)
			throw new IllegalArgumentException(
					"Only 16-bit signed format supported");

		if (!inputStream.readStringFrom4Byte().equals("data"))
			throw new RuntimeException("expected data tag");

		inputStream.readIntLittleEndian();

		/*it reads until the start of the data tag, the content of this tag will
		 be read with the function readSample*/

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.icdif.audio.io.Decoder#readSamples(float[])
	 */
	@Override
	public int readSamples(float[] samples) {

		int numSamples = 0;
		for (int i = 0; i < samples.length; i++) {
			float sample = 0;
			try {
				// the sample is an average of the values in the several
				// channels, i.e, we convert to mono, in order to easily make
				// analysis. We also put the sample in the interval [-1,1]
				for (int channel = 0; channel < channels; channel++) {
					int shortValue = inputStream.readShortLittleEndian();
					sample += (shortValue / MAX_SHORT_VALUE);
				}
				sample /= channels;
				samples[i] = sample;
				numSamples++;

			} catch (Exception e) {
				// e.printStackTrace();
				/*
				 * When the exception is thrown, it means that there is no more
				 * data to read from the stream
				 */
				break;
			}
		}

		return numSamples;
	}

	@Override
	public float getSampleRate() {
		return this.sampleRate;
	}

	

}
