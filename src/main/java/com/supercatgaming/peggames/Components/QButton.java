package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.GUI;
import com.supercatgaming.peggames.Handler;
import com.supercatgaming.peggames.Options;
import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public class QButton extends JButton {
	
	private Image hoverBackground;
	private Image pressedBackground;
	private Image background;
	
	private ImageIcon hover;
	private ImageIcon selected;
	private ImageIcon unselected;
	
	private Dimension us;
	private Dimension s;
	private Dimension h;
	
	public QButton(String text, Type t) {
		super(text);
		String location = "Buttons" + Handler.S + t.name() + Handler.S;
		setContentAreaFilled(false);
		//init
		unselected = new ImageIcon(Handler.getResources(location + "unselected.png"));
		hover = new ImageIcon(Handler.getResources(location + "hover.png"));
		selected = new ImageIcon(Handler.getResources(location + "selected.png"));
		
		us = new Dimension(unselected.getIconWidth(), unselected.getIconHeight());
		s = new Dimension(selected.getIconWidth(), selected.getIconHeight());
		h = new Dimension(hover.getIconWidth(), hover.getIconHeight());
		
		if (!us.equals(s) || !s.equals(h)) {
			System.err.println("Dimensions of button states are not the same: " + location +
					"\nIt may look weird when hovering or clicking");
		}
		
		setScale(1);
		setBorder(BorderFactory.createEmptyBorder());
		setMargin(new Insets(0, 0, 0, 0));
	}
	
	/**
	 * Sets the scale of the button
	 *
	 * @param scale New scale
	 */
	public void setScale(int scale) {
		int width = (int) us.getWidth() * scale;
		int height = (int) us.getHeight() * scale;
		background = Handler.scale(width, height, unselected).getImage();
		hoverBackground = Handler.scale(width, height, hover).getImage();
		pressedBackground = Handler.scale(width, height, selected).getImage();
		setFont(GUI.subFont);
		setSize(getDimensions());
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (getModel().isPressed()) {
			g.drawImage(pressedBackground, 0, 0, (img, infoflags, x, y, width, height) -> false);
			setForeground(new Color(255, 255, 255));
		} else if (getModel().isRollover()) {
			g.drawImage(hoverBackground, 0, 0, (img, infoflags, x, y, width, height) -> false);
			setForeground(new Color(227, 195, 109));
		} else {
			g.drawImage(background, 0, 0, (img, infoflags, x, y, width, height) -> false);
			setForeground(new Color(216, 187, 166));
		}
		super.paintComponent(g);
	}
	
	/**
	 * @return The dimensions of unselected button (which should be the same as all other versions of the button).
	 */
	public Dimension getDimensions() {
		if (!us.equals(s) || !s.equals(h)) {
			new Exception("Dimensions of button states are not the same: " +
					"\nReturned dimensions are those of unselected").printStackTrace();
		}
		ImageObserver i = (img, infoflags, x, y, width, height) -> false;
		return new Dimension(background.getWidth(i), background.getHeight(i));
	}
	
	public enum Type {
		Large,
		Small,
		Thin
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		switch(id) {
			case MouseEvent.MOUSE_PRESSED:
				Sounds.Click.play();
				break;
			case MouseEvent.MOUSE_ENTERED:
				Sounds.Hover.play();
				break;
		}
	}
}
