package AQUA;

import UTILL.Client_Settings;
import UTILL.Entity;
import UTILL.SpriteStore;

public class AquaMissile extends Entity {

	private static final int MISSILE_SPEED_SLOW = Client_Settings.MISSILE_SPEED_SLOW;;
	private static final int MISSILE_SPEED_FAST = Client_Settings.MISSILE_SPEED_FAST;
	private static final int MISSILE_SPEED_F4 = Client_Settings.MISSILE_SPEED_F4;;
	private static final double MISSILE_ACC_F4 = Client_Settings.MISSILE_ACC_F4;
	private static final double MISSILE_ACC = Client_Settings.MISSILE_ACC;
	
	private AquaGame game;

	private int type;

	private int teamid;
	private int slotid;

	private double velx;
	private double vely;

	public AquaMissile(AquaGame game, int slotid, int teamid, int type, int x, int y, double velx, double vely) {
		super(x, y);

		this.game = game;
		this.teamid = teamid;
		this.slotid = slotid;
		this.type = type;
		
		if (type == 4 || type == -4) {
			
			this.x = x + (game.PLAYER_SIZEX / 2);
			this.y = y + 3;
			this.velx = velx;
			this.vely = vely;
			
		} else {
			this.x = x + (game.PLAYER_SIZEX / 2);
			this.y = y + game.PLAYER_SIZEY + 12;
			
		}

		this.boundX = Client_Settings.MAP_SIZEX;
		this.boundY = Client_Settings.MAP_SIZEY;
		
		setDress(type);
		
		setMovement(type, velx, vely);
		
		setAcceleration(type);
	}

	public int getSlotId() {
		return this.slotid;
	}
	
	public int getTeamId() {
		return this.teamid;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setInitPos() {
		this.x = -1000;
		this.y = -1000;
	}
	
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < -80)) {
//		if ((x < -80)) {
			
			this.setState(false);
			
			if (slotid == game.slotid) {
//				System.out.println("reloaded");
				game.lastShoot = System.currentTimeMillis() + 300;
				game.setMissileMoving(false);
			}
			return;
		}

		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > boundX)) {
//		if ((x > boundX)) {

			this.setState(false);
			
			if (slotid == game.slotid) {
//				System.out.println("reloaded");
				game.lastShoot = System.currentTimeMillis() + 300;
				game.setMissileMoving(false);
			}
			return;
		}

		if ((dy > 0) && (y > boundY)) {
//		if ((y > boundY)) {

			this.setState(false);
			
			if (slotid == game.slotid) {
//				System.out.println("reloaded");
				game.lastShoot = System.currentTimeMillis() + 300;
				game.setMissileMoving(false);
			}
			return;
		}
		super.move(delta);
	}

	@Override
	public void collidedWith(Entity other) {
	}

	@Override
	public void setDress(int dresscode) {
		
		switch(dresscode) {
		case 1:
			this.sprite = SpriteStore.get().getSprite("res/f1.png");
			break;
		case -1:
			this.sprite = SpriteStore.get().getSprite("res/f1L.png");
			break;
		case 2:
			this.sprite = SpriteStore.get().getSprite("res/f2.png");
			break;
		case -2:
			this.sprite = SpriteStore.get().getSprite("res/f2L.png");
			break;
		case 4:
			this.sprite = SpriteStore.get().getSprite("res/f4.png");
			break;
		case -4:
			this.sprite = SpriteStore.get().getSprite("res/f4.png");
			break;
		}
	}
	
	public void setMovement(int type, double velx, double vely) {
		switch(type) {
		case 1:
			setHorizontalMovement(MISSILE_SPEED_FAST);
			break;
		case -1:
			setHorizontalMovement(-MISSILE_SPEED_FAST);
			break;
		case 2:
			setHorizontalMovement(MISSILE_SPEED_SLOW);
			break;
		case -2:
			setHorizontalMovement(-MISSILE_SPEED_SLOW);
			break;
		case 4:
			if(dx < 0)
				setHorizontalMovement(MISSILE_SPEED_F4 - velx / 2);
			else 
				setHorizontalMovement(MISSILE_SPEED_F4);
			
			setVerticalMovement(-MISSILE_SPEED_F4);	
			break;
		case -4:
			
			if(dx > 0)
				setHorizontalMovement(-MISSILE_SPEED_F4 - velx / 2);
			else 
				setHorizontalMovement(-MISSILE_SPEED_F4);
			
			setVerticalMovement(-MISSILE_SPEED_F4);
			break;
			
		}
	}

	public void setAcceleration (int type) {
		
		if ( type == 4 || type == -4) 
			setVerticalAcc(MISSILE_ACC_F4);
		
		else 
			setVerticalAcc(-MISSILE_ACC);
	}
	
}
