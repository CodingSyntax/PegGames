package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class Slider extends JSlider {
	
	public Slider(int min, int max, int value) {
		super(min, max, value);
		setOpaque(false);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		switch(id) {
			case MouseEvent.MOUSE_RELEASED:
				Sounds.Click.play();
				break;
		}
	}
}
