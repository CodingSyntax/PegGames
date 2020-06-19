package com.supercatgaming.peggames;

import com.supercatgaming.peggames.Components.CButton;
import com.supercatgaming.peggames.Components.CheckBox;
import com.supercatgaming.peggames.Components.Slider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import static com.supercatgaming.peggames.Handler.*;

public class GUI {
	private static final JFrame frame = new JFrame();
	private static final JLayeredPane layer = new JLayeredPane();
	
	private static final ArrayList<ComponentListener> cLs = new ArrayList<>();
	
	public static void mainMenu(boolean init) {
		if (init) {
			layer.setPreferredSize(new Dimension(854, 420));
			frame.setTitle(Handler.NAME);
			ArrayList<Image> iconImages = new ArrayList<>();
			int[] nums = {16, 32, 64, 128};
			for(int i = 0; i < 2; i++){
				iconImages.add(new ImageIcon(getResources("Icons/" + nums[i] + ".png")).getImage());
			}
			frame.setIconImages(iconImages);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		else {
			layer.setPreferredSize(layer.getSize());
			layer.removeAll();
			removeAllCLs();
		}
		
		CButton bPlay = new CButton("Play");
		CButton bOptions = new CButton("Options");
		CButton bTut = new CButton("Controls");
		CButton bQuit = new CButton("Quit");
		
		JLabel title = new JLabel("Peg Games");
		JLabel desc = new JLabel();
		
		ImageIcon pegboard = new ImageIcon(getResources("Icons/128.png"));
		JLabel iLabel = new JLabel(pegboard);
		
		
		bPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectGame();
			}
		});
		bOptions.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				options();
			}
		});
		bTut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tutorial();
			}
		});
		bQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				confirmQuit();
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lh = layer.getHeight();
				int lw = layer.getWidth();
				int m = lw / 9; //amnt of buttons *2 +1
				
				title.setFont(new Font("Serif", Font.PLAIN, Math.min(lw / 10, lh / 10)));
				desc.setFont(new Font("Serif", Font.PLAIN, Math.min(lw / 20, lh / 20)));
				Handler.setText(desc, "A selection of peg games to play alone or with some friends", lw - 10);
				title.setSize(Handler.getLengthHTML(title), Handler.getHeightHTML(title));
				title.setLocation((lw - title.getWidth()) / 2, 5);
				desc.setSize(Handler.getLengthHTML(desc), Handler.getHeightHTML(desc));
				desc.setLocation((lw - desc.getWidth()) / 2, getBottom(title) + 10);
				
				int min = Math.max(Math.min(lw / 128, (lh / 2) / 128), 1);
				iLabel.setIcon(Handler.scale(min, pegboard));
				iLabel.setSize(pegboard.getIconWidth() * min, pegboard.getIconHeight() * min);
				iLabel.setLocation((lw - iLabel.getWidth()) / 2, getBottom(desc));
				
				min = Math.min(lh / 4, 64);
				bPlay.setSize(m, min);
				int y = lh - (bPlay.getHeight() + 10);
				bPlay.setLocation(m, y);
				bOptions.setSize(m, min);
				bOptions.setLocation(3 * m, y);
				bTut.setSize(m, min);
				bTut.setLocation(5 * m, y);
				bQuit.setSize(m, min);
				bQuit.setLocation(7 * m, y);
			}
		};
		
		addToLayer(title, desc, iLabel, bPlay, bOptions, bTut, bQuit);
		
		frame.setContentPane(layer);
		addCL(cA);
		
		cA.componentResized(null);
		
		frame.pack();
		frame.setVisible(true);
		frame.requestFocus();
	}
	
	public static int getLayerWidth() {
		return layer.getWidth();
	}
	public static int getLayerHeight() {
		return layer.getHeight();
	}
	
	public static void addCL(ComponentListener cL) {
		frame.addComponentListener(cL);
		cLs.add(cL);
	}
	public static void removeAllCLs() {
		for (ComponentListener cL : cLs) {
			frame.removeComponentListener(cL);
		}
		cLs.clear();
	}
	
	public static void addToLayer(Component... components) {
		for (Component c : components) {
			if (c != null)
				layer.add(c, 0, 0);
		}
	}
	
	public static void removeFromLayer(Component... components) {
		for (Component c : components) {
			layer.remove(c);
		}
	}
	
	public static void repaintLayer() {
		layer.repaint();
	}
	
	public static Point getMousePos() {
		Point p = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(p, layer);
		return p;
	}
	
	public static int getBottom(Component c) {
		return c.getY() + c.getHeight();
	}
	
	public static void tutorial() {
		layer.removeAll();
		removeAllCLs();
		
		JLabel title = new JLabel("How to play:");
		JLabel leftM = new JLabel(Handler.scale(2, new ImageIcon(getResources("Controls/LeftClick.png"))));
		JLabel leftMDesc = new JLabel("Left click on a hole to place a peg, to change the current peg's " +
				"color (selected by clicking on the palette), or to move the selected peg to that hole");
		JLabel leftMD = new JLabel(Handler.scale(2, new ImageIcon(getResources("Controls/LeftClickDrag.png"))));
		JLabel leftMDDesc = new JLabel("Left click and drag to move a peg, releasing over a hole will place it" +
				" in the hole. Freeplay: dragging from the palette will spawn a peg, dropping it on the palette " +
				"deletes it");
		JLabel rightM = new JLabel(Handler.scale(2, new ImageIcon(getResources("Controls/RightClick.png"))));
		JLabel rightMDesc = new JLabel("Right click to remove a peg (Freeplay), to send it back to it's hole " +
				"(not Freeplay), or to select it when it is in a hole (not Freeplay)");
		
		CButton bExit = new CButton("Exit");
		bExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainMenu(false);
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				autoBreak(leftMDesc, 20);
				autoBreak(leftMDDesc, 20);
				autoBreak(rightMDesc, 20);
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				leftM.setSize(leftM.getIcon().getIconWidth(), leftM.getIcon().getIconHeight());
				leftM.setLocation((lw - leftM.getWidth()) / 2, getBottom(title) + 5);
				leftMDesc.setSize(getLengthHTML(leftMDesc), getHeightHTML(leftMDesc));
				leftMDesc.setLocation((lw - leftMDesc.getWidth()) / 2, getBottom(leftM) + 5);
				leftMD.setSize(leftMD.getIcon().getIconWidth(), leftMD.getIcon().getIconHeight());
				leftMD.setLocation((lw - leftMD.getWidth()) / 2, getBottom(leftMDesc) + 5);
				leftMDDesc.setSize(getLengthHTML(leftMDDesc), getHeightHTML(leftMDDesc));
				leftMDDesc.setLocation((lw - leftMDDesc.getWidth()) / 2, getBottom(leftMD) + 5);
				rightM.setSize(rightM.getIcon().getIconWidth(), rightM.getIcon().getIconHeight());
				rightM.setLocation((lw - rightM.getWidth()) / 2, getBottom(leftMDDesc) + 5);
				rightMDesc.setSize(getLengthHTML(rightMDesc), getHeightHTML(rightMDesc));
				rightMDesc.setLocation((lw - rightMDesc.getWidth()) / 2, getBottom(rightM) + 5);
				bExit.setSize(lw / 6, Math.min(Math.max(20, lh / 20), 50));
				bExit.setLocation((lw - bExit.getWidth()) / 2, lh - (bExit.getHeight() + 5));
			}
		};
		cA.componentResized(null);
		addCL(cA);
		addToLayer(title, leftM, leftMDesc, leftMD, leftMDDesc, rightM, rightMDesc, bExit);
		frame.repaint();
	}
	
	public static void selectGame() {
		layer.removeAll();
		removeAllCLs();
		
		JLabel title = new JLabel("Select a game");
		Games.Game curr = Games.get();
		JLabel gameTitle = new JLabel(curr.getName());
		final ImageIcon[] board = {new ImageIcon(getResources(curr.getImgLoc()))};
		JLabel image = new JLabel(board[0]);
		JLabel desc = new JLabel(curr.getInstructions());
		
		CButton left = new CButton("<");
		CButton right = new CButton(">");
		
		CButton play = new CButton("Play");
		CButton cancel = new CButton("Cancel");
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				
				double min = Math.min((lw / 2.) / board[0].getIconWidth(), (lh / 2.) / board[0].getIconHeight());
				int w = (int)(board[0].getIconWidth() * min);
				int h = (int)(board[0].getIconHeight() * min);
				image.setIcon(scale(w, h, board[0]));
				image.setSize(w, h);
				image.setLocation((lw - image.getWidth()) / 2, (lh - image.getHeight()) / 2);
				gameTitle.setSize(getLengthHTML(gameTitle), getHeightHTML(gameTitle));
				gameTitle.setLocation((lw - gameTitle.getWidth()) / 2, image.getY() - (gameTitle.getHeight() + 15));
				autoBreak(desc, 20);
				desc.setSize(getLengthHTML(desc), getHeightHTML(desc));
				desc.setLocation((lw - desc.getWidth()) / 2, getBottom(image) + 15);
				
				left.setSize(50, 50);
				left.setLocation(image.getX() - (left.getWidth() + 10), (lh - left.getHeight()) / 2);
				right.setSize(50, 50);
				right.setLocation(image.getX() + image.getWidth() + 10, (lh - right.getHeight()) / 2);
				
				Dimension d = new Dimension(lw / 6, Math.min(Math.max(20, lh / 20), 50));
				play.setSize(d);
				play.setLocation(10, lh - (play.getHeight() + 10 ));
				cancel.setSize(d);
				cancel.setLocation(lw - (cancel.getWidth() + 10), lh - (cancel.getHeight() + 10 ));
				
			}
		};
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Games.prev();
				Games.Game curr = Games.get();
				gameTitle.setText(curr.getName());
				board[0] = new ImageIcon(getResources(curr.getImgLoc()));
				desc.setText(curr.getInstructions());
				cA.componentResized(null);
			}
		});
		right.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Games.next();
				Games.Game curr = Games.get();
				gameTitle.setText(curr.getName());
				board[0] = new ImageIcon(getResources(curr.getImgLoc()));
				desc.setText(curr.getInstructions());
				cA.componentResized(null);
			}
		});
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainMenu(false);
			}
		});
		play.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				removeAllCLs();
				layer.removeAll();
				frame.repaint();
				Games.get().play();
			}
		});
		
		cA.componentResized(null);
		
		addCL(cA);
		addToLayer(title, gameTitle, image, desc, left, right, play, cancel);
		
		frame.repaint();
	}
	
	public static void autoBreak(JLabel l, int buffer) {
		int lw = layer.getWidth() - buffer;
		String text;
		if (l.getText().contains("<html>")) {
			String html = "<[^\\s][^>]*>";
			text = l.getText().replaceAll("<br/>", " ").replaceAll(html, "");
		} else {
			text = l.getText();
		}
		
		String[] words = text.split(" ");
		l.setText(limitWords(words, l, lw));
	}
	
	private static String limitWords(String[] words, JLabel l, int width) {
		StringBuilder endString = new StringBuilder();
		StringBuilder curr = new StringBuilder("<html>");
		for (String word : words) {
			if (getLengthHTML(curr.toString() + " " + word, l.getFont(), l) > width) {
				endString.append(curr).deleteCharAt(endString.lastIndexOf(" ")).append("<br/>");
				curr.setLength(0);
			}
			curr.append(word).append(" ");
		}
		endString.append(curr).deleteCharAt(endString.lastIndexOf(" "));
		endString.append("</html>");
		return endString.toString();
	}
	
	public static void options() {
		layer.removeAll();
		removeAllCLs();
		
		JLabel title = new JLabel("Options");
		CheckBox freePlay = new CheckBox("Free play: Play how you want, rules aren't enforced", Options.freePlay);
		CheckBox realDice = new CheckBox("Use real dice instead of RNG", Options.useRealDice);
		CheckBox endGame = new CheckBox("Prevent game from continuing once a player has won", Options.endGameOnWin);
		Slider volume = new Slider(2, 100, Options.volume);
		JLabel volumeL = new JLabel("Volume:");
		JLabel colorTitle = new JLabel("Custom Colors");
		ArrayList<CButton> colors = new ArrayList<>();
		int loc = 0;
		for (Color c : Options.customColors) {
			int l = loc;
			CButton b = new CButton();
			b.setBackground(c);
			colors.add(b);
			b.setToolTipText("Click to edit");
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					editColor(l);
				}
			});
			addToLayer(b);
			loc++;
		}
		volume.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Options.volume = volume.getValue();
			}
		});
		CButton add = new CButton("+");
		colors.add(add);
		add.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				editColor(-1);
			}
		});
		add.setMargin(new Insets(0,0,0,0));//remove ellipsis
		add.setToolTipText("Add a color");
		
		CButton ok = new CButton("OK");
		ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Options.freePlay = freePlay.isSelected();
				Options.useRealDice = realDice.isSelected();
				Options.endGameOnWin = endGame.isSelected();
				Options.save();
				mainMenu(false);
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				final int S = 10; //Space
				
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, S);
				
				freePlay.setSize(getLengthHTML(freePlay.getText(), freePlay.getFont(), freePlay) + 25, 20);
				freePlay.setLocation((lw - freePlay.getWidth())/2, lh/4);
				realDice.setSize(getLengthHTML(realDice.getText(), realDice.getFont(), realDice) + 25, 20);
				realDice.setLocation((lw - realDice.getWidth())/2, getBottom(freePlay) + S);
				endGame.setSize(getLengthHTML(endGame.getText(), endGame.getFont(), endGame) + 25, 20);
				endGame.setLocation((lw - endGame.getWidth())/2, getBottom(realDice) + S);
				volumeL.setSize(getLengthHTML(volumeL), getHeightHTML(volumeL));
				volumeL.setLocation(5, getBottom(endGame) + S);
				volume.setSize(lw - (volumeL.getX() + volumeL.getWidth() + 5), 20);
				volume.setLocation(5 + volumeL.getX() + volumeL.getWidth(), volumeL.getY());
				colorTitle.setSize(getLengthHTML(colorTitle), getHeightHTML(colorTitle));
				colorTitle.setLocation((lw - colorTitle.getWidth())/2, getBottom(volume) + S);
				ok.setSize(lw/2, Math.min(64, lh/4));
				ok.setLocation(lw/4, lh-(ok.getHeight() + S));
				
				int amnt = colors.size();
				final int size = 20;
				int space = 5;
				int max = (lw - 35) / (size + space); //max in a row
				if (max > amnt) {
					if (amnt != 1)
						space = ((lw - 40) - (amnt * size)) / (amnt - 1);
				}
				int i = 0;
				int i2 = 0;
				for (CButton b : colors) {
					b.setSize(size, size);
					b.setLocation(15 + ((space + size) * i), getBottom(colorTitle) + 10 + ((size + space) * i2));
					i++;
					if (i % max == 0) {
						i = 0;
						i2++;
					}
				}
			}
		};
		cA.componentResized(null);
		
		addCL(cA);
		addToLayer(title, freePlay, realDice, endGame, volumeL, volume, colorTitle, ok, add);
		
		frame.repaint();
	}
	
	public static void editColor(int loc) {
		layer.removeAll();
		removeAllCLs();
		
		Color c;
		if (loc == -1) {
			Random rand = new Random();
			c = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		} else {
			c = Options.customColors.get(loc);
		}
		JLabel title = new JLabel("Edit Color");
		JColorChooser chooser = new JColorChooser(c);
		CButton random = new CButton("Random");
		random.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Random rand = new Random();
				chooser.setColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
			}
		});
		CButton ok = new CButton(loc == -1 ? "Add" : "Change");
		ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (loc == -1) {
					Options.customColors.add(chooser.getColor());
				} else {
					Options.customColors.set(loc, chooser.getColor());
				}
				options();
			}
		});
		CButton del = new CButton("Delete");
		del.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (loc != -1) {
					Options.customColors.remove(loc);
					options();
				}
			}
		});
		if (loc == -1)
			del.setEnabled(false);
		CButton cancel = new CButton("Cancel");
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				options();
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				chooser.setBounds(10, getBottom(title) + 5, lw - 20, lh / 2);
				random.setSize(lw / 2, Math.min(lh / 4, 64));
				random.setLocation((lw - random.getWidth()) / 2, getBottom(chooser) + 5);
				int m = lw / 7;
				int min = Math.min(lh / 4, 64);
				ok.setSize(m, min);
				ok.setLocation(m, lh - (ok.getHeight() + 5));
				del.setSize(m, min);
				del.setLocation(3 * m, lh - (del.getHeight() + 5));
				cancel.setSize(m, min);
				cancel.setLocation(5 * m, lh - (cancel.getHeight() + 5));
			}
		};
		
		cA.componentResized(null);
		addCL(cA);
		addToLayer(title, chooser, random, ok, del, cancel);
		
		frame.repaint();
	}
	
	public static void confirmQuit() {
		layer.removeAll();
		removeAllCLs();
		
		JLabel text = new JLabel("Are you sure?");
		CButton bYes = new CButton("Yes");
		CButton bNo = new CButton("No");
		
		bYes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		bNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				mainMenu(false);
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				int m = lw / 5;
				int min = Math.min(lh / 4, 64);
				text.setSize(getLengthHTML(text), getHeightHTML(text));
				text.setLocation((lw - text.getWidth())/2, (lh/2)-text.getHeight());
				bYes.setSize(m, min);
				bYes.setLocation(m, lh/2 + lh/4);
				bNo.setSize(m, min);
				bNo.setLocation(3*m, lh/2 + lh/4);
			}
		};
		
		addToLayer(text, bYes, bNo);
		
		cA.componentResized(null);
		
		frame.repaint();
		
		addCL(cA);
	}
}
