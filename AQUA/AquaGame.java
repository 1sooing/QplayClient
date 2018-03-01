package AQUA;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import AQUA.AquaPlayer.unitMove;
import MAIN.Client;
import UTILL.Client_Settings;
import UTILL.Entity;
import UTILL.SoundEffect;
import UTILL.Sprite;
import UTILL.SpriteStore;

public class AquaGame extends Canvas implements Runnable, KeyListener {

	DataOutputStream out;
	Client client;

	/*
	 * Private settings
	 */
	public static final int AQUA_PLAYER_MAX_COUNT = Client_Settings.AQUA_PLAYER_MAX_COUNT;

	public static final int PLAYER_SIZEX = Client_Settings.AQUA_PLAYER_SIZEX;
	public static final int PLAYER_SIZEY = Client_Settings.AQUA_PLAYER_SIZEY;

	private boolean gameRunning = true;
	
	private static final double PLAYER_ACC = Client_Settings.AQUA_PLAYER_ACC;
	private static final double OBSERVER_SPEED = Client_Settings.AQUA_OBSERVER_SPEED;

	/*
	 * Not necessarily
	 */
	public int WINDOW_SIZEX = 1200;
	public int WINDOW_SIZEY = 800;

	private BufferedImage bg_aqua; 
	private BufferStrategy strategy;

	public ArrayList<AquaPlayer> players = new ArrayList<AquaPlayer>();
	public ArrayList<AquaMissile> missiles = new ArrayList<AquaMissile>();

	int teamid = -1;
	int roomid = -1;
	int slotid = -1;
	int playerid = -1;
	String nick = "";
	int dresscode = -1;

	boolean left, down, right, up, f1, f2, f3, f4;
	boolean isEnded = false;
	boolean bgm_aqua = true;
	
	private boolean isMissileMoving;
	long lastShoot = 0;
	
	public AquaGame(DataOutputStream out, Client c, int roomid, int slotid) {

		this.out = out;
		this.client = c;
		this.nick = c.nick;
		this.playerid = c.playerid;
		this.roomid = c.roomid;
		this.slotid = c.slotid;
		this.teamid = c.teamid;
		
		this.bgm_aqua = c.bgm_aqua;
		
		WINDOW_SIZEX = c.WINDOW_SIZEX;
		WINDOW_SIZEY = c.WINDOW_SIZEY;

		JPanel panel = (JPanel) client.frame.getContentPane();

		panel.setPreferredSize(new Dimension(WINDOW_SIZEX, WINDOW_SIZEY));
		panel.setLayout(null);

		setBounds(0, 0, WINDOW_SIZEX, WINDOW_SIZEY);

		panel.add(this);

		setIgnoreRepaint(true);

		client.frame.pack();
		client.frame.setResizable(false);
		client.frame.setVisible(true);

		addKeyListener(this);
		requestFocus();

		createBufferStrategy(2);
		strategy = getBufferStrategy();

		initGame();
	}

