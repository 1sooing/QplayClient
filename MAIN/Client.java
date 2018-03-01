package MAIN;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import AQUA.AquaGame;
import LOBBY.LobbyGame;
import ROOM.AquaRoom;
import UTILL.Client_Settings;
import UTILL.SoundEffect;

public class Client implements Runnable {

	public int latest_version = -1;
	public int my_version = Client_Settings.VERSION;
	
	/*
	 * Public settings
	 */
	private boolean loggedin = true;
	private static String SERVER_IP;
	private static int SERVER_PORT;
	private long recentAlive = System.currentTimeMillis();
	public int CURRENT_USERS = 0;
	
	/*
	 * Private settings
	 */
	private static int USER_STATUS = -1;

	public int WINDOW_SIZEX = 1000;
	public int WINDOW_SIZEY = 600;

	static Socket socket;
	static DataOutputStream out;
	static DataInputStream in;

	public static JFrame frame;

	public LobbyGame lg;
	public AquaRoom ar;
	public AquaGame ag;

	public Thread lobby_start;
	public Thread aquaroom_start;
	public Thread aqua_start;

	// DEFAULT
	public static int roomid = -1;
	public static int slotid = -1;
	public static int teamid = -1;
	public static int playerid = -1;
	public static String nick = "";
	public static int dresscode = -1;

	public boolean bgm_lobby = true, bgm_aqua = true, alarm = true;
	
	public Client(String serverip, int serverport, String nick) {
		this.SERVER_IP = serverip;
		this.SERVER_PORT = serverport;
		this.nick = nick;

		init();
	}

	public void init() {

		// INIT CONNECTION
		try {
			socket = new Socket(SERVER_IP, SERVER_PORT);
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			logoutAlarm();
		}

		// INIT FRAME
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		WINDOW_SIZEX = (int) screenSize.getWidth();
		WINDOW_SIZEY = (int) screenSize.getHeight();

		frame = new JFrame();

		// frame settings like size, close operation etc.
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setSize(WINDOW_SIZEX, WINDOW_SIZEY);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setVisible(true);
		
		SoundEffect.init();
	}

	// Enter Lobby
	public void enterLobby() {

		switch (USER_STATUS) {
		case 2:
			aqua_start.interrupt();
			ag.removeGame();
			frame.getContentPane().removeAll();
			break;
		case 3:
			aquaroom_start.interrupt();
			ar.removeGame();
			frame.getContentPane().removeAll();
			break;
		}

		USER_STATUS = 1;

		lg = new LobbyGame(this, out);
		lobby_start = new Thread(lg);
		lobby_start.start();
		System.out.println("enterLobby()");
	}

	public void enterAquaRoom() {

		switch (USER_STATUS) {
		case 1:
			lobby_start.interrupt();
			lg.removeGame();
			frame.getContentPane().removeAll();
			break;
		case 2:
			aqua_start.interrupt();
			ag.removeGame();
			frame.getContentPane().removeAll();
			break;
		}

		USER_STATUS = 3;

		ar = new AquaRoom(out, this, roomid, slotid, dresscode);
		aquaroom_start = new Thread(ar);
		aquaroom_start.start();
//		System.out.println("enterAquaRoom(), slot: " + slotid + ", teamid: " + teamid);
	}

	// Enter Aqua
	public void enterAqua(int roomid, int slotid) {

		switch (USER_STATUS) {
		case 1:
			lobby_start.interrupt();
			lg.removeGame();
			frame.getContentPane().removeAll();
			break;
		case 3:
			aquaroom_start.interrupt();
			ar.removeGame();
			frame.getContentPane().removeAll();
			break;
		}

		USER_STATUS = 2;

		ag = new AquaGame(out, this, roomid, slotid);

		aqua_start = new Thread(ag);
		aqua_start.start();

		System.out.println("enterAqua()");
	}

