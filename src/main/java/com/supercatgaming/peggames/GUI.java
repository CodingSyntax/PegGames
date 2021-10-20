package com.supercatgaming.peggames;

import com.supercatgaming.peggames.Components.Label;
import com.supercatgaming.peggames.Components.*;

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
	private static JLabel bg;
	private static int GUIScale = 1;
	public static Font font = new Font("Times New Roman", Font.PLAIN, 24);
	public static Font subFont;
	
	private static void updateGUIScale() {
		int lw = layer.getWidth();
		int lh = layer.getHeight();
		if (lw < 1500 || lh < 570) GUIScale = 1;
		else GUIScale = 2;
		
		font = font.deriveFont((float)(30*(GUIScale)));
		subFont = font.deriveFont(font.getSize() * 0.5f);
	}
	
	public static int getScale() {
		return GUIScale;
	}
	
	public static String changeFolder() {
		FileDialog fD = new FileDialog((Dialog) null, "Error, select save directory", FileDialog.LOAD);
		fD.setDirectory(Handler.getFolderLoc());
		fD.setVisible(true);
		while (true) {
			String selectedFile = fD.getDirectory();
			if (selectedFile != null)
				return selectedFile;
			else
				fD.setVisible(true);
		}
	}
	
	public static void mainMenu(boolean init) {
		if (init) {
			layer.setPreferredSize(new Dimension(854, 480));
			frame.setTitle(Handler.NAME);
			ArrayList<Image> iconImages = new ArrayList<>();
			int[] nums = {16, 32, 64, 128};
			for(int i = 0; i < 2; i++){
				iconImages.add(new ImageIcon(getResources("Icons/" + nums[i] + ".png")).getImage());
			}
			frame.setIconImages(iconImages);
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			ImageIcon background = new ImageIcon(getResources("bg.png"));
			int bgh = background.getIconHeight();
			int bgw = background.getIconWidth();
			bg = new JLabel(background);
			bg.setLocation(0,0);
			ComponentAdapter cB = new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					updateGUIScale();
					bg.setIcon(Handler.scale(Math.max(bgw, layer.getWidth()), Math.max(bgh, layer.getHeight()), background));
					bg.setSize(layer.getWidth(), layer.getHeight());
				}
			};
			frame.addComponentListener(cB);
		}
		else {
			layer.setPreferredSize(layer.getSize());
			clearLayer();
		}
		
		QButton bPlay = new QButton("Play", QButton.Type.Thin);
		QButton bOptions = new QButton("Options", QButton.Type.Thin);
		QButton bTut = new QButton("Controls", QButton.Type.Thin);
		QButton bQuit = new QButton("Quit", QButton.Type.Thin);
		
		Label title = new Label("Peg Games");
		JLabel desc = new JLabel();
		desc.setForeground(Color.WHITE);
		
		ImageIcon peg = new ImageIcon(getResources("Icons/128.png"));
		JLabel iLabel = new JLabel(peg);
		
		
		
		bPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				selectGame();
			}
		});
		bOptions.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				options();
			}
		});
		bTut.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				tutorial();
			}
		});
		bQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				confirmQuit();
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lh = layer.getHeight();
				int lw = layer.getWidth();
				
				title.setFont(new Font("Serif", Font.PLAIN, Math.min(lw / 10, lh / 10)));
				desc.setFont(new Font("Serif", Font.PLAIN, Math.min(lw / 20, lh / 20)));
				Handler.setText(desc, "A selection of peg games to play alone or with some friends", lw - 10);
				title.setSize(Handler.getLengthHTML(title), Handler.getHeightHTML(title));
				title.setLocation((lw - title.getWidth()) / 2, 5);
				desc.setSize(Handler.getLengthHTML(desc), Handler.getHeightHTML(desc));
				desc.setLocation((lw - desc.getWidth()) / 2, getBottom(title) + 10);
				
				int min = Math.max(Math.min(lw / 128, (lh / 2) / 128), 1);
				iLabel.setIcon(Handler.scale(min, peg));
				iLabel.setSize(peg.getIconWidth() * min, peg.getIconHeight() * min);
				iLabel.setLocation((lw - iLabel.getWidth()) / 2, getBottom(desc));
				
				bPlay.setScale(GUIScale);
				bOptions.setScale(GUIScale);
				bTut.setScale(GUIScale);
				bQuit.setScale(GUIScale);
				int w = bPlay.getWidth() / 2;
				int m = lw / 5;
				int y = lh - (bPlay.getHeight() + 10);
				bPlay.setLocation(m - w, y);
				bOptions.setLocation(2 * m - w, y);
				bTut.setLocation(3 * m - w, y);
				bQuit.setLocation(4 * m - w, y);
			}
		};
		
		addToLayer(bg, title, desc, iLabel, bPlay, bOptions, bTut, bQuit);
		
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
	
	public static void clearLayer() {
		layer.removeAll();
		removeAllCLs();
		layer.add(bg, 0, 0);
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
		clearLayer();
		
		Label title = new Label("How to play:");
		ImageIcon leftMI = new ImageIcon(getResources("Controls/LeftClick.png"));
		JLabel leftM = new JLabel(Handler.scale(1+GUIScale, leftMI));
		Label leftMDesc = new Label("Left click on a hole to place a peg, to change the current peg's " +
				"color (selected by clicking on the palette), or to move the selected peg to that hole.");
		ImageIcon leftMDI = new ImageIcon(getResources("Controls/LeftClickDrag.png"));
		JLabel leftMD = new JLabel(Handler.scale(1+GUIScale, leftMDI));
		Label leftMDDesc = new Label("Left click and drag to move a peg, releasing over a hole will place it" +
				" in the hole. Freeplay: dragging from the palette will spawn a peg, dropping it on the palette " +
				"deletes it.");
		ImageIcon rightMI = new ImageIcon(getResources("Controls/RightClick.png"));
		JLabel rightM = new JLabel();
		Label rightMDesc = new Label("Right click to send a peg back to it's hole " +
				"(if not in a hole), or to select it when it is in a hole. Freeplay: deletes the peg.");
		
		QButton bExit = new QButton("Exit", QButton.Type.Thin);
		bExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mainMenu(false);
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				title.setFont(font);
				leftMDesc.setFont(subFont);
				leftMDDesc.setFont(subFont);
				rightMDesc.setFont(subFont);
				autoBreak(leftMDesc, 20);
				autoBreak(leftMDDesc, 20);
				autoBreak(rightMDesc, 20);
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				leftM.setIcon(Handler.scale(1+GUIScale, leftMI));
				leftM.setSize(leftM.getIcon().getIconWidth(), leftM.getIcon().getIconHeight());
				leftM.setLocation((lw - leftM.getWidth()) / 2, getBottom(title) + 5);
				leftMDesc.setSize(getLengthHTML(leftMDesc), getHeightHTML(leftMDesc));
				leftMDesc.setLocation((lw - leftMDesc.getWidth()) / 2, getBottom(leftM) + 5);
				leftMD.setIcon(Handler.scale(1+GUIScale, leftMDI));
				leftMD.setSize(leftMD.getIcon().getIconWidth(), leftMD.getIcon().getIconHeight());
				leftMD.setLocation((lw - leftMD.getWidth()) / 2, getBottom(leftMDesc) + 5);
				leftMDDesc.setSize(getLengthHTML(leftMDDesc), getHeightHTML(leftMDDesc));
				leftMDDesc.setLocation((lw - leftMDDesc.getWidth()) / 2, getBottom(leftMD) + 5);
				rightM.setIcon(Handler.scale(1+GUIScale, rightMI));
				rightM.setSize(rightM.getIcon().getIconWidth(), rightM.getIcon().getIconHeight());
				rightM.setLocation((lw - rightM.getWidth()) / 2, getBottom(leftMDDesc) + 5);
				rightMDesc.setSize(getLengthHTML(rightMDesc), getHeightHTML(rightMDesc));
				rightMDesc.setLocation((lw - rightMDesc.getWidth()) / 2, getBottom(rightM) + 5);
				bExit.setScale(GUIScale);
				bExit.setLocation((lw - bExit.getWidth()) / 2, lh - (bExit.getHeight() + 5));
			}
		};
		cA.componentResized(null);
		addCL(cA);
		addToLayer(title, leftM, leftMDesc, leftMD, leftMDDesc, rightM, rightMDesc, bExit);
		frame.repaint();
	}
	
	public static void selectGame() {
		clearLayer();
		
		Label title = new Label("Select a game");
		Games.Game curr = Games.get();
		Label gameTitle = new Label(curr.getName());
		final ImageIcon[] board = {new ImageIcon(getResources(curr.getImgLoc()))};
		JLabel image = new JLabel(board[0]);
		Label desc = new Label(curr.getInstructions());
		
		QButton left = new QButton("<", QButton.Type.Small);
		QButton right = new QButton(">", QButton.Type.Small);
		
		QButton play = new QButton("Play", QButton.Type.Thin);
		QButton cancel = new QButton("Cancel", QButton.Type.Thin);
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				
				title.setFont(font);
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				
				double min = Math.min((lw / 2.) / board[0].getIconWidth(), (lh / 2.) / board[0].getIconHeight());
				int w = (int)(board[0].getIconWidth() * min);
				int h = (int)(board[0].getIconHeight() * min);
				image.setIcon(scale(w, h, board[0]));
				image.setSize(w, h);
				image.setLocation((lw - w) / 2, (lh - h) / 2);
				gameTitle.setFont(subFont);
				gameTitle.setSize(getLengthHTML(gameTitle), getHeightHTML(gameTitle));
				gameTitle.setLocation((lw - gameTitle.getWidth()) / 2, image.getY() - (gameTitle.getHeight() + 15));
				desc.setFont(subFont);
				autoBreak(desc, 20);
				desc.setSize(getLengthHTML(desc), getHeightHTML(desc));
				desc.setLocation((lw - desc.getWidth()) / 2, getBottom(image) + 15);
				
				left.setScale(GUIScale);
				left.setLocation(image.getX() - (left.getWidth() + 10), (lh - left.getHeight()) / 2);
				right.setScale(GUIScale);
				right.setLocation(image.getX() + image.getWidth() + 10, (lh - right.getHeight()) / 2);
				
				play.setScale(GUIScale);
				play.setLocation(10, lh - (play.getHeight() + 10 ));
				cancel.setScale(GUIScale);
				cancel.setLocation(lw - (cancel.getWidth() + 10), lh - (cancel.getHeight() + 10 ));
				
			}
		};
		left.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
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
			public void mousePressed(MouseEvent e) {
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
			public void mousePressed(MouseEvent e) {
				mainMenu(false);
			}
		});
		play.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clearLayer();
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
		clearLayer();
		
		Label title = new Label("Options");
		CheckBox freePlay = new CheckBox("Freeplay: Play how you want, rules aren't enforced", Options.freePlay);
		CheckBox realDice = new CheckBox("Use real dice instead of RNG", Options.useRealDice);
		realDice.setToolTipText("When you roll, you will be asked to input what you have rolled.");
		CheckBox endGame = new CheckBox("Prevent game from continuing once a player has won", Options.endGameOnWin);
		Slider volume = new Slider(2, 100, Options.volume);
		Label volumeL = new Label("Volume:");
		Label colorTitle = new Label("Custom Colors");
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
				public void mousePressed(MouseEvent e) {
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
			public void mousePressed(MouseEvent e) {
				editColor(-1);
			}
		});
		add.setMargin(new Insets(0,0,0,0));//remove ellipsis
		add.setToolTipText("Add a color");
		
		QButton ok = new QButton("OK", QButton.Type.Thin);
		ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
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
				title.setFont(font);
				freePlay.setFont(subFont);
				realDice.setFont(subFont);
				endGame.setFont(subFont);
				volumeL.setFont(subFont);
				colorTitle.setFont(subFont);
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, S);
				freePlay.setScale(GUIScale);
				realDice.setScale(GUIScale);
				endGame.setScale(GUIScale);
				freePlay.setLocation((lw - freePlay.getWidth())/2, lh/4);
				realDice.setLocation((lw - realDice.getWidth())/2, getBottom(freePlay) + S);
				endGame.setLocation((lw - endGame.getWidth())/2, getBottom(realDice) + S);
				volumeL.setSize(getLengthHTML(volumeL), getHeightHTML(volumeL));
				volume.setSize(((lw - volumeL.getWidth()) * 9)/10, 20*GUIScale);
				volumeL.setLocation((lw/2) - ((volume.getWidth() + volumeL.getWidth() + 5)/2), getBottom(endGame) + S);
				volume.setLocation(5 + volumeL.getX() + volumeL.getWidth(), volumeL.getY());
				colorTitle.setSize(getLengthHTML(colorTitle), getHeightHTML(colorTitle));
				colorTitle.setLocation((lw - colorTitle.getWidth())/2, getBottom(volume) + S);
				ok.setScale(GUIScale);
				ok.setLocation((lw-ok.getWidth())/2, lh-(ok.getHeight() + S));
				
				int amnt = colors.size();
				final int size = 20 * GUIScale;
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
		clearLayer();
		
		Color c;
		if (loc == -1) {
			Random rand = new Random();
			c = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
		} else {
			c = Options.customColors.get(loc);
		}
		Label title = new Label("Edit Color");
		JColorChooser chooser = new JColorChooser(c);
		chooser.setOpaque(false);
		QButton random = new QButton("Random", QButton.Type.Thin);
		random.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Random rand = new Random();
				chooser.setColor(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
			}
		});
		QButton ok = new QButton(loc == -1 ? "Add" : "Change", QButton.Type.Thin);
		ok.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Color c = chooser.getColor();
				chooser.setColor(c.getRed(), c.getGreen(), c.getBlue());
				if (loc == -1) {
					Options.customColors.add(chooser.getColor());
				} else {
					Options.customColors.set(loc, chooser.getColor());
				}
				options();
			}
		});
		QButton del = new QButton("Delete", QButton.Type.Thin);
		del.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (loc != -1) {
					Options.customColors.remove(loc);
					options();
				}
			}
		});
		if (loc == -1)
			del.setEnabled(false);
		QButton cancel = new QButton("Cancel", QButton.Type.Thin);
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				options();
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				
				title.setFont(font);
				ok.setScale(GUIScale);
				random.setScale(GUIScale);
				cancel.setScale(GUIScale);
				del.setScale(GUIScale);
				
				title.setSize(getLengthHTML(title), getHeightHTML(title));
				title.setLocation((lw - title.getWidth())/2, 10);
				chooser.setBounds(10, getBottom(title) + 5, lw - 20, lh / 2);
				random.setLocation((lw - random.getWidth()) / 2, getBottom(chooser) + 5*GUIScale);
				int h = lh - (ok.getHeight() + (5 * GUIScale));
				int w = ok.getWidth() / 2;
				int m = lw / 4;
				del.setLocation(2 * m - w, h);
				ok.setLocation(m - w, h);
				cancel.setLocation(3 * m - w, h);
			}
		};
		
		cA.componentResized(null);
		addCL(cA);
		addToLayer(title, chooser, random, ok, del, cancel);
		
		frame.repaint();
	}
	
	public static void confirmQuit() {
		clearLayer();
		
		Label text = new Label("Are you sure?");
		QButton bYes = new QButton("Yes", QButton.Type.Thin);
		QButton bNo = new QButton("No", QButton.Type.Thin);
		
		bYes.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				System.exit(0);
			}
		});
		bNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mainMenu(false);
			}
		});
		
		ComponentAdapter cA = new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int lw = layer.getWidth();
				int lh = layer.getHeight();
				bYes.setScale(GUIScale);
				bNo.setScale(GUIScale);
				int m = lw / 3;
				int w = bYes.getWidth() / 2;
				text.setFont(subFont);
				text.setSize(getLengthHTML(text), getHeightHTML(text));
				text.setLocation((lw - text.getWidth())/2, (lh/2)-text.getHeight());
				bYes.setLocation(m - w, lh/2 + lh/4);
				bNo.setLocation(2 * m - w, lh/2 + lh/4);
			}
		};
		
		addToLayer(text, bYes, bNo);
		
		cA.componentResized(null);
		
		frame.repaint();
		
		addCL(cA);
	}
}
