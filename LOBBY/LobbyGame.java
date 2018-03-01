package LOBBY;

import java.awt.AlphaComposite;
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
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import MAIN.Client;
import UTILL.Client_Settings;
import UTILL.SoundEffect;
import UTILL.Sprite;
import UTILL.SpriteStore;

public class LobbyGame extends Canvas implements Runnable, KeyListener {

	DataOutputStream out;

	Client client;

	public static final int LOBBY_PLAYER_MAX_COUNT = Client_Settings.LOBBY_PLAYER_MAX_COUNT;

	public static final int PLAYER_SIZEX = Client_Settings.LOBBY_PLAYER_SIZEX;
	public static final int PLAYER_SIZEY = Client_Settings.LOBBY_PLAYER_SIZEX;

	/*
	 * Private settings
	 */
	public boolean gameRunning = true;
	private static final int LOBBY_PLAYER_MAX_SPEED = Client_Settings.LOBBY_PLAYER_MAX_SPEED;

	JLabel lb_board_tmp = new JLabel("공 지 사 항");
	JLabel lb_current = new JLabel("접속중: ");
	
	ArrayList<JButton> roomlist = new ArrayList<JButton>();
	ArrayList<JButton> profilelist = new ArrayList<JButton>();
	ArrayDeque<String> chatlist = new ArrayDeque<>();	
	
	/*
	 * Not necessarily
	 */
	static int WINDOW_SIZEX = 1200;
	static int WINDOW_SIZEY = 800;
	public Sprite bg_lobby = SpriteStore.get().getSprite("res/lobby.png");

	public BufferStrategy strategy;

	private int playerid = -1;
	private String nick = "";
	private int dresscode = 0;
	double x = -1;
	double y = -1;
	
	static boolean left, down, right, up;
	static boolean bgm_lobby, bgm_aqua, alarm;

	public ArrayList<LobbyPlayer> players = new ArrayList<LobbyPlayer>();

