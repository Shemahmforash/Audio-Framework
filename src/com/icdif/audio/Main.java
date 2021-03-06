package com.icdif.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.io.AudioDevice;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {
		float[] samples = new float[1024];

		WavDecoder reader = new WavDecoder(new FileInputStream(
				"/home/wanderer/Dropbox/code/java/SoundIt-Basic/teste-wav2.wav"));

		AudioDevice device = new AudioDevice(reader.getSampleRate());

		while (reader.readSamples(samples) > 0) {
			device.playSamples(samples);
		}

		//Thread.sleep(10000);

	}

}
