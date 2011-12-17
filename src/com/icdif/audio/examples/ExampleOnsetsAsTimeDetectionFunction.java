/**
 * 
 */
package com.icdif.audio.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.icdif.audio.analysis.ComplexDomain;
import com.icdif.audio.analysis.DetectionFunction;
import com.icdif.audio.analysis.HighFrequencyContent;
import com.icdif.audio.analysis.OnsetMethodology;
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

	public static final File DIRECTORY = new File("/home/wanderer/corpus/");

	/**
	 * Define the methodology
	 */
	public static OnsetMethodology methodology = OnsetMethodology.PhaseDeviation;

	/*
	 * Parameters to use in the calculus
	 */
	public static final int sampleWindowSize = 1024;

	public static final int hopSize = 512;

	public static final int thresholdWindowSize = 10;

	public static final float multiplier = 1.3f;

	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		String[] children = DIRECTORY.list();
		if (children == null) {
			System.out.println("The directory is empty or does not exist");
		} else {
			// runs the files in the directory and calculates the onsets to all
			// of them
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

				/**
				 * If the file has no mp3 or wav extension, move to the next
				 * file
				 */
				if (decoder == null) {
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
				case WeightedPhaseDeviation:
					onsetDetector = new PhaseDeviation(decoder,
							sampleWindowSize, hopSize, true, true);
					break;
				case NormalisedWeightedPhaseDeviation:
					onsetDetector = new PhaseDeviation(decoder,
							sampleWindowSize, hopSize, true, true, true);
					break;
				case HighFrequencyContent:
					onsetDetector = new HighFrequencyContent(decoder, sampleWindowSize,
							hopSize, true);
					break;
				case ComplexDomain:
					onsetDetector = new ComplexDomain(decoder,
							sampleWindowSize, hopSize, true);
					break;
				case RectifiedComplexDomain:
					onsetDetector = new ComplexDomain(decoder,
							sampleWindowSize, hopSize, true, true);
					break;
				default:
					// by default one uses the spectral flux
					onsetDetector = new SpectralDifference(decoder,
							sampleWindowSize, hopSize, true);
					break;
				}
				
				System.out.println(methodology.toString());
				System.out.println(onsetDetector.getDetectionFunction());

				/**
				 * instantiates the peak detector, by passing the values of the
				 * detection function already calculated, the threshold window
				 * size and the multiplier
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

				// prints the onsets to a file with a same name but with
				// extension .txt
				peaks.printOnsetsToFile(DIRECTORY + "/onsets/"
						+ replaceFileExtension(filename, ".txt"));

			}
		}
	}

	/**
	 * Auxiliary method that gets the file extension of the supplied file name
	 * 
	 * @param fileName
	 *            file Name
	 * @return the extension of the file name
	 */
	public static String getFileExtension(String fileName) {
		String ext = "";
		int i = fileName.lastIndexOf('.');
		if (i > 0 && i < fileName.length() - 1) {
			ext = fileName.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	/**
	 * Auxiliary method that receives a filename and an extension and replaces
	 * the current file extension with the new one
	 * 
	 * @param fileName
	 * @param newExtension
	 * @return the filename with the new extension
	 */
	public static String replaceFileExtension(String fileName,
			String newExtension) {

		String ext = getFileExtension(fileName);
		String newFileName;

		if (ext.equals("")) {
			newFileName = fileName + "." + newExtension;
		} else {
			newFileName = fileName.replaceAll("." + ext, newExtension);
		}

		return newFileName;

	}
}
