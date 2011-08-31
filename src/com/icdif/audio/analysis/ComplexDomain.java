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
public class ComplexDomain extends DetectionFunction {

	public ComplexDomain(AudioDecoder decoder, int sampleWindowSize,
			int hopSize, boolean isHamming) {
		super(decoder, sampleWindowSize, hopSize, isHamming);
		// TODO Auto-generated constructor stub
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
