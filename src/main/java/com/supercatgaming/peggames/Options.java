package com.supercatgaming.peggames;

import java.awt.*;
import java.util.ArrayList;

public final class Options {
	private static boolean init = false;
	private static ArrayList<Color> defaultColors = new ArrayList<>();
	public static boolean freePlay = false; //Allow placement of pegs anywhere, rules are honorary.
	public static boolean useRealDice = false; //RNG or actually rolling dice.
	public static boolean endGameOnWin = true;
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
				defaultColors.add(new Color(255, 100, 0)); //Orange
				init = true;
				Options.save();
			}
		}
	}
	
	public static Color[] getColors() {
		ArrayList<Color> colors = new ArrayList<>(defaultColors);
		colors.addAll(customColors);
		return colors.toArray(new Color[0]);
	}
	
	public static void save() {
		Handler.writeFile("Options.var", new Object[] {init, defaultColors, freePlay, useRealDice, endGameOnWin,
				customColors, volume});
	}
	
	@SuppressWarnings("unchecked")
	public static boolean load() {
		Object[] opts = (Object[])Handler.readFile("Options.var");
		try { //should load up until the point where null is found (or where array ends).
			init = (boolean)opts[0];
			defaultColors = (ArrayList<Color>)opts[1];
			freePlay = (boolean)opts[2];
			useRealDice = (boolean)opts[3];
			endGameOnWin = (boolean)opts[4];
			customColors = (ArrayList<Color>)opts[5];
			volume = (int)opts[6];
			return true;
		} catch (NullPointerException | ArrayIndexOutOfBoundsException | ClassCastException e) {
			return false;
		}
	}
}
