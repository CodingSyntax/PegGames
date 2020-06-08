package com.supercatgaming.peggames;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Handler {
	//References
	
	//File Separator
	public static final String S = System.getProperty("file.separator");
	//Game name
	public static final String NAME = "Peg Games";
	private static final String OS = System.getProperty("os.name").toLowerCase();
	//Folder location
	public static final String FOLDER_LOC = OS.contains("windows") ? "C:\\ProgramData\\" + NAME :
			OS.contains("mac") ? System.getProperty("user.home") + S + NAME : "";
	
	//Methods
	public static void init() {
		File saveLocation = new File(FOLDER_LOC);
		if (saveLocation.exists())
			System.out.println("Folder location already exists!");
		else
			if(saveLocation.mkdirs()) //if dir is created
				System.out.println("Directory Created");
			else
				System.err.println("Options directory couldn't be created... Access may be denied?\n" +
						"This means you won't be able to save your options (including colors)!");
	}
	
	/**
	 * Scales an ImageIcon by the multiplier
	 * @param scale Scale multiplier
	 * @param icon ImageIcon to be scaled
	 * @return Scaled ImageIcon
	 */
	static ImageIcon scale(float scale, ImageIcon icon) {
		if (scale == 1) return icon;
		return scale((int)(icon.getIconWidth() * scale), (int)(icon.getIconHeight() * scale), icon);
	}
	/**
	 * Scales an ImageIcon by the multiplier
	 * @param scale Scale multiplier
	 * @param icon ImageIcon to be scaled
	 * @return Scaled ImageIcon
	 */
	static ImageIcon scale(int scale, ImageIcon icon) {
		return scale(icon.getIconWidth() * scale, icon.getIconHeight() * scale, icon);
	}
	/**
	 * Scales an ImageIcon to the specified dimensions
	 * @param newWidth Exact width of scaled image
	 * @param newHeight Exact height of scaled image
	 * @param icon ImageIcon to be scaled
	 * @return Scaled ImageIcon
	 */
	static ImageIcon scale(int newWidth, int newHeight, ImageIcon icon) {
		//Convert icon to buffered
		BufferedImage sourceBufferedImage = toBufferedImage(icon.getImage());
		//Create new buffered with specified h & w and scale
		BufferedImage destinationBufferedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = destinationBufferedImage.createGraphics();
		g2.drawImage(sourceBufferedImage, 0, 0, newWidth, newHeight, null);
		g2.dispose();
		
		return new ImageIcon(destinationBufferedImage);
	}
	
	/**
	 * Converts image to buffered image for editing
	 * @param img Input image
	 * @return Buffered image
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}
		
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		
		// Return the buffered image
		return bimage;
	}
	
	public static void setText(JLabel label, String text, int widthRestrict) {
		int width = 0;
		int index = 1;
		StringBuilder sFinal = new StringBuilder("<html><div style='text-align: center;'>");
		label.setText(text);
		String[] strs = text.split(" ");
		for (String s : strs) {
			boolean end = index == strs.length;
			int nextWidth = getLengthHTML(end ? s : s + " ", label.getFont(), label);
			if (width + nextWidth >= widthRestrict) {
				//System.out.println("NEW " + width);
				sFinal.append("<br/>").append(s);
				if (!end) {
					sFinal.append(" ");
				}
				width = 0;
			} else {
				width += nextWidth;
				sFinal.append(s);
				if (!end) {
					sFinal.append(" ");
				}
			}
			index++;
		}
		sFinal.append("</html>");
		label.setText(sFinal.toString());
	}
	
	/**
	 * Returns the string's length using the font.
	 * @param text The string to get the length of.
	 * @param font The font to get the string length from.
	 * @param t access to font metrics, just use the JLabel for which you are measuring length.
	 * @return Length of the {@code label}'s text in {@code font}.
	 */
	public static int getLengthHTML(String text, Font font, Component t) {
		if (text.contains("<html>")) {
			String strRegEx = "<[^\\s][^>]*>";
			String[] texts = text.split("<br/>");
			int l = 0;
			for (String s : texts) {
				s = s.replaceAll(strRegEx, "");
				FontMetrics m = t.getFontMetrics(font);
				l = Math.max(l, m.stringWidth(s));
			}
			return l;
		} else {
			return t.getFontMetrics(font).stringWidth(text);
		}
	}
	
	/**
	 * Returns the longest line of text if the label has html creating multiple lines.
	 * @param label The label to get the string length from.
	 * @return Length of the {@code label}'s text.
	 */
	public static int getLengthHTML(JLabel label) {
			return getLengthHTML(label.getText(), label.getFont(), label);
	}
	
	/**
	 * Returns the height of the text in a label if it has html breaks
	 * @param label The label to get the string length from.
	 * @return The number of lines * font height.
	 */
	public static int getHeightHTML(JLabel label) {
		return getHeightHTML(label.getText(), label.getFont(), label);
	}
	
	public static int getHeightHTML(String text, Font font, Component t) {
		if (text.contains("<html>")) {
			String[] texts = text.split("<br/>");
			return texts.length * t.getFontMetrics(font).getHeight();
		} else {
			return t.getFontMetrics(font).getHeight();
		}
	}
	
	/**
	 * Gets the exact location as <code>URL</code> of <code>loc</code>
	 * @param loc relative location of the resource
	 * @return the URL of the resource
	 */
	public static URL getResources(String loc) {
		String dirRoot = getRawResources(loc).replaceAll("\\\\", "/").replaceAll(" ", "%20");
		String file = "file:///" + dirRoot;
		try {
			return new URL(file);
		} catch (MalformedURLException e){
			e.printStackTrace();
			throw new IllegalArgumentException("File at " + file + " is not found!");
		}
	}
	public static String getRawResources(String relLoc) {
		String file = System.getProperty("user.dir") + "/src/main/resources/" + relLoc;
		return file.replace("\\", S).replace("/", S);
	}
	
	/**
	 * Writes object to file
	 * @param file File to create & write to
	 * @param obj Var to write to file
	 */
	public static void writeFile(File file, Object obj) {
		try { //tries creating file and writing to file
			file.createNewFile();
			ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
			os.writeObject(obj); //insert name of var to write here
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Writes object to file
	 * @param loc Location of file to create & write to in relation to game folder
	 * @param obj Var to write to file
	 */
	public static void writeFile(String loc, Object obj) {
		writeFileAbsolute(FOLDER_LOC + S + loc, obj);
	}
	/**
	 * Writes object to file
	 * @param loc Absolute location of file to create & write to
	 * @param obj Var to write to file
	 */
	public static void writeFileAbsolute(String loc, Object obj) {
		writeFile(new File(loc), obj);
	}
	
	/**
	 * Reads object from file
	 * @param loc Location of file to read from in relation to game folder
	 * @return Object saved to folder; null if file doesn't exist
	 */
	public static Object readFile(String loc) {
		return readFileAbsolute(FOLDER_LOC + S + loc);
	}
	/**
	 * Reads object from file
	 * @param loc Location of file to create & write to in relation to game folder
	 * @return Object saved to folder; null if file doesn't exist
	 */
	public static Object readFileAbsolute(String loc) {
		try {
			File file = new File(loc);
			ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
			return is.readObject();
		} catch (NullPointerException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
}
