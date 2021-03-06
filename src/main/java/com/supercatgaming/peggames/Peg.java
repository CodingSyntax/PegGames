package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.ArrayList;

public class Peg extends JLabel {
	final static ImageIcon desat = new ImageIcon(Handler.getResources("Peg.png"));
	final static ImageIcon highlight = new ImageIcon(Handler.getResources("Peg_Highlight.png"));
	static float scale = 1f;
	static ArrayList<Peg> pegs = new ArrayList<>();
	
	int color;
	ImageIcon baseIcon;
	ImageIcon highlightIco;
	boolean follow = false;
	PegHole hole = null;
	
	public Peg(int color) {
		this.color = color;
		addColor();
		Peg p = this;
		
		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (follow) {
					locateAt();
				}
			}
			@Override
			public void mouseMoved(MouseEvent e) {
				if (follow) {
					locateAt();
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == 1)
					followMouse(true);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				followMouse(false);
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					if (Options.freePlay)
						delete(p);
					else {
						hole.setPeg(p);
						PegHole.loosePeg = false;
						GUI.repaintLayer();
					}
				}
			}
		});
		
		pegs.add(this);
	}
	
	public void locateAt() {
		Point l = GUI.getMousePos();
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		setLocation(l.x - (getIcon().getIconWidth() / 2), l.y - (getIcon().getIconHeight()) + (int)(scale * 5));
	}
	
	public void locateAt(int x, int y) {
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		setLocation(x, y);
	}
	
	public ImageIcon getHighlight() {
		return highlightIco;
	}
	
	public PegHole getHole() {
		return hole;
	}
	
	private void addColor() {
		Color c = getColors()[color];
		Color b = new Color(c.getRed(), c.getGreen(), c.getBlue(), 128);
		//Convert icon to buffered
		BufferedImage image = Handler.toBufferedImage(desat.getImage());
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int clr = image.getRGB(x, y);
				int alpha = (clr & 0xff000000);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				red = (red + b.getRed()) / 2;
				green = (green + b.getGreen()) / 2;
				blue = (blue + b.getBlue()) / 2;
				image.setRGB(x, y, (alpha) + (red << 16) + (green << 8) + blue);
			}
		}
		baseIcon = new ImageIcon(image);
		ColorModel cm = image.getColorModel();
		BufferedImage i2 = new BufferedImage(cm, image.copyData(image.getRaster().createCompatibleWritableRaster()),
				cm.isAlphaPremultiplied(), null);
		Graphics2D g2 = i2.createGraphics();
		g2.drawImage(Handler.toBufferedImage(highlight.getImage()), 0, 0, null);
		g2.dispose();
		highlightIco = new ImageIcon(i2);
		update();
	}
	
	public void followMouse(boolean b) {
		follow = b;
		if (!b) {
			Games.dropPeg(GUI.getMousePos(), this);
		}
	}
	
	public void update() {
		setIcon(Handler.scale(scale, baseIcon));
		setSize(getIcon().getIconWidth(), getIcon().getIconHeight());
		if (getY() >= GUI.getLayerHeight() - 15 || getX() >= GUI.getLayerWidth() - 15)
			locateAt(GUI.getLayerWidth() - GUI.getLayerWidth() / 8, GUI.getLayerHeight() / 2);
	}
	
	public ImageIcon getBaseIcon() {
		return baseIcon;
	}
	
	public static void setScale(float s) {
		scale = s;
		for (Peg p : pegs) {
			p.update();
		}
	}
	
	public static void delete(Peg p) {
		pegs.remove(p);
		GUI.removeFromLayer(p);
		GUI.repaintLayer();
	}
	
	public static boolean isLoose(int color) {
		for (Peg p : pegs) {
			if (p.color == color && p.hole == null) {
				return true;
			}
		}
		return false;
	}
	
	public static int getColor(Peg p) {
		if (p == null) {
			return -1;
		}
		return p.color;
	}
	
	public static Color[] getColors() {
		return Options.getColors();
	}
}
