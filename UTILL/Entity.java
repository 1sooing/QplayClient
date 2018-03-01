package UTILL;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Entity {
	
	public boolean state = false;
	
	/** The sprite that represents this entity */
	protected Sprite sprite;
	protected Sprite sprite_id;
	protected Sprite sprite_emo;
	protected Sprite sprite_hp = SpriteStore.get().getSprite("res/hp10.png");
	
	/** Entity id */
	protected int entityid = -1;
	
	/** The current x location of this entity */ 
	protected double x;
	/** The current y location of this entity */
	protected double y;
	
	/** The current speed of this entity horizontally (pixels/sec) */
	protected double dx;
	/** The current speed of this entity vertically (pixels/sec) */
	protected double dy;
	
	/** The current acc of this entity horizontally */
	protected double accx;
	/** The current acc of this entity vertically */
	protected double accy;
	
	
	/** The rectangle used for this entity during collisions  resolution */
	protected Rectangle me = new Rectangle();
	/** The rectangle used for other entities during collision resolution */
	protected Rectangle him = new Rectangle();
	
	protected int boundX = -1;
	
	protected int boundY = -1;
	
	protected String nick = "";
	public int nick_width = 0;
	public int nick_posx = 0;
	
	protected int dresscode = -1;
	
	/**
	 * Construct a entity based on a sprite image and a location.
	 * 
	 * @param ref The reference to the image to be displayed for this entity
 	 * @param x The initial x location of this entity
	 * @param y The initial y location of this entity
	 */
	public Entity(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getId() {
		return entityid;
	}
	
	public void setId(int pid) {
		this.entityid = pid;
	}
	
	public String getNick() {
		return nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
		
		this.nick_width = nick.length();
		
		Matcher m = Pattern.compile("[ㄱ-ㅎㅏ-ㅣ가-힣]").matcher(nick);
		while(m.find()) {
			this.nick_width++;
		}
		
		this.nick_posx = 64 - nick_width * 7 / 2;
	}
	
	/**
	 * Request that this entity move itself based on a certain amount
	 * of time passing.
	 * 
	 * @param delta The amount of time that has passed in milliseconds
	 */
	
	public void accelerate(long delta) {
		dx += (delta * accx) / 1000;
		dy += (delta * accy) / 1000;
	}
	
	public void deAccelerate(long delta) {
		
		if(dx > 0) {
			dx -= (delta * Client_Settings.AQUA_NATURAL_ACC) / 1000;
			if(dx < 0)
				dx = 0;
		}
		else if (dx < 0) {
			dx += (delta * Client_Settings.AQUA_NATURAL_ACC) / 1000;
			if(dx > 0)
				dx = 0;
		}
		
		if(dy > 0) {
			dy -= (delta * Client_Settings.AQUA_NATURAL_ACC) / 1000;
			if(dy < 0)
				dy = 0;
		}
		else if (dy < 0) {
			dy += (delta * Client_Settings.AQUA_NATURAL_ACC) / 1000;
			if(dy > 0)
				dy = 0;
		}
		
	}
	
	public void move(long delta) {
		// update the location of the entity based on move speeds
		x += (delta * dx) / 1000;
		y += (delta * dy) / 1000;
	}
	
	/**
	 * Set the horizontal speed of this entity
	 * 
	 * @param dx The horizontal speed of this entity (pixels/sec)
	 */
	public void setHorizontalMovement(double dx) {
		this.dx = dx;
	}

	/**
	 * Set the vertical speed of this entity
	 * 
	 * @param dx The vertical speed of this entity (pixels/sec)
	 */
	public void setVerticalMovement(double dy) {
		this.dy = dy;
	}
	
	/**
	 * Get the horizontal speed of this entity
	 * 
	 * @return The horizontal speed of this entity (pixels/sec)
	 */
	public double getHorizontalMovement() {
		return dx;
	}

	/**
	 * Get the vertical speed of this entity
	 * 
	 * @return The vertical speed of this entity (pixels/sec)
	 */
	public double getVerticalMovement() {
		return dy;
	}
	
	public void setHorizontalAcc(double accx) {
		this.accx = accx;
	}
	
	public void setVerticalAcc(double accy) {
		this.accy = accy;
	}
	
	public void setHorizontalLimitedAcc(double accx) {
		
		if (dx > Client_Settings.AQUA_PLAYER_MAX_SPEED) {
			this.accx = 0;
			return;
		}
		else if (-dx > Client_Settings.AQUA_PLAYER_MAX_SPEED) {
			this.accx = 0;
			return;
		}
			
		this.accx = accx;
	}
	
	public void setVerticalLimitedAcc(double accy) {
		
		if (dy > Client_Settings.AQUA_PLAYER_MAX_SPEED) {
			this.accy = 0;
			return;
		}
		else if (-dy > Client_Settings.AQUA_PLAYER_MAX_SPEED) {
			this.accy = 0;
			return;
		}
		
		this.accy = accy;
	}
	
	public double getHorizontalAcc() {
		return accx;
	}
	
	public double getVerticalAcc() {
		return accy;
	}
	
	/**
	 * Draw this entity to the graphics context provided
	 * 
	 * @param g The graphics context on which to draw
	 */
	public void draw(Graphics g) {
		sprite.draw(g, (int) x,(int) y);
	}
	
	public void shiftedDraw(Graphics g, int shiftX, int shiftY) {
		
		int x = (int)this.x + shiftX;
		int y = (int)this.y + shiftY;
		
		sprite.draw(g, (int) x,(int) y);
	}
	
	public void drawWithNick(Graphics g) {
		
		sprite.draw(g, (int) x,(int) y);
		
		g.setColor(Color.BLUE);
		g.fillRect((int) x + nick_posx, (int) y + 110, 4 + nick_width * 7, 20);
		g.setColor(Color.WHITE);
		g.drawString(nick, (int) x + nick_posx + 2, (int) y + 125);
	}

	public void shiftedDrawWithNick(Graphics g, int shiftX, int shiftY) {
		
		int x = (int) this.x + shiftX;
		int y = (int) this.y + shiftY;
		
		sprite.draw(g, (int) x,(int) y);
		
//		System.out.println("nick_width: " + nick_width);
		
		g.setColor(Color.BLUE);
		g.fillRect((int) x + nick_posx, (int) y + 110, 4 + nick_width * 7, 20);
		g.setColor(Color.WHITE);
		g.drawString(nick, (int) x + nick_posx + 2, (int) y + 125);

		/* draw character */
		g.drawImage(sprite_id.getImage(), x + 44, y + 36, 80, 80, null);
		
		/* print HP */
		g.drawImage(sprite_hp.getImage(), x, y, null);
		
	}
	
	public boolean getState() {
		return state;
	}
	
	public void setState(boolean state) {
		this.state = state;
	}
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setLocationShift(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	/**
	 * Get the x location of this entity
	 * 
	 * @return The x location of this entity
	 */
	public int getX() {
		return (int) x;
	}

	/**
	 * Get the y location of this entity
	 * 
	 * @return The y location of this entity
	 */
	public int getY() {
		return (int) y;
	}
	
	public int getSizeX() {
		return this.sprite.getWidth();
	}
	
	public int getSizeY() {
		return this.sprite.getHeight();
	}
	
	/**
	 * Check if this entity collised with another.
	 * 
	 * @param other The other entity to check collision against
	 * @return True if the entities collide with each other
	 */
	public boolean collidesWith(Entity other) {
//		System.out.println("sprite: " + other.sprite.getWidth() + ", " + other.sprite.getHeight());
		me.setBounds((int) x,(int) y,sprite.getWidth(),sprite.getHeight());
		him.setBounds((int) other.x,(int) other.y,other.sprite.getWidth(),other.sprite.getHeight());

		return me.intersects(him);
	}
	
	/**
	 * Notification that this entity collided with another.
	 * 
	 * @param other The entity with which this entity collided.
	 */
	public abstract void collidedWith(Entity other);
	
	public abstract void setDress(int dresscode);
	
}
