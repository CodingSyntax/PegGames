package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class CButton extends JButton {
	public CButton() {
		super();
	}
	public CButton(Icon icon) {
		super(icon);
	}
	public CButton(String text) {
		super(text);
	}
	public CButton(Action a) {
		super(a);
	}
	public CButton(String text, Icon icon) {
		super(text, icon);
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
