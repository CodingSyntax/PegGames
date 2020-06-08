package com.supercatgaming.peggames;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public enum Sounds {
	Click("button_click.wav"),
	Hover("button_hover.wav"),
	Peg("place_peg.wav");
	
	private static final String BASE_LOC = "Sounds/";
	private Clip clip;
	
	Sounds(String soundFileName) {
		try {
			URL url = Handler.getResources(BASE_LOC + soundFileName);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		float volume = (Math.max(Math.min((float)Options.volume / 100, 1f), 0));
		if (clip.isRunning()) clip.stop();
		clip.setFramePosition(0); //rewind to the beginning
		FloatControl gainControl = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN); //volume control
		float gain = (float)(20 * Math.log(volume));
		gainControl.setValue(gain);
		clip.start();
	}
	
	static void init() {
		//noinspection ResultOfMethodCallIgnored
		values(); //calls the constructor for all the elements
	}
}
