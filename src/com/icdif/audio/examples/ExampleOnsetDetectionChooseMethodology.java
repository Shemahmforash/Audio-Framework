package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.analysis.ComplexDomain;
import com.icdif.audio.analysis.DetectionFunction;
import com.icdif.audio.analysis.HighFrequencyContent;
import com.icdif.audio.analysis.OnsetMethodology;
import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.PhaseDeviation;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.AudioDecoder;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

public class ExampleOnsetDetectionChooseMethodology {

	// public static final String FILE =
	// "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/8-ambrielb.wav";
	// public static final String FILE =
	// "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/6-three.wav";

	/*
	 * public static final String FILE =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/realorgan3.wav"
	 * ; public static final String groundTruth =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/realorgan3.txt"
	 * ; public static final String myResult =
	 * "/home/wanderer/corpus/results-2011-08-30/SFonsets_mult=1.6/realorgan3.txt"
	 * ;
	 */

	/*
	 * public static final String FILE =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/6-three.wav"
	 * ; public static final String groundTruth =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/6-three.txt"
	 * ; public static final String myResult =
	 * "/home/wanderer/corpus/results-2011-08-30/SFonsets_mult=1.6/6-three.txt";
	 */

	/*
	 * public static final String FILE =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C002.wav"
	 * ; public static final String groundTruth =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/RM-C002.txt"
	 * ; public static final String myResult =
	 * "/home/wanderer/corpus/results-2011-08-30/SFonsets_mult=1.6/RM-C002.txt";
	 */

	/*
	 * public static final String FILE =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C027.wav"
	 * ; public static final String groundTruth =
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/RM-C027.txt"
	 * ; public static final String myResult =
	 * "/home/wanderer/corpus/results-2011-08-30/SFonsets_mult=1.6/RM-C027.txt";
	 */

	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C038.wav";
	public static final String groundTruth = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/RM-C038.txt";
	public static final String myResult = "/home/wanderer/corpus/results-2011-08-30/SFonsets_mult=1.6/RM-C038.txt";

	/**
	 * Define the methodology
	 */
	public static OnsetMethodology methodology = OnsetMethodology.ComplexDomain;
	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 0.04f;

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		AudioDecoder decoder = null;
		DetectionFunction onsetDetector = null;

		/*
		 * System.out.println(Math.exp(0.0)); System.out.println(Math.abs(0.0));
		 */

		if (FILE.endsWith(".wav")) {
			decoder = new WavDecoder(new FileInputStream(FILE));
		} else if (FILE.endsWith(".mp3")) {
			decoder = new MP3Decoder(new FileInputStream(FILE));
		} else {
			System.out.println("Only mp3 or wav files supported");
			System.exit(1);
		}

		switch (methodology) {
		case SpectralFlux:
			onsetDetector = new SpectralDifference(decoder, sampleWindowSize,
					hopSize, true);
			break;
		case PhaseDeviation:
			onsetDetector = new PhaseDeviation(decoder, sampleWindowSize,
					hopSize, true);
			break;
		case WeightedPhaseDeviation:
			onsetDetector = new PhaseDeviation(decoder, sampleWindowSize,
					hopSize, true, true);
			break;
		case NormalisedWeightedPhaseDeviation:
			onsetDetector = new PhaseDeviation(decoder, sampleWindowSize,
					hopSize, true, true, true);
			break;
		case HighFrequencyContent:
			onsetDetector = new HighFrequencyContent(decoder, sampleWindowSize,
					hopSize, true);
			break;
		case ComplexDomain:
			onsetDetector = new ComplexDomain(decoder, sampleWindowSize,
					hopSize, true);
			break;
		case RectifiedComplexDomain:
			onsetDetector = new ComplexDomain(decoder, sampleWindowSize,
					hopSize, true, true);
			break;
		default:
			// by default one uses the spectral flux
			onsetDetector = new SpectralDifference(decoder, sampleWindowSize,
					hopSize, true);
			break;
		}

		/**
		 * instantiates the peak detector, by passing the values of the
		 * detection function already calculated, the threshold window size and
		 * the multiplier
		 */
		PeakDetector peaks = new PeakDetector(
				onsetDetector.getDetectionFunction(), thresholdWindowSize,
				multiplier, true);

		/*
		 * calculates the peaks
		 */
		peaks.calcPeaks();
		// peaks.calcPeaks("Bello");

		System.out.println(methodology.toString());
		System.out.println(peaks.getDetectionFunction());
		System.out.println("Onsets:");
		System.out.println(peaks.getPeaksAsInstantsInTime(hopSize,
				(int) decoder.getSampleRate()));

		Plot plot = new Plot(methodology.toString() + "(multiplier = "
				+ multiplier + ")", 800, 600);

		plot.plot(peaks.getThreshold(), 1, Color.red);
		// plot.plot(peaks.getFilteredDetectionFunction(), 1, Color.green);

		// plot.plot(peaks.getPeaks(), 1, Color.CYAN);

		plot.plotFromTimeInstants(
				peaks.getPeaksAsInstantsInTime(hopSize,
						(int) decoder.getSampleRate()), hopSize,
				decoder.getSampleRate(), Color.MAGENTA);

		plot.plotFromFile(new FileInputStream(groundTruth), hopSize,
				decoder.getSampleRate(), Color.blue);

		// plot.plotFromFile(new FileInputStream(myResult), hopSize,
		// decoder.getSampleRate(), Color.magenta);

		plot.PlayInPlot(hopSize, new WavDecoder(new FileInputStream(FILE)));

	}

}
