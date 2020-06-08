package com.supercatgaming.peggames;

import java.awt.*;
import java.util.ArrayList;

public final class Options {
	private static boolean init = false;
	private static ArrayList<Color> defaultColors = new ArrayList<>();
	public static boolean freePlay = false; //Allow placement of pegs anywhere, rules are honorary.
	public static boolean useRealDice = false; //RNG or actually rolling dice.
	public static ArrayList<Color> customColors = new ArrayList<>(); //self-explanatory
	public static int volume = 50; //0-100
	
	public static void init() {
		if (!load()) { //if load wasn't successful
			if (!init) { //Makes sure init being called twice won't duplicate the array
				defaultColors.add(Color.BLUE);
				defaultColors.add(Color.YELLOW);
				defaultColors.add(Color.RED);
				defaultColors.add(Color.GREEN.darker());
				defaultColors.add(Color.WHITE);
				defaultColors.add(Color.ORANGE);
				init = true;
			}
		}
	}
	
	public static Color[] getColors() {
		ArrayList<Color> colors = new ArrayList<>(defaultColors);
		colors.addAll(customColors);
		return colors.toArray(new Color[0]);
	}
	
	public static void save() {
		Handler.writeFile("Options.var", new Object[] {init, defaultColors, freePlay, useRealDice, customColors,
				volume});
	}
	
	@SuppressWarnings("unchecked")
	public static boolean load() {
		Object[] opts = (Object[])Handler.readFile("Options.var");
		try { //should load up until the point where null is found (or where array ends).
			init = (boolean)opts[0];
			defaultColors = (ArrayList<Color>)opts[1];
			freePlay = (boolean)opts[2];
			useRealDice = (boolean)opts[3];
			customColors = (ArrayList<Color>)opts[4];
			volume = (int)opts[5];
			return true;
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			return false;
		}
	}
}
