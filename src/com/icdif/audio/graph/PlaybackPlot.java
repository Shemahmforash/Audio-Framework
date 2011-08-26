package com.icdif.audio.graph;

import java.awt.Color;

import com.icdif.audio.io.AudioDevice;
import com.icdif.audio.io.AudioDecoder;

/**
 * It's no used in the application, it was replaced by a method in the Plot class
 */

/**
 * Receives a plot and a decoder and plays back the audio from the decoder at
 * the same time as it sets the marker in the plot accordingly. The marker
 * position is calculated in pixels by measuring the elapsed time between the
 * start of the playback and the current time. The elapsed time is then
 * multiplied by the frequency divided by the sample window size (1024 samples
 * in this case). This gives us the x-coordinate of the marker in the plot.
 * After writting a sample window to the audio device and setting the marker we
 * sleep for 20ms to give the Swing GUI thread time to repaint the plot with the
 * updated marker position.
 * 
 * @author wanderer
 */
public class PlaybackPlot {
	
	private AudioDevice device;
	
	/**
	 * This constructor plays back the audio form the decoder and sets the
	 * marker of the plot accordingly.
	 * 
	 * @param plot
	 *            The plot in which it will set the marker
	 * @param samplesPerPixel
	 *            the number of samples per pixel.
	 * @param decoder
	 *            The decoder instance, in order to play the audio
	 * @throws Exception
	 *             - When the program can connect to the Line to play the audio
	 */
	public PlaybackPlot(Plot plot, int samplesPerPixel, AudioDecoder decoder)
			throws Exception {
		
		device = new AudioDevice(decoder.getSampleRate());
		
		float[] samples = new float[1024];

		long startTime = 0;
		while (decoder.readSamples(samples) > 0) {

			// starts playing
			device.playSamples(samples);

			// at the first iteration I define the startingTime of the song
			if (startTime == 0)
				startTime = System.nanoTime();

			// TODO: Pq a divisão por 1 x 10^-9 ???
			float elapsedTime = (System.nanoTime() - startTime) / 1000000000.0f;

			// this gives us the position, in pixels, of the marker at the
			// elapsed time
			int position = (int) (elapsedTime * (44100 / samplesPerPixel));

			// sets the Marker in the plot at the position calculated
			plot.setMarker(position, Color.white);

			// sleeps so that the repaint can take effect
			Thread.sleep(20);
		}
	}
	
	public void pausePlaying() {
		device.stopPlaying();
	}
	
	public void resumePlaying() {
		device.resumePlaying();
	}
	
	public void stopPlaying() {
		device.stopPlaying();
	}

}