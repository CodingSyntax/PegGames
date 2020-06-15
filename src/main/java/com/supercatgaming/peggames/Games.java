package com.supercatgaming.peggames;

import com.supercatgaming.peggames.Components.CButton;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import static com.supercatgaming.peggames.Handler.*;

public final class Games {
	private Games() {/*Don't allow instantiating*/}
	private static int game = 0;
	
	private static int p1Color = -1;
	private static int p2Color = -1;
	private static int pTurn = 1; //player turn
	private static Random die = new Random();
	private static boolean is2P = false;
	
	public static final Game[] games = new Game[] {new TicTacToe(), new Conqueror(), new Conqueror2(), new Universe(),
			new Racing(), new HorseRace(), new Challenger(), new Magic(), new No1Race()};
	
	public static void prev() {
		if (--game < 0)
			game = games.length - 1;
	}
	
	public static void next() {
		if (++game >= games.length)
			game = 0;
	}
	
	public static boolean is2P() {
		return is2P;
	}
	
	public static int rollDie() {
		return die.nextInt(6) + 1;
	}
	
	private static void nextPlayerTurn() {
		if (pTurn == 1) {
			pTurn = 2;
			get().updateTitle(" - P2's turn");
		} else if (pTurn == 2) {
			pTurn = 1;
			get().updateTitle(" - P1's turn");
		}
	}
	
	public static Game get() {
		return games[game];
	}
	
	public static void dropPeg(Point po, Peg p) {
		if (po.x >= GUI.getLayerWidth() - Game.COLOR_DIST) {
			Peg.delete(p);
		} else
			get().board.dropPeg(po.x, po.y, p);
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
		
		public static final int COLOR_DIST = 40;
		
		String NAME;
		String IMG_LOC = "";
		String INST = "";
		int[][][] holesPos;
		BoardLabel board;
		JLabel title;
		ComponentAdapter cA;
		
		void updateTitle(String add) {
			title.setText(NAME + add);
			cA.componentResized(null);
		}
		
		public abstract boolean requiresFrom(); //If the game has the player move a peg from one place to another
		public abstract boolean isDiceOnly(); //This game has nothing to do with moving pegs or stats, just dice
		public abstract boolean check(int[] pos);
		public abstract void play();
		public abstract void rolled(int dice, int dice2);
		
		void setup() {
			setup(false);
		}
		void setup(boolean selectColors) {
			title = new JLabel(NAME + (Options.freePlay ? " - Freeplay" : selectColors ? " - Select P1 Color"
					: ""));
			CButton quit = new CButton("Quit");
			CButton roll = new CButton("Roll");
			board = new BoardLabel(new ImageIcon(getResources(getImgLoc())), holesPos);
			if (selectColors) is2P = true;
			//Colors
			JPanel buttons = new JPanel();
			JScrollPane scrollPane = new JScrollPane(buttons, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			BoxLayout boxlayout = new BoxLayout(buttons, BoxLayout.Y_AXIS);
			buttons.setLayout(boxlayout);
			int loc = 0;
			
			CButton die1 = new CButton(true);
			CButton die2 = new CButton(true);
			
			cA = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					int lw = GUI.getLayerWidth();
					int lh = GUI.getLayerHeight();
					
					int w = lw / 8;
					int h = Math.min(Math.max(20, lh / 20), 50);
					if (isDiceOnly()) {
						quit.setSize(w, h);
						quit.setLocation((lw / 2) - (w + 5), lh - (quit.getHeight() + 5));
						roll.setSize(w, h);
						roll.setLocation((lw / 2) + 5, lh - (quit.getHeight() + 5));
					} else {
						quit.setSize(w, h);
						quit.setLocation((lw - w) / 2, lh - (h + 5));
					}
					scrollPane.setSize(COLOR_DIST, lh);
					scrollPane.setLocation(lw - COLOR_DIST, 0);
					
					int iw = board.getBase().getIconWidth();
					int ih = board.getBase().getIconHeight();
					//calculates how many of the base image fits into lw..., lh...
					float min = Math.min((lw - 20f) / iw,
							(lh - ((quit.getHeight() + 5f) * 2) - 5) / ih);
					board.setScale(Math.min(min, 1.5f));
					board.setLocation((lw - board.getIcon().getIconWidth()) / 2, (lh - board.getIcon().getIconHeight()) / 2);
					//not efficient, but resize is one time so it isn't important
					if (board.getX() + board.getWidth() >= scrollPane.getX() - 5) {
						min = Math.min((lw - (20f + scrollPane.getWidth())) / iw,
								(lh - ((quit.getHeight() + 5f) * 2) - 5) / ih);
						board.setScale(Math.min(min, 1.5f));
						board.setLocation(((lw - scrollPane.getWidth()) - board.getIcon().getIconWidth()) / 2,
								(lh - board.getIcon().getIconHeight()) / 2);
					}
					title.setSize(Handler.getLengthHTML(title), Handler.getHeightHTML(title));
					title.setLocation((lw - title.getWidth()) / 2, board.getY() - (title.getHeight() + 10));
					
					int dieSize = Math.min(lw/8, lh/8);
					die1.setSize(dieSize, dieSize);
					die1.setLocation((lw / 2) - (dieSize + 5), (lh - dieSize) / 2);
					die1.resize();
					die2.setSize(dieSize, dieSize);
					die2.setLocation((lw / 2) + 5, (lh - dieSize) / 2);
					die2.resize();
				}
			};
			
			final int[] player = {1};
			for (Color c : Options.getColors()) {
				int lo = loc;
				CButton b = new CButton();
				b.setBackground(c);
				b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				
				MouseAdapter mA = new MouseAdapter() {
					boolean drag = false;
					boolean pegSpawned = false;
					Peg curr = null;
					@Override
					public void mouseClicked(MouseEvent e) {
						if (selectColors) {
							if (player[0] == 1) {
								p1Color = lo;
								player[0]++;
								board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p1Color));
								updateTitle(" - Select P2 Color");
							} else if (lo != p1Color) {
								p2Color = lo;
								GUI.removeFromLayer(scrollPane);
								updateTitle(" - P1's turn");
							} else {
								updateTitle(" - Select a different color for P2");
							}
						} else
							PegHole.setDefaultColor(lo);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						if (!selectColors)
							if (drag && !pegSpawned) {
								Peg.test();
								if (!Peg.isLoose(lo)) {
									curr = new Peg(lo);
									GUI.addToLayer(curr);
								}
								curr.locateAt();
								curr.followMouse(true);
								pegSpawned = true;
							}
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						if (!selectColors)
							if (e.getButton() == 1)
								drag = true;
					}
					
					@Override
					public void mouseReleased(MouseEvent e) {
						if (!selectColors) {
							drag = false;
							pegSpawned = false;
							if (curr != null)
								curr.followMouse(false);
						}
					}
					
					@Override
					public void mouseDragged(MouseEvent e) {
						if (curr != null)
							curr.locateAt();
					}
				};
				
