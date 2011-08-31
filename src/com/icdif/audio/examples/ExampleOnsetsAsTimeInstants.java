package com.icdif.audio.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExampleOnsetsAsTimeInstants {

	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/2-artificial.wav";

	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 1.6f;

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		// MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));
		WavDecoder decoder = new WavDecoder(new FileInputStream(FILE));

		/*
		 * Calculates the spectral difference
		 */
		SpectralDifference spectDiff = new SpectralDifference(decoder,
				sampleWindowSize, hopSize, true);

		System.out.println("SF (size = "
				+ spectDiff.getSpectralDifference().size() + "):");
		System.out.println(spectDiff.getSpectralDifference());

		/*
		 * instantiates the peak detector, by passing the spectral difference
		 * already calculated, the threshold window size and the multiplier
		 */
		PeakDetector peaks = new PeakDetector(
				spectDiff.getSpectralDifference(), thresholdWindowSize,
				multiplier);

		/*
		 * calculates the peaks
		 */
		peaks.calcPeaks();

		System.out.println(peaks.getPeaksAsInstantsInTime(hopSize,
				(int) decoder.getSampleRate()));

	}
}