	public void initGame() {

		URL url = this.getClass().getClassLoader().getResource("res/background.png");
		
		try {
			bg_aqua = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {
			AquaPlayer ap = new AquaPlayer(this, -1, -1, "", -1, -1, -1);
			players.add(ap);
		}

		for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {
			AquaMissile as = new AquaMissile(this, -1, -1, -1, -1, -1, -1, -1);
			missiles.add(as);
		}

		isEnded = false;

		// MISSILE INIT
		f1 = false;
		f2 = false;
		f3 = false;
		f4 = false;
		isMissileMoving = false;

		// PLAYER INIT
		left = false;
		down = false;
		right = false;
		up = false;
	}

	public void removeGame() {
		gameRunning = false;
		client.frame.setVisible(false);
		removeKeyListener(this);

		players.clear();
		missiles.clear();
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.dispose();
		strategy.dispose();
	}

	public void updatePlayerObserver(long delta, double velx, double vely) {
		players.get(slotid).setHorizontalMovement(velx);
		players.get(slotid).setVerticalMovement(vely);
		players.get(slotid).move(delta);
	}
	
	/*
	 * Movement packet is received on every ~30ms. Always Ordered sequence.
	 * So we will handle this by queue. 
	 * 
	 */
	public void updatePlayerMove(int slotid, boolean direction, double acc, long delta) {
		players.get(slotid).pushM(delta, direction, acc);
	}

	public void updatePlayerHitMove(int slotid, double velx, double vely) {
		players.get(slotid).addHorizontalMovement(velx);
		players.get(slotid).addVerticalMovement(vely);
	}
	
	/*
	 * Consider transmission time using keepalive
	 */
	
	/*
	 * Server-sides's authoritative sending loop is 40ms
	 * Client-side's loop is 10ms
	 * 
	 *  we may move 4 times divide the gap
	 */
	 public void updatePlayerPosition(int slotid, long delta, int posx, int posy) {
		 
		 /* old data */
		 int pastX = players.get(slotid).getX();
		 int pastY = players.get(slotid).getY();
		 
		 /* new data */
		 
//		 players.get(slotid).setLocation(posx, posy);
		 
		 /* Current step ratio is 40ms : 10ms */
//		 players.get(slotid).setScale((posx - pastX) / 4, (posy - pastY) / 4);
		 
		 players.get(slotid).setHorizontalMovement(1 * (posx - pastX));
		 players.get(slotid).setVerticalMovement(1 * (posy - pastY));
//		 players.get(slotid).setLocation(posx, posy);
//		 players.get(slotid).move(delta);
	 }

	public void updateMissileMove(int slotid, int teamid, int type, int x, int y, double velx, double vely) {

		switch (type) {
		case 1:
			AquaMissile f1 = new AquaMissile(this, slotid, teamid, 1, x, y, velx, vely);
			missiles.set(slotid, f1);
			break;

		case -1:
			AquaMissile f1L = new AquaMissile(this, slotid, teamid, -1, x, y, velx, vely);
			missiles.set(slotid, f1L);
			break;

		case 2:
			AquaMissile f2 = new AquaMissile(this, slotid, teamid, 2, x, y, velx, vely);
			missiles.set(slotid, f2);
			break;

		case -2:
			AquaMissile f2L = new AquaMissile(this, slotid, teamid, -2, x, y, velx, vely);
			missiles.set(slotid, f2L);
			break;

			/* Handles initial velocity of f4*/
		case 4:
			AquaMissile f4 = new AquaMissile(this, slotid, teamid, 4, x, y, velx, vely);
			missiles.set(slotid, f4);
			break;

		case -4:
			AquaMissile f4L = new AquaMissile(this, slotid, teamid, -4, x, y, velx, vely);
			missiles.set(slotid, f4L);
			break;
		}

		missiles.get(slotid).setState(true);
		
//		System.out.println("[updateMissile] status: " + missiles.get(slotid).getState() + ", canShoot:" + canShoot(System.currentTimeMillis()) + ", isMissilemoving: " + isMissileMoving + ", location: " + missiles.get(slotid).getX() + ", " + missiles.get(slotid).getY());
	}

	public boolean getMissileMoving() {
		return this.isMissileMoving;
	}
	
	public void setMissileMoving(boolean move) {
		this.isMissileMoving = move;
	}
	
	public void updateAquaGamePlayerInfo(int slotid, int teamid, String nick, int dresscode, int x, int y) {
		AquaPlayer ap = new AquaPlayer(this, slotid, teamid, nick, dresscode, x, y);
		players.set(slotid, ap);
		players.get(slotid).setState(true);
	}

	public void updatePlayerExit(int slotid) {
		players.get(slotid).setAlive(false);
		players.get(slotid).setState(false);
		missiles.get(slotid).setState(false);
	}

	public void updatePlayerHp(int slotidx, int slotid, int hp) {
		players.get(slotid).setHP(hp);

		/* Check for kill count increase */
		if( !players.get(slotid).isAlive() )
			players.get(slotidx).setKilled();
	}

	public void writeExitAqua() {
		String msg = "" + Client_Settings.SET_EXIT_AQUA + (char) 007;

			int length = 50 - msg.length();

		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);

		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeAquaPlayerMove(boolean direction, double acc) {
		String msg = "" + Client_Settings.SET_AQUA_PLAYER_MOVE + (char) 007
				+ direction + (char) 007 + acc + (char) 007;

		int length = 50 - msg.length();
		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeAquaPlayerPos(int posx, int posy) {
		String msg = "" + Client_Settings.SET_AQUA_PLAYER_POS + (char) 007 + posx + (char) 007 + posy + (char) 007;

		int length = 50 - msg.length();
		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeAquaPlayerHit(int slotid, int damage, double accx, double accy) {
		String msg = "" + Client_Settings.SET_AQUA_HIT + (char) 007 + slotid + (char) 007 + damage + (char) 007 + accx + (char) 007 + accy + (char) 007;

		int length = 50 - msg.length();
		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeMissileMove(int type, double x, double y, double velx,	double vely) {
		String msg = "" + Client_Settings.SET_AQUA_MISSILE_MOVE + (char) 007 + type + (char) 007 + x + (char) 007 + y + (char) 007 + velx + (char) 007 + vely
				+ (char) 007;

		int length = 50 - msg.length();
		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);
		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private void writeTime() {
//		String msg = "" + Client_Settings.SET_DEBUG_TIME + (char) 007 + System.currentTimeMillis()
//				+ (char) 007;
//
//		int length = 50 - msg.length();
//		for (int i = 0; i < length; i++)
//			msg += "X";
//
//		byte[] data = msg.getBytes(StandardCharsets.UTF_8);
//		try {
//			out.write(data);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public void gameEnded(boolean victory) {

		SoundEffect.BGM_AQUA.stop();
		SoundEffect.DEATH.play();
		
		/* show victory graphics */
		gameRunning = false;

		Sprite result_aqua = SpriteStore.get().getSprite("res/defeat.png");

		if (victory)
			result_aqua = SpriteStore.get().getSprite("res/victory.png");

		int count = 150;

		while (count > 0) {
			count--;
			
			Graphics2D g = (Graphics2D) getGraphics();
			g.drawImage(result_aqua.getImage(), 0, 0, getWidth(), getHeight(), this);
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
		writeExitAqua();
	}

	public boolean canShoot(long time) {
		
		if (lastShoot < time)
			return true;
		
		return false;
	}

	/*
	 * Key event
	 * 
	 * 37: left
	 * 38: up
	 * 39: right
	 * 40: down
	 * 
	 * 112: F1
	 * 113: F2
	 * 114: F3
	 * 115: F4
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int pushedkey = e.getKeyCode();

		// left
		if(pushedkey == 37) {
			
			if(right) {}
						
			else if(!left) {
				writeAquaPlayerMove(true, -PLAYER_ACC);
			}	
			left = true;			
			
			// right
		} else if (pushedkey == 39) {
			
			if(left) {}
			
			else if(!right) {
				writeAquaPlayerMove(true, PLAYER_ACC);
			}
			right = true;
		}
		
		// up
		if(pushedkey == 38) {
			
			if(down) {}
			
			else if(!up) {
				writeAquaPlayerMove(false, -PLAYER_ACC);
			}
			up = true;
			
			// down
		} else if (pushedkey == 40) {
			
			if(up) {}
				
			else if(!down) {
				writeAquaPlayerMove(false, PLAYER_ACC);
			}
			down = true;
		}
		
		switch(pushedkey) {
		case 112:
//			System.out.println("slotid: " + slotid + "[*] status: " + missiles.get(slotid).getState() + ", canShoot:" + canShoot(System.currentTimeMillis()) + ", isMissilemoving: " + isMissileMoving + ", location: " + missiles.get(slotid).getX() + ", " + missiles.get(slotid).getY());
			f1 = true;
			break;
		case 113:
//			System.out.println("slotid: " + slotid + "[*] status: " + missiles.get(slotid).getState() + ", canShoot:" + canShoot(System.currentTimeMillis()) + ", isMissilemoving: " + isMissileMoving + ", location: " + missiles.get(slotid).getX() + ", " + missiles.get(slotid).getY());
			f2 = true;
			break;
		case 114:
//			System.out.println("slotid: " + slotid + "[*] status: " + missiles.get(slotid).getState() + ", canShoot:" + canShoot(System.currentTimeMillis()) + ", isMissilemoving: " + isMissileMoving + ", location: " + missiles.get(slotid).getX() + ", " + missiles.get(slotid).getY());
			f3 = true;
			break;
		case 115:
//			System.out.println("slotid: " + slotid + "[*] status: " + missiles.get(slotid).getState() + ", canShoot:" + canShoot(System.currentTimeMillis()) + ", isMissilemoving: " + isMissileMoving + ", location: " + missiles.get(slotid).getX() + ", " + missiles.get(slotid).getY());
			f4 = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int pushedkey = e.getKeyCode();

		if (pushedkey == 37) {
			writeAquaPlayerMove(true, 0);
			left = false;
		}
		else if (pushedkey == 38) {
			writeAquaPlayerMove(false, 0);
			up = false;
		}
		
		if (pushedkey == 39) {
			writeAquaPlayerMove(true, 0);
			right = false;
		} 
		else if (pushedkey == 40) {
			writeAquaPlayerMove(false, 0);
			down = false;
		}
		
		switch(pushedkey) {
		case 112:
			f1 = false;
			break;
		case 113:
			f2 = false;
			break;
		case 114:	
			f3 = false;
			break;
		case 115:
			f4 = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void run() {

		long lastLoopTime = System.currentTimeMillis();
		long lastBGMPlayTime = lastLoopTime;
		
		SoundEffect.BGM_LOBBY.stop();
		SoundEffect.BGM_AQUA.stop();
		
		if(bgm_aqua)
			SoundEffect.BGM_AQUA.play();
		
		/*
		 * Current player center edges
		 */
		int currentX = players.get(slotid).getX() + 70;
		int currentY = players.get(slotid).getY() + 65;
		
		/*
		 * Current map edges
		 */
		int mapX = currentX - WINDOW_SIZEX / 2;
		int mapY = currentY - WINDOW_SIZEY / 2;

		/*
		 * Virtual rectangle
		 */
		int vboundx_1 = mapX + WINDOW_SIZEX / 8 * 3;
		int vboundx_2 = mapX + WINDOW_SIZEX / 8 * 5;
		int vboundy_1 = mapY + WINDOW_SIZEY / 8 * 3;
		int vboundy_2 = mapY + WINDOW_SIZEY / 8 * 5;
		
		int shiftX = 0;
		int shiftY = 0;
		
		while (gameRunning) {

			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			/* 
			 * Check BGM is finished and wait 10 seconds 
			 * current BGM length is 00:53 = 53 sec == 53000 ms
			 */
			if(bgm_aqua) {
				if ( lastLoopTime - lastBGMPlayTime >= 60000) {
					lastBGMPlayTime = lastLoopTime;
					SoundEffect.BGM_AQUA.play();
				}
			}
			
//			client.checkKeepAlive(lastLoopTime);
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

			/*
			 * Show local maps abjust to my current position and window size
			 */
			// First, check if player is in
			
			currentX = players.get(slotid).getX() + 70;
			currentY = players.get(slotid).getY() + 65;
			
			shiftX = 0;
			shiftY = 0;
			
			if(vboundx_1 > currentX) {
				
				shiftX = currentX - vboundx_1;
				
				vboundx_1 += shiftX;
				vboundx_2 += shiftX;
				mapX += shiftX;
			}
			else if(vboundx_2 < currentX) {
				
				shiftX = currentX - vboundx_2;
				
				vboundx_1 += shiftX;
				vboundx_2 += shiftX;
				mapX += shiftX;
			}
			
			if(vboundy_1 > currentY) {
				
				shiftY = currentY - vboundy_1;
				
				vboundy_1 += shiftY;
				vboundy_2 += shiftY;
				mapY += shiftY;
			}
			else if(vboundy_2 < currentY) {
				
				shiftY = currentY - vboundy_2;
				
				vboundy_1 += shiftY;
				vboundy_2 += shiftY;
				mapY += shiftY;
			}

			vboundx_1 = mapX + WINDOW_SIZEX / 8 * 3;
			vboundx_2 = mapX + WINDOW_SIZEX / 8 * 5;
			vboundy_1 = mapY + WINDOW_SIZEY / 8 * 3;
			vboundy_2 = mapY + WINDOW_SIZEY / 8 * 5;
			
			if (mapX < 0) {
				vboundx_1 = 0;
				vboundx_2 = WINDOW_SIZEX / 8 * 5;
				mapX = 0;
			}
			else if (mapX > Client_Settings.MAP_SIZEX - WINDOW_SIZEX) {
				vboundx_1 = Client_Settings.MAP_SIZEX - WINDOW_SIZEX / 8 * 3;
				vboundx_2 = Client_Settings.MAP_SIZEX;
				mapX = Client_Settings.MAP_SIZEX - WINDOW_SIZEX;
			}
			
			if (mapY < 0) {
				vboundy_1 = 0;
				vboundy_2 = WINDOW_SIZEY / 8 * 5;
				mapY = 0;
			}
			else if (mapY > Client_Settings.MAP_SIZEY - WINDOW_SIZEY) {
				vboundy_1 = Client_Settings.MAP_SIZEY - WINDOW_SIZEY / 8 * 3;
				vboundy_2 = Client_Settings.MAP_SIZEY;
				mapY = Client_Settings.MAP_SIZEY - WINDOW_SIZEY;
			}
			
			BufferedImage img = bg_aqua.getSubimage(mapX, mapY, WINDOW_SIZEX, WINDOW_SIZEY);
			g.drawImage(img, 0, 0, null);
			
			
			/* Inform server location */
//			writeAquaPlayerPos((int) players.get(slotid).getX(), (int) players.get(slotid).getY());				

			/* Missile handling */
			if (f1 == true && !isMissileMoving && canShoot(lastLoopTime)) {
				isMissileMoving = true;

				if (right) {
					
					writeMissileMove(1, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), 0, 0);

				} else if (left) {
					
					writeMissileMove(-1, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), 0, 0);
				}
			}

			else if (f2 == true && !isMissileMoving && canShoot(lastLoopTime)) {
				isMissileMoving = true;

				if (right)
					writeMissileMove(2, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), 0, 0);

				else if (left)
					writeMissileMove(-2, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), 0, 0);
			}

			else if (f4 == true && !isMissileMoving && canShoot(lastLoopTime)) {
				isMissileMoving = true;

				if (right)
					writeMissileMove(4, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), players.get(slotid).getHorizontalMovement(), 0);
				else if (left)
					writeMissileMove(-4, (int) players.get(slotid).getX(),
							(int) players.get(slotid).getY(), players.get(slotid).getHorizontalMovement(), 0);
			}

			if(!players.get(slotid).isAlive()) {
				
				if (right) {

					if (up) {
						updatePlayerObserver(delta, OBSERVER_SPEED, -OBSERVER_SPEED);
					} else if (down) {
						updatePlayerObserver(delta, OBSERVER_SPEED, OBSERVER_SPEED);
					} else {
						updatePlayerObserver(delta, OBSERVER_SPEED, 0);
					}
					
				} else if (left) {

					if (up) {
						updatePlayerObserver(delta, -OBSERVER_SPEED, -OBSERVER_SPEED);
					} else if (down) {
						updatePlayerObserver(delta, -OBSERVER_SPEED, OBSERVER_SPEED);
					} else {
						updatePlayerObserver(delta, -OBSERVER_SPEED, 0);
					}
					
				} else {
					if (up) {
						updatePlayerObserver(delta, 0, -OBSERVER_SPEED);
					} else if (down) {
						updatePlayerObserver(delta, 0, OBSERVER_SPEED);
					}
				}
			}

			// judgement
			if (isMissileMoving) {
				
				Entity mine = (Entity) missiles.get(slotid);
				
				for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {

					if (i == slotid)
						continue;

					if (!players.get(i).getState())
						continue;

					if (!players.get(i).isAlive())
						continue;
					
					Entity him = (Entity) players.get(i);
					
					/* If same team, skip */
					if( players.get(i).getTeamId() == teamid ) {
						continue;
					}

					// calculate the collision point
					if (mine.collidesWith(him)) {
						
						int type = missiles.get(slotid).getType();
						double cx = PLAYER_SIZEX;
						double cy = 2 * players.get(i).getY() + PLAYER_SIZEY - missiles.get(slotid).getY();

						if (type == 1 || type == -1) {
							
							if (missiles.get(slotid).getX() > players.get(i).getX())
								cx = -cx;
							
							writeAquaPlayerHit(i, -1, cx * 2, (cy - missiles.get(slotid).getY()) * 2);
							
							// we should have time delay to recharge
							lastShoot = System.currentTimeMillis() + 500;
						} else if (type == 2 || type == -2) {
							
							if (missiles.get(slotid).getX() > players.get(i).getX())
								cx = -cx;
							
							writeAquaPlayerHit(i, -2, cx * 4, (cy - missiles.get(slotid).getY()) * 4);
							
							// we should have time delay to recharge
							lastShoot = System.currentTimeMillis() + 300;
						}

						else if (type == 4 || type == -4) {
							
							if (missiles.get(slotid).getX() > players.get(i).getX())
								cx = -cx;

							writeAquaPlayerHit(i, -1, cx * 3, (cy - missiles.get(slotid).getY()) * 3);
							
							// we should have time delay to recharge
							lastShoot = System.currentTimeMillis() + 700;
						}

						isMissileMoving = false;
						missiles.get(slotid).setInitPos();
						missiles.get(slotid).setState(false);
						break;
					}
				}
			}

			// execute player movement
//			unitMove um = players.get(i).popM();
			
			
			
			
			/* Execute movement */
			for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {
				
				if (players.get(i).getState()) {

					/* Execute missile movement */
					if (missiles.get(i).getState()) {
						
						missiles.get(i).accelerate(delta);
						missiles.get(i).move(delta);
					}

					/* Execute player movement */
					if (players.get(i).isAlive()) {
						
						players.get(i).executeUnitMove(delta);
						players.get(i).deAccelerate(delta);
						players.get(i).move(delta);
					}
				}
			}

			// draw everyone
			for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {
				
				if (players.get(i).getState()) {

					// missile draw
					if (missiles.get(i).getState()) {
						missiles.get(i).shiftedDraw(g, -mapX, -mapY);
//						System.out.println("m(" + missiles.get(i).getX() + ", " + missiles.get(i).getY() + ")");
					}
					/*
					 * Drawing entity needs some trick
					 * we always have their data about 150ms past
					 * move smoothly with that position
					 * 
					 */
					if(players.get(i).isAlive()) {
						players.get(i).shiftedDrawWithNick(g, -mapX, -mapY);
					}
						
				}
			}

			/* Position board */
			int boardX = WINDOW_SIZEX / 2 - 140;
			int boardY = 2;

			float alpha = 0.2f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            AlphaComposite alcomb = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
            g.setComposite(alcom);
            g.drawImage(bg_aqua, boardX, boardY, 280, 130, null);
			
            g.setComposite(alcomb);
			g.setColor(Color.BLACK);
			g.drawRect(boardX, boardY, 280, 130);
			
			// draw board
			for (int i = 0; i < AQUA_PLAYER_MAX_COUNT; i++) {
			
				if (players.get(i).getState()) {
					
					// missile draw board
					if (missiles.get(i).getState()) {
						
						switch(missiles.get(i).getTeamId()) {
						case 0:
							g.setColor(Color.RED);
							break;
						case 1:
							g.setColor(Color.BLUE);
							break;
						case 2:
							g.setColor(Color.BLACK);
							break;
						case 3:
							g.setColor(Color.GREEN);
							break;
						case 4:
							g.setColor(Color.ORANGE);
							break;
						case 5:
							g.setColor(Color.YELLOW);
							break;
						}
						g.fillRect(boardX + missiles.get(i).getX() * 280 / Client_Settings.MAP_SIZEX, boardY + missiles.get(i).getY() * 130 / Client_Settings.MAP_SIZEY, 4, 2);
					}

					if(players.get(i).isAlive()) {
						
						// player draw board
						switch(players.get(i).getTeamId()) {
						case 0:
							g.setColor(Color.RED);
							break;
						case 1:
							g.setColor(Color.BLUE);
							break;
						case 2:
							g.setColor(Color.BLACK);
							break;
						case 3:
							g.setColor(Color.GREEN);
							break;
						case 4:
							g.setColor(Color.ORANGE);
							break;
						case 5:
							g.setColor(Color.YELLOW);
							break;
						}
						g.fillRect(boardX + players.get(i).getX() * 280 / Client_Settings.MAP_SIZEX, boardY + players.get(i).getY() * 130 / Client_Settings.MAP_SIZEY, 9, 9);						
					}
				}
			}
			
			g.dispose();
			strategy.show();

			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			}
		}
	}

}
