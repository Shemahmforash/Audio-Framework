package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.graph.PlaybackPlot;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

public class ExamplePeaksWithoutPlot {

	// public static final String FILE =
	// "/home/wanderer/samples/cutted/hidden-flame.mp3";

	// public static final String FILE =
	// "/home/wanderer/samples/cutted/6-three.mp3";

	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/6-three.wav";

	// public static final String FILE =
	// "/home/wanderer/samples/pedro/cranberries_-_zombie.wav";

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		// MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));

		WavDecoder decoder = new WavDecoder(new FileInputStream(FILE));

		SpectralDifference spectDiff = new SpectralDifference(decoder, 1024,
				512, true);

		// instantiates the peak detector, by passing the spectral difference
		// already calculateds
		PeakDetector peaks = new PeakDetector(spectDiff.getSpectralDifference());

		// calculates the peaks
		peaks.calcPeaks();

		// eh importante notar q o tamanho da janela da SD é igual ao hopsize
		ArrayList<Double> onsets = peaks.getPeaksAsInstantsInTime(512,
				(int) decoder.getSampleRate());

		/*
		 * for(int i = 0; i < peaks.getPeaks().size(); i++) {
		 * if(peaks.getPeaks().get(i) > 0) { onsets.add((double)(i *
		 * 1024.0/44100.0));
		 * 
		 * }
		 * 
		 * System.out.println("onset(" + i + ") = " + (double)(i *
		 * 1024.0/44100.0)); }
		 */

		peaks.printOnsetsToFile(FILE + "onsets.txt");

		// System.out.println("onsets: " + onsets);
	}

}
