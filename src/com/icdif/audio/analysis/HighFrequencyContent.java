/**
 * 
 */
package com.icdif.audio.analysis;

import java.util.ArrayList;

import com.icdif.audio.io.AudioDecoder;

/**
 * @author wanderer
 *
 */
public class HighFrequencyContent extends DetectionFunction {

	public HighFrequencyContent(AudioDecoder decoder, int sampleWindowSize,
			int hopSize, boolean isHamming) {
		super(decoder, sampleWindowSize, hopSize, isHamming);
	}

	/* (non-Javadoc)
	 * @see com.icdif.audio.analysis.DetectionFunction#getDetectionFunction()
	 */
	@Override
	public ArrayList<Float> getDetectionFunction() {
		// TODO Auto-generated method stub
		return null;
	}

}
