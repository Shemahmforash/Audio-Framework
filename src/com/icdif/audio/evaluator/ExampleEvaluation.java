package com.icdif.audio.evaluator;

public class ExampleEvaluation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ResultsEvaluator evaluator = new ResultsEvaluator();
		
		evaluator.evaluate("6-three.txt", "/home/wanderer/corpus/onsets/SpectralFlux/sample=1024_thresh=20_multpl=1.6/");

		System.out.println("fmeasure = " + evaluator.getfMeasure());
		
	}

}
