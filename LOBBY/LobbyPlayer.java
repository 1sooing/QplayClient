package LOBBY;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import UTILL.Entity;
import UTILL.SpriteStore;

public class LobbyPlayer extends Entity {

	private LobbyGame game;
	
	private long timeout_balloon = 0;
	public boolean has_balloon = false;
	
	public String balloon = "";
	private boolean emoBig = false;
	
	private long timeout_emoticon = 0;
	public boolean has_emoticon = false;
	
	private int boundX = -1;
	private int boundY = -1;
	
	private int ball_width = 0;
	private int ball_height = 0;
	
	ArrayList<String> ball_array = new ArrayList<String>();
	private Deque<Integer> deque = new ArrayDeque<>();

	public LobbyPlayer(LobbyGame game, int pid, String nick, int dresscode, int x, int y) {
		super(x,y);

		this.entityid = pid;
		this.nick = nick;
		this.dresscode = dresscode;
		this.game = game;
		
		this.boundX = game.WINDOW_SIZEX - 128;
		this.boundY = game.WINDOW_SIZEY - 208;
		
		this.x = x;
		this.y = y;
		
		this.nick_width = nick.length();
		
		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(nick);
		while(m.find()) {
			this.nick_width++;
		}
		
		this.nick_posx = 64 - nick_width * 7 / 2;
		
		setDress(dresscode);
	}

	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 0)) {
			return;
		}
		
		if ((dy < 0) && (y < 0)) {
			return;
		}
		
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > boundX)) {
			return;
		}
		
		if ((dy > 0) && (y > boundY)) {
			return;
		}
		
		super.move(delta);
	}
	
	public void setEmoticon(String type, Boolean big) {

		this.has_emoticon = true;
		this.emoBig = big;
		this.timeout_emoticon = System.currentTimeMillis() + 8000;
		String ref = "res/emoticon/" + type + ".png";
		
		this.sprite_emo = SpriteStore.get().getSprite(ref);
	}
	
	public int getDresscode() {
		return this.dresscode;
	}
	
	public void changeDress() {
		
		dresscode++;
		dresscode %= 15;
		
		String ref = "res/pk";
		ref += dresscode;
		ref += ".png";
		
		this.sprite = SpriteStore.get().getSprite(ref);
	}
	
	public void setBalloon(String text) {
		this.balloon = text;
		this.has_balloon =  true;
		this.timeout_balloon = System.currentTimeMillis() + 8000;		
		
		/* Decide balloon position */
		ball_array.clear();
		
		int ball_length = balloon.length();
		int count = 0;
		int breakpoint = 0;
		ball_width = 0;
		ball_height = 0;
		
		for (int i = 0; i < ball_length; i++) {
			
			count++;
			
			if(ball_width < 20)
				ball_width++;
			
			Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(balloon);
			if(m.find()) {
				
				if(ball_width < 20)
					ball_width++;
				
				count++;
			}
			
			if(count >= 20) {
				
				ball_array.add(text.substring(breakpoint, i));
				
				breakpoint = i;
				ball_width = 20;
				ball_height++;
				count = 0;
			}
		}
		
		if(breakpoint < ball_length) {
			ball_array.add(text.substring(breakpoint, ball_length));
			ball_height++;
		}
	}

	public boolean checkTimeoutBalloon(long current) {
		
		if (timeout_balloon > current)
			return true;

		has_balloon = false;
		return false;
	}
	
	public boolean checkTimeoutEmoticon (long current) {
		
		if (timeout_emoticon > current)
			return true;

		has_emoticon = false;
		return false;
	}

	public void drawBalloon(Graphics2D g) {
		
			g.setColor(Color.WHITE);
			g.fillRect((int) x - 10, (int) y - 20 - ball_height * 25, ball_width * 7, ball_height * 25);

			g.setColor(Color.BLACK);
			for(int i = 0; i < ball_array.size(); i++) {
				g.drawString(ball_array.get(i), (int) x - 9, (int) y + i * 25 - ball_height * 25);	
			}	
	}
	
	public void drawEmoticon(Graphics2D g) {
		if(emoBig)
			g.drawImage(this.sprite_emo.getImage(), (int)x + 10, (int)y, 108, 108, null);
		else
			g.drawImage(this.sprite_emo.getImage(), (int)x - 20, (int)y + 64, null);
	}
	
	public int getLength() {
		return deque.size();
	}
	
	public void clearQueue() {
		deque.clear();
	}
	
	public int pop() {
		if(!deque.isEmpty())
			return deque.pop();
		return -1;
	}
	
	public void push(int n) {
		deque.push(n);
	}
	
	public void clearPlayer() {
//		text_balloon.interrupt();
		deque.clear();
	}
	
	@Override
	public void collidedWith(UTILL.Entity other) {
	}
	
	public void setDress(int dresscode) {

		if (dresscode == -1)
			return;
		
		this.dresscode = dresscode;
		
		dresscode %= 15;
		
		String ref = "res/pk";
		ref += dresscode;
		ref += ".png";
		
		this.sprite = SpriteStore.get().getSprite(ref);
	}
}
