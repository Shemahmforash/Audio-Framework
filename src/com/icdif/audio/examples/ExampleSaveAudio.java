package com.icdif.audio.examples;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.icdif.audio.io.AudioDecoder;
import com.icdif.audio.io.AudioDevice;
import com.icdif.audio.io.WavDecoder;

public class ExampleSaveAudio {

	public static final String INPUTFILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/RM-C027.wav";
	public static final String OUTPUTFILE = "/home/wanderer/RM-C027.wav";

	public static final int sampleWindowSize = 1024;

	public static void main(String[] argv) throws Exception {
		AudioDecoder decoder = new WavDecoder(new FileInputStream(INPUTFILE));

		//Lets play the samples:
		AudioDevice device = new AudioDevice(decoder.getSampleRate());
		
		ArrayList<Float> samples = new ArrayList<Float>();
		ArrayList<Float> finalSamples = new ArrayList<Float>();
		
		float[] tmpSamples;
		tmpSamples = new float[sampleWindowSize];
		while (decoder.readSamples(tmpSamples) != 0) {
			//device.playSamples(tmpSamples);
			for (int i = 0; i < tmpSamples.length; i++) {
				samples.add(tmpSamples[i]);
			}
		}

		System.out.println(samples.size());
		
		for(int i = 0; i < samples.size(); i = i + 2*sampleWindowSize) {
			List<Float> head = samples.subList(i, sampleWindowSize + i);
			List<Float> tail = samples.subList(sampleWindowSize + i, i + 2*sampleWindowSize);
			
			//swap consecutive windows
			finalSamples.addAll(tail);
			finalSamples.addAll(head);			
		}
		System.out.println(finalSamples.size());
		
		System.out.println(finalSamples.get(2* sampleWindowSize -1));
		System.out.println(samples.get(sampleWindowSize));
		
		/*float[] toPlay = new float[finalSamples.size()];
		for (int i = 0; i < finalSamples.size(); i++) {
		    Float f = finalSamples.get(i);
		    toPlay[i] = (f != null ? f : 0);
		}
		
		//TODO: está a dar erro a tocar!! DEVE SER POR TER DE PASSAR SAMPLES DE 1024!!!
		device.playSamples(toPlay);*/
	}

}