	private String readData()
	/*
	 * Read a message in the form "<length> msg". The length allows us to know
	 * exactly how many bytes to read to get the complete message. Only the
	 * message part (msg) is returned, or null if there's been a problem.
	 */
	{
		byte[] data = null;

		try {
			data = new byte[50];
			int len = 0;
			// read the message, perhaps requiring several read() calls
			while (len != data.length) {
				int ch = in.read(data, len, data.length - len);

				if (ch == -1) {
					return null;
				}
				len += ch;
			}
		} catch (IOException e) {
			return null;
		}

		return new String(data, StandardCharsets.UTF_8).trim();
	}

	// Handle received from server
	public void run() {

		int roomidin;
		String roomnamein = "";
		int slotidin = -1;
		int playeridin = -1;
		int teamidin;
		String nickin = "";
		int dresscodein = 0;

		int direction;
		boolean movement;
		double velocity = 0;

		long delta;
		double acc;
		double accx;
		double accy;

		int xin = 0;
		int yin = 0;

		int xmissilein;
		int ymissilein;

		double vel_missile_x = 0;
		double vel_missile_y = 0;
		int missiletype;
		int hpin;

		String text1 = "";
		String text2 = "";
		int concat = -1;
		long timein = -1;
		
		boolean readyin = false;
		
		// RUN UNTIL EVERYTHING ENDED
		while (true) {

			String line = readData();

			if (line == null)
				continue;

			String contents[] = line.split(Character.toString((char) 007));

			// GET PLAYER PROFILE FROM SERVER
			int mode = Integer.parseInt(contents[0]);

			switch (USER_STATUS) {

			/*
			 * user in aqua
			 */
			case 2:
				switch (mode) {
//				case Client_Settings.SET_DEBUG_TIME:
//					timein = Long.parseLong(contents[1]);
//					System.out.println("Client elapsed scale: " + (System.currentTimeMillis() - timein) / 10);
//					break;
					
//				case Client_Settings.SET_KEEP_ALIVE:
//					timein = Long.parseLong(contents[1]);
//					setRecentAlive();
//					writeKeepAlive(timein);
////					System.out.println("received alive");
//					break;
					
//				case Client_Settings.SET_AQUA_PLAYER_POS:
//					 slotidin = Integer.parseInt(contents[1]);
//					 long delta2 = Long.parseLong(contents[2]);
//					 xin = Integer.parseInt(contents[3]);
//					 yin = Integer.parseInt(contents[4]);
//					 ag.updatePlayerPosition(slotidin, delta2, xin, yin);
//				 break;
				 
				case Client_Settings.SET_AQUA_PLAYER_MOVE:
					slotidin = Integer.parseInt(contents[1]);
					movement = Boolean.parseBoolean(contents[2]);
					
					acc = Double.parseDouble(contents[3]);
					delta = Long.parseLong(contents[4]);
					
					ag.updatePlayerMove(slotidin, movement, acc, delta);
					break;
					
				case Client_Settings.SET_AQUA_MISSILE_MOVE:
					slotidin = Integer.parseInt(contents[1]);
					teamidin = Integer.parseInt(contents[2]);
					missiletype = Integer.parseInt(contents[3]);
					xmissilein = (int) Double.parseDouble(contents[4]);
					ymissilein = (int) Double.parseDouble(contents[5]);
					vel_missile_x = Double.parseDouble(contents[6]);
					vel_missile_y = Double.parseDouble(contents[7]);

					ag.updateMissileMove(slotidin, teamidin, missiletype, xmissilein, ymissilein, vel_missile_x,
							vel_missile_y);
					break;
					
				case Client_Settings.SET_AQUA_HIT:
					int slotidx = Integer.parseInt(contents[1]);
					slotidin = Integer.parseInt(contents[2]);
					hpin = Integer.parseInt(contents[3]);
					
					accx = Double.parseDouble(contents[4]);
					accy = Double.parseDouble(contents[5]);
					
					ag.updatePlayerHp(slotidx, slotidin, hpin);
					ag.updatePlayerHitMove(slotidin, accx, accy);
					ag.missiles.get(slotidx).setState(false);
					break;
				
				 

				case Client_Settings.SET_AQUA_ENTERANCE:
					slotidin = Integer.parseInt(contents[1]);
					teamidin = Integer.parseInt(contents[2]);
					nickin = contents[3];
					dresscodein = Integer.parseInt(contents[4]);
					xin = Integer.parseInt(contents[5]);
					yin = Integer.parseInt(contents[6]);

					ag.updateAquaGamePlayerInfo(slotidin, teamidin, nickin, dresscodein, (int) xin, (int) yin);
					break;

				case Client_Settings.SET_EXIT_AQUA:
					slotidin = Integer.parseInt(contents[1]);
					ag.updatePlayerExit(slotidin);
					break;

				case Client_Settings.SET_AQUA_ROOM_ENTER_ACCEPT:
					enterAquaRoom();
					break;

				case Client_Settings.SET_AQUA_GAME_RESULT:
					boolean victory = Boolean.parseBoolean(contents[1]);
					ag.gameEnded(victory);
					break;
					
				case Client_Settings.SET_LOBBY_ENTER_ACCEPT:
					enterLobby();
					break;

				}
				break;
			
			
			/*
			 * not identified user
			 */
			case -1:
				switch (mode) {
//				case Client_Settings.SET_KEEP_ALIVE:
//					
////					window.Say("[*] receive keep alive");
//					
//					timein = Long.parseLong(contents[1]);
//					setRecentAlive();
//					writeKeepAlive(timein);
//					break;
				
				case Client_Settings.SET_ID:

					/*
					 * First got playerid
					 */
//					window.Say("[*] receive ID from server");
					
					playerid = Integer.parseInt(contents[1]);
					dresscode = playerid;
					
					writeLobbyEnterRequest(nick, Client_Settings.VERSION);
					break;
					
				case Client_Settings.SET_VERSION:
					latest_version = Integer.parseInt(contents[1]);
					
					if(latest_version == my_version) {
						
					}
					else {
						versionAlarm();
					}
					break;
					
				case Client_Settings.SET_LOBBY_ENTER_ACCEPT:
					
					USER_STATUS = 1;
					enterLobby();
					break;
					
				case Client_Settings.SET_CURRENT_USER:
					int cnt = Integer.parseInt(contents[1]);
					CURRENT_USERS = cnt;
					break;

				}
				break;

			/*
			 * user in lobby
			 */
			case 1:
				switch (mode) {
//				case Client_Settings.SET_KEEP_ALIVE:
//					timein = Long.parseLong(contents[1]);
//					setRecentAlive();
//					writeKeepAlive(timein);
//					break;
				
				case Client_Settings.SET_LOBBY_PLAYER_MOVE:
					playeridin = Integer.parseInt(contents[1]);

					if (playeridin != playerid) {
						direction = Integer.parseInt(contents[2]);
						
						lg.players.get(playeridin).push(direction);
//						lg.updatePlayerMove(playeridin, direction);
					}
					break;

//				case Client_Settings.SET_LOBBY_PLAYER_POS:
//					xin = Integer.parseInt(contents[1]);
//					yin = Integer.parseInt(contents[2]);
//					lg.updatePlayerPosition(playeridin, xin, yin);
//					break;

				case Client_Settings.SET_LOBBY_PLAYER_TEXT:
					playeridin = Integer.parseInt(contents[1]);
					concat = Integer.parseInt(contents[2]);
					text1 = contents[3];
					
					switch(concat) {
					case 0:
						lg.updatePlayerBalloon(playeridin, text1);
						break;
					case 1:
						text2 = text1;
						break;
					case 2:
						text2 += text1;
						lg.updatePlayerBalloon(playeridin, text2);
						text2 = "";
						break;
					}
					break;

				case Client_Settings.SET_LOBBY_ENTRANCE:
					
					playeridin = Integer.parseInt(contents[1]);
					nickin = contents[2];
					dresscodein = Integer.parseInt(contents[3]);
					xin = Integer.parseInt(contents[4]);
					yin = Integer.parseInt(contents[5]);

					lg.updateLobbyPlayerInfo(playeridin, nickin, dresscodein, (int) xin, (int) yin);
					break;

				case Client_Settings.SET_AQUA_ROOM_INFO:
					roomidin = Integer.parseInt(contents[1]);
					roomnamein = contents[2];
					int capacity = Integer.parseInt(contents[3]);
					
					lg.updateAquaRoomInfo(roomidin, roomnamein, capacity);
					break;

				case Client_Settings.SET_LOBBY_DRESSCODE:
					playeridin = Integer.parseInt(contents[1]);
					dresscodein = Integer.parseInt(contents[2]);
					lg.updatePlayerDresscode(playeridin, dresscodein);
					break;
					
				case Client_Settings.SET_AQUA_ROOM_ENTER_ACCEPT:

					/*
					 * Set roomid Set slotid
					 */
					roomid = Integer.parseInt(contents[1]);
					slotid = Integer.parseInt(contents[2]);
					teamid = slotid;
					
					enterAquaRoom();
					break;

				case Client_Settings.SET_NOTICE:
					
					concat = Integer.parseInt(contents[1]);
					text1 = contents[2];
					
					switch(concat) {
					case 0:
						lg.updateNotice(text1);
						break;
					case 1:
						text2 = text1;
						break;
					case 2:
						text2 += text1;
						lg.updateNotice(text2);
						text2 = "";
						break;
					}
					break;
					
				case Client_Settings.SET_EXIT_LOBBY:
					playeridin = Integer.parseInt(contents[1]);
					lg.updatePlayerExit(playeridin);
					break;
					
				case Client_Settings.SET_CURRENT_USER:
					int cnt = Integer.parseInt(contents[1]);
					CURRENT_USERS = cnt;
					lg.updateCurrent(CURRENT_USERS);
					break;

				}
				break;

			/*
			 * user in aquaroom
			 */
			case 3:
				switch (mode) {
//				case Client_Settings.SET_KEEP_ALIVE:
//					timein = Long.parseLong(contents[1]);
//					setRecentAlive();
//					writeKeepAlive(timein);
//					break;
				
				case Client_Settings.SET_AQUA_READY:
					slotidin = Integer.parseInt(contents[1]);
					readyin = Boolean.parseBoolean(contents[2]);
					ar.updateReady(slotidin, readyin);
					break;

				case Client_Settings.SET_AQUA_TEAM:
					slotidin = Integer.parseInt(contents[1]);
					teamidin = Integer.parseInt(contents[2]);
					ar.updateTeamid(slotidin, teamidin);
					break;
					
				case Client_Settings.SET_ENABLE_START:
					boolean start = Boolean.parseBoolean(contents[1]);
					ar.enableStart(start);
					break;
					
				case Client_Settings.SET_AQUA_PLAYER_TEXT:
					slotidin = Integer.parseInt(contents[1]);
					concat = Integer.parseInt(contents[2]);
					text1 = contents[3];
					
					switch(concat) {
					case 0:
						ar.updatePlayerText(slotidin, text1);
						break;
					case 1:
						text2 = text1;
						break;
					case 2:
						text2 += text1;
						ar.updatePlayerText(slotidin, text2);
						text2 = "";
						break;
					}
					break;
					
				case Client_Settings.SET_START:
					enterAqua(roomid, slotid);
					break;

				case Client_Settings.SET_EXIT_ROOM:
					slotidin = Integer.parseInt(contents[1]);
					ar.updatePlayerExit(slotidin);
					break;

				case Client_Settings.SET_AQUA_ROOM_PLAYER_INFO:
					slotidin = Integer.parseInt(contents[1]);
					teamidin = Integer.parseInt(contents[2]);
					nickin = contents[3];
					dresscodein = Integer.parseInt(contents[4]);
					readyin = Boolean.parseBoolean(contents[5]);

					ar.updateAquaRoomPlayerInfo(slotidin, teamidin, nickin, dresscodein, readyin);
					break;
					
				case Client_Settings.SET_LOBBY_ENTER_ACCEPT:
					enterLobby();
					break;

				}
				break;

			} // end of switch(USER_STATUS)

		} // end of while(true)

	} // end of run()

	
	
