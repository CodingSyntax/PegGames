package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class CheckBox extends JCheckBox {
	public CheckBox () {
		super();
	}
	public CheckBox(Icon icon) {
		super(icon);
	}
	public CheckBox(Icon icon, boolean selected) {
		super(icon, selected);
	}
	public CheckBox (String text) {
		super(text);
	}
	public CheckBox(Action a) {
		super(a);
	}
	public CheckBox (String text, boolean selected) {
		super(text, selected);
	}
	public CheckBox(String text, Icon icon) {
		super(text, icon);
	}
	public CheckBox (String text, Icon icon, boolean selected) {
		super(text, icon, selected);
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
