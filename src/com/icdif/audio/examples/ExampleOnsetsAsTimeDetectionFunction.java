/**
 * 
 */
package com.icdif.audio.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.icdif.audio.analysis.DetectionFunction;
import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.PhaseDeviation;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.io.AudioDecoder;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExampleOnsetsAsTimeDetectionFunction {

	public static final File DIRECTORY = new File(
			"/home/wanderer/Área de Trabalho/corpus/");

	public enum onsetMethodology {
		SpectralFlux, PhaseDeviation
	}

	public static onsetMethodology methodology = onsetMethodology.PhaseDeviation;

	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 1.05f;

	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		String[] children = DIRECTORY.list();
		if (children == null) {
			System.out.println("The directory is empty or does not exist");
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];

				System.out.println("File: " + DIRECTORY + "/" + filename);

				AudioDecoder decoder = null;
				DetectionFunction onsetDetector = null;

				if (filename.endsWith(".wav")) {
					decoder = new WavDecoder(new FileInputStream(DIRECTORY
							+ "/" + filename));

				} else if (filename.endsWith(".mp3")) {
					decoder = new MP3Decoder(new FileInputStream(DIRECTORY
							+ "/" + filename));
				}
				
				if(decoder == null) {
					continue;
				}

				/**
				 * In this switch we instantiate the correct detection function
				 */
				switch (methodology) {
				case SpectralFlux:
					onsetDetector = new SpectralDifference(decoder,
							sampleWindowSize, hopSize, true);
					break;

				case PhaseDeviation:
					onsetDetector = new PhaseDeviation(decoder,
							sampleWindowSize, hopSize, true);
					break;
				default:
					// by default one uses the spectral flux
					onsetDetector = new SpectralDifference(decoder,
							sampleWindowSize, hopSize, true);
					break;
				}

				/*
				 * instantiates the peak detector, by passing the spectral
				 * difference already calculated, the threshold window size and
				 * the multiplier
				 */
				PeakDetector peaks = new PeakDetector(
						onsetDetector.getDetectionFunction(),
						thresholdWindowSize, multiplier);

				/**
				 * calculates the peaks
				 */
				peaks.calcPeaks();

				/**
				 * Calculates the onsets as time instants and writes them to a
				 * file
				 */
				ArrayList<Double> onsets = peaks.getPeaksAsInstantsInTime(
						hopSize, (int) decoder.getSampleRate());
				
				System.out.println("Onsets:");
				System.out.println(onsets);

				peaks.printOnsetsToFile(DIRECTORY + "/onsets/" + filename
						+ ".txt");

			}
		}
	}
}
