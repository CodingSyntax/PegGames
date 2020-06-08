package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class Slider extends JSlider {
	
	public Slider() {
		super();
	}
	public Slider(int orientation) {
		super(orientation);
	}
	public Slider(int min, int max) {
		super(min, max);
	}
	public Slider(int min, int max, int value) {
		super(min, max, value);
	}
	public Slider(int orientation, int min, int max, int value) {
		super(orientation, min, max, value);
	}
	public Slider(BoundedRangeModel brm)
	{
		super(brm);
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
