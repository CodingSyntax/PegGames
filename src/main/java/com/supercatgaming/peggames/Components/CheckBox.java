package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.GUI;
import com.supercatgaming.peggames.Handler;
import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CheckBox extends JCheckBox {
	private static String loc = "Buttons" + Handler.S + "CheckBox" + Handler.S;
	private static ImageIcon hover = new ImageIcon(Handler.getResources(loc + "hover.png"));
	private static ImageIcon selected = new ImageIcon(Handler.getResources(loc + "selected.png"));
	private static ImageIcon unselected = new ImageIcon(Handler.getResources(loc + "unselected.png"));
	private static ImageIcon s_hover = new ImageIcon(Handler.getResources(loc + "selected_hover.png"));
	
	public CheckBox (String text, boolean selected) {
		super(text, selected);
		setOpaque(false);
		setForeground(Color.WHITE);
	}
	
	/**
	 * Sets the scale of the button
	 *
	 * @param scale New scale
	 */
	public void setScale(int scale) {
		ImageIcon background = Handler.scale(scale / 2.15f, unselected);
		ImageIcon hoverBackground = Handler.scale(scale / 2.15f, hover);
		ImageIcon selectedBackground = Handler.scale(scale / 2.15f, selected);
		ImageIcon sHoverBackground = Handler.scale(scale / 2.15f, s_hover);
		setFont(GUI.subFont);
		setSelectedIcon(selectedBackground);
		setIcon(background);
		setRolloverIcon(hoverBackground);
		setRolloverSelectedIcon(sHoverBackground);
		setSize(Handler.getLengthHTML(getText(), getFont(), this) + background.getIconWidth() + 12, 20*scale);
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
