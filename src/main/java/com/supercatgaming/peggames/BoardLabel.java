package com.supercatgaming.peggames;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BoardLabel extends JLabel {
	ArrayList<PegHole> holes = new ArrayList<>();
	float scale;
	ImageIcon base;
	
	public BoardLabel(ImageIcon i, int[][][] holesPos) {
		super(i);
		base = i;
		scale = 1;
		for (int x = 0; x < holesPos.length; x++) {
			for (int y = 0; y < holesPos[x].length; y++) {
				PegHole ph = new PegHole(holesPos[x][y][0], holesPos[x][y][1], new int[] {x, y});
				add(ph);
				ph.setBounds(ph.getPos()[0], ph.getPos()[1], ph.getIcon().getIconWidth(), ph.getIcon().getIconHeight());
				holes.add(ph);
			}
		}
	}
	
	public void setScale(float i) {
		scale = i;
		super.setIcon(Handler.scale(scale, base));
		for (PegHole h : holes) {
			h.setScale(i);
		}
		setSize((int)(base.getIconWidth() * scale), (int)(base.getIconHeight() * scale));
	}
	
	public Peg getPegAt(int[] pos) {
		for (PegHole hole : holes) {
			if (Arrays.equals(hole.getIndex(), pos)) {
				return hole.getPeg();
			}
		}
		System.err.println("Peg hole with index " + pos[0] + ", " + pos[1] + " couldn't be found! Returning null");
		return null;
	}
	
	public PegHole getHoleAt(int[] pos) {
		for (PegHole hole : holes) {
			if (Arrays.equals(hole.getIndex(), pos)) {
				return hole;
			}
		}
		System.err.println("Peg hole with index " + pos[0] + ", " + pos[1] + " couldn't be found! Returning null");
		return null;
	}
	
	public ImageIcon getBase() {
		return base;
	}
	
	public void dropPeg(int x, int y, Peg p) {
		x -= getX();
		y -= getY();
		for (PegHole h : holes) {
			if(h.checkPos(x, y) && (Options.freePlay/* || check for rules*/)) {
				h.setPeg(p);
				repaint();
			}
		}
	}
	
//	public float getScale() {
//		return scale;
//	}
}
