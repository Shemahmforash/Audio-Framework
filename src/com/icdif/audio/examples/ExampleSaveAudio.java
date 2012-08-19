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

	//public static final String INPUTFILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C027.wav";
	public static final String INPUTFILE = "/home/wanderer/Dropbox/artigo vitor/Comboio.wav";
	//public static final String OUTPUTFILE = "/home/wanderer/RM-C027.wav";
	public static final String OUTPUTFILE = "/home/wanderer/Dropbox/artigo vitor/comboio-obfuscated.wav";

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

			// get previousSamples again
			decoder.readSamples(previousSamples);
		}
		System.out.println(samples.size());

		float finalSamples[] = new float[samples.size()];
		for (int i = 0; i < samples.size(); i++) {
			finalSamples[i] = samples.get(i);
		}

		// SAVE IT
		device.saveSamples(finalSamples, OUTPUTFILE);
		//TODO: Está a gravar ao dobro da velocidade
		
		/* Play it */
		/*for (int i = 0; i < finalSamples.length; i = i + sampleWindowSize) {
			float tmp[] = Arrays.copyOfRange(finalSamples, i, i
					+ sampleWindowSize);
			device.playSamples(tmp);
		}*/
	}
}
