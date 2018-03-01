package UTILL;
// CLIENT SIDE COMMAND

public final class Client_Settings {
	
	public static final int VERSION = 6;
	
//	public static final String SERVER_IP = "localhost";
	public static final String SERVER_IP = "133.130.115.99";
//	public static final String SERVER_IP = "52.89.97.164";
	
	public static final int SERVER_PORT = 5292;
	public static final int AQUA_SYNC_COUNT = 200;
	public static final int USER_ALIVE_TIMEOUT = 5000;
	
	/*
	 * Lobby settings
	 */
	public static final int LOBBY_PLAYER_MAX_COUNT = 20;
	public static final int LOBBY_PLAYER_SIZEX = 128;
	public static final int LOBBY_PLAYER_SIZEY = 128;
	public static final int LOBBY_PLAYER_MAX_SPEED = 120;

	/*
	 * Aqua settings
	 */	
	public static final int MAP_SIZEX = 2490;
	public static final int MAP_SIZEY = 1344;
	
	public static final int AQUA_PLAYER_MAX_COUNT = 6;
	public static final int AQUA_PLAYER_MAX_HP = 10;
	public static final int AQUA_PLAYER_SIZEX = 160;
	public static final int AQUA_PLAYER_SIZEY = 154;
	public static final int MISSILE_SIZEX = 80;
	public static final int MISSILE_SIZEY = 50;
	
	/* Movement of player */
	public static final double AQUA_OBSERVER_SPEED = 400;
	public static final int AQUA_PLAYER_MAX_SPEED = 370;
	public static final double AQUA_PLAYER_ACC = 230;
	public static final double AQUA_NATURAL_ACC = 150;
	
	/* Movement of missile */
	public static final int MISSILE_SPEED_SLOW = 460;
	public static final int MISSILE_SPEED_FAST = 800;
	public static final int MISSILE_SPEED_F4 = 500;
	public static final double MISSILE_ACC_F4 = 1400;
	public static final double MISSILE_ACC = 10;
	
	/*
	 * Room settings
	 */
	public static final int ROOM_PLAYER_MAX_COUNT = 6;
	
	
	/*
	 * 0 ~ 19: COMMON
	 * 20 ~ 39: LOBBY
	 * 40 ~ 59: AQUA
	 * 60 ~   : NOT RESERVED 
	 */
	
	// COMMON
	public static final int SET_DEFAULT = 0;
	public static final int SET_ID = 1;
	public static final int SET_LOGOUT = 2;
	public static final int SET_EXIT_AQUA = 3;
	public static final int SET_EXIT_ROOM = 4;
	public static final int SET_EXIT_LOBBY = 5;
	public static final int SET_DEBUG_TIME = 6;
	public static final int SET_KEEP_ALIVE = 7;
	public static final int SET_VERSION = 8;
	
	// LOBBY
	public static final int SET_LOBBY_ENTRANCE = 20;
	public static final int SET_LOBBY_PLAYER_MOVE = 21;
	public static final int SET_LOBBY_PLAYER_POS = 22;
	public static final int SET_LOBBY_PLAYER_TEXT = 23;
	public static final int EXIT_LOBBY = 25;
	public static final int SET_AQUA_ROOM_INFO = 26;
	public static final int SET_AQUA_ROOM_CREATE_REQUEST = 27;
	public static final int SET_AQUA_ROOM_ENTER_REQUEST = 28; 
	public static final int SET_AQUA_ROOM_ENTER_ACCEPT = 29;
	public static final int SET_AQUA_ROOM_PLAYER_INFO = 30;
	public static final int SET_AQUA_ROOM_REQUEST = 31;
	public static final int SET_ENABLE_START = 32;
	public static final int SET_LOBBY_ENTER_REQUEST = 33;
	public static final int SET_LOBBY_ENTER_ACCEPT = 34;
	public static final int SET_LOBBY_DRESSCODE = 35;
	public static final int SET_NOTICE = 36;
	public static final int SET_CURRENT_USER = 37;
	
	
	// AQUA
	public static final int SET_AQUA = 40;
	public static final int SET_AQUA_PLAYER_POS = 41;
	public static final int SET_AQUA_CHILD_POS = 43;
	public static final int SET_RESTART = 44;
	public static final int SET_START = 45;
	public static final int SET_AQUA_READY = 46;
	public static final int SET_AQUA_PLAYER_TEXT = 47;
	public static final int SET_AQUA_PLAYER_MOVE = 48;
	public static final int SET_AQUA_REQUEST = 49;
	public static final int SET_AQUA_ENTERANCE = 50;
	public static final int SET_AQUA_MISSILE_MOVE = 51;
	public static final int SET_AQUA_GAME_RESULT = 52;
	public static final int SET_AQUA_HIT = 53;
	public static final int SET_AQUA_TEAM = 54;
}

