package AQUA;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import UTILL.Client_Settings;
import UTILL.Entity;
import UTILL.SoundEffect;
import UTILL.SpriteStore;

public class AquaPlayer extends Entity {

	public static final int PLAYER_MAX_SPEED = Client_Settings.AQUA_PLAYER_MAX_SPEED;
	
	private AquaGame game;
	private int slotid = -1;
	private int teamid = -1;
	private boolean isAlive = true;
	
	private int hp = 10;
	
	public int won = 0;
	public int killed = 0;
	
//	private long sync_delta = 0;
	private int q_head = 0;
	private int q_tail = 0;
	
	private long remain = 0;
	private unitMove[] q_movement = new unitMove[20];
	private unitMove c_movement = new unitMove();
	
	public AquaPlayer(AquaGame game, int slotid, int teamid, String nick, int dresscode, int x, int y) {
		super(x,y);

		this.slotid = slotid;
		this.teamid = teamid;
		this.nick = nick;
		this.dresscode = dresscode;
		this.x = x;
		this.y = y;
		
		this.game = game;
		
		this.boundX = Client_Settings.MAP_SIZEX - 160;
		this.boundY = Client_Settings.MAP_SIZEY - 154;
		
		nick_width = nick.length();
		
		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(nick);
		while(m.find()) {
			nick_width++;
		}
		
		nick_posx = 70 - 7 * nick_width / 2;
		
		setDress(dresscode);
	}

	public void pushM(long delta, boolean direction, double acc) {
		q_movement[q_tail] = new unitMove(delta, direction, acc);
//		q_movement[q_tail].delta = delta;
//		q_movement[q_tail].direction = direction;
//		q_movement[q_tail].acc = acc;
		q_tail++;
		q_tail %= 20;	
	}
		
	public unitMove popM() {
		unitMove tmp = q_movement[q_head];
		q_movement[q_head] = null;
		q_head++;
		q_head %= 20;
		
		if(q_head == q_tail) {
			return new unitMove();
		}
		return tmp;
	}
	
	public void addHorizontalMovement(double vel) {
		this.dx += vel;
	}
	
	public void addVerticalMovement(double vel) {
		this.dy += vel;
	}
	
	public void executeUnitMove(long delta) {

		if(remain <= 0) {
			c_movement = popM();

		} else if (remain < delta) {
			this.accelerate(delta);
			c_movement = popM();
		}
		
		if(c_movement == null)
			return;
		
		if(c_movement.direction)
			this.setHorizontalLimitedAcc(c_movement.acc);
		else 
			this.setVerticalLimitedAcc(c_movement.acc);
		
		this.accelerate(delta);
	}
		
	public void setHP(int hp) {
		
		this.hp = hp;

		if (hp <= 0) {
			isAlive = false;
			SoundEffect.DEATH.play();
			return;
		}
			
		String ref = "res/hp";
		ref += hp;
		ref += ".png";
		
//		String ref = "res/sub_";
//		ref += teamid;
//		ref += ".png";
		
		this.sprite_hp = SpriteStore.get().getSprite(ref);
		
//		this.sprite = SpriteStore.get().getSprite(ref);
	}
	
	public void setAlive(boolean alive) {
		this.isAlive = alive;
	}
	
	public boolean isAlive() {
		return this.isAlive;
	}
	
	public int getSlotId() {
		return this.slotid;
	}
	
	public int getTeamId() {
		return this.teamid;
	}
	
	public void setKilled() {
		this.killed += 1;
	}
	
	public int getKilled() {
		return this.killed;
	}
	
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {

			dx = 0;
			
			if(accx < 0)
				accx = 0;
			
			return;
		}
		
		if ((dy < 0) && (y < 0)) {
			
			dy = 0;
			
			if(accy < 0)
				accy = 0;
			
			return;
		}
		
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > boundX)) {
			
			dx = 0;
			
			if(accx > 0)
				accx = 0;
			
			return;
		}
		
		if ((dy > 0) && (y > boundY)) {
			
			dy = 0;
			
			if(accy > 0)
				accy = 0;
			
			return;
		}
		
		super.move(delta);
	}
	
	public void executePlayerHitAcc(double accx, double accy) {
		this.setHorizontalAcc(accx * 5);
		this.setVerticalAcc(accy * 5);
	}
	
	@Override
	public void collidedWith(Entity other) {
	}

	@Override
	public void setDress(int dresscode) {
		
		if (dresscode == -1)
			return;
		
		String ref = "res/sub";
		ref += teamid;
		ref += ".png";
		
		String ref_id = "res/pk";
		ref_id += dresscode;
		ref_id += ".png";
		
		this.sprite = SpriteStore.get().getSprite(ref);
		this.sprite_id = SpriteStore.get().getSprite(ref_id);
	}

	class unitMove {
		long delta = 0;
		boolean direction = false;
		double acc = 0;
		
		public unitMove() {
			
		}
		
		public unitMove(long delta, boolean direction, double acc) {
			this.delta = delta;
			this.direction = direction;
			this.acc = acc;
		}
	}
}