	public void writeLobbyEnterRequest(String nick, int VERSION) {
		String msg = "" + Client_Settings.SET_LOBBY_ENTER_REQUEST + (char) 007 + nick  + (char) 007 + VERSION + (char) 007;

		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(nick);
		int count = 0;
		while (m.find()) {
			count++;
		}

		int length = 50 - msg.length() - count * 2;

		for (int i = 0; i < length; i++)
			msg += "X";

		byte[] data = msg.getBytes(StandardCharsets.UTF_8);

		try {
			out.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeKeepAlive (long time) {
		String msg = "" + Client_Settings.SET_KEEP_ALIVE + (char) 007 + time + (char) 007;

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
	
//	public void say(String text) {
//		System.out.println(text);
//	}

	private void logoutAlarm() {
		JFrame f = new JFrame("User connection lost");
		
		JLabel lb_title = new JLabel("서버와의 접속이 끊겼습니다.");
		JLabel lb_title_english = new JLabel("User connection lost");
		JButton btn_ok = new JButton("확인");
		
		int width = 300;
		int height = 200;

		f.setSize(width, height);
		f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
		f.setResizable(false);
		
		f.setTitle("Connection lost");
		f.setLayout(null);
		f.add(lb_title);
		f.add(lb_title_english);
		f.add(btn_ok);

		lb_title.setBounds(65, 10, 200, 30);
		lb_title_english.setBounds(75, 50, 200, 30);
		btn_ok.setBounds(130, 100, 60, 40);

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.close();
				} catch (IOException e1) {
				}
				f.setVisible(false);
				f.dispose();
				System.exit(0);
			}
		});

