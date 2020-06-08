package com.supercatgaming.peggames;

import javax.swing.*;

import static com.supercatgaming.peggames.Handler.getResources;

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
			BoardLabel l = new BoardLabel(new ImageIcon(getResources(getImgLoc())), holesPos);
			l.setScale(.2f);
			l.setBounds(0, 0, l.getIcon().getIconWidth(), l.getIcon().getIconHeight());
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
							{{227, 689}, {819, 692}, {1419, 694}},
							{{216, 1294}, {812, 1301}, {1413, 1307}},
							{{216, 1914}, {816, 1918}, {1406, 1923}}
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
							{{1262, 298}},
							{{1013, 733}, {1513, 729}},
							{{772, 1159}, {1262, 1150}, {1764, 1152}},
							{{503, 1580}, {1021, 1587}, {1515, 1582}, {2001, 1574}},
							{{268, 2034}, {777, 2037}, {1271, 2032}, {1780, 2021}, {2272, 2010}}
					});
		}
		
		public void play() {
		
		}
	}
	public static class Conqueror2 extends Conqueror {
		Conqueror2() {
			super();
			IMG_LOC = "Conqueror2";
			holesPos = new int[][][] {
					{{1344, 363}},
					{{1090, 825}, {1611, 825}},
					{{820, 1281}, {1350, 1281}, {1870, 1281}},
					{{559, 1743}, {1089, 1743}, {1609, 1743}, {2141, 1743}},
					{{287, 2181}, {817, 2181}, {1357, 2181}, {1887, 2181}, {2417, 2181}}
			};
		}
		
		public void play() {
		
		}
	}
	
	public static class Universe extends Game {
		Universe() {
			super("Universe", "Players: 1. Place a peg in any of the eight star holes then follow any line that " +
					"goes from the starting star hole and put a peg into that hole. Continue the same procedure from " +
					"that star hole until you cannot make a move. Fill 7 or 8 star holes to win.",
					new int[][][] {{
							{730, 797}, {1701, 1095}, {749, 2097}, {1768, 1700}, {378, 1184}, {1314, 2086}, {1306, 793},
							{319, 1668}
			}});
		}
		
		public void play() {
		
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
							{167, 713}, {474, 1188}, {1063, 1297}, {1656, 1294}, {2244, 1286}, {2837, 1155},
							{3137, 683}, {2819, 223}, {2226, 105}, {1639, 105}, {1049, 117}, {467, 248}
			}});
		}
		
		public void play() {
		
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
							{252, 697}, {484, 1133}, {1142, 1230}, {1713, 1253}, {2239, 1251}, {2872, 1109}, {3085, 665},
							{2911, 215}, {2258, 90}, {1683, 85}, {1139, 92}, {486, 209}
			}});
		}
		
		public void play() {
		
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
							{171, 814}, {513, 806}, {834, 805}, {1171, 806}, {1495, 801}, {1833, 806}, {2157, 802},
							{2494, 804}, {2823, 799}, {3156, 811}
			}});
		}
		
		public void play() {
		
		}
	}
	
	public static class Magic extends Game {
		Magic() {
			super("Magic", "Players: 1. 4 blue pegs in left most holes and 4 red pegs in right most " +
					"holes leaving center 2 holes empty. The object is to move blue pegs to the right and red pegs " +
					"to the left. You can only move pegs by jumping another peg or moving 1 space to an empty hole.",
					new int[][][] {{
							{223, 310}, {547, 297}, {891, 294}, {1218, 289}, {1557, 285}, {1893, 284}, {2224, 286},
							{2556, 282}, {2885, 269}, {3210, 273}
			}});
		}
		
		public void play() {
		
		}
	}
	
	public static class No1Race extends Game {
		No1Race() {
			super("No1Race", "Who is No. 1", "Players: 2. Begin by rolling both dice. Each player starts on position " +
					"1. To move to position 2 the player must roll a 2 on one dice or a combination equalling 2. To " +
					"move on, continue in the same way -- For example: to move to position 3, roll a 3 or a " +
					"combination equalling three. First to reach position 10 wins.",
					new int[][][] {
							{{207, 153}, {520, 154}, {824, 150}, {1141, 153}, {1444, 154}, {1760, 156}, {2061, 166},
									{2375, 165}, {2684, 163}, {2999, 161}},
							{{201, 854}, {517, 855}, {818, 863}, {1134, 865}, {1441, 860}, {1760, 856}, {2057, 896},
									{2374, 864}, {2682, 865}, {2991, 864}}
			});
		}
		
		public void play() {
		
		}
	}
}
