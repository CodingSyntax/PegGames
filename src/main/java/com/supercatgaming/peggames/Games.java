package com.supercatgaming.peggames;

import com.supercatgaming.peggames.Components.CButton;

import javax.swing.*;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static com.supercatgaming.peggames.Handler.*;
import static com.supercatgaming.peggames.Handler.scale;

public final class Games {
	private Games() {/*Don't allow instantiating*/}
	private static int game = 0;
	
	public static final Game[] games = new Game[] {new TicTacToe(), new Conqueror(), new Conqueror2(), new Universe(),
			new Racing(),
			new HorseRace(), new Challenger(), new Magic(), new No1Race()};
	
	public static void prev() {
		if (--game < 0)
			game = games.length - 1;
	}
	
	public static void next() {
		if (++game >= games.length)
			game = 0;
	}
	
	public static Game get() {
		return games[game];
	}
	
	public static abstract class Game {
		protected Game(String name, String inst, int[][][] holesPos) {
			this(name, name, inst, holesPos);
		}
		protected Game(String loc, String name, String inst, int[][][] holesPos) {
			IMG_LOC = loc;
			NAME = name;
			INST = inst;
			this.holesPos = holesPos;
		}
		
		String NAME;
		String IMG_LOC = "";
		String INST = "";
		int[][][] holesPos;
		
