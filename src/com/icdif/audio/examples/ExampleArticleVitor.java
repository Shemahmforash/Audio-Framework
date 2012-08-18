package com.icdif.audio.examples;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.graph.Plot;

public class ExampleArticleVitor {

//	public static final String FILE = "/home/wanderer/Dropbox/artigo vitor/alloco1.txt";
//	public static final String FILE = "/home/wanderer/Dropbox/artigo vitor/barbagianni3.txt";
	public static final String FILE = "/home/wanderer/Dropbox/artigo vitor/civetta8.txt";

	public static void main(String[] args) {

		ArrayList<Float> values = new ArrayList<Float>();

		values = readFile(FILE);

		PeakDetector peaks = new PeakDetector(values, 10, 0.1f, true, 0.03f);

		// calculates the peaks
		peaks.calcPeaks("mean-norm-no-filter-local-max");
		
		Plot plot = new Plot("Peaks", 1024, 512);
		/* plot.plot(spectDiff.getDetectionFunction(), 1, Color.blue); */
		plot.plot(peaks.getDetectionFunction(), 1, Color.blue);
		plot.plot(peaks.getThreshold(), 1, Color.red);
		plot.plot(peaks.getPeaks(), 1, Color.green);

	}

	/**
	 * 
	 */
	public static ArrayList<Float> readFile(String filename) {

		ArrayList<Float> values = new ArrayList<Float>();

		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(filename);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// Print the content on the console
				// System.out.println(strLine);

				Float f = new Float(strLine);

				values.add(f);

			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		return values;
	}

}
