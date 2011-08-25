/**
 * 
 */
package com.icdif.audio.examples;

import java.awt.Color;
import java.awt.List;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.icdif.audio.analysis.FFT;
import com.icdif.audio.graph.PlaybackPlot;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.MP3Decoder;

/**
 * @author wanderer
 * 
 */
public class ExampleThresholdAndPeaks {

	// public static final String FILE = "samples/explosivo.mp3";
	
	//public static final String FILE = "/home/wanderer/Dropbox/code/java/audio-analysis-read-only/samples/judith.mp3";	

	//public static final String FILE = "/media/LaCie/musica/Rock/Nirvana - Greatest Hits/11.Rape me.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Oldies/Roxette (by Tweety) - Greatest Hits/03_Roxette - The Look.mp3";
	
	public static final String FILE = "/media/LaCie/musica/Neo-Folk/sonne hagal - jordansfrost - 2008/05_sonne_hagal-hidden_flame.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Neo-Folk/hekate - 2004 - goddess/08-hekate-dance_of_taurus-just.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Neo-Classical/Ataraxia - 1998 - Historiae/03 - Filava Melis.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/OST/John Williams - Memoirs Of A Geisha (2005)/03 - Going To School.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Metal/Power-Heavy-Speed-Proggressive Metal/Blind Guardian - Imaginations From The Other Side (95)/Blind Guardian - 08 - Another Holy War.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Portuguesa/Rodrigo Leão - Alma Mater/07 - Pasion.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Rock/QUEEN Greatest Hits I II & III The Platinum Collection/Greatest Hits I/16 - We Will Rock You.mp3";
	
	//public static final String FILE = "/media/LaCie/musica/Neo-Folk/Sol Invictus - 1990 - Trees In Winter/Sol Invictus - TIW - 04 - Media.mp3";
	

	//o numero de valores para cada lado do espectro nos quais faço a média
	public static final int THRESHOLD_WINDOW_SIZE = 10;

	// Multiplico uma constante pela media.
	// Neste caso, para ser um outlier, tem de ser 1.5x maior q a média
	public static final float MULTIPLIER = 1.6f;

	public static void main(String[] argv) throws Exception {
		MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));
		FFT fft = new FFT(1024, 44100);
		fft.window(FFT.HAMMING);
		float[] samples = new float[1024];
		float[] spectrum = new float[1024 / 2 + 1];
		float[] lastSpectrum = new float[1024 / 2 + 1];
		
		//for each spectrum we calculate the difference to the spectrum of the 
		//sample window before the current spectrum. 
		//That’s it. What we get is a single number that tells us the 
		//absolute difference between the bin values of the current 
		//spectrum and the bin values of the last spectrum.
		ArrayList<Float> spectralFlux = new ArrayList<Float>();

		ArrayList<Float> threshold = new ArrayList<Float>();

		while (decoder.readSamples(samples) > 0) {
			fft.forward(samples);
			System.arraycopy(spectrum, 0, lastSpectrum, 0, spectrum.length);
			System
					.arraycopy(fft.getSpectrum(), 0, spectrum, 0,
							spectrum.length);

			float flux = 0;
			for (int i = 0; i < spectrum.length; i++) {
				float value = (spectrum[i] - lastSpectrum[i]);
				flux += value < 0 ? 0 : value;
			}
			spectralFlux.add(flux);
		}

		for (int i = 0; i < spectralFlux.size(); i++) {
			int start = Math.max(0, i - THRESHOLD_WINDOW_SIZE);
			int end = Math.min(spectralFlux.size() - 1, i
					+ THRESHOLD_WINDOW_SIZE);
			float mean = 0;
			for (int j = start; j <= end; j++)
				mean += spectralFlux.get(j);
			mean /= (end - start);
			threshold.add(mean * MULTIPLIER);
		}

		// Escolho apenas aqueles maiores q o treshold
		ArrayList<Float> prunnedSpectralFlux = new ArrayList<Float>();
		for (int i = 0; i < threshold.size(); i++) {
			if (threshold.get(i) <= spectralFlux.get(i))
				prunnedSpectralFlux.add(spectralFlux.get(i) - threshold.get(i));
			else
				prunnedSpectralFlux.add((float) 0);
		}

		// A Peak is a value that is bigger then the next value.
		ArrayList<Float> peaks = new ArrayList<Float>();
		for (int i = 0; i < prunnedSpectralFlux.size() - 1; i++) {
			if (prunnedSpectralFlux.get(i) > prunnedSpectralFlux.get(i + 1))
				peaks.add(prunnedSpectralFlux.get(i));
			else
				peaks.add((float) 0);
		}

		Plot plot = new Plot("Peaks", 1024, 512);
		//plot.plot(spectralFlux, 1, Color.red);
		//plot.plot(threshold, 1, Color.green);
		//plot.plot(prunnedSpectralFlux, 1, Color.blue);
		plot.plot(peaks, 1, Color.yellow);
		new PlaybackPlot(plot, 1024, new MP3Decoder(new FileInputStream(
				FILE)));

	}
}