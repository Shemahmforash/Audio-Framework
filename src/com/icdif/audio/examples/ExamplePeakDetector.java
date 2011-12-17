/**
 * 
 */
package com.icdif.audio.examples;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.icdif.audio.analysis.PeakDetector;
import com.icdif.audio.analysis.SpectralDifference;
import com.icdif.audio.graph.PlaybackPlot;
import com.icdif.audio.graph.Plot;
import com.icdif.audio.io.MP3Decoder;
import com.icdif.audio.io.WavDecoder;

/**
 * @author wanderer
 * 
 */
public class ExamplePeakDetector {

	// public static final String FILE =
	// "/media/LaCie/musica/Rock/Nirvana - Greatest Hits/11.Rape me.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Oldies/Roxette (by Tweety) - Greatest Hits/03_Roxette - The Look.mp3";

	// public static final String FILE =
	// "/media/Lacie/musica/Neo-Folk/sonne hagal - jordansfrost - 2008/05_sonne_hagal-hidden_flame.mp3";

	public static final String FILE = "/home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/sounds/6-three.wav";

	// public static final String FILE =
	// "/media/LaCie/musica/Neo-Folk/hekate - 2004 - goddess/08-hekate-dance_of_taurus-just.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Neo-Classical/Ataraxia - 1998 - Historiae/03 - Filava Melis.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/OST/John Williams - Memoirs Of A Geisha (2005)/03 - Going To School.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Metal/Power-Heavy-Speed-Proggressive Metal/Blind Guardian - Imaginations From The Other Side (95)/Blind Guardian - 08 - Another Holy War.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Portuguesa/Rodrigo Leão - Alma Mater/07 - Pasion.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Rock/QUEEN Greatest Hits I II & III The Platinum Collection/Greatest Hits I/16 - We Will Rock You.mp3";

	// public static final String FILE =
	// "/media/LaCie/musica/Neo-Folk/Sol Invictus - 1990 - Trees In Winter/Sol Invictus - TIW - 04 - Media.mp3";

	// public static final String FILE =
	// "/home/wanderer/Dropbox/code/java/SoundIt-Basic/teste-wav2.wav";

	/**
	 * @param args
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException,
			Exception {

		// MP3Decoder decoder = new MP3Decoder(new FileInputStream(FILE));

		WavDecoder decoder = new WavDecoder(new FileInputStream(FILE));

		SpectralDifference spectDiff = new SpectralDifference(decoder, 1024,
				512, true);

		// instantiates the peak detector, by passing the spectral difference
		// already calculated
		PeakDetector peaks = new PeakDetector(spectDiff.getSpectralDifference());

		// calculates the peaks
		peaks.calcPeaks();

		Plot plot = new Plot("Peaks", 1024, 512);
		// plot.plot(spectDiff.getSpectralDifference(), 1, Color.blue);
		// plot.plot(peaks.getThreshold(), 1, Color.red);
		plot.plot(peaks.getPeaks(), 1, Color.green);

		/*
		 * !! the samples per pixel has to be equal to the hopping size supplied
		 * to the spectral difference!!
		 */

		// plot.PlayInPlot(512, new MP3Decoder(new FileInputStream(FILE)));

		plot.PlayInPlot(512, new WavDecoder(new FileInputStream(FILE)));

		// plays and updates the marker in the plot
		// new PlaybackPlot(plot, 512, new WavDecoder(new
		// FileInputStream(FILE)));

	}

}
