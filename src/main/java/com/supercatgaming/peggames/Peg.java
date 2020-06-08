package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Peg {
	static Color[] colors = Options.getColors();
	static ImageIcon desat = new ImageIcon(Handler.getResources("Peg.png"));
	
	int color;
	ImageIcon icon;
	
	public Peg(int color) {
		this.color = color;
		addColor();
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
		icon = Handler.scale(5.5f, new ImageIcon(image));
	}
	
	public ImageIcon getIcon() {
		return icon;
	}
}
