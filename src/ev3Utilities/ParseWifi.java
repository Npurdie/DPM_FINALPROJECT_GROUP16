package ev3Utilities;

import java.io.IOException;
import java.util.HashMap;

import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

/**
 * This class provides methods to parse data contained by the hashmap returned
 * by the wifi class.
 */
public class ParseWifi {

	private static final String SERVER_IP = "192.168.10.200"; // "localhost";
	private static final int TEAM_NUMBER = 16;
	private static TextLCD LCD = LocalEV3.get().getTextLCD();
	private int player, ballColor, lowerLocX, lowerLocY, upperLocX, upperLocY, d1, d2, w1;
	private final double tile = 30.48;
	private HashMap<String, Integer> t;
	private int defCorner;
	private int forwCorner;

	/**
	 * Parse wifi connects to the host computer and creates a hashmap of all the
	 * game parameters
	 */
	public ParseWifi() {
		// WIFI
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			LCD.drawString("Connection failed", 0, 8);
		}

		LCD.clear();
		if (conn != null) {
			this.t = conn.StartData;
			if (t == null) {
				LCD.drawString("Failed to read transmission", 0, 5);
			} else {
				this.player = t.get("DTN"); // OFFENCE DEFENSE ?
				this.defCorner = t.get("DSC");
				this.forwCorner = t.get("OSC");
				this.ballColor = t.get("BC");
				this.lowerLocX = t.get("ll-x"); // lower left location of row of
											// balls
				this.lowerLocY = t.get("ll-y");
				this.upperLocX = t.get("ur-x");
				this.upperLocY = t.get("ur-y");
				this.w1 = t.get("w1");
				this.d1 = t.get("d1");
				this.d2 = t.get("d2");

			}
		} else {
			LCD.drawString("Connection failed", 0, 5);
		}
	}
	public int getCornerID(){
		int ID;
		if(player == 16){
		ID = defCorner;} else {
		ID = forwCorner;
		}
		
		
		return ID;
		
		
	}

	/**
	 * Get corner returns the x,y and theta coordinate of the corner the EV3
	 * will be starting in.
	 * 
	 * @return An array of doubles where (x coord, y coord, theta)
	 */
	public double[] getCorner() {
		int id;
		if(player == 16){
		id = defCorner;} else {
		id = forwCorner;
		}
		double[] result = new double[3];
		if (id == 1) {
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
		}
		if (id == 2) {
			result[0] = 10 * tile;
			result[1] = 0;
			result[2] = Math.toRadians(90);
		}
		if (id == 3) {
			result[0] = 10 * tile;
			result[1] = 10 * tile;
			result[2] = Math.toRadians(180);
		}
		if (id == 4) {
			result[0] = 0;
			result[1] = 10 * tile;
			result[2] = Math.toRadians(270);
		}
		return result;
	}

	/**
	 * The get role method returns the player role(attacker or defender)
	 * 
	 * @return True = Attacker, False = Defender
	 */
	public boolean getRole() {
		if (player == 16) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * The get Ball loc method returns the location of the balls to be
	 * retrieved.
	 * 
	 * @return An array where (x coord, y coord) of the lower left corner of the
	 *         tile containing the balls.
	 */
	public double[] getBallLoc() {
		double[] loc = new double[2];
		loc[0] = tile * lowerLocX;
		loc[1] = tile * lowerLocY;
		return loc;
	}
	
	/**
	 * The get goal width method returns the width of the goal
	 * 
	 * @return An integer that represents the width of the goal.
	 */
	public double getGoalWidth() {
		return w1*tile;
	}
	
	/**
	 * The get defender line method returns the location of the defender line
	 * 
	 * @return An integer that represents the location of the defender line.
	 */
	public double getDefenderLine()	{
		return d1*tile;
	}
	
	/**
	 * The get forward line method returns the location of the forward line
	 * 
	 * @return An integer that represents the location of the forward line
	 */
	public double getForwardLine()	{
		return d2*tile;
	}
}