	public LobbyGame(Client c, DataOutputStream out) {

		this.out = out;
		this.client = c;
		this.nick = c.nick;
		this.playerid = c.playerid;
		this.dresscode = Client.dresscode;

		this.bgm_lobby = c.bgm_lobby;
		this.bgm_aqua = c.bgm_aqua;
		this.alarm = c.alarm;
		updateCurrent(c.CURRENT_USERS);
		
		WINDOW_SIZEX = c.WINDOW_SIZEX;
		WINDOW_SIZEY = c.WINDOW_SIZEY;

		JPanel panel = (JPanel) client.frame.getContentPane();
		JTextArea area = new JTextArea();

//		JButton btn_friends = new JButton("Friends");
		JButton btn_credits = new JButton("Makers");
		
		JButton btn_settings = new JButton("Settings");
		
		JButton btn_board = new JButton("Board");
		
		JButton btn_show_room = new JButton("방목록");
		JButton btn_create_room = new JButton("방만들기");
		JButton btn_dress = new JButton("변신!");
		JButton btn_exit = new JButton("종료");
		
		area.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {

					e.consume();
					String text = area.getText();
					
					if(!text.equals(""))
						writeLobbyPlayerText(text);
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
		
		btn_credits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Makers");
				f.setBackground(Color.BLACK);
				f.setForeground(Color.BLACK);
				
				JLabel lb_remember = new JLabel("\"Never ending aqua story\"");
				
				JLabel lb_title = new JLabel("[ Thanks to contributors ]");
				JLabel lb_PM = new JLabel("[ PM ]");
				JLabel lb_sounds = new JLabel("[ Sounds ]");
				JLabel lb_graphics = new JLabel("[ Graphics ]");
				JLabel lb_algorithms = new JLabel("[ Algorithms & Coder ]");
				
				/* Detailed users */
				JLabel lb_PM_sooHan = new JLabel("Project Manager - sooHan");
				JLabel lb_Sounds_lobby = new JLabel("Lobby BGM - Nexon's - ollaolla");
				JLabel lb_Sounds_aqua = new JLabel("Aqua BGM - Nexon's - Aqua");
				JLabel lb_Graphics_aqua = new JLabel("Aqua background - sweetrain");
				JLabel lb_Graphics_aqua2 = new JLabel("Aqua submarine - sooHan");
				
				JLabel lb_Algorithms_server = new JLabel("Algorithm of server - sooHan");
				JLabel lb_Algorithms_client = new JLabel("Algorithm of client - sooHan");
				JLabel lb_Coder = new JLabel("Coder - sooHan");
				
				/* Contact me */
				JLabel lb_contributes0 = new JLabel("Services are all free unless my financial states are good enough.");
				JLabel lb_contributes1 = new JLabel("It' will be a lot of help for Qplay server to donate if you want.");
				JLabel lb_contributes2 = new JLabel("Funds will be used for improvement of server capacity and quality of service.");
				JLabel lb_contributes_core = new JLabel("Shinhan 110-292-069475 Hanjoongsoo");
				JLabel lb_contributes_final1 = new JLabel("contact: jameshan9212@gmail.com");
				JLabel lb_contributes_final2 = new JLabel("Official blog(Korean): http://jameshan92.blog.me");
				JLabel lb_contributes_final3 = new JLabel("Official blog(English): https://sites.goolge.com/view/soohan");
				
				JButton btn_exit = new JButton("EXIT");
				
				int width = 1200;
				int height = WINDOW_SIZEY - 150;

				f.setSize(width, height);
				f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
				f.setResizable(false);
				
				f.setTitle("Makers");

				f.setLayout(null);
				f.add(lb_remember);
				f.add(lb_title);

				f.add(lb_PM);
				f.add(lb_PM_sooHan);
				
				f.add(lb_sounds);
				f.add(lb_Sounds_lobby);
				f.add(lb_Sounds_aqua);
				
				f.add(lb_graphics);
				f.add(lb_Graphics_aqua);
				f.add(lb_Graphics_aqua2);
				
				f.add(lb_algorithms);
				f.add(lb_Algorithms_server);
				f.add(lb_Algorithms_client);
				
				f.add(lb_Coder);
				
				f.add(lb_contributes0);
				f.add(lb_contributes1);
				f.add(lb_contributes2);
				f.add(lb_contributes_core);
				f.add(lb_contributes_final1);
				f.add(lb_contributes_final2);
				f.add(lb_contributes_final3);
				
				f.add(btn_exit);

				int line_height = 30;
				int between_title = line_height + 20;
				int between_line = line_height;

				/* Line starts */
				int between_line_sum = 10;
				
				lb_remember.setBounds(width / 2 - 80, between_line_sum, 500, line_height);
				between_line_sum += between_title;
				
				lb_title.setBounds(width / 2 - 70, between_line_sum, 500, line_height);
				between_line_sum += between_title;
				between_line_sum += 5;
				
				// PM
				lb_PM.setBounds(30, between_line_sum, 300, line_height);
				between_line_sum += between_line;
				
				lb_PM_sooHan.setBounds(80, between_line_sum, 300, line_height);
				between_line_sum += between_title;
				
				// SOUND AND GRAPHICS AND ALGORITHMS
				lb_sounds.setBounds(80, between_line_sum, 300, line_height);
				lb_graphics.setBounds(480, between_line_sum, 300, line_height);
				lb_algorithms.setBounds(830, between_line_sum, 300, line_height);
				between_line_sum += between_line;

				
				lb_Sounds_lobby.setBounds(130, between_line_sum, 300, line_height);
				lb_Graphics_aqua.setBounds(530, between_line_sum, 300, line_height);
				lb_Algorithms_server.setBounds(880, between_line_sum, 300, line_height);
				between_line_sum += between_line;
				
				lb_Sounds_aqua.setBounds(130, between_line_sum, 300, line_height);
				lb_Graphics_aqua2.setBounds(530, between_line_sum, 300, line_height);
				lb_Algorithms_client.setBounds(880, between_line_sum, 300, line_height);
				between_line_sum += between_title;
				between_line_sum += between_title;
				
				// FUNDS
				lb_contributes0.setBounds(427, between_line_sum, 500, line_height);
				between_line_sum += between_line;
				
				lb_contributes1.setBounds(427, between_line_sum, 500, line_height);
				between_line_sum += between_line;
				
				lb_contributes2.setBounds(427, between_line_sum, 500, line_height);
				between_line_sum += between_title;
				
				lb_contributes_core.setBounds(517, between_line_sum, 500, line_height);
				between_line_sum += between_line;
				
				lb_contributes_final1.setBounds(517, between_line_sum, 500, line_height);
				between_line_sum += between_title;
				
				lb_contributes_final2.setBounds(475, between_line_sum, 500, line_height);
				between_line_sum += between_line;
				
				lb_contributes_final3.setBounds(475, between_line_sum, 500, line_height);
				between_line_sum += between_line;
				
				
				btn_exit.setBounds(width / 2 - 50, WINDOW_SIZEY - 250, 100, 50);

				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							f.setVisible(false);
							f.dispose();
					}
				});

				f.setAlwaysOnTop(true);
				f.setVisible(true);
			}
		});
		
		btn_settings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("Settings");
				
				JLabel lb_title = new JLabel("Settings");
				JLabel lb_bgmlobby = new JLabel("BGM-lobby");
				JLabel lb_bgmaqua = new JLabel("BGM-aqua");
				JLabel lb_alarm = new JLabel("Alarm");

				JButton btn_bgmlobby = new JButton("ON");
				JButton btn_bgmaqua = new JButton("ON");
				JButton btn_alarm = new JButton("ON");
				
				JButton btn_exit = new JButton("나가기");
				
				int width = 400;
				int height = 400;

				f.setSize(width, height);
				f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
				f.setResizable(false);
				
				f.setTitle("Aqua");
				
				f.setLayout(null);
				f.add(lb_title);
				f.add(lb_bgmlobby);
				f.add(btn_bgmlobby);
				f.add(lb_bgmaqua);
				f.add(btn_bgmaqua);
				f.add(lb_alarm);
				f.add(btn_alarm);
				f.add(btn_exit);

				lb_title.setBounds(165, 10, 100, 60);
				lb_bgmlobby.setBounds(10, 80, 80, 40);
				btn_bgmlobby.setBounds(120, 80, 150, 40);
				
				lb_bgmaqua.setBounds(10, 140, 80, 40);
				btn_bgmaqua.setBounds(120, 140, 150, 40);
				
				lb_alarm.setBounds(10, 200, 80, 40);
				btn_alarm.setBounds(120, 200, 150, 40);
				
				btn_exit.setBounds(150, 300, 90, 40);

				if(bgm_lobby)
					btn_bgmlobby.setText("ON");
				else
					btn_bgmlobby.setText("OFF");
				
				if(bgm_aqua)
					btn_bgmaqua.setText("ON");
				else
					btn_bgmaqua.setText("OFF");
				
				if(alarm)
					btn_alarm.setText("ON");
				else
					btn_alarm.setText("OFF");
				
				btn_bgmlobby.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(bgm_lobby == true) {
							btn_bgmlobby.setText("OFF");
							bgm_lobby = false;
							c.bgm_lobby = false;
							SoundEffect.BGM_LOBBY.stop();
						}
						else {
							btn_bgmlobby.setText("ON");
							bgm_lobby = true;
							c.bgm_lobby = true;
						}
				}
			});
				
				btn_bgmaqua.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(bgm_aqua == true) {
							btn_bgmaqua.setText("OFF");
							bgm_aqua = false;
							c.bgm_aqua = false;
						}
						else {
							btn_bgmaqua.setText("ON");
							bgm_aqua = true;
							c.bgm_aqua = true;
						}
				}
			});
				
				btn_alarm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(alarm == true) {
							btn_alarm.setText("OFF");
							alarm = false;
							c.alarm = false;
						}
						else {
							btn_alarm.setText("ON");
							alarm = true;
							c.alarm = true;
						}
				}
			});
				
				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							f.setVisible(false);
							f.dispose();
					}
				});

				f.setAlwaysOnTop(true);
				f.setVisible(true);
			}
		});
		
		btn_board.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("유저 프로필");
				
				JLabel lb_title = new JLabel("");
				JButton btn_exit = new JButton("나가기");

				int width = 400;
				int height = 600;

				f.setSize(width, height);
				f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
				f.setResizable(false);
				
				f.setTitle("Board");
				lb_title.setText("List");
				
				f.setLayout(null);
				f.add(lb_title);
				
				// READ PROFILE INFO
				writeNoticeInfo();
				
				for(int i = 0; i < 5; i++) {
					f.add(profilelist.get(2 * i));
					f.add(profilelist.get(1 + 2 * i));

					profilelist.get(2 * i).setBounds(20, 80 + 60 * i, 160, 50);
					roomlist.get(2 * i + 1).setBounds(220, 80 + 60 * i, 160, 50);
					
					// basic listener dispose when clicked
					profilelist.get(2 * i).addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								f.setVisible(false);
								f.dispose();
						}
					});
					
					profilelist.get(1 + 2 * i).addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								f.setVisible(false);
								f.dispose();
						}
					});
					
				}
				
				f.add(btn_exit);
				lb_title.setBounds(185, 10, 220, 50);
				btn_exit.setBounds(155, 470, 90, 40);
				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							f.setVisible(false);
							f.dispose();
					}
				});

				f.setVisible(true);
			}
		});

		
		btn_create_room.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("방만들기");
				
				JLabel lb_title = new JLabel("만들기");
				JLabel lb_roomname = new JLabel("이름");
				JLabel lb_capacity = new JLabel("인원");
				JLabel lb_password = new JLabel("PW");

				JTextField tf_roomname = new JTextField();
				JTextField tf_capacity = new JTextField();
				JTextField tf_password = new JTextField();
				
				JButton btn_create = new JButton("생성");
				JButton btn_exit = new JButton("나가기");
				
				int width = 400;
				int height = 600;

				f.setSize(width, height);
				f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
				f.setResizable(false);
				
				f.setTitle("Aqua");
				
				f.setLayout(null);
				f.add(lb_title);
				f.add(lb_roomname);
				f.add(tf_roomname);
				f.add(lb_capacity);
				f.add(tf_capacity);
				f.add(lb_password);
				f.add(tf_password);
				f.add(btn_create);
				f.add(btn_exit);

				lb_title.setBounds(190, 30, 100, 60);
				lb_roomname.setBounds(30, 80, 40, 40);
				tf_roomname.setBounds(120, 80, 150, 40);
				
				lb_capacity.setBounds(30, 140, 40, 40);
				tf_capacity.setBounds(120, 140, 150, 40);
				
				lb_password.setBounds(30, 200, 40, 40);
				tf_password.setBounds(120, 200, 150, 40);
				
				btn_create.setBounds(90, 470, 90, 40);
				btn_exit.setBounds(220, 470, 90, 40);

				btn_create.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						String roomname = "";
						int capacity = 6;
						String password = "";
						
						roomname = tf_roomname.getText();
						writeAquaRoomCreateRequest(roomname, capacity, password);
						
						f.setVisible(false);
						f.dispose();
					}
				});

				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							f.setVisible(false);
							f.dispose();
					}
				});

				f.setAlwaysOnTop(true);
				f.setVisible(true);
			}
		});
		
		btn_show_room.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame f = new JFrame("목록");
				
				JLabel lb_title = new JLabel("");
