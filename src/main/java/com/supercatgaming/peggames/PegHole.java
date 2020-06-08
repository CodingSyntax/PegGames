package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PegHole extends JLabel {
	public static final ImageIcon HOLE_IMG = new ImageIcon(Handler.getResources("GameImages/PegHole.png"));
	public static final ImageIcon MASK = new ImageIcon(Handler.getResources("GameImages/HoleMask.png"));
	private static final int DEPTH = 75; //How far into the hole the peg is rendered
	private ImageIcon preScale = HOLE_IMG;
	float scale = 1;
	private int x, y; //Coords when scale = 1
	private Peg peg;
	private int holeY = 0;
	private int holeX = 0;
	
	private int[] index; //"array pos" used for game mechanics/rules
	
	public PegHole(int x, int y, int[] index) {
		this(x, y, index, null);
	}
	public PegHole(int x, int y, int[] index, Peg peg) {
		this.x = x;
		this.y = y;
		this.index = index;
		setPeg(peg);
	}
	
	public int[] getPos() {
		return new int[] {(int)((x - holeX) * scale), (int)((y - holeY) * scale)};
	}
	
	public void setPeg(Peg p) {
		peg = p;
		if (p != null) stitch();
		else setIcon(Handler.scale(scale, preScale));
		setBounds();
	}
	
	private void setBounds() {
		setBounds(getPos()[0], getPos()[1], getIcon().getIconWidth(), getIcon().getIconHeight());
	}
	
	private void stitch() {
		BufferedImage hole = Handler.toBufferedImage(HOLE_IMG.getImage());
		BufferedImage p = Handler.toBufferedImage(peg.getIcon().getImage());
		BufferedImage mask = Handler.toBufferedImage(MASK.getImage());
		
		BufferedImage result = new BufferedImage(p.getWidth(), p.getHeight() - DEPTH, BufferedImage.TYPE_INT_ARGB);
		
		holeY = result.getHeight() - hole.getHeight();
		holeX = (p.getWidth() - hole.getWidth()) / 2;
		
		// Draw the image on to the buffered image
		Graphics2D bGr = result.createGraphics();
		bGr.drawImage(hole, holeX, holeY, null);
		bGr.drawImage(p, 0, 0, null);
		//mask
		for (int y = 0; y < mask.getHeight(); y++) {
			for (int x = 0; x < mask.getWidth(); x++) {
				if (mask.getRGB(x, y) == Color.BLACK.getRGB()) {
					bGr.setColor(new Color(0, 0, 0, 0));
					bGr.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
					bGr.fillRect(holeX + x, holeY + y, 1, 1);
				}
			}
		}
		bGr.dispose();
		preScale = new ImageIcon(result);
		setScale(scale);
	}
	
	public void setScale(float i) {
		scale = i;
		if (preScale != null) setIcon(Handler.scale(i, preScale));
		setBounds();
	}
	
	public void removePeg() {
		peg = null;
		preScale = HOLE_IMG;
		setIcon(Handler.scale(scale, preScale));
		setBounds();
	}
}
