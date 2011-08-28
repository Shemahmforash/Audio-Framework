/**
 * 
 */
package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.graph.PlaybackPlot;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExampleSpectralDifference {

	public static final String FILE = "/media/Lacie/musica/Rock/Nirvana - Greatest Hits/11.Rape me.mp3";

	// public static final String FILE =
	// "/home/wanderer/samples/teste-wav2.wav";

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	/**
	 * @param args
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));

		// WavDecoder decoder = new WavDecoder(new FileInputStream(FILE));

		int windowSize = 1024;
		// o hop é usado para se fazer o cálculo da spectral flux sobrepondo-se
		// várias windows consecutivas
		int hop = 512;

		SpectralDifference spectDiff = new SpectralDifference(decoder,
				windowSize, hop, true);

		Plot plot = new Plot("Spectral Difference", 800, 600);

		System.out.println("Spect Diff Size: "
				+ spectDiff.getSpectralDifference().size());

		// plot.plot(spectDiff.getTotalSamples(), 1024, Color.blue);

		// here we use 1 sample per pixel, i.e., 1 sample per window, because
		// the spectral difference is a function that has a single value per
		// window
		plot.plot(spectDiff.getSpectralDifference(), 1, Color.green);

		// instantiates the peak detector, by passing the spectral difference
		// already calculated
		// PeakDetector peaks = new
		// PeakDetector(spectDiff.getSpectralDifference());

		// calculates the peaks
		// peaks.calcPeaks();

		// plot.plot(peaks.getThreshold(), 1, Color.red);

		// the samples per pixel has to be equal to the hopping size supplied to
		// the spectral difference
		// new PlaybackPlot(plot, 512, new WavDecoder(new
		// FileInputStream(FILE)));
		/*
		 * PlaybackPlot playBack = new PlaybackPlot(plot, 512, new MP3Decoder(
		 * new FileInputStream(FILE)));
		 */

		// plot.PlayInPlot(hop, new MP3Decoder(new FileInputStream(FILE)));

		// float miii = 512f;

		plot.PlayInPlot(hop, new WavDecoder(new FileInputStream(FILE)));

	}

}
