package com.supercatgaming.peggames;

import javax.swing.*;
import java.util.ArrayList;

public class BoardLabel extends JLabel {
	ArrayList<PegHole> holes = new ArrayList<>();
	float scale;
	ImageIcon base;
	
	public BoardLabel(ImageIcon i, int[][][] holesPos) {
		super(i);
		base = i;
		scale = 1;
		for (int y = 0; y < holesPos.length; y++) {
			for (int x = 0; x < holesPos[y].length; x++) {
				PegHole ph = new PegHole(holesPos[y][x][0], holesPos[y][x][1], new int[] {x, y});
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
	}
	
//	public float getScale() {
//		return scale;
//	}
}