		f.setAlwaysOnTop(true);
		f.setVisible(true);
	}
	
	private void versionAlarm() {
		JFrame f = new JFrame("Alert Version");
		
		JLabel lb_title = new JLabel("불가피하게 버전업이 필요할 때만 나옵니다^^");
		
		JLabel lb_latest_version = new JLabel("최신 버전: " + latest_version);
		JLabel lb_my_version = new JLabel("내 버전: " + my_version);
		JButton btn_URL = new JButton("Click to download (Google Drive)");
		JButton btn_ok = new JButton("확인");
		
		int width = 300;
		int height = 400;

		f.setSize(width, height);
		f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
		f.setResizable(false);
		
		f.setTitle("Qplay Version verification");
		f.setLayout(null);
		f.add(lb_title);
		f.add(lb_latest_version);
		f.add(lb_my_version);
		f.add(btn_URL);
		f.add(btn_ok);

		lb_title.setForeground(Color.BLUE);
		lb_my_version.setForeground(Color.RED);
		
		lb_title.setBounds(10, 10, 280, 30);
		lb_latest_version.setBounds(65, 60, 200, 30);
		lb_my_version.setBounds(75, 100, 200, 30);
		btn_URL.setBounds(20, 160, 260, 80);
		btn_ok.setBounds(120, 300, 60, 40);

		btn_URL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				URL url = null;
				try {
					url = new URL("https://drive.google.com/open?id=0BzwzEKRERCb7Q3dfUnJqb3J6UXc");
				} catch (MalformedURLException e1) {}
				openWebpage(url);
				f.setVisible(false);
				f.dispose();
				System.exit(0);
			}
		});
		
		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					out.close();
				} catch (IOException e1) {}
				f.setVisible(false);
				f.dispose();
				System.exit(0);
			}
		});

		f.setAlwaysOnTop(true);
		f.setVisible(true);
	}
	
	public static void openWebpage(URI uri) {
	    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
	    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
	        try {
	            desktop.browse(uri);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	public static void openWebpage(URL url) {
	    try {
	        openWebpage(url.toURI());
	    } catch (URISyntaxException e) {
	        e.printStackTrace();
	    }
	}
	
//	public void checkKeepAlive(long currenttime) {
//		
//		/* 
//		 * User not responding during timeout
//		 * consider these users as logged out
//		 * current timeout is 5000 ms (5 sec)
//		 */
//		if(currenttime - recentAlive > Client_Settings.USER_ALIVE_TIMEOUT) {
//			if(loggedin == true) {
//				logoutAlarm();
//				loggedin = false;
//			}
//		}
//	}
//	public void setRecentAlive() {
//		this.recentAlive = System.currentTimeMillis();
//	}
}