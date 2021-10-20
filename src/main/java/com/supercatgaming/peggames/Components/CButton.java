package com.supercatgaming.peggames.Components;

import com.supercatgaming.peggames.Handler;
import com.supercatgaming.peggames.Options;
import com.supercatgaming.peggames.Sounds;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CButton extends JButton {
	private static final ImageIcon[] dice = new ImageIcon[] {
			getImage(1), getImage(2), getImage(3), getImage(4), getImage(5), getImage(6)
	};
	
	private static ImageIcon getImage(int dieNum) {
		return new ImageIcon(Handler.getResources("GameImages/Dice/" + dieNum + ".png"));
	}
	
	int dieNum = 0;
	boolean isDie = false;
	public CButton() {
		super();
	}
	public CButton(String text) {
		super(text);
	}
	public CButton(boolean b) {
		super();
		isDie = b;
		if (b) {
			setBackground(new Color(0, true));
		}
	}
	
	public int getDieNum() {
		return dieNum + 1;
	}
	public void setDieNum(int num) {
		dieNum = num - 1;
		resize();
	}
	
	public void resize() {
		setIcon(Handler.scale(getWidth(), getHeight(), dice[dieNum]));
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		switch(id) {
			case MouseEvent.MOUSE_PRESSED:
				if (Options.useRealDice || !isDie) {
					Sounds.Click.play();
					if (isDie) {
						if (e.getButton() == 1) {
							if (++dieNum == 6) dieNum = 0;
						} else if (e.getButton() == 3) {
							if (--dieNum == -1) dieNum = 5;
						}
						resize();
					}
				}
				break;
			case MouseEvent.MOUSE_ENTERED:
				if (Options.useRealDice || !isDie)
					Sounds.Hover.play();
				break;
		}
	}
}
