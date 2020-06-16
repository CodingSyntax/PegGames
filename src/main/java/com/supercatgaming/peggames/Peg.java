package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Peg extends JLabel {
	static Color[] colors = Options.getColors();
	static ImageIcon desat = new ImageIcon(Handler.getResources("Peg.png"));
	static float scale = 1f;
	static ArrayList<Peg> pegs = new ArrayList<>();
	
	int color;
	ImageIcon baseIcon;
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
	
	private void addColor() {
		Color b = new Color(colors[color].getRed(), colors[color].getGreen(), colors[color].getBlue(), 128);
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
	
	@Override
	public String toString() {
		return "Peg{" +
				"color=" + color +
				", hole=" + hole +
				'}';
	}
}