				b.addMouseMotionListener(mA);
				b.addMouseListener(mA);
				loc++;
				buttons.add(b);
			}
			
			quit.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					//TODO: save progress
					GUI.mainMenu(false);
				}
			});
			roll.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (isDiceOnly() && p2Color != -1)
						if (Options.useRealDice) {
							if (roll.getText().equals("OK"))
							{
								roll.setText("Roll");
								GUI.removeFromLayer(die1, die2);
								rolled(die1.getDieNum(), die2.getDieNum());
								cA.componentResized(null);
							} else {
								GUI.addToLayer(die1, die2);
								updateTitle(" - P" + pTurn + ", what did you roll?");
								roll.setText("OK");
							}
						} else rolled(rollDie(), rollDie());
				}
			});
			
			GUI.addToLayer(title, board, scrollPane, quit, isDiceOnly() ? roll : null);
			GUI.addCL(cA);
			cA.componentResized(null);
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
		
		public boolean requiresFrom() {
			return false;
		}
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean check(int[] pos) {
			return board.getPegAt(pos) == null;
		}
		
		public void play() {
			setup(!Options.freePlay);
		}
		
		public void rolled(int dice, int dice2) {}
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
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
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
		
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
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
		
		int p1 = 0;
		int p2 = 0;
		
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup(!Options.freePlay);
			
		}
		
		public void rolled(int dice, int dice2) {
			if (pTurn == 1) {
				if ((p1 == 0 && dice == dice2) || dice == p1 + 1 || dice2 == p1 + 1 || dice + dice2 == p1 + 1) {
					Peg p = board.getHoleAt(new int[] {0, p1}).removePeg();
					if (p1 == 0 && p2 == 0) {
						board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p2Color));
					}
					p1++;
					if (p1 == 12) {
						p1 = 0;
					}
					PegHole h = board.getHoleAt(new int[] {0, p1});
					if (h.getPeg() != null) {
						p2 -= 2;
						p2 = Math.max(p2, 0);
						board.getHoleAt(new int[] {0, p2}).setPeg(h.removePeg());
					}
					h.setPeg(p);
				}
			} else {
				if ((p2 == 0 && dice == dice2) || dice == p2 + 1 || dice2 == p2 + 1 || dice + dice2 == p2 + 1) {
					Peg p;
					if (p2 == 0 && p1 == 0) {
						p = new Peg(p2Color);
					} else
						p = board.getHoleAt(new int[] {0, p2}).removePeg();
					p2++;
					if (p2 == 12) {
						p2 = 0;
					}
					PegHole h = board.getHoleAt(new int[] {0, p2});
					if (h.getPeg() != null) {
						p1 -= 2;
						p1 = Math.max(p1, 0);
						board.getHoleAt(new int[] {0, p1}).setPeg(h.removePeg());
					}
					h.setPeg(p);
				}
			}
			
			nextPlayerTurn();
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
		
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup(!Options.freePlay);
		}
		
		public void rolled(int dice, int dice2) {
		
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
		
		public boolean requiresFrom() {
			return false;
		}
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup();
		}
		
		public void rolled(int dice, int dice2) {
		
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
		
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
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
		
		public boolean requiresFrom() {
			return true;
		}
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean check(int[] pos) {
			return false;
		}
		
		public void play() {
			setup();
		}
		
		public void rolled(int dice, int dice2) {
		
		}
	}
}