		public abstract void play();
		BoardLabel setup() {
			CButton quit = new CButton("Quit");
			BoardLabel l = new BoardLabel(new ImageIcon(getResources(getImgLoc())), holesPos);
			ComponentAdapter cA = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int lw = GUI.getLayerWidth();
					int lh = GUI.getLayerHeight();
					
					quit.setSize(lw / 8, Math.min(Math.max(20, lh / 20), 50));
					quit.setLocation((lw - quit.getWidth()) / 2, lh - (quit.getHeight() + 5));
					
					int iw = l.getBase().getIconWidth();
					int ih = l.getBase().getIconHeight();
					//calculates how many of the base image fits into lw..., lh...
					float min = Math.min((lw - 20f) / iw, (lh - ((quit.getHeight() + 5f) * 2) - 5) / ih);
					l.setScale(Math.min(min, 1.5f));
					l.setLocation((lw - l.getIcon().getIconWidth()) / 2, (lh - l.getIcon().getIconHeight()) / 2);
				}
			};
			quit.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					//TODO: save progress
					GUI.mainMenu(false);
				}
			});
			GUI.addToLayer(quit, l);
			GUI.addCL(cA);
			cA.componentResized(null);
			return l;
		}
		
		public String getName() {
			return NAME;
		}
		public String getInstructions() {
			return INST;
		}
		public String getImgLoc() {
			return "GameImages/" + IMG_LOC + ".png";
		}
	}
	
	public static class TicTacToe extends Game {
		TicTacToe() {
			super("TicTacToe","Players: 2. Place \"X's\" and \"O's\" trying to get three in a row.",
					new int[][][] {
							{{45, 138}, {164, 138}, {284, 139}},
							{{43, 259}, {162, 260}, {283, 261}},
							{{43, 383}, {163, 384}, {281, 385}}
			});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class Conqueror extends Game {
		Conqueror() {
			super("Conqueror", "Players: 1. Fill all the holes with pegs except for one. " +
					"Jump a peg and remove it from the game. Continue until you can't make any more jumps.",
					new int[][][] {
							{{252, 60}},
							{{203, 147}, {303, 146}},
							{{154, 232}, {252, 230}, {353, 230}},
							{{101, 316}, {204, 317}, {303, 316}, {400, 315}},
							{{54, 407}, {155, 407}, {254, 406}, {356, 404}, {454, 402}}
					});
		}
		
		public void play() {
			setup();
		}
	}
	public static class Conqueror2 extends Conqueror {
		Conqueror2() {
			super();
			IMG_LOC = "Conqueror2";
			holesPos = new int[][][] {
					{{269, 73}},
					{{218, 165}, {322, 165}},
					{{164, 256}, {270, 256}, {374, 256}},
					{{112, 349}, {218, 349}, {322, 349}, {428, 349}},
					{{57, 436}, {163, 436}, {271, 436}, {377, 436}, {483, 436}}
			};
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class Universe extends Game {
		Universe() {
			super("Universe", "Players: 1. Place a peg in any of the eight star holes then follow any line that " +
					"goes from the starting star hole and put a peg into that hole. Continue the same procedure from " +
					"that star hole until you cannot make a move. Fill 7 or 8 star holes to win.",
					new int[][][] {{
						{146, 159}, {340, 219}, {150, 419}, {354, 340}, {76, 237}, {263, 417}, {261, 159}, {64, 334}
					}});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class Racing extends Game {
		Racing() {
			super("Racing", "Players: 2. Players alternate rolls of the dice. To start, a player must " +
					"roll a double or the number 1 on one die. Then that player moves to the fist hole. After the " +
					"first hole the player must match the number in the next hole with either the total of both dice " +
					"or a number shown on one die. If a player lands on the opponent's peg, they must go back two " +
					"holes. Winner  is the first player to get to the eleventh hole.",
					new int[][][] {{
							{33, 174}, {95, 269}, {213, 290}, {331, 290}, {449, 288}, {567, 262}, {627, 168}, {564, 76},
							{445, 52}, {328, 52}, {210, 54}, {93, 81}
			}});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class HorseRace extends Game {
		HorseRace() {
			super("Horse Race", "Players: 2. Roll the dice to determine player to start. High roll goes " +
					"first. Beginning at the start hole, a player must role a 2 either on one of the two dice or a " +
					"combination of the two to advance. For hole 3, the player must roll a 3 on one of the dice or " +
					"combination of the two dice. Player continues until he cannot roll to move to the next hole. " +
					"Player 2 then goes. If a player lands on opponent's peg, opponent goes back to start.",
					new int[][][] {{
							{50, 174}, {97, 262}, {228, 281}, {343, 286}, {448, 285}, {574, 257}, {617, 168}, {582, 78},
							{452, 53}, {337, 52}, {228, 53}, {97, 77}
			}});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class Challenger extends Game {
		Challenger() {
			super("Challenger", "Players: 1-2. To start place a peg in each hole. A player rolls the dice " +
					"and removes a peg of total number rolled or any combination of number rolled. Example: a roll " +
					"of 8 can remove the following combinations: 1 + 7, 2 + 6, 3 + 5, 1 + 2 + 5, 1 + 3 + 4, or 8. " +
					"The object is to remove all the pegs. When you can't remove the number rolled your turn is over " +
					"if playing against others. The player with the least amount of pegs remaining is the winner.",
					new int[][][] {{
							{34, 163}, {103, 161}, {167, 161}, {234, 161}, {299, 160}, {367, 161}, {431, 160},
							{499, 161}, {565, 160}, {631, 162}
			}});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class Magic extends Game {
		Magic() {
			super("Magic", "Players: 1. 4 blue pegs in left most holes and 4 red pegs in right most " +
					"holes leaving center 2 holes empty. The object is to move blue pegs to the right and red pegs " +
					"to the left. You can only move pegs by jumping another peg or moving 1 space to an empty hole.",
					new int[][][] {{
							{45, 62}, {109, 59}, {178, 59}, {244, 58}, {311, 57}, {379, 57}, {445, 57}, {511, 56},
							{577, 54}, {642, 55}
			}});
		}
		
		public void play() {
			setup();
		}
	}
	
	public static class No1Race extends Game {
		No1Race() {
			super("No1Race", "Who is No. 1", "Players: 2. Begin by rolling both dice. Each player starts on position " +
					"1. To move to position 2 the player must roll a 2 on one dice or a combination equalling 2. To " +
					"move on, continue in the same way -- For example: to move to position 3, roll a 3 or a " +
					"combination equalling three. First to reach position 10 wins.",
					new int[][][] {
							{{41, 53}, {104, 53}, {165, 52}, {228, 53}, {289, 53}, {352, 53}, {412, 55}, {475, 55},
									{537, 55}, {600, 54}},
							{{40, 193}, {103, 193}, {164, 195}, {227, 195}, {288, 194}, {352, 193}, {411, 197},
									{475, 195}, {536, 195}, {598, 195}}
			});
		}
		
		public void play() {
			setup();
		}
	}
}
