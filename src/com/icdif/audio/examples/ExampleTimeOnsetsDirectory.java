package com.icdif.audio.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

public class ExampleTimeOnsetsDirectory {

	public static final File DIRECTORY = new File(
			"/home/wanderer/Área de Trabalho/corpus_wav/");

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
		String[] children = DIRECTORY.list();
		if (children == null) {
			System.out.println("The directory is empty or does not exist");
		} else {
			for (int i = 0; i < children.length; i++) {
				// Get filename of file or directory
				String filename = children[i];

				System.out.println("File: " + DIRECTORY + "/" + filename);

				if (filename.endsWith(".wav")) {
					WavDecoder decoder = new WavDecoder(new FileInputStream(
							DIRECTORY + "/" + filename));
					SpectralDifference spectDiff = new SpectralDifference(
							decoder, 1024, 512, true, 44100);

					// instantiates the peak detector, by passing the spectral
					// difference
					// already calculateds
					PeakDetector peaks = new PeakDetector(spectDiff
							.getSpectralDifference());

					// calculates the peaks
					peaks.calcPeaks();

					// eh importante notar q o tamanho da janela da SD é igual
					// ao
					// hopsize
					ArrayList<Double> onsets = peaks.getPeaksAsInstantsInTime(
							512, 44100);

					peaks
							.printOnsetsToFile(DIRECTORY + "/onsets/" + filename
									+ ".txt");
				} else if (filename.endsWith(".mp3")) {
					MP3Decoder decoder = new MP3Decoder(new FileInputStream(
							DIRECTORY + "/" + filename));
					SpectralDifference spectDiff = new SpectralDifference(
							decoder, 1024, 512, true, 44100);

					// instantiates the peak detector, by passing the spectral
					// difference
					// already calculateds
					PeakDetector peaks = new PeakDetector(spectDiff
							.getSpectralDifference());

					// calculates the peaks
					peaks.calcPeaks();

					// eh importante notar q o tamanho da janela da SD é igual
					// ao
					// hopsize
					ArrayList<Double> onsets = peaks.getPeaksAsInstantsInTime(
							512, 44100);

					peaks
							.printOnsetsToFile(DIRECTORY + "/onsets/" + filename
									+ ".txt");
				}
			}
		}

	}

}
