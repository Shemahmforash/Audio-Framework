package com.icdif.audio.examples;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;

import com.icdif.audio.io.AudioDecoder;
import com.icdif.audio.io.AudioDevice;
import com.icdif.audio.io.WavDecoder;

public class ExampleSaveAudio {

	public static final String INPUTFILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C027.wav";
	public static final String OUTPUTFILE = "/home/wanderer/RM-C027.wav";

	public static final int sampleWindowSize = 8192;

	public static void main(String[] argv) throws Exception {
		AudioDecoder decoder = new WavDecoder(new FileInputStream(INPUTFILE));

		// Lets play the samples:
		AudioDevice device = new AudioDevice(decoder.getSampleRate());

		ArrayList<Float> samples = new ArrayList<Float>();

		Float[] previousSamples;
		previousSamples = new Float[sampleWindowSize];
		Float[] currentSamples;
		currentSamples = new Float[sampleWindowSize];

		decoder.readSamples(previousSamples);
		while (decoder.readSamples(currentSamples) != 0) {
			// device.playSamples(currentSamples);

			// swap current and previous
			samples.addAll(Arrays.asList(currentSamples));
			samples.addAll(Arrays.asList(previousSamples));
			// Collections.addAll(samples, currentSamples);
			/*
			 * for(int i = 0; i < currentSamples.length; i++) {
			 * samples.add(currentSamples[i]); } for(int i = 0; i <
			 * previousSamples.length; i++) { samples.add(previousSamples[i]); }
			 */

			// calculate previousSamples again
			decoder.readSamples(previousSamples);
		}
		System.out.println(samples.size());

		float finalSamples[] = new float[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			finalSamples[i] = samples.get(i);
		}

		for (int i = 0; i < finalSamples.length; i = i + sampleWindowSize) {
			float tmp[] = Arrays.copyOfRange(finalSamples, i, i
					+ sampleWindowSize);
			device.playSamples(tmp);
		}

		/*
		 * ArrayList<Float> finalSamples = new ArrayList<Float>();
		 * 
		 * float[] tmpSamples; tmpSamples = new float[sampleWindowSize]; while
		 * (decoder.readSamples(tmpSamples) != 0) {
		 * //device.playSamples(tmpSamples); for (int i = 0; i <
		 * tmpSamples.length; i++) { samples.add(tmpSamples[i]); } }
		 * 
		 * System.out.println(samples.size());
		 * 
		 * for(int i = 0; i < samples.size(); i = i + 2*sampleWindowSize) {
		 * List<Float> head = samples.subList(i, sampleWindowSize + i);
		 * List<Float> tail = samples.subList(sampleWindowSize + i, i +
		 * 2*sampleWindowSize);
		 * 
		 * //swap consecutive windows finalSamples.addAll(tail);
		 * finalSamples.addAll(head); } System.out.println(finalSamples.size());
		 * 
		 * System.out.println(finalSamples.get(2* sampleWindowSize -1));
		 * System.out.println(samples.get(sampleWindowSize));
		 */

		/*
		 * float[] toPlay = new float[finalSamples.size()]; for (int i = 0; i <
		 * finalSamples.size(); i++) { Float f = finalSamples.get(i); toPlay[i]
		 * = (f != null ? f : 0); }
		 * 
		 * //TODO: está a dar erro a tocar!! DEVE SER POR TER DE PASSAR SAMPLES
		 * DE 1024!!! device.playSamples(toPlay);
		 */
	}

}
