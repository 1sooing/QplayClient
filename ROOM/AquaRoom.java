package ROOM;

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
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import MAIN.Client;
import UTILL.Client_Settings;
import UTILL.SoundEffect;
import UTILL.Sprite;
import UTILL.SpriteStore;

public class AquaRoom extends Canvas implements Runnable, KeyListener {

	DataOutputStream out;
	Client client;

	public static final int ROOM_PLAYER_MAX_COUNT = Client_Settings.ROOM_PLAYER_MAX_COUNT;

	/*
	 * Private settings
	 */
	private boolean gameRunning = true;
	private boolean alarm = true;
	
	/*
	 * Not necessarily
	 */
	public static int WINDOW_SIZEX = 1200;
	public static int WINDOW_SIZEY = 800;

	public Sprite bg_aqua = SpriteStore.get().getSprite("res/aquaroom.png");

	private BufferStrategy strategy;

	ArrayDeque<String> chatlist = new ArrayDeque<>();
	public ArrayList<RoomPlayer> players = new ArrayList<RoomPlayer>();

	int roomid = -1;
	int slotid = -1;
	int teamid = -1;
	int playerid = -1;
	String nick = "";
	int dresscode = 0;
	
	boolean f5;
	boolean canStart = false;
	JButton btn_ready = new JButton("READY");

