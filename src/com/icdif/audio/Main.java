package com.icdif.audio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.io.AudioDevice;
import com.icdif.audio.io.WaveDecoder;

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

		WaveDecoder reader = new WaveDecoder(new FileInputStream(
				"/home/wanderer/Dropbox/code/java/SoundIt-Basic/teste-wav2.wav"));

		AudioDevice device = new AudioDevice();

		while (reader.readSamples(samples) > 0) {
			device.playSamples(samples);
		}

		//Thread.sleep(10000);

	}

}
