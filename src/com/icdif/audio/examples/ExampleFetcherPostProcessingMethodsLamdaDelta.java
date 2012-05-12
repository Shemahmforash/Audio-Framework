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
public class ExampleFetcherPostProcessingMethodsLamdaDelta {

	/*
	 * public static final File DIRECTORYCORPUS = new File(
	 * "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/"
	 * );
	 */

	/*
	 * public static final File DIRECTORYCORPUS = new File(
	 * "/home/wanderer/Dropbox/ONSETHOLFZAPEL/OnsetDetection_A2/main/audio/");
	 */

	/*
	 * public static final File DIRECTORYCORPUS = new File(
	 * "/home/wanderer/Dropbox/OnsetDB/audio/");
	 */

	public static final File DIRECTORYCORPUS = new File(
			"/home/wanderer/corpus/corpus-small-bello/");

	// public static final String GROUNDTRUTHPATH =
	// "/home/wanderer/Dropbox/ONSETHOLFZAPEL/OnsetDetection_A2/main/ground-truth/";
	public static final String GROUNDTRUTHPATH = "/home/wanderer/Dropbox/OnsetDB/ground-truth/";
	// public static final String GROUNDTRUTHPATH =
	// "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/";

	public static final String FMEASUREDIR = "/home/wanderer/corpus/fmeasure/";

	public static final OnsetMethodology[] methodologies = {
			OnsetMethodology.SpectralFlux,
			OnsetMethodology.HighFrequencyContent,
			OnsetMethodology.PhaseDeviation,
			OnsetMethodology.WeightedPhaseDeviation,
			OnsetMethodology.NormalisedWeightedPhaseDeviation,
			OnsetMethodology.ComplexDomain,
			OnsetMethodology.RectifiedComplexDomain };

	public static final int[] sampleWindowSizeParameters = { /* 512*, */1024 /*
																			 * ,
																			 * 2048
																			 */};

	public static final double hopPercentage = 0.5;

	public static final int[] thresholdWindowSizeParameters = { 10 };

	public static final String separator = ";";

	public static final double deltaDelta = 0.1;

	public static final double maxDelta = 0.5;
	public static final double minDelta = -0.5;

	// public static final float lambda = 0.5f;

	public static final float deltaLamda = 0.1f;
	public static final float minLambda = 0.1f;
	public static final float maxLamda = 1f;

	/*
	 * public static final String postProcessing =
	 * "mean-norm-no-filter-local-max";
	 */

	public static final String[] postProcessingMethods = {
			"mean-norm-no-filter", "mean-norm-filter",
			"mean-norm-no-filter-local-max", "mean-norm-filter-local-max",
			"mean-stdev-no-filter", "mean-stdev-filter",
			"mean-stdev-no-filter-local-max", "mean-stdev-filter-local-max",
			"median-norm-no-filter", "median-norm-filter",
			"median-norm-no-filter-local-max", "median-norm-filter-local-max",
			"median-stdev-no-filter", "median-stdev-filter",
			"median-stdev-no-filter-local-max", "median-stdev-filter-local-max" };

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		for (OnsetMethodology methodology : methodologies) {
			for (int sampleWindowSize : sampleWindowSizeParameters) {
				for (int thresholdWindowSize : thresholdWindowSizeParameters) {
					for (float lambda = minLambda; lambda <= maxLamda; lambda = lambda
							+ deltaLamda) {
						for (double delta = minDelta; delta <= maxDelta; delta = delta
								+ deltaDelta) {
							for (String postProcessing : postProcessingMethods) {
								/* create the dir to receive the results */
								String directoryToPlaceResults = "/home/wanderer/corpus/onsets-2012-04-09/"
										+ methodology.toString()
										+ "/"
										// + "sample="
										+ sampleWindowSize
										// + "_thresh="
										+ +thresholdWindowSize
										+ "lambda="
										+ lambda
										+ "delta="
										+ delta
										+ "postProcessing="
										+ postProcessing
										+ "/";

								if ((new File(directoryToPlaceResults))
										.mkdirs()) {
									System.out.println("Directory: "
											+ directoryToPlaceResults
											+ " created");
								} else {
									System.out
											.println("Couldn't create directory "
													+ directoryToPlaceResults);
									System.exit(1);
								}

								/* run through all the files in the corpus */
								String[] children = DIRECTORYCORPUS.list();
								if (children == null) {
									System.out
											.println("The directory is empty or does not exist");
									System.exit(1);
								} else {

									for (int i = 0; i < children.length; i++) {

										// Get filename of file or directory
										String filename = children[i];

										System.out.println("File: "
												+ DIRECTORYCORPUS + "/"
												+ filename);

										// start processing the files
										AudioDecoder decoder = null;
										DetectionFunction onsetDetector = null;

										if (filename.endsWith(".wav")) {
											decoder = new WavDecoder(
													new FileInputStream(
															DIRECTORYCORPUS
																	+ "/"
																	+ filename));
										} else if (filename.endsWith(".mp3")) {
											decoder = new MP3Decoder(
													new FileInputStream(
															DIRECTORYCORPUS
																	+ "/"
																	+ filename));
										} else {
											System.out
													.println("Only mp3 or wav files supported");
											continue;
										}

										// initializes the method according to
										// the
										// methodology chosen
										switch (methodology) {
										case SpectralFlux:
											onsetDetector = new SpectralDifference(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true);
											break;
										case PhaseDeviation:
											onsetDetector = new PhaseDeviation(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true);
											break;
										case WeightedPhaseDeviation:
											onsetDetector = new PhaseDeviation(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true, true);
											break;
										case NormalisedWeightedPhaseDeviation:
											onsetDetector = new PhaseDeviation(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true, true, true);
											break;
										case HighFrequencyContent:
											onsetDetector = new HighFrequencyContent(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true);
											break;
										case ComplexDomain:
											onsetDetector = new ComplexDomain(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true);
											break;
										case RectifiedComplexDomain:
											onsetDetector = new ComplexDomain(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true, true);
											break;
										default:
											// by default one uses the spectral
											// flux
											onsetDetector = new SpectralDifference(
													decoder,
													sampleWindowSize,
													(int) (sampleWindowSize * hopPercentage),
													true);
											break;
										}

										PeakDetector peaks = new PeakDetector(
												onsetDetector
														.getDetectionFunction(),
												thresholdWindowSize, lambda,
												true, (float) delta);

										/*
										 * calculates the peaks
										 */
										peaks.calcPeaks(postProcessing);

										ArrayList<Double> onsets = peaks
												.getPeaksAsInstantsInTime(
														(int) (sampleWindowSize * hopPercentage),
														(int) decoder
																.getSampleRate());

										peaks.printOnsetsToFile(directoryToPlaceResults
												+ filename + ".txt");

										ResultsEvaluator evaluator = new ResultsEvaluator(
												GROUNDTRUTHPATH);

										evaluator.evaluate(filename,
												directoryToPlaceResults);

										printToFile(
												evaluator.getPrecision()
														+ separator
														+ evaluator.getRec()
														+ separator
														+ evaluator.getFalseNegative()
														+ separator
														+ evaluator.getFalsePositive()
														+ separator
														+ evaluator.getOk()
														+ separator
														+ evaluator.getfMeasure()
														+ separator
														+ methodology.toString()
														+ separator
														+ sampleWindowSize
														+ separator
														+ thresholdWindowSize
														+ separator + lambda
														+ separator + delta
														+ separator
														+ postProcessing,
												FMEASUREDIR + filename + ".csv");

									}// end for that runs through the files

								}

							}

						}
					}
				}// fim do threshold windowsize parameters
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
