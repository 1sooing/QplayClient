package ROOM;

public abstract class Room {

	/*
	 * Room has roomid
	 * 
	 * Room has player current count, capacity
	 * Room has playlist and player class
	 */
	
	public int roomid;
	public int current_count = 0;
	public int capacity = 0;
	
	public abstract class Player {
		
		public int slotid;
		public int teamid;
		public int playerid;
	}
}