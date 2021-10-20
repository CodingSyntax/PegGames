package com.supercatgaming.peggames;

import com.supercatgaming.peggames.Components.CButton;
import com.supercatgaming.peggames.Components.Label;
import com.supercatgaming.peggames.Components.QButton;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.supercatgaming.peggames.Handler.*;

public final class Games {
	private Games() {/*Don't allow instantiating*/}
	private static int game = 0;
	
	private static int p1Color = -1;
	private static int p2Color = -1;
	private static int pTurn = 1; //player turn
	private static final Random die = new Random();
	private static boolean is2P = false;
	
	private static final Game[] games = new Game[] {new TicTacToe(), new Conqueror(), new Conqueror2(), new Universe(),
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
		//TODO: seems not so random
		return die.nextInt(6) + 1;
	}
	
	private static void nextPlayerTurn() {
		if (!Game.won) {
			if (pTurn == 1) {
				pTurn = 2;
				Game.updateTitle(" - P2's turn");
			} else if (pTurn == 2) {
				pTurn = 1;
				Game.updateTitle(" - P1's turn");
			}
		}
	}
	
	public static Game get() {
		return games[game];
	}
	
	public static void dropPeg(Point po, Peg p) {
		int margin = 15;
		//Dropped out of bounds, put it back in bounds
		if (po.y >= GUI.getLayerHeight() - margin || po.x >= GUI.getLayerWidth() - margin || po.y <= margin || po.x <= margin)
			p.locateAt(GUI.getLayerWidth() - GUI.getLayerWidth() / 8, GUI.getLayerHeight() / 2);
		else if (po.x >= GUI.getLayerWidth() - Game.colorDist && Game.deleteOnPalette) {
			Peg.delete(p);
		} else {
			PegHole h = Game.board.dropPeg(po.x, po.y, p);
			if (h != null && !Options.freePlay) {
				get().providePeg(p);
				get().check(h.getIndex());
			} else if ((Games.get() instanceof Conqueror && ((Games.Conqueror)Games.get()).turn == 0)) {
				Peg.delete(p);
				Game.updateTitle("");
				PegHole.loosePeg = false;
				((Games.Conqueror) Games.get()).turn++;
			}
		}
	}
	
	public static abstract class Game {
		protected Game(String name, String inst) {
			this(name, name, inst);
		}
		protected Game(String loc, String name, String inst) {
			IMG_LOC = loc;
			NAME = name;
			INST = inst;
		}
		
		public static int colorDist = 18;
		static boolean deleteOnPalette = Options.freePlay;
		
		String NAME;
		String IMG_LOC;
		String INST;
		static int[][][] holesPos;
		static BoardLabel board;
		static Label title;
		static ComponentAdapter cA;
		static boolean won;
		static Peg provided;
		
		static void updateTitle(String add) {
			title.setText(get().NAME + add);
			cA.componentResized(null);
		}
		
		public abstract boolean isDiceOnly(); //This game has nothing to do with moving pegs or stats, just dice
		public abstract void check(int[] pos);
		public abstract void play();
		public abstract void rolled(int dice, int dice2);
		public abstract void begin();
		public abstract boolean allowPegMoving();
		public boolean isChallenger() {
			return get() instanceof Challenger;
		}
		
		public void providePeg(Peg p) {
			provided = p;
		}
		
		void setup() {
			setup(false);
		}
		void setup(boolean selectColors) {
			//reset
			p1Color = -1;
			p2Color = -1;
			pTurn = 1;
			won = false;
			
			title = new Label(NAME + (Options.freePlay ? " - Freeplay" : selectColors ? " - Select P1 Color"
					: ""));
			QButton quit = new QButton("Quit", QButton.Type.Thin);
			QButton roll = new QButton("Roll", QButton.Type.Thin);
			board = new BoardLabel(new ImageIcon(getResources(getImgLoc())), holesPos);
			is2P = selectColors;
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
					title.setFont(GUI.subFont);
					int lw = GUI.getLayerWidth();
					int lh = GUI.getLayerHeight();
					quit.setScale(GUI.getScale());
					roll.setScale(GUI.getScale());
					
					int w = quit.getWidth();
					int h = quit.getHeight();
					int s = GUI.getScale() * 5;
					if (isDiceOnly()) {
						quit.setLocation((lw / 2) - (w + s), lh - (h + s));
						roll.setLocation((lw / 2) + s, lh - (h + s));
					} else {
						quit.setLocation((lw - w) / 2, lh - (h + s));
					}
					int sc = 5 + 5 * GUI.getScale();
					for(Component b : buttons.getComponents()) {
						if (b instanceof CButton) {
							CButton c = (CButton)b;
							c.setBorder(BorderFactory.createEmptyBorder(sc, sc, sc, sc));
						}
						else System.out.println("Not a button");
					}
					colorDist = 18 + sc * 2;
					scrollPane.setSize(colorDist, lh);
					scrollPane.setLocation(lw - (colorDist), 0);
					
					int iw = board.getBase().getIconWidth();
					int ih = board.getBase().getIconHeight();
					//calculates how many of the base image fits into lw..., lh...
					float min = Math.min((lw - 20f) / iw,
							(lh - ((quit.getHeight() + 5f) * 2) - 5) / ih);
					board.setScale(Math.min(min, 1.5f));
					board.setLocation((lw - board.getIcon().getIconWidth()) / 2, (lh - board.getIcon().getIconHeight()) / 2);
					//not efficient, but resize is one time, so it isn't important
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
								updateTitle(" - Select P2 Color");
							} else if (lo != p1Color) {
								p2Color = lo;
								GUI.removeFromLayer(scrollPane);
								deleteOnPalette = false;
								updateTitle(" - P1's turn");
								GUI.repaintLayer();
								PegHole.loosePeg = false;
								begin();
							} else {
								updateTitle(" - Select a different color for P2");
							}
						} else
							PegHole.setDefaultColor(lo);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						if (Options.freePlay)
							if (drag && !pegSpawned) {
								if (!Peg.isLoose(lo) || curr == null) {
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
						if (Options.freePlay)
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
					if (isDiceOnly() && (p2Color != -1 || Options.freePlay || !is2P) && !won)
						if (Options.useRealDice) {
							if (!isChallenger()) {
								if (roll.getText().equals("OK")) {
									roll.setText("Roll");
									GUI.removeFromLayer(die1, die2);
									rolled(die1.getDieNum(), die2.getDieNum());
									cA.componentResized(null);
								} else {
									GUI.addToLayer(die1, die2);
									updateTitle(" - P" + pTurn + ", what did you roll?");
									roll.setText("OK");
								}
							} else {
								switch (roll.getText()) {
									case "Select":
										if (((Challenger)get()).checkPegs())
											roll.setText("Roll");
										break;
									case "OK":
										roll.setText("Select");
										GUI.removeFromLayer(die1, die2);
										rolled(die1.getDieNum(), die2.getDieNum());
										cA.componentResized(null);
										break;
									case "Roll":
										GUI.addToLayer(die1, die2);
										updateTitle(" - P" + pTurn + ", what did you roll?");
										roll.setText("OK");
										break;
								}
							}
						} else {
							switch (roll.getText()) {
								case "Stop":
									roll.setText("OK");
									updateTitle(" - P" + pTurn + " rolled: "
											+ die1.getDieNum() + " + " + die2.getDieNum());
									break;
								case "Roll":
									GUI.addToLayer(die1, die2);
									updateTitle(" - P" + pTurn + ", rolling");
									roll.setText("Stop");
									Thread randomize = new Thread(() -> {
										while (!roll.getText().equals("OK")) {
											die1.setDieNum(rollDie());
											die2.setDieNum(rollDie());
											try {
												TimeUnit.MILLISECONDS.sleep(20);
											} catch (InterruptedException exception) {
												exception.printStackTrace();
											}
										}
									});
									randomize.start();
									break;
								case "OK":
									if (isChallenger())
										roll.setText("Select");
									else
										roll.setText("Roll");
									rolled(die1.getDieNum(), die2.getDieNum());
									GUI.removeFromLayer(die1, die2);
									cA.componentResized(null);
									break;
								case "Select":
									if (((Challenger)get()).checkPegs())
										roll.setText("Roll");
									break;
							}
						}
				}
			});
			
			GUI.addToLayer(title, board, scrollPane, quit, isDiceOnly() ? roll : null);
			GUI.addCL(cA);
			cA.componentResized(null);
			if (!selectColors) {
				PegHole.loosePeg = false;
				begin();
			}
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
			super("TicTacToe","Players: 2. Place \"X's\" and \"O's\" trying to get three in a row.");
		}
		
		Peg free;
		boolean complete = false;
		String winner = "null";
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {
			if (board.getPegAt(pos) == null && !complete) {
				board.getHoleAt(pos).setPeg(free);
				nextPlayerTurn();
				int cW = checkWin();
				if (cW != -1) {
					complete = true;
					if (cW != 0) {
						Sounds.playWin();
					}
				}
				summonPeg();
				GUI.repaintLayer();
			}
		}
		
		/**
		 * Checks for win
		 * @return -1 if game is undecided, 0 if tied, 1 if player 1, 2 if player 2.
		 */
		private int checkWin() {
			//Check tie
			int win = 0;
			winner = "Tie";
			for (PegHole h : board.holes) {
				if (h.getPeg() == null) {
					win = -1;
					winner = "Undecided. This should not be visible!";
					break;
				}
			}
			
			int dia1Prev = -1, dia2Prev = -1;
			boolean dia1Same = true, dia2Same = true;
			
			for (int x = 0; x < 3; x++) {
				int yPrev = -1, xPrev = -1;
				boolean ySame = true, xSame = true;
				for (int y = 0; y < 3; y++) {
					if (yPrev == -1)
						yPrev = Peg.getColor(board.getPegAt(new int[] {x, y}));
					if (xPrev == -1)
						xPrev = Peg.getColor(board.getPegAt(new int[] {y, x}));
					//Don't check if it's already determined that no win can happen
					if (ySame || xSame) {
						int cur = Peg.getColor(board.getPegAt(new int[] {x, y}));
						if (cur != yPrev || yPrev == -1)
							ySame = false;
						cur = Peg.getColor(board.getPegAt(new int[] {y, x}));
						if (cur != xPrev || xPrev == -1)
							xSame = false;
					}
				}
				if (ySame) {
					winner = "P" + checkPlayer(yPrev);
					return checkPlayer(yPrev);
				}
				if (xSame) {
					winner = "P" + checkPlayer(xPrev);
					return checkPlayer(xPrev);
				}
				//diagonals
				if (dia1Prev == -1)
					dia1Prev = Peg.getColor(board.getPegAt(new int[] {x, x}));
				if (dia2Prev == -1)
					dia2Prev = Peg.getColor(board.getPegAt(new int[] {Math.abs(x - 2), x}));
				//Don't check if it's already determined that no win can happen
				if (dia1Same || dia2Same) {
					int cur = Peg.getColor(board.getPegAt(new int[] {x, x}));
					if (cur != dia1Prev || dia1Prev == -1)
						dia1Same = false;
					cur = Peg.getColor(board.getPegAt(new int[] {Math.abs(x - 2), x}));
					if (cur != dia2Prev || dia2Prev == -1)
						dia2Same = false;
				}
			}
			if (dia1Same) {
				winner = "P" + checkPlayer(dia1Prev);
				return checkPlayer(dia1Prev);
			}
			if (dia2Same) {
				winner = "P" + checkPlayer(dia2Prev);
				return checkPlayer(dia2Prev);
			}
			return win;
		}
		
		private int checkPlayer(int color) {
			if (color == p1Color)
				return 1;
			if (color == p2Color)
				return 2;
			return -1;
		}
		
		public void play() {
			holesPos = new int[][][] {
					{{45, 138}, {164, 138}, {284, 139}},
					{{43, 259}, {162, 260}, {283, 261}},
					{{43, 383}, {163, 384}, {281, 385}}
			};
			setup(!Options.freePlay);
			complete = true;
		}
		
		public void rolled(int dice, int dice2) {}
		
		public void begin() {
			complete = false;
			summonPeg();
		}
		
		private void summonPeg() {
			if (!Options.freePlay && !complete) {
				if (pTurn == 1)
					free = new Peg(p1Color);
				else
					free = new Peg(p2Color);
				free.update();
				free.locateAt(GUI.getLayerWidth() - GUI.getLayerWidth() / 8, GUI.getLayerHeight() / 2);
				GUI.addToLayer(free);
			} else if (complete) {
				updateTitle(" - Winner: " + winner);
			}
		}
	}
	
	public static class Conqueror extends Game {
		Conqueror() {
			super("Conqueror", "Players: 1. Fill all the holes with pegs except for one. " +
					"Jump a peg and remove it from the game. Continue until you can't make any more jumps.");
		}
		int turn = 0;
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean allowPegMoving() {
			return true;
		}
		
		public void check(int[] pos) {
			if (provided != null && (PegHole.loosePeg == (provided.getHole().getPeg() == null)))
				if (board.getPegAt(pos) == null) {
					int[] index = provided.getHole().getIndex();
					int x = pos[0], y = pos[1], x2 = index[0], y2 = index[1];
					//noinspection ConditionCoveredByFurtherCondition as it is reported incorrectly
					if (((x + 2 == x2 || x - 2 == x2) && y == y2) || ((y + 2 == y2 || y - 2 == y2) && x == x2) ||
							(x + 2 == x2 && y + 2 == y2) || (x - 2 == x2 && y - 2 == y2)) {
						Peg p = board.getHoleAt(new int[] {(x + x2) / 2, (y + y2) / 2}).removePeg(true);
						if (p != null) {
							board.getHoleAt(pos).setPeg(provided);
							board.getHoleAt(index).removePeg();
						} else {
							provided.getHole().setPeg(provided);
						}
						PegHole.loosePeg = false;
					} else if (provided.getHole().getPeg() == null) {
						provided.getHole().setPeg(provided);
						PegHole.loosePeg = false;
					}
				} else if (provided.getHole().getPeg() == null) {
					provided.getHole().setPeg(provided);
					PegHole.loosePeg = false;
				}
			GUI.repaintLayer();
		}
		
		public void play() {
			holesPos = new int[][][] {
					{{252, 60}},
					{{203, 147}, {303, 146}},
					{{154, 232}, {252, 230}, {353, 230}},
					{{101, 316}, {204, 317}, {303, 316}, {400, 315}},
					{{54, 407}, {155, 407}, {254, 406}, {356, 404}, {454, 402}}
			};
			if (this instanceof Conqueror2)
				holesPos = new int[][][] {
						{{269, 73}},
						{{218, 165}, {322, 165}},
						{{164, 256}, {270, 256}, {374, 256}},
						{{112, 349}, {218, 349}, {322, 349}, {428, 349}},
						{{57, 436}, {163, 436}, {271, 436}, {377, 436}, {483, 436}}
				};
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
		
		public void begin() {
			board.getHoleAt(new int[] {0,0}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {1,0}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {1,1}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {2,0}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {2,1}).setPeg(new Peg(4)); //White
			board.getHoleAt(new int[] {2,2}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {3,0}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {3,1}).setPeg(new Peg(1)); //Yellow
			board.getHoleAt(new int[] {3,2}).setPeg(new Peg(1)); //Yellow
			board.getHoleAt(new int[] {3,3}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {4,0}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {4,1}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {4,2}).setPeg(new Peg(4)); //White
			board.getHoleAt(new int[] {4,3}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {4,4}).setPeg(new Peg(2)); //Red
			turn = 0;
			updateTitle(" - Remove a peg");
		}
	}
	public static class Conqueror2 extends Conqueror {
		Conqueror2() {
			super();
			IMG_LOC = "Conqueror2";
			NAME = "Conqueror (Re-skin)";
		}
	}
	
	public static class Universe extends Game {
		Universe() {
			super("Universe", "Players: 1. Place a peg in any of the eight star holes then follow any line that " +
					"goes from the starting star hole and put a peg into that hole. Continue the same procedure from " +
					"that star hole until you cannot make a move. Fill 7 or 8 star holes to win.");
		}
		
		int turn = 0;
		Peg last;
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {
			if (board.getPegAt(pos) == null && (last != null && checkPos(pos[1])) || turn == 0) {
				last = board.getHoleAt(pos).setPeg();
				turn++;
			}
		}
		
		private boolean checkPos(int x) {
			if (x == 0) {
				x = 1;
			}
			int x2 = last.getHole().getIndex()[1];
			if (x2 == 0) {
				x2 = 1;
			}
			return (x2 + 1 == x || x2 - 1 == x) || x2 + 1 == 8 && x == 1 || x2 - 1 == 0 && x == 7;
		}
		
		public void play() {
			holesPos = new int[][][] {{
					{146, 159}, {340, 219}, {150, 419}, {354, 340}, {76, 237}, {263, 417}, {261, 159}, {64, 334}
			}};
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
		
		public void begin() {
			turn = 0;
			last = null;
		}
	}
	
	public static class Racing extends Game {
		Racing() {
			super("Racing", "Players: 2. Players alternate rolls of the dice. To start, a player must " +
					"roll a double or the number 1 on one die. Then that player moves to the fist hole. After the " +
					"first hole the player must match the number in the next hole with either the total of both dice " +
					"or a number shown on one die. If a player lands on the opponent's peg, they must go back two " +
					"holes. Winner  is the first player to get to the eleventh hole.");
		}
		
		int p1 = 0;
		int p2 = 0;
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {
		
		}
		
		public void play() {
			holesPos = new int[][][] {{
					{33, 174}, {95, 269}, {213, 290}, {331, 290}, {449, 288}, {567, 262}, {627, 168}, {564, 76},
					{445, 52}, {328, 52}, {210, 54}, {93, 81}
			}};
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
					if (p1 == 11) {
						updateTitle(" - P1 wins!");
						Sounds.playWin();
						won = true;
					} else if (p1 == 12) {
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
					if (p2 == 11) {
						updateTitle(" - P2 wins!");
						Sounds.playWin();
						won = true;
					} else if (p2 == 12) {
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
		
		public void begin() {
			p1 = 0;
			p2 = 0;
			board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p1Color));
		}
	}
	
	public static class HorseRace extends Game {
		HorseRace() {
			super("Horse Race", "Players: 2. Roll the dice to determine player to start. High roll goes " +
					"first. Beginning at the start hole, a player must role a 2 either on one of the two dice or a " +
					"combination of the two to advance. For hole 3, the player must roll a 3 on one of the dice or " +
					"combination of the two dice. Player continues until he cannot roll to move to the next hole. " +
					"Player 2 then goes. If a player lands on opponent's peg, opponent goes back to start.");
		}
		
		int p1 = 0;
		int p2 = 0;
		boolean dPS = true; //determine player start
		boolean p1Start = true; //if p1 starts.
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {
		
		}
		
		public void play() {
			holesPos = new int[][][] {{
					{50, 174}, {97, 262}, {228, 281}, {343, 286}, {448, 285}, {574, 257}, {617, 168}, {582, 78},
					{452, 53}, {337, 52}, {228, 53}, {97, 77}
			}};
			setup(!Options.freePlay);
		}
		
		public void rolled(int dice, int dice2) {
			if (dPS) {
				if (pTurn == 1) {
					p1 = dice + dice2;
					nextPlayerTurn();
				} else {
					p2 = dice + dice2;
					if (p1 > p2) {
						board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p1Color));
						dPS = false;
						p1 = 0;
						p2 = 0;
						nextPlayerTurn();
					} else if (p2 > p1) {
						board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p2Color));
						dPS = false;
						p1Start = false;
						p1 = 0;
						p2 = 0;
					} else {
						nextPlayerTurn();
					}
				}
			}
			else if (pTurn == 1) {
				if (dice == p1 + 2 || dice2 == p1 + 2 || dice + dice2 == p1 + 2) {
					Peg p;
					if (p1 == 0 && p2 == 0) {
						if (p1Start) {
							p = board.getHoleAt(new int[] {0, p1}).removePeg();
							board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p2Color));
						} else
							p = new Peg(p1Color);
					} else
						p = board.getHoleAt(new int[] {0, p1}).removePeg();
					p1++;
					if (p1 == 11) {
						won = true;
						Sounds.playWin();
						updateTitle(" - P1 wins!");
					} else if (p1 == 12) {
						p1 = 0;
					}
					PegHole h = board.getHoleAt(new int[] {0, p1});
					if (h.getPeg() != null) {
						p2 = 0;
						board.getHoleAt(new int[] {0, p2}).setPeg(h.removePeg());
					}
					h.setPeg(p);
				}
				else
					nextPlayerTurn();
			} else {
				if (dice == p2 + 2 || dice2 == p2 + 2 || dice + dice2 == p2 + 2) {
					Peg p;
					if (p2 == 0 && p1 == 0) {
						if (p1Start)
							p = new Peg(p2Color);
						else {
							p = board.getHoleAt(new int[] {0, p1}).removePeg();
							board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p1Color));
						}
					} else
						p = board.getHoleAt(new int[] {0, p2}).removePeg();
					p2++;
					if (p2 == 11) {
						won = true;
						Sounds.playWin();
						updateTitle(" - P2 wins!");
					} else if (p2 == 12) {
						p2 = 0;
					}
					PegHole h = board.getHoleAt(new int[] {0, p2});
					if (h.getPeg() != null) {
						p1 = 0;
						board.getHoleAt(new int[] {0, p1}).setPeg(h.removePeg());
					}
					h.setPeg(p);
				}
				else
					nextPlayerTurn();
			}
		}
		
		public void begin() {
			dPS = true;
			p1 = 0;
			p2 = 0;
		}
	}
	
	public static class Challenger extends Game {
		Challenger() {
			super("Challenger", "Players: 1-2. To start place a peg in each hole. A player rolls the dice " +
					"and removes a peg of total number rolled or any combination of number rolled. Example: a roll " +
					"of 8 can remove the following combinations: 1 + 7, 2 + 6, 3 + 5, 1 + 2 + 5, 1 + 3 + 4, or 8. " +
					"The object is to remove all the pegs. When you can't remove the number rolled your turn is over " +
					"if playing against others. The player with the least amount of pegs remaining is the winner.");
		}
		
		int num = -1;
		
		public boolean checkPegs() {
			PegHole[] hs = PegHole.getSelected();
			int total = 0;
			for (PegHole h : hs) {
				total += h.getIndex()[1] + 1;
			}
			if (total == num) {
				num = -1;
				for (PegHole h : hs) {
					Peg.delete(h.removePeg());
				}
				updateTitle(" - Removed");
				return true;
			} else {
				updateTitle(" - The selected pegs don't add up to " + num +
						". Game over? Click quit to restart.");
			}
			return false;
		}
		
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {} //Not used, pegs will not be dropped in this game!
		
		public void play() {
			holesPos = new int[][][] {{
					{34, 163}, {103, 161}, {167, 161}, {234, 161}, {299, 160}, {367, 161}, {431, 160},
					{499, 161}, {565, 160}, {631, 162}
			}};
			setup();
		}
		
		public void rolled(int dice, int dice2) {
			num = dice + dice2;
		}
		
		public void begin() {
			board.getHoleAt(new int[] {0,0}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,1}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,2}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,3}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,4}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,5}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,6}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,7}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,8}).setPeg(new Peg(0));
			board.getHoleAt(new int[] {0,9}).setPeg(new Peg(0));
		}
	}
	
	public static class Magic extends Game {
		Magic() {
			super("Magic", "Players: 1. 4 blue pegs in left most holes and 4 red pegs in right most " +
					"holes leaving center 2 holes empty. The object is to move blue pegs to the right and red pegs " +
					"to the left. You can only move pegs by jumping another peg or moving 1 space to an empty hole.");
		}
		
		public boolean isDiceOnly() {
			return false;
		}
		
		public boolean allowPegMoving() {
			return true;
		}
		
		public void check(int[] pos) {
			if (board.getPegAt(pos) == null && provided != null) {
				int x = pos[1];
				int x2 = provided.getHole().getIndex()[1];
				
				if (x - 1 == x2 || x + 1 == x2 || (x - 2 == x2 && board.getPegAt(new int[] {0, x - 1}) != null) ||
						(x + 2 == x2 && board.getPegAt(new int[] {0, x + 1}) != null)) {
					provided.getHole().removePeg();
					board.getHoleAt(pos).setPeg(provided);
					PegHole.loosePeg = false;
				} else if (provided.getHole().getPeg() == null) {
					provided.getHole().setPeg(provided);
					PegHole.loosePeg = false;
				}
			} else if (provided != null && provided.getHole().getPeg() == null) {
				provided.getHole().setPeg(provided);
				PegHole.loosePeg = false;
			}
			GUI.repaintLayer();
		}
		
		public void play() {
			holesPos = new int[][][] {{
					{45, 62}, {109, 59}, {178, 59}, {244, 58}, {311, 57}, {379, 57}, {445, 57}, {511, 56},
					{577, 54}, {642, 55}
			}};
			setup();
		}
		
		public void rolled(int dice, int dice2) {}
		
		public void begin() {
			board.getHoleAt(new int[] {0,0}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {0,1}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {0,2}).setPeg(new Peg(0)); //Blue
			board.getHoleAt(new int[] {0,3}).setPeg(new Peg(0)); //Blue
			
			board.getHoleAt(new int[] {0,6}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {0,7}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {0,8}).setPeg(new Peg(2)); //Red
			board.getHoleAt(new int[] {0,9}).setPeg(new Peg(2)); //Red
		}
	}
	
	public static class No1Race extends Game {
		No1Race() {
			super("No1Race", "Who is No. 1", "Players: 2. Begin by rolling both dice. Each player starts on position " +
					"1. To move to position 2 the player must roll a 2 on one dice or a combination equalling 2. To " +
					"move on, continue in the same way -- For example: to move to position 3, roll a 3 or a " +
					"combination equalling three. First to reach position 10 wins.");
		}
		
		int p1 = 0;
		int p2 = 0;
		
		public boolean isDiceOnly() {
			return true;
		}
		
		public boolean allowPegMoving() {
			return false;
		}
		
		public void check(int[] pos) {
		
		}
		
		public void play() {
			holesPos = new int[][][] {
					{{41, 53}, {104, 53}, {165, 52}, {228, 53}, {289, 53}, {352, 53}, {412, 55}, {475, 55},
							{537, 55}, {600, 54}},
					{{40, 193}, {103, 193}, {164, 195}, {227, 195}, {288, 194}, {352, 193}, {411, 197},
							{475, 195}, {536, 195}, {598, 195}}
			};
			setup(!Options.freePlay);
		}
		
		public void rolled(int dice, int dice2) {
			if (pTurn == 1) {
				if (dice == p1 + 2 || dice2 == p1 + 2 || dice + dice2 == p1 + 2) {
					Peg p = board.getHoleAt(new int[] {0, p1}).removePeg();
					p1++;
					if (p1 == 9) {
						won = true;
						Sounds.playWin();
						updateTitle(" - P1 wins!");
					} else if (p1 == 10) {
						p1 = 0;
					}
					board.getHoleAt(new int[] {0, p1}).setPeg(p);
				}
			} else {
				if (dice == p2 + 2 || dice2 == p2 + 2 || dice + dice2 == p2 + 2) {
					Peg p = board.getHoleAt(new int[] {1, p2}).removePeg();
					p2++;
					if (p2 == 9) {
						won = true;
						Sounds.playWin();
						updateTitle(" - P2 wins!");
					} else if (p2 == 10) {
						p2 = 0;
					}
					board.getHoleAt(new int[] {1, p2}).setPeg(p);
				}
			}
			
			nextPlayerTurn();
		}
		
		public void begin() {
			p1 = 0;
			p2 = 0;
			board.getHoleAt(new int[] {0, 0}).setPeg(new Peg(p1Color));
			board.getHoleAt(new int[] {1, 0}).setPeg(new Peg(p2Color));
		}
	}
}
