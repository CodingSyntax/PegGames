package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;

public class PegHole extends JLabel {
	private static final ImageIcon HOLE_IMG = new ImageIcon(Handler.getResources("GameImages/PegHole.png"));
	private static final ImageIcon MASK = new ImageIcon(Handler.getResources("GameImages/HoleMask.png"));
	private static final int DEPTH = 15; //How far into the hole the peg is rendered
	private static PegHole selected;
	private static ArrayList<PegHole> multiSelect = new ArrayList<>();
	private ImageIcon preScale = HOLE_IMG;
	private ImageIcon highlight;
	private boolean isHighlight = false;
	private float scale = 1;
	private int x, y; //Coords when scale = 1
	private Peg peg;
	private int holeY = 0;
	private int holeX = 0;
	
	private static int defaultColor = 0;
	
	private int[] index; //"array pos" used for game mechanics/rules
	
	public PegHole(int x, int y, int[] index) {
		this(x, y, index, null);
	}
	public PegHole(int x, int y, int[] index, Peg p) {
		this.x = x;
		this.y = y;
		this.index = index;
		setPeg(p);
		
		MouseAdapter mA = new MouseAdapter() {
			Peg p;
			boolean removed = false;
			@Override
			public void mouseClicked(MouseEvent e) {
				if ((Options.freePlay || (peg != null && !Games.is2P())) && e.getButton() == 1) {
					setPeg(new Peg(defaultColor));
				} else if (e.getButton() == 3) {
					if (Options.freePlay)
						removePeg(true);
					else
						select();
				} else {
					if (!Games.get().isDiceOnly()) {
						if (selected != null)
							Games.get().providePeg(selected.getPeg());
						else
							Games.get().providePeg(null);
						Games.get().check(index);
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (removed)
					p.followMouse(false);
				removed = false;
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (Options.freePlay) {
					Point m = GUI.getMousePos();
					if (!checkPos(m.x - getX(), m.y - getY()) && !removed) {
						p = removePeg();
						if (p != null) {
							removed = true;
							GUI.addToLayer(p);
						}
					}
					if (p != null) {
						p.locateAt();
						p.followMouse(true);
					}
				}
			}
		};
		
		addMouseMotionListener(mA);
		addMouseListener(mA);
	}
	
	private void select() {
		if (peg != null) {
			if (!Games.get().isChallenger()) {
				if (selected != null)
					selected.deselect();
				if (selected != this) {
					selected = this;
					isHighlight = true;
					setScale(scale);
				} else {
					selected = null;
					deselect();
				}
			} else {
				if (!multiSelect.contains(this)) {
					multiSelect.add(this);
					isHighlight = true;
					setScale(scale);
				} else {
					multiSelect.remove(this);
					deselect();
				}
			}
		}
	}
	
	private void deselect() {
		isHighlight = false;
		setScale(scale);
	}
	
	public Peg getPeg() {
		return peg;
	}
	
	public int[] getIndex() {
		return index;
	}
	
	public int[] getPos() {
		return new int[] {(int)((x - holeX) * scale), (int)((y - holeY) * scale)};
	}
	
	public void setPeg(Peg p) {
		peg = p;
		if (p != null) {
			stitch();
			GUI.removeFromLayer(p);
			p.hole = this;
		}
		else setIcon(Handler.scale(scale, preScale));
		setBounds();
	}
	
	public static void setDefaultColor(int c) {
		if (c >= Options.getColors().length) c = Options.getColors().length - 1;
		defaultColor = c;
	}
	
	public static PegHole[] getSelected() {
		return multiSelect.toArray(PegHole[]::new);
	}
	
	private void setBounds() {
		setBounds(getPos()[0], getPos()[1], getIcon().getIconWidth(), getIcon().getIconHeight());
	}
	
	private void stitch() {
		BufferedImage hole = Handler.toBufferedImage(HOLE_IMG.getImage());
		BufferedImage p = Handler.toBufferedImage(peg.getBaseIcon().getImage());
		BufferedImage pH = Handler.toBufferedImage(peg.getHighlight().getImage());
		BufferedImage mask = Handler.toBufferedImage(MASK.getImage());
		
		BufferedImage result = new BufferedImage(p.getWidth(), p.getHeight() - DEPTH, BufferedImage.TYPE_INT_ARGB);
		
		holeY = result.getHeight() - hole.getHeight(); //Height of peg (that extrudes from hole)
		holeX = (p.getWidth() - hole.getWidth()) / 2; //Half of width of peg (that extrudes...)
		
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
		preScale = new ImageIcon(result);
		bGr.dispose();
		ColorModel cm = result.getColorModel();
		BufferedImage i2 = new BufferedImage(cm, result.copyData(result.getRaster().createCompatibleWritableRaster()),
				cm.isAlphaPremultiplied(), null);
		bGr = i2.createGraphics();
		bGr.drawImage(pH, 0, 0, null);
		highlight = new ImageIcon(i2);
		bGr.dispose();
		
		setScale(scale);
	}
	
	public void setScale(float i) {
		scale = i;
		if (preScale != null && !isHighlight) {
			setIcon(Handler.scale(scale, preScale));
		} else if (highlight != null && isHighlight) {
			setIcon(Handler.scale(scale, highlight));
		}
		setBounds();
		Peg.setScale(scale);
	}
	
	public Peg removePeg() {
		return removePeg(false);
	}
	
	public Peg removePeg(boolean delete) {
		isHighlight = false;
		multiSelect.remove(this);
		Peg p = peg;
		if (p != null) {
			//p.hole = null;
			if (delete)
				Peg.delete(p);
		}
		peg = null;
		preScale = HOLE_IMG;
		setIcon(Handler.scale(scale, preScale));
		holeY = 0;
		holeX = 0;
		setBounds();
		return p;
	}
	
	/**
	 *
	 * @param x X-coord to check
	 * @param y Y-coord to check
	 * @return {@code true} if the given position is contained within the hole part of this object; {@code false}
	 * otherwise
	 */
	public boolean checkPos(int x, int y) {
		return x >= this.x * scale && x <= (this.x * scale) + (getWidth() - (holeX * 2 * scale)) &&
				y >= this.y * scale && y <= ((this.y * scale) + getHeight() - (holeY * scale));
	}
}