//				JButton btn_roomenter = new JButton("입장");
				JButton btn_exit = new JButton("나가기");

				int width = 400;
				int height = 600;

				f.setSize(width, height);
				f.setLocation((WINDOW_SIZEX - width) / 2, (WINDOW_SIZEY - height) / 2);
				f.setResizable(false);
				
				f.setTitle("Aqua");
				lb_title.setText("List");
				
				f.setLayout(null);
				f.add(lb_title);
				
				// READ ROOM INFO
				writeRequestRoomInfo();
				
				for(int i = 0; i < 5; i++) {
					f.add(roomlist.get(2 * i));
					f.add(roomlist.get(1 + 2 * i));

					roomlist.get(2 * i).setBounds(20, 80 + 60 * i, 160, 50);
					roomlist.get(2 * i + 1).setBounds(220, 80 + 60 * i, 160, 50);
					
					// basic listener dispose when clicked
					roomlist.get(2 * i).addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								f.setVisible(false);
								f.dispose();
						}
					});
					
					roomlist.get(1 + 2 * i).addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
								f.setVisible(false);
								f.dispose();
						}
					});
					
				}
				
//				f.add(btn_roomenter);
				f.add(btn_exit);

				lb_title.setBounds(185, 10, 220, 50);
//				
//				btn_roomenter.setBounds(90, 470, 90, 40);
				btn_exit.setBounds(155, 470, 90, 40);

				btn_exit.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
							f.setVisible(false);
							f.dispose();
					}
				});

				f.setVisible(true);
			}
		});

		btn_dress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				players.get(playerid).changeDress();
				writeDresscode(players.get(playerid).getDresscode());
			}
		});

		btn_exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					writeLogout();
					out.close();
					System.exit(0);
				} catch (IOException e1) {}
			}
		});

		panel.setPreferredSize(new Dimension(WINDOW_SIZEX, WINDOW_SIZEY));
		panel.setLayout(null);

		setBounds(0, 40, WINDOW_SIZEX, WINDOW_SIZEY - 140);

		panel.add(this);
		panel.add(btn_credits);
		panel.add(btn_settings);
