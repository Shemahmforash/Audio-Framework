package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.PhaseDeviation;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.WavDecoder;

public class ExampleOnsetPhaseDeviation {

	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/2-artificial.wav";

	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 1.1f;

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		// TODO Auto-generated method stub

		// MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));
		WavDecoder decoder = new WavDecoder(new FileInputStream(FILE));

		/*
		 * Calculates the PD
		 */
		PhaseDeviation PD = new PhaseDeviation(decoder, sampleWindowSize,
				hopSize, true);

		System.out.println("PD:");
		System.out.println(PD.getPD());

		/*
		 * instantiates the peak detector, by passing the spectral difference
		 * already calculated, the threshold window size and the multiplier
		 */
		PeakDetector peaks = new PeakDetector(PD.getPD(), thresholdWindowSize,
				multiplier);

		/*
		 * calculates the peaks
		 */
		peaks.calcPeaks();
		System.out.println("Onsets:");
		System.out.println(peaks.getPeaksAsInstantsInTime(hopSize,
				(int) decoder.getSampleRate()));

		Plot plot = new Plot("Phase Deviation", 800, 600);
		plot.plot(PD.getPD(), 1, Color.green);
		plot.plot(peaks.getThreshold(), 1, Color.red);
		
		plot.PlayInPlot(hopSize, new WavDecoder(new FileInputStream(FILE)));

	}

}
