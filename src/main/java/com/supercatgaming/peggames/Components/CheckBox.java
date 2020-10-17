package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CheckBox extends JCheckBox {
	public CheckBox (String text, boolean selected) {
		super(text, selected);
		setOpaque(false);
		setForeground(Color.WHITE);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		switch(id) {
			case MouseEvent.MOUSE_CLICKED:
				Sounds.Click.play();
				break;
			case MouseEvent.MOUSE_ENTERED:
				Sounds.Hover.play();
				break;
		}
	}
}