	public AquaRoom(DataOutputStream out, Client c, int roomid, int slotid, int dresscode) {

		this.out = out;
		this.client = c;
		this.playerid = c.playerid;
		this.nick = c.nick;

		this.roomid = roomid;
		this.teamid = slotid;
		this.slotid = slotid;
		this.dresscode = dresscode;

		this.alarm = c.alarm;
		
		WINDOW_SIZEX = c.WINDOW_SIZEX;
		WINDOW_SIZEY = c.WINDOW_SIZEY;

		JPanel panel = (JPanel) client.frame.getContentPane();
		JTextArea area = new JTextArea();
		
		JButton btn_teamA = new JButton("Team A");
		JButton btn_teamB = new JButton("Team B");
		JButton btn_teamC = new JButton("Team C");
		JButton btn_teamD = new JButton("Team D");
		JButton btn_teamE = new JButton("Team E");
		JButton btn_teamF = new JButton("Team F");
		
		JButton btn_exit = new JButton("나가기");
		
		area.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					e.consume();
					String text = area.getText();
					if(!text.equals(""))
						writeAquaPlayerText(text);
					area.setText(null);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		});

		panel.setPreferredSize(new Dimension(WINDOW_SIZEX, WINDOW_SIZEY));
		panel.setLayout(null);
		
		setBounds(0, 0, WINDOW_SIZEX- 400, WINDOW_SIZEY - 55);
		area.setBounds(0, WINDOW_SIZEY - 55, WINDOW_SIZEX, 55);
		btn_ready.setBounds(WINDOW_SIZEX - 300, 100, 200, 120);
		
		btn_teamA.setBounds(WINDOW_SIZEX - 350, 250, 80, 80);
		btn_teamB.setBounds(WINDOW_SIZEX - 250, 250, 80, 80);
		btn_teamC.setBounds(WINDOW_SIZEX - 150, 250, 80, 80);
		btn_teamD.setBounds(WINDOW_SIZEX - 350, 350, 80, 80);
		btn_teamE.setBounds(WINDOW_SIZEX - 250, 350, 80, 80);
		btn_teamF.setBounds(WINDOW_SIZEX - 150, 350, 80, 80);
		
		btn_exit.setBounds(WINDOW_SIZEX - 300, 500, 200, 120);
		
		btn_teamA.setBackground(Color.RED);
		btn_teamB.setBackground(Color.BLUE);
		btn_teamC.setBackground(Color.BLACK);
		btn_teamD.setBackground(Color.GREEN);
		btn_teamE.setBackground(Color.ORANGE);
		btn_teamF.setBackground(Color.YELLOW);
		
		btn_teamA.setForeground(Color.WHITE);  
		btn_teamB.setForeground(Color.WHITE);
		btn_teamC.setForeground(Color.WHITE);
		btn_teamD.setForeground(Color.BLACK);
		btn_teamE.setForeground(Color.BLACK);
		btn_teamF.setForeground(Color.BLACK);
		 
		panel.add(this);
		panel.add(area, BorderLayout.SOUTH);
		
		panel.add(btn_ready, BorderLayout.EAST);
		
		panel.add(btn_teamA, BorderLayout.EAST);
		panel.add(btn_teamB, BorderLayout.EAST);
		panel.add(btn_teamC, BorderLayout.EAST);
		panel.add(btn_teamD, BorderLayout.EAST);
		panel.add(btn_teamE, BorderLayout.EAST);
		panel.add(btn_teamF, BorderLayout.EAST);
		
		panel.add(btn_exit, BorderLayout.EAST);
		
		btn_ready.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (slotid == 0) {
					if(canStart) {
						writeAquaStart();
					}	
				}
				
				else {
					if(!f5) {
						f5 = true;
						btn_ready.setSelected(true);
						btn_ready.setRolloverEnabled(true);
						writeAquaReady(true);
					} else {
						f5 = false;
						btn_ready.setSelected(false);
						btn_ready.setRolloverEnabled(false);
						writeAquaReady(false);
					}
				}
				
			}
		});

		btn_teamA.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5 && (teamid != 0)) {
					c.teamid = 0;
					teamid = 0;
					players.get(slotid).setTeamid(0);
					writeTeamid(0);
				}
			}
		});
		
		btn_teamB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5&& (teamid != 1)) {
					c.teamid = 1;
					teamid = 1;
					players.get(slotid).setTeamid(1);
					writeTeamid(1);	
				}
			}
		});
		
		btn_teamC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5 && (teamid != 2)) {
					c.teamid = 2;
					teamid = 2;
					players.get(slotid).setTeamid(2);
					writeTeamid(2);	
				}
			}
		});
		
		btn_teamD.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5 && (teamid != 3)) {
					c.teamid = 3;
					teamid = 3;
					players.get(slotid).setTeamid(3);
					writeTeamid(3);	
				}
			}
		});
		
		btn_teamE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5 && (teamid != 4)) {
					c.teamid = 4;
					teamid = 4;
					players.get(slotid).setTeamid(4);
					writeTeamid(4);	
				}
				
			}
		});
		
		btn_teamF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!f5 && (teamid != 5)) {
					c.teamid = 5;
					teamid = 5;
					players.get(slotid).setTeamid(5);
					writeTeamid(5);	
				}
			}
		});

		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeExitRoom();
			}
		});

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

		for (int i = 0; i < ROOM_PLAYER_MAX_COUNT; i++) {
			RoomPlayer rp = new RoomPlayer(this, -1, -1, "", 0, false);
			players.add(rp);
		}

		canStart = false;
		f5 = false;

		// if boss
		if(slotid == 0) {
			btn_ready.setRolloverEnabled(true);
			btn_ready.setText("start");
			btn_ready.setSelected(false);
		}
		enterAquaRoom();
	}
	
	public void enterAquaRoom() {
		RoomPlayer rp = new RoomPlayer(this, teamid, slotid, nick, dresscode, false);
		players.set(slotid, rp);
		players.get(slotid).setState(true);
	}
	
	public void removeGame() {
		gameRunning = false;
		client.frame.setVisible(false);
		removeKeyListener(this);
		
		players.clear();
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
		g.dispose();
		strategy.dispose();
	}
	
	public void enableStart(boolean start) {
		if (!start) {
			btn_ready.setSelected(false);
			btn_ready.setRolloverEnabled(false);
			canStart = false;
		} else if (start) {
			btn_ready.setSelected(true);
			btn_ready.setRolloverEnabled(true);
			canStart = true;	
		}
	}
	
	public void updateAquaRoomPlayerInfo(int slotid, int teamid, String nick, int dresscode, boolean ready) {
		
		/* New comer */
		if (slotid < 0) {
			slotid *= -1;
			
			if(alarm)
				SoundEffect.ENTERANCE.play();
		}
		
		RoomPlayer rp = new RoomPlayer(this, slotid, teamid, nick, dresscode, ready);
		players.set(slotid,  rp);
		players.get(slotid).setState(true);
	}
	
	public void updatePlayerText(int slotid, String text) {
		

		// case of emoticon
				if(text.charAt(0) == '/') {
					
					String[] parts = text.split(" ");
					if(parts.length == 1) {
						String emoticon = parts[0].substring(1, parts[0].length());
						
						if(emoticon.charAt(0) == '왕') {
							String emoticonBig = emoticon.substring(1, emoticon.length());
							
							switch(emoticonBig) {
							case "띵":
							case "메롱":
							case "부끄":
							case "빠직":
							case "뽀":
							case "삥글":
							case "썰렁":
							case "아잉":
							case "우씨":
							case "윙크":
							case "으악":
							case "으앙":
							case "윽":
							case "졸려":
							case "즐":
							case "침묵":
							case "사랑해":	
							case "컨닝":
							case "터프":
							case "하하":
							case "흐미":
							case "흥":
							case "히죽":
								players.get(slotid).setEmoticon(emoticonBig, true);
								break;
							}
						}
						
						switch(emoticon) {
						case "띵":
						case "메롱":
						case "부끄":
						case "빠직":
						case "뽀":
						case "삥글":
						case "썰렁":
						case "아잉":
						case "우씨":
						case "윙크":
						case "으악":
						case "으앙":
						case "윽":
						case "졸려":
						case "즐":
						case "침묵":
						case "사랑해":	
						case "컨닝":
						case "터프":
						case "하하":
						case "흐미":
						case "흥":
						case "히죽":
							players.get(slotid).setEmoticon(emoticon, false);
							break;
						}
					}
					
					return;
				}
	
		/* check if size exceeds 10 */
		while ( chatlist.size() > 10) {
			chatlist.removeLast();
		}
		chatlist.push("[" + players.get(slotid).getNick() + "] " + text);
	}
	
	public void updatePlayerExit(int slotid) {
		players.get(slotid).slotid = -1;
		players.get(slotid).setState(false);
	}
	
	public void updateReady(int slotid, boolean ready) {
		players.get(slotid).setReady(ready);
	}
	
	public void updateTeamid(int slotid, int teamid) {
		players.get(slotid).setTeamid(teamid);
	}
	
	public void dressUp(int slotid, int teamid) {
		players.get(slotid).setTeamid(teamid);
	}
	
	public void writeAquaRoomRequest() {
		String msg = "" + Client_Settings.SET_AQUA_ROOM_PLAYER_INFO + (char)007;

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
	
	public void writeExitRoom() {
		String msg = "" + Client_Settings.SET_EXIT_ROOM + (char)007;

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

	private void writeAquaPlayerText(String text) {

		/* Calculate the unicode */
		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(text);
		int count = 0;
		while (m.find()) {
			count++;
		}
		
		int length = text.length() + 2 * count;
		
		if (length > 84)
			return;
		
		/* 
		 * We consider to send msg through two packet 
		 * split the message in half 
		 */
		if (length > 42) {
			
			int index = text.length() / 2; 

			String msg1 = text.substring(0, index);
			String msg2 = text.substring(index);
	
			Matcher m1 = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(msg1);
			int count1 = 0;
			while (m1.find()) {
				count1++;
			}
			
			Matcher m2 = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(msg2);
			int count2 = 0;
			while (m2.find()) {
				count2++;
			}
			
			String msg1out = "" + Client_Settings.SET_AQUA_PLAYER_TEXT + (char)007 + "1" + (char)007 + msg1 + (char)007;
			String msg2out = "" + Client_Settings.SET_AQUA_PLAYER_TEXT + (char)007 + "2" + (char)007 + msg2 + (char)007;

			int length1 = 50 - msg1out.length() - count1 * 2;
			int length2 = 50 - msg2out.length() - count2 * 2;
			
			for (int i = 0; i < length1; i++) {
				msg1out += "X";
			}
			
			for (int i = 0; i < length2; i++) {
				msg2out += "X";
			}

			byte[] data1 = msg1out.getBytes(StandardCharsets.UTF_8);
			byte[] data2 = msg2out.getBytes(StandardCharsets.UTF_8);

			try {
				out.write(data1);
				out.write(data2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		/* Othercase, we send msg through single packet */
		String msg = "" + Client_Settings.SET_AQUA_PLAYER_TEXT + (char)007 + "0" + (char)007 + text + (char)007;

		length = 50 - msg.length() - count * 2;
		
		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);

		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeAquaReady(boolean ready) {
		String msg = "" + Client_Settings.SET_AQUA_READY + (char)007 + ready + (char)007;

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
	
	public void writeAquaStart() {
		String msg = "" + Client_Settings.SET_START + (char)007;

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
	
	public void writeTeamid(int teamid) {
		
		String msg = "" + Client_Settings.SET_AQUA_TEAM+ (char)007 + teamid + (char)007;

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
	
	@Override
	public void keyPressed(KeyEvent e) {
		int pushedkey = e.getKeyCode();

		switch (pushedkey) {

		case 116:
			if(!f5) {
				f5 = true;
				btn_ready.setSelected(true);
				writeAquaReady(true);
			} else {
				f5 = false;
				btn_ready.setSelected(false);
				writeAquaReady(false);
			}
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void run() {

		long lastLoopTime = System.currentTimeMillis();

		while (gameRunning) {

			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

//			client.checkKeepAlive(lastLoopTime);
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

			g.drawImage(bg_aqua.getImage(), 0, 0, getWidth(), getHeight(), this);

			// draw nick name and ready states
			for (int i = 0; i < ROOM_PLAYER_MAX_COUNT; i++) {
				
				if(players.get(i).getState())
					players.get(i).drawWithNickWithReady(g);
			}

			// draw every emoticons
						for (int i = 0; i < ROOM_PLAYER_MAX_COUNT; i++) {

							if (players.get(i).getState()) {
								if (players.get(i).checkTimeoutEmoticon(lastLoopTime)) {
									players.get(i).drawEmoticon(g);
								}
							}
						}
						
			/* Draw chatting window */
			String[] chatarray = chatlist.toArray(new String[0]);
			
			g.setColor(Color.WHITE);
			int chatlength = chatarray.length;
			
			for (int i = 0; i < chatlength; i++) {
				g.drawString(chatarray[i], 60, WINDOW_SIZEY - 57 - i * 20);
			}
			
			g.dispose();
			strategy.show();
			
			// finally pause for a bit. Note: this should run us at about
			// 100 fps but on windows this might vary each loop due to
			// a bad implementation of timer
			try {
				Thread.sleep(20);
			} catch (Exception e) {
			}
		}
	}

	private boolean writeData(byte[] data) {
		try {
			out.write(data);
		} catch (IOException e) {
			System.out.println("[*] writeData error - broken pipe");
			System.exit(1);
			return false;
		}
		return true;
	}
	
	private boolean writeMessage(byte[] data1, byte[] data2) {
		try {
			out.write(data1);
			out.write(data2);
		} catch (IOException e) {
			System.out.println("[*] writeMessage error - broken pipe");
			System.exit(1);
			return false;
		}
		return true;
	}
}
