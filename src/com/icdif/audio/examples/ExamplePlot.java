/**
 * 
 */
package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.icdif.audio.analysis.SamplesReader;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExamplePlot {
	
	public static final String FILE = "/media/LaCie/musica/Rock/Nirvana - Greatest Hits/11.Rape me.mp3";
	
	//public static final String FILE = "/home/wanderer/samples/09_sonne_hagal-who_has_seen_the_wind.mp3";
	/**
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		/*
		 * WavDecoder decoder = new WavDecoder( new FileInputStream(
		 * "/home/wanderer/Dropbox/code/java/SoundIt-Basic/teste-wav2.wav"));
		 */

		MP3Decoder decoder = new MP3Decoder(
				new FileInputStream(FILE));

		/*
		 * ArrayList<Float> allSamples = new ArrayList<Float>(); float[] samples
		 * = new float[1024];
		 * 
		 * while (decoder.readSamples(samples) > 0) { for (int i = 0; i <
		 * samples.length; i++) allSamples.add(samples[i]); }
		 */

		SamplesReader samplesReader = new SamplesReader(decoder, 1024);

		ArrayList<Float> allSamples = samplesReader.getAllSamples();

		// converto de array list para array, para poder usar no plot
		/*
		 * float[] totalSamples = new float[allSamples.size()]; for (int i = 0;
		 * i < totalSamples.length; i++) totalSamples[i] = allSamples.get(i);
		 */

		System.out.println("Samples: " + allSamples.size());

		Plot plot = new Plot("Teste Wav", 800, 600);
		// o 2º numero dá a "resolução", isto é, o numero total de pontos a
		// aparecer em cada pixel
		// plot.plot(allSamples, 44100 / 1000, Color.green);
		
		float samplesPerPixel = 1024;//allSamples.size() / 10000;
		

		plot.plot(allSamples, samplesPerPixel,
				Color.green);

		plot
				.PlayInPlot(
						samplesPerPixel,
						new MP3Decoder(
								new FileInputStream(FILE)));

	}
}
