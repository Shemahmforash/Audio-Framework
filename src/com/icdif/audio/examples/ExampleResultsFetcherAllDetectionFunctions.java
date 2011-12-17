/**
 * 
 */
package com.icdif.audio.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.icdif.audio.analysis.ComplexDomain;
import com.icdif.audio.analysis.DetectionFunction;
import com.icdif.audio.analysis.HighFrequencyContent;
import com.icdif.audio.analysis.OnsetMethodology;
import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.PhaseDeviation;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.evaluator.ResultsEvaluator;
import com.icdif.audio.io.AudioDecoder;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExampleResultsFetcherAllDetectionFunctions {

	public static final String DIRECTORYFMEASURE = "/home/wanderer/corpus/fmeasure/";

	/**
	 * The directory where the corpus files are stored
	 */
	public static final File DIRECTORYCORPUS = new File(
			"/home/wanderer/corpus/");

	public static final String FMEASUREDIR = "/home/wanderer/corpus/fmeasure/";

	/**
	 * Define the methodology
	 */
	// public static OnsetMethodology methodology =
	// OnsetMethodology.ComplexDomain;

	public static final OnsetMethodology[] methodologies = {
			OnsetMethodology.SpectralFlux,
			OnsetMethodology.HighFrequencyContent,
			OnsetMethodology.PhaseDeviation,
			OnsetMethodology.WeightedPhaseDeviation,
			OnsetMethodology.NormalisedWeightedPhaseDeviation,
			OnsetMethodology.ComplexDomain,
			OnsetMethodology.RectifiedComplexDomain };

	/*
	 * Define the parameters
	 */
	public static final int[] sampleWindowSizeParameters = { 1024 };

	public static final int hopSize = 512;

	public static final int[] thresholdWindowSizeParameters = { 10, 20 };

	public static final float[] multiplierParameters = { 0.0f, 0.1f, 0.2f,
			0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f,
			1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f };

	public static final int[] peakSelectionWindowSizeParameters = { 0, 1, 2, 3,
			4, 5 };

	public static final String separator = ";";

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		// TODO Auto-generated method stub
		for (OnsetMethodology methodology : methodologies) {
			for (int sampleWindowSize : sampleWindowSizeParameters) {
				for (int thresholdWindowSize : thresholdWindowSizeParameters) {
					for (float multiplier : multiplierParameters) {
						for (int peakSelectionWindowSize : peakSelectionWindowSizeParameters) {
							/* create the dir to receive the results */
							String directoryToPlaceResults = "/home/wanderer/corpus/onsets-2011-12-12/"
									+ methodology.toString()
									+ "/"
									// + "sample="
									+ sampleWindowSize
									// + "_thresh="
									+ +thresholdWindowSize
									+ "_multpl="
									+ multiplier
									+ "_peakwindow"
									+ peakSelectionWindowSize + "/";

							if ((new File(directoryToPlaceResults)).mkdirs()) {
								System.out.println("Directory: "
										+ directoryToPlaceResults + " created");
							} else {
								System.out.println("Couldn't create directory "
										+ directoryToPlaceResults);
								System.exit(1);
							}

							/* run through all the files in the corpus */
							String[] children = DIRECTORYCORPUS.list();
							if (children == null) {
								System.out
										.println("The directory is empty or does not exist");
							} else {
								for (int i = 0; i < children.length; i++) {
									// Get filename of file or directory
									String filename = children[i];

									System.out.println("File: "
											+ DIRECTORYCORPUS + "/" + filename);

									// start processing the files
									AudioDecoder decoder = null;
									DetectionFunction onsetDetector = null;

									if (filename.endsWith(".wav")) {
										decoder = new WavDecoder(
												new FileInputStream(
														DIRECTORYCORPUS + "/"
																+ filename));
									} else if (filename.endsWith(".mp3")) {
										decoder = new MP3Decoder(
												new FileInputStream(
														DIRECTORYCORPUS + "/"
																+ filename));
									} else {
										System.out
												.println("Only mp3 or wav files supported");
										continue;
									}

									// initializes the method according to the
									// methodology chosen
									switch (methodology) {
									case SpectralFlux:
										onsetDetector = new SpectralDifference(
												decoder, sampleWindowSize,
												hopSize, true);
										break;
									case PhaseDeviation:
										onsetDetector = new PhaseDeviation(
												decoder, sampleWindowSize,
												hopSize, true);
										break;
									case WeightedPhaseDeviation:
										onsetDetector = new PhaseDeviation(
												decoder, sampleWindowSize,
												hopSize, true, true);
										break;
									case NormalisedWeightedPhaseDeviation:
										onsetDetector = new PhaseDeviation(
												decoder, sampleWindowSize,
												hopSize, true, true, true);
										break;
									case HighFrequencyContent:
										onsetDetector = new HighFrequencyContent(
												decoder, sampleWindowSize,
												hopSize, true);
										break;
									case ComplexDomain:
										onsetDetector = new ComplexDomain(
												decoder, sampleWindowSize,
												hopSize, true);
										break;
									case RectifiedComplexDomain:
										onsetDetector = new ComplexDomain(
												decoder, sampleWindowSize,
												hopSize, true, true);
										break;
									default:
										// by default one uses the spectral flux
										onsetDetector = new SpectralDifference(
												decoder, sampleWindowSize,
												hopSize, true);
										break;
									}

									/**
									 * instantiates the peak detector, by
									 * passing the values of the detection
									 * function already calculated, the
									 * threshold window size and the multiplier
									 */
									/*
									 * PeakDetector peaks = new PeakDetector(
									 * onsetDetector.getDetectionFunction(),
									 * thresholdWindowSize, multiplier);
									 */
									PeakDetector peaks;

									if (peakSelectionWindowSize > 0) {
										peaks = new PeakDetector(
												onsetDetector
														.getDetectionFunction(),
												thresholdWindowSize,
												multiplier, true,
												peakSelectionWindowSize);
									} else {
										peaks = new PeakDetector(
												onsetDetector
														.getDetectionFunction(),
												thresholdWindowSize,
												multiplier, false);
									}

									/*
									 * calculates the peaks
									 */
									peaks.calcPeaks();

									ArrayList<Double> onsets = peaks
											.getPeaksAsInstantsInTime(hopSize,
													(int) decoder
															.getSampleRate());

									peaks.printOnsetsToFile(directoryToPlaceResults
											+ filename + ".txt");

									ResultsEvaluator evaluator = new ResultsEvaluator();

									evaluator.evaluate(filename,
											directoryToPlaceResults);

									// System.out.println("Fmeasure: " +
									// evaluator.getfMeasure());

									printToFile(
											evaluator.getfMeasure() + separator
													+ methodology.toString()
													+ separator
													+ sampleWindowSize
													+ separator
													+ thresholdWindowSize
													+ separator + multiplier
													+ separator
													+ peakSelectionWindowSize,
											DIRECTORYFMEASURE + filename
													+ ".txt");

								}// end for that runs through the files
							}
						}
					}
				}
			}
		}
		System.out.println("Done!");
	}

	/**
	 * prints data to a file
	 * 
	 * @param dataToPrint
	 * @param filename
	 *            - the full path to the file
	 */
	private static void printToFile(String dataToPrint, String filename) {
		try {
			FileWriter outFile = new FileWriter(filename, true);
			PrintWriter out = new PrintWriter(outFile);

			// for (int i = 0; i < this.onsets.size(); i++) {
			out.println(dataToPrint);
			// }

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
