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
	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/6-three.wav";

	/**
	 * Define the methodology
	 */
	public static OnsetMethodology methodology = OnsetMethodology.ComplexDomain;

	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 1.3f;

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		AudioDecoder decoder = null;
		DetectionFunction onsetDetector = null;

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

		System.out.println(methodology.toString());
		System.out.println(onsetDetector.getDetectionFunction());

		/**
		 * instantiates the peak detector, by passing the values of the
		 * detection function already calculated, the threshold window size and
		 * the multiplier
		 */
		PeakDetector peaks = new PeakDetector(
				onsetDetector.getDetectionFunction(), thresholdWindowSize,
				multiplier);

		/*
		 * calculates the peaks
		 */
		peaks.calcPeaks();
		System.out.println("Onsets:");
		System.out.println(peaks.getPeaksAsInstantsInTime(hopSize,
				(int) decoder.getSampleRate()));

		Plot plot = new Plot(methodology.toString() + "(multiplier = "
				+ multiplier + ")", 800, 600);

		plot.plot(onsetDetector.getDetectionFunction(), 1, Color.green);
		plot.plot(peaks.getThreshold(), 1, Color.red);

		plot.PlayInPlot(hopSize, new WavDecoder(new FileInputStream(FILE)));

	}

}
