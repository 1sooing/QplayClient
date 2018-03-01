package ROOM;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import UTILL.Sprite;
import UTILL.SpriteStore;

public class RoomPlayer {

	private boolean state = false;
	
	public int slotid = -1;
	public int teamid = -1;
	public int playerid = -1;
	public String nick = "";
	public int dresscode = -1;

	private boolean emoBig = false;
	
	private long timeout_emoticon = 0;
	public boolean has_emoticon = false;

	protected Sprite sprite;
	protected Sprite sprite_emo;
	
	
	private int WINDOW_SIZEX = -1;
	private int WINDOW_SIZEY = -1;

	public int x;
	public int y;
	
	public boolean ready = false;
	
	public RoomPlayer(AquaRoom room, int slotid, int teamid, String nick, int dresscode, boolean ready) {
		
		this.teamid = teamid;
		this.slotid = slotid;
		this.nick = nick;
		this.dresscode = dresscode;
		this.ready = ready;

		this.WINDOW_SIZEX = room.WINDOW_SIZEX;
		this.WINDOW_SIZEY = room.WINDOW_SIZEY;
		
		double ratex = (WINDOW_SIZEX) / 407;
		double ratey = (WINDOW_SIZEY) / 492;
		
		/*
		 * 
		 * 
		 */
		switch(slotid) {
		case 0:	
			this.x = (int) ratex * 60;
			this.y = (int) ratey * 220;
			break;
		case 1:
			this.x = (int) ratex * 260;
			this.y = (int) ratey * 220;;
			break;
		case 2:
			this.x = (int) ratex * 60;
			this.y = (int) ratey * 450;
			break;
		case 3:
			this.x = (int) ratex * 260;
			this.y = (int) ratey * 450;
			break;
		case 4:
			this.x = (int) ratex * 60;
			this.y = (int) ratey * 680;
			break;
		case 5:
			this.x = (int) ratex * 260;
			this.y = (int) ratey * 680;
			break;
		case -1:
			this.x = -300;
			this.y = -300;
			break;
		}
		
		setDress(dresscode);
	}

	public void setState(boolean state) {
		this.state = state;
	}
	
	public boolean getState() {
		return this.state;
	}
	
	public boolean getReady() {
		return ready;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
	public String getNick() {
		return this.nick;
	}
	
	public void setDress(int dresscode) {
		
		String ref = "res/pk";
		ref += dresscode;
		ref += ".png";
		
		this.sprite = SpriteStore.get().getSprite(ref);
	}
	
	public void setEmoticon(String type, Boolean big) {

		this.has_emoticon = true;
		this.emoBig = big;
		this.timeout_emoticon = System.currentTimeMillis() + 8000;
		String ref = "res/emoticon/" + type + ".png";
		
		this.sprite_emo = SpriteStore.get().getSprite(ref);
	}
	
	public void setTeamid (int teamid) {
		this.teamid = teamid;
	}

	public boolean checkTimeoutEmoticon (long current) {
		
		if (timeout_emoticon > current)
			return true;

		has_emoticon = false;
		return false;
	}

	public void drawEmoticon(Graphics2D g) {
		if(emoBig)
			g.drawImage(this.sprite_emo.getImage(), (int)x + 10, (int)y, 108, 108, null);
		else
			g.drawImage(this.sprite_emo.getImage(), (int)x - 20, (int)y + 64, null);
	}
	
	public void draw(Graphics g) {
		sprite.draw(g, x, y);
	}
	
	public void drawWithNickWithReady(Graphics2D g) {
		
		/* Set team color */
		switch(teamid) {
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
		g.fillOval(x, y, 140, 140);
		
		if (slotid == 0) {
			g.setColor(Color.ORANGE);
			g.fillRect(x + 40, y - 20, 60, 20);
			g.setColor(Color.WHITE);
			g.drawString("BOSS", x + 45, y - 5);
		} else {
			if(getReady()) {
				g.setColor(Color.ORANGE);
				g.fillRect((int) x + 40, y - 20, 60, 20);
				g.setColor(Color.WHITE);
				g.drawString("READY", x + 45, y - 5);	
			}
		}
		
		
		/* Character color transprency */
//		float alpha = 0.7f;
//        AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
//        g.setComposite(alcom);
        sprite.draw(g, (int) x,(int) y);

		g.setColor(Color.BLACK);
		g.fillRect((int) x + 40, (int) y + 110, 60, 20);
		g.setColor(Color.WHITE);
		g.drawString(nick, (int) x + 42, (int) y + 125);
		
//        Color newColor = new Color(1.0f, 0f, 0f, 0f);
//        g.setXORMode(newColor);
//        g.drawImage(sprite.getImage(), x, y, sprite.getWidth(), sprite.getHeight(), null);
	}
}
