/**
 * 
 */
package com.icdif.audio.evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * Evaluates the results by using a cpp command line evaluator that compares the
 * results with the ground truths
 * 
 * @author wanderer
 * 
 */
public class ResultsEvaluator {

	private final String GROUNDTRUTHPATH = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/";

	private int ok;

	private int falsePositive;

	private int falseNegative;

	private int doubled;

	private int merged;

	private float mergedRate;

	private float doubledRate;

	private float meanDeviation;

	private float precision;

	private float recall;

	private float fMeasure;

	/**
	 * 
	 */
	public ResultsEvaluator() {
		super();
	}

	/**
	 * @param ok
	 * @param falsePositive
	 * @param falseNegative
	 * @param doubled
	 * @param merged
	 * @param mergedRate
	 * @param doubledRate
	 * @param meanDeviation
	 * @param precision
	 * @param rec
	 * @param fMeasure
	 */
	public ResultsEvaluator(int ok, int falsePositive, int falseNegative,
			int doubled, int merged, int mergedRate, int doubledRate,
			float meanDeviation, float precision, float rec, float fMeasure) {
		super();
		this.ok = ok;
		this.falsePositive = falsePositive;
		this.falseNegative = falseNegative;
		this.doubled = doubled;
		this.merged = merged;
		this.mergedRate = mergedRate;
		this.doubledRate = doubledRate;
		this.meanDeviation = meanDeviation;
		this.precision = precision;
		this.recall = rec;
		this.fMeasure = fMeasure;
	}

	/**
	 * It runs the cpp evaluator and puts its output into variables of this
	 * class
	 * 
	 * @param onsetsFileName
	 *            - just the name of the file, do not include paths
	 * @param resultsPath
	 *            - the path where the results are stored
	 */
	public void evaluate(String onsetsFileName, String resultsPath) {
		File onsetsFile = new File(onsetsFileName);

		CommandExecution CE = new CommandExecution("./eval " + GROUNDTRUTHPATH
				+ getFileNameWithoutExtension(onsetsFileName) + ".txt" + " "
				+ resultsPath + getFileNameWithoutExtension(onsetsFileName)
				+ ".wav" + ".txt");

		String evaluation = CE.getCommandStdOut();

		if (evaluation != null) {
			BufferedReader reader = new BufferedReader(new StringReader(
					evaluation));

			String line;
			try {
				while ((line = reader.readLine()) != null) {
					int equalPosition = 0;
					if (line.contains("=")) {
						equalPosition = line.lastIndexOf("=");
					}

					// System.out.println("Line: " + line);
					
					//to avoid error when converting nan to integer/float:
					if(line.endsWith("nan")) {
						line = line.replace("nan", "0");
					}

					if (line.contains("OK")) {
						this.ok = Integer.parseInt(line
								.substring(equalPosition + 2));
					} else if (line.contains("FP")) {
						this.falsePositive = Integer.parseInt(line
								.substring(equalPosition + 2));
					} else if (line.contains("FN")) {
						this.falseNegative = Integer.parseInt(line
								.substring(equalPosition + 2));
					} else if (line.contains("DoubledRate")) {
						this.doubledRate = Float.parseFloat(line
								.substring(equalPosition + 2));
					} else if (line.contains("MergedRate")) {
						this.mergedRate = Float.parseFloat(line
								.substring(equalPosition + 2));
					} else if (line.contains("Merged")) {
						this.merged = Integer.parseInt(line
								.substring(equalPosition + 2));
					} else if (line.contains("Doubled")) {
						this.doubled = Integer.parseInt(line
								.substring(equalPosition + 2));
					} else if (line.contains("MeanDeviation")) {
						this.meanDeviation = Float.parseFloat(line
								.substring(equalPosition + 2));
					} else if (line.contains("Prec")) {
						this.precision = Float.parseFloat(line
								.substring(equalPosition + 2));
					} else if (line.contains("Rec")) {
						this.recall = Float.parseFloat(line
								.substring(equalPosition + 2));
					} else if (line.contains("Fmeasure")) {
						this.fMeasure = Float.parseFloat(line
								.substring(equalPosition + 2));
					}

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Returns a filename without extension
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileNameWithoutExtension(String fileName) {

		int dot = fileName.lastIndexOf('.');
		int sep = fileName.lastIndexOf('/');

		return fileName.substring(sep + 1, dot);
	}

	public String getGROUNDTRUTHPATH() {
		return GROUNDTRUTHPATH;
	}

	public int getOk() {
		return ok;
	}

	public int getFalsePositive() {
		return falsePositive;
	}

	public int getFalseNegative() {
		return falseNegative;
	}

	public int getDoubled() {
		return doubled;
	}

	public int getMerged() {
		return merged;
	}

	public float getMergedRate() {
		return mergedRate;
	}

	public float getDoubledRate() {
		return doubledRate;
	}

	public float getMeanDeviation() {
		return meanDeviation;
	}

	public float getPrecision() {
		return precision;
	}

	public float getRec() {
		return recall;
	}

	public float getfMeasure() {
		return fMeasure;
	}

}
