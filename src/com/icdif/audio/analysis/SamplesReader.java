package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * This class reads samples, a window at a time, from the Decoder and saves
 * them in an ArrayList
 * 
 * @author wanderer
 * 
 */
public class SamplesReader {

	/**
	 * The attribute that keeps the samples retrieved from the decoder
	 */
	private ArrayList<Float> allSamples = new ArrayList<Float>();

	/**
	 * Instantiates this class, by receiving the audio decoder and the window
	 * size (i.e. the size of the successive steps of retrieval of the data) and
	 * retrieves the data to a attribute of this class
	 * 
	 * @param decoder The Decoder that "contains" the stream to be read
	 * @param windowSize the size of the successive steps of retrieval of the data
	 */
	public SamplesReader(AudioDecoder decoder, final int windowSize) {

		/**
		 * a temporary array to hold the successive windows
		 */
		float[] samples = new float[windowSize];

		/**
		 * It keeps filling the arraylist while there are samples to read from
		 * the decoder
		 */
		while (decoder.readSamples(samples) > 0) {
			for (int i = 0; i < samples.length; i++)
				allSamples.add(samples[i]);
		}

	}

	/**
	 * Gets all the samples retrieved from the decoder
	 * 
	 * @return all the samples
	 */
	public ArrayList<Float> getAllSamples() {
		return allSamples;
	}

}