//		panel.add(btn_board);
		panel.add(lb_board_tmp);
		panel.add(lb_current);
		
		panel.add(btn_show_room);
		panel.add(btn_create_room);
		panel.add(btn_exit);
		panel.add(btn_dress);
		panel.add(area);
		
		btn_credits.setBounds(10, 5, 90, 30);
		btn_settings.setBounds(110, 5, 90, 30);
//		btn_board.setBounds(250, 5, 150, 30);
		lb_board_tmp.setBounds(250, 5, WINDOW_SIZEX - 700, 30);
		lb_current.setBounds(WINDOW_SIZEX - 500, 5, 90, 30);
		btn_show_room.setBounds(WINDOW_SIZEX - 400, 5, 90, 30);
		btn_create_room.setBounds(WINDOW_SIZEX - 300, 5, 90, 30);
		btn_dress.setBounds(WINDOW_SIZEX - 200, 5, 90, 30);
		btn_exit.setBounds(WINDOW_SIZEX - 100, 5, 90, 30);
		area.setBounds(0, WINDOW_SIZEY - 100, WINDOW_SIZEX, 100);

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

		for (int i = 0; i < LOBBY_PLAYER_MAX_COUNT; i++) {
			LobbyPlayer lp = new LobbyPlayer(this, -1, "", -1, -1, -1);
			players.add(lp);
		}

		for (int i = 0; i < 10; i++) {
			JButton ri = new JButton("-");
			roomlist.add(ri);
		}
		
		left = false;
		down = false;
		right = false;
		up = false;
		
		enterLobby();
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
	
	public void enterLobby() {
		LobbyPlayer lp = new LobbyPlayer(this, playerid, nick, dresscode, -1, -1);
		lp.setState(true);
		players.set(playerid, lp);
		
		/* Check notice */
		writeNoticeInfo();
	}
	
	// Except mine
	public void updatePlayerMove(int pid, int direction) {
	
		int speed = LOBBY_PLAYER_MAX_SPEED;
		
		switch (direction) {
		case 2:
			players.get(pid).setVerticalMovement(-speed);
			players.get(pid).setHorizontalMovement(speed);
			break;
		case 3:
			players.get(pid).setHorizontalMovement(speed);
			break;	
		case 4:
			players.get(pid).setVerticalMovement(speed);
			players.get(pid).setHorizontalMovement(speed);
			break;

		case 6:
			players.get(pid).setVerticalMovement(speed);
			break;
		case 8:
			players.get(pid).setVerticalMovement(speed);
			players.get(pid).setHorizontalMovement(-speed);
			break;
		case 9:
			players.get(pid).setHorizontalMovement(-speed);
			break;
		case 10:
			players.get(pid).setVerticalMovement(-speed);
			players.get(pid).setHorizontalMovement(-speed);
			break;
		case 12:
			players.get(pid).setVerticalMovement(-speed);
			break;
		}
	}

	public void updatePlayerPosition(int pid, double x, double y) {
		players.get(pid).setLocation((int) x,  (int) y); 
	}
	
	public void updatePlayerDresscode(int pid, int dresscode) {
		players.get(pid).setDress(dresscode); 
	}

	public void updateAquaRoomInfo(int roomid, String roomname, int capacity) {

		roomlist.get(roomid).setText(roomid + ". " + roomname);
		
		roomlist.get(roomid).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				writeAquaRoomEnterRequest(roomid);
			}
		});
	}

	public void updateNotice (String text) {
		lb_board_tmp.setText(text);
		lb_board_tmp.setForeground(Color.RED);
		
		/* check if size exceeds 15 */
		while ( chatlist.size() > 15) {
			chatlist.removeLast();
		}
		chatlist.push(text);
	}

	public void updateCurrent(int count) {
		lb_current.setText("접속중: " + count + " 명");
		lb_current.setForeground(Color.BLUE);
	}
	
	public void updateProfileInfo(int roomid, String roomname, int capacity) {

		profilelist.get(roomid).setText(roomid + ". " + roomname);
		
		profilelist.get(roomid).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				writeAquaRoomEnterRequest(roomid);
			}
		});
	}
	
	public void updateLobbyPlayerInfo(int pid, String nick, int dresscode, int x, int y) {
		
		/* New comer */
		if ( pid < 0 ) {
			pid *= -1;
			
			/* check if size exceeds 15 */
			while ( chatlist.size() > 15) {
				chatlist.removeLast();
			}
			chatlist.push("[" + nick + "] entered lobby");	
			
			if(alarm) {
				SoundEffect.ENTERANCE.play();
			}
					
		}
		
		players.get(pid).setId(pid);
		players.get(pid).setNick(nick);
		players.get(pid).setDress(dresscode);
		players.get(pid).setLocation((int) x,  (int) y);
		players.get(pid).setState(true);
	}
	
	public void updatePlayerExit(int pid) {
		
		/* check if size exceeds 15 */
		while ( chatlist.size() > 15) {
			chatlist.removeLast();
		}
		chatlist.push("[" + players.get(pid).getNick() + "] exited lobby");	
		
		players.get(pid).setState(false);
	}
	
	public void updatePlayerBalloon(int pid, String text) {

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
								players.get(pid).setEmoticon(emoticonBig, true);
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
							players.get(pid).setEmoticon(emoticon, false);
							break;
						}
					}
					
					return;
				}
		
		players.get(pid).setBalloon(text);
		
		/* check if size exceeds 15 */
		while ( chatlist.size() > 15) {
			chatlist.removeLast();
		}
		chatlist.push("[" + players.get(pid).getNick() + "] " + text);
	}
	
	public void writeRequestRoomInfo() {
		
		String msg = "" + Client_Settings.SET_AQUA_ROOM_INFO + (char)007;

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
	
	public void writeNoticeInfo() {
		
		String msg = "" + Client_Settings.SET_NOTICE + (char)007;

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
	
	public void writeDresscode(int dresscode) {
		
		String msg = "" + Client_Settings.SET_LOBBY_DRESSCODE + (char)007 + dresscode + (char)007;

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
	
	public void writeAquaRoomEnterRequest(int roomid) {
		String msg = "" + Client_Settings.SET_AQUA_ROOM_ENTER_REQUEST + (char)007 + roomid + (char)007;

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

	public void writeLogout() {
		String msg = "" + Client_Settings.SET_LOGOUT + (char)007;

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

	private void writeLobbyPlayerMove(int direction) {
		String msg = "" + Client_Settings.SET_LOBBY_PLAYER_MOVE + (char)007 + direction + (char)007;

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

	public void writeLobbyPlayerPos(int x, int y) {
		String msg = "" + Client_Settings.SET_LOBBY_PLAYER_POS + (char)007 + x + (char)007 + y + (char)007;

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

	private void writeLobbyPlayerText(String text) {

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
			
			String msg1out = "" + Client_Settings.SET_LOBBY_PLAYER_TEXT + (char)007 + "1" + (char)007 + msg1 + (char)007;
			String msg2out = "" + Client_Settings.SET_LOBBY_PLAYER_TEXT + (char)007 + "2" + (char)007 + msg2 + (char)007;

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
		String msg = "" + Client_Settings.SET_LOBBY_PLAYER_TEXT + (char)007 + "0" + (char)007 + text + (char)007;

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
	
	public void writeAquaRoomCreateRequest (String roomname, int capacity, String password) {
		String msg = "" + Client_Settings.SET_AQUA_ROOM_CREATE_REQUEST + (char)007 + roomname + (char)007 + capacity + (char)007 + password + (char)007;

		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(roomname);
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
	
	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println("Pressed: " + e.getKeyCode());
		int pushedkey = e.getKeyCode();

		switch (pushedkey) {

		case 37:
			left = true;
			break;
		case 38:
			up = true;
			break;
		case 39:
			right = true;
			break;
		case 40:
			down = true;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int pushedkey = e.getKeyCode();

		switch (pushedkey) {
		case 37:
			left = false;
			break;
		case 38:
			up = false;
			break;
		case 39:
			right = false;
			break;
		case 40:
			down = false;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * Check if new connection Manage new players Handle display (track all
	 * players) Handle event (aqua start)
	 * 
	 */
	@Override
	public void run() {

		long lastLoopTime = System.currentTimeMillis();

		long lastBGMPlayTime = lastLoopTime;
		
		SoundEffect.BGM_LOBBY.stop();
		
		if(bgm_lobby)
			SoundEffect.BGM_LOBBY.play();
		
		while (gameRunning) {

			// work out how long its been since the last update, this
			// will be used to calculate how far the entities should
			// move this loop
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

//			client.checkKeepAlive(lastLoopTime);

			/* 
			 * Check BGM is finished and wait 10 seconds 
			 * current BGM length is 01:23 = 83 sec == 83000 ms
			 */
			if(bgm_lobby) {
				if ( lastLoopTime - lastBGMPlayTime >= 90000) {
					lastBGMPlayTime = lastLoopTime;
					SoundEffect.BGM_LOBBY.play();
				}	
			}
			
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.drawImage(bg_lobby.getImage(), 0, 0, getWidth(), getHeight(), this);

			// my move
			if (right) {
				players.get(playerid).setHorizontalMovement(LOBBY_PLAYER_MAX_SPEED);

				if (up) {
					players.get(playerid).setVerticalMovement(-LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(2);
				} else if (down) {
					players.get(playerid).setVerticalMovement(LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(4);
				} else {
					writeLobbyPlayerMove(3);
				}

			} else if (left) {
				players.get(playerid).setHorizontalMovement(-LOBBY_PLAYER_MAX_SPEED);

				if (up) {
					players.get(playerid).setVerticalMovement(-LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(10);
				} else if (down) {
					players.get(playerid).setVerticalMovement(LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(8);
				} else {
					writeLobbyPlayerMove(9);
				}

			} else {
				if (up) {
					players.get(playerid).setVerticalMovement(-LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(12);
				} else if (down) {
					players.get(playerid).setVerticalMovement(LOBBY_PLAYER_MAX_SPEED);
					writeLobbyPlayerMove(6);
				}
			}

			// move and draw everyone
			for (int i = 0; i < LOBBY_PLAYER_MAX_COUNT; i++) {
				if (players.get(i).getState()) {
					
					updatePlayerMove(players.get(i).getId(), players.get(i).pop());
					
					players.get(i).move(delta);
					players.get(i).drawWithNick(g);
				}
			}

			// draw every emoticons
			for (int i = 0; i < LOBBY_PLAYER_MAX_COUNT; i++) {

				if (players.get(i).getState()) {
					if (players.get(i).checkTimeoutEmoticon(lastLoopTime)) {
						players.get(i).drawEmoticon(g);
					}
				}
			}
			
			// draw every balloons
			for (int i = 0; i < LOBBY_PLAYER_MAX_COUNT; i++) {

				if (players.get(i).getState()) {
					if (players.get(i).checkTimeoutBalloon(lastLoopTime)) {

						players.get(i).drawBalloon(g);
					}
				}
			}

			/* Init movement */
			for (int i = 0; i < LOBBY_PLAYER_MAX_COUNT; i++) {
				if (players.get(i).getState()) {
					
					players.get(i).setHorizontalMovement(0);
					players.get(i).setVerticalMovement(0);
				}
			}
			
			/* Draw chatting window */
			String[] chatarray = chatlist.toArray(new String[0]);
			
			float alpha = 0.4f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            AlphaComposite alcomb = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
            g.setComposite(alcom);
            g.setColor(Color.BLUE);
			g.fillRect(5,300,400,300);
            
            g.setComposite(alcomb);
			g.setColor(Color.WHITE);
			int chatlength = chatarray.length;
			
			for (int i = 1; i <= chatlength; i++) {
				g.drawString(chatarray[chatlength - i], 10, 300 + i * 20);
			}
			
			/* Inform to server this player's position */
			writeLobbyPlayerPos(players.get(playerid).getX(), players.get(playerid).getY());
			
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
	
	public int getLength() {
		return chatlist.size();
	}
	
	public String pop() {
		if(!chatlist.isEmpty())
			return chatlist.pop();
		return "";
	}
	
	public void push(String n) {
		chatlist.push(n);
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
