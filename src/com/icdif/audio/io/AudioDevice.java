package com.icdif.audio.io;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;

/**
 * A class that allows the passage of PCM float data to the audio device
 * 
 * @author wanderer
 * 
 */
public class AudioDevice {

	/**
	 * the buffer size in samples
	 */
	private final int BUFFER_SIZE = 1024;

	/**
	 * the java sound line, where to send data to play
	 */
	private final SourceDataLine soundLine;

	/**
	 * buffer used for playing BUFFER_SIZE 16-bit (2 bytes) samples
	 */
	private byte[] byteBuffer = new byte[BUFFER_SIZE * 2];

	/**
	 * Initializes the audio system
	 * 
	 * @throws LineUnavailableException
	 *             when the audio is not available
	 */
	public AudioDevice(final float sampleRate) throws LineUnavailableException {

		/*
		 * Defines the audio format
		 */
		AudioFormat format = getAudioFormat(sampleRate);

		/*
		 * Opens the line to be used in playing
		 */
		soundLine = AudioSystem.getSourceDataLine(format);
		soundLine.open(format);
		soundLine.start();
	}

	/**
	 * Passes the samples to the soundcard that plays them. (Old times )Note:
	 * The samples have to be sampled at 44100Hz, mono and have to be in the
	 * range [-1,1].
	 * 
	 * @param samples
	 *            The Samples to play
	 */
	public void playSamples(final float[] samples) {
		// TODO: tocar em stereo e noutro sample rate

		// fills the buffer by converting the integers to byte arrays
		this.fillBuffer(samples);
		// passes the array of bytes to the line, i.e., the soundcard
		soundLine.write(byteBuffer, 0, byteBuffer.length);
	}

	/**
	 * Converts the samples from the array of float bytes and populates the
	 * byteBuffer array
	 * 
	 * @param samples
	 *            The Samples to fill the buffer
	 */
	private void fillBuffer(final float[] samples) {
		for (int i = 0, j = 0; i < samples.length; i++, j += 2) {
			// converts a normalized float into a short
			short value = (short) (samples[i] * Short.MAX_VALUE);

			/*
			 * buffer[j] = (byte) (value | 0xff); // >> Signed right shift
			 * buffer[j + 1] = (byte) (value >> 8);
			 */

			// converts the short, to an array of two bytes
			// the 0xff and the bitshift are meant to force the results to be in
			// the interval 0-254
			byteBuffer[j] = (byte) (value & 0xff);
			byteBuffer[j + 1] = (byte) ((value >> 8) & 0xff);

			// Note: A narrowing cast discards the high-order bits that
			// don't fit into the narrower type.
			// !!!parece q assim dá saltos ao ouvir!!
			// byteBuffer[j] = (byte) value;
			// byteBuffer[j + 1] = (byte) (value >>> 8);
		}
	}

	/**
	 * By using the stop method of the Line, it can pause the playing (it can be
	 * resumed later)
	 */
	public void pausePlaying() {
		try {
			if (soundLine.isRunning()) {
				soundLine.stop();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * By using the start method of the line, it can resume playing
	 */
	public void resumePlaying() {
		try {
			if (!soundLine.isRunning()) {
				soundLine.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops playing, flushes and closes the line
	 */
	public void stopPlaying() {
		try {
			soundLine.stop();
			soundLine.flush();
			// soundLine.drain();
			soundLine.close();
		} catch (Exception e) {
			System.out.println("It's not possible to stop the audio because: "
					+ e.getMessage());
		}
	}

	/**
	 * Defines the audio format by default
	 * 
	 * @return the audio format
	 */
	private AudioFormat getAudioFormat() {
		return new AudioFormat(Encoding.PCM_SIGNED, 44100, 16, 1, 2, 44100,
				false);
	}

	/**
	 * Defines the audio format
	 * 
	 * @param sampleRate
	 *            the sample rate of the audio to be played
	 * @return the audio format object
	 */
	private AudioFormat getAudioFormat(final float sampleRate) {
		return new AudioFormat(Encoding.PCM_SIGNED, sampleRate, 16, 1, 2,
				sampleRate, false);
	}

	// TODO: Suporte para tocar outro sample size (agora só suporta 16bits), para
	// stéreo e para outro framerate (agora apenas suporta 2)

}