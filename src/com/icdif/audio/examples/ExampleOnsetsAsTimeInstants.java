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
				sampleWindowSize, hopSize, true, (int) decoder.getSampleRate());

		/*
		 * instantiates the peak detector, by passing the spectral difference
		 * already calculated
		 */
		PeakDetector peaks = new PeakDetector(spectDiff.getSpectralDifference());

		/*
		 * calculates the peaks
		 */
		peaks.calcPeaks();
		
		System.out.println(peaks.getPeaksAsInstantsInTime(hopSize,
				(int) decoder.getSampleRate()));

	}
}
