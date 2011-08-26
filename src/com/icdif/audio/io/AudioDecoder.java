/**
 * 
 */
package com.icdif.audio.io;

/**
 * An interface that represents a general audio decoder It's purpose is to be
 * implemented by the wav, mp3, ogg, etc. decoders
 * 
 * @author wanderer
 * 
 */
public interface AudioDecoder {

	/**
	 * Reads in samples from the decoder and fills the array passed as parameter
	 * with the values read (It tries to read as many samples from the stream as
	 * there are elements in the array passed in). Returns the actual number
	 * read in. If this number is smaller than samples.length then the end of
	 * stream has been reached. Note: In the present version, it averages from
	 * all the channels into a single channel.
	 * 
	 * @param samples
	 *            The array to which it will write the samples read.
	 * 
	 * 
	 * @return The number of read samples.
	 */
	public int readSamples(float[] samples);
	
	/**
	 * Gets the sample rate of the audio to be decoded
	 * @return the sample rate
	 */
	public float getSampleRate();

}
