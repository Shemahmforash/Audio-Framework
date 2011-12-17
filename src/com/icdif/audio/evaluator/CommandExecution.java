package com.icdif.audio.evaluator;

import java.io.*;

public class CommandExecution {
	public CommandExecution(String command) {
		
		System.out.println("Comando: " + command);
		
		try {
			String line;
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				//System.out.println(line);
				this.commandStdOut += line + "\n";
			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	private String commandStdOut;

	public String getCommandStdOut() {
		return commandStdOut;
	}



	public static void main(String argv[]) {
		//new CommandExecution("./home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/evaluator/eval");
		
		new CommandExecution("./eval /home/wanderer/Dropbox/inesc/ist-chorus/onset-detection/grfia.dlsi.ua.es/cm/worklines/pertusa/onset/ODB/ground-truth/6-three.txt /home/wanderer/corpus/onsets/SpectralFlux/sample=1024_thresh=20_multpl=1.6/6-three.wav.txt");
	}
}