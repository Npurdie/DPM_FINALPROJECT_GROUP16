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

	private static final String SERVER_IP = "142.157.146.74"; // "localhost";
	private static final int TEAM_NUMBER = 16;
	private static TextLCD LCD = LocalEV3.get().getTextLCD();
	private int player, ballColor, corner, lowerLocX, lowerLocY, upperLocX, upperLocY, d1, d2, w1;
	private final double tile = 30.48;

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
			HashMap<String, Integer> t = conn.StartData;
			if (t == null) {
				LCD.drawString("Failed to read transmission", 0, 5);
			} else {
				this.player = t.get("Role"); // OFFENCE DEFENSE ?
				this.ballColor = t.get("BC");
				this.corner = t.get("SC");
				this.lowerLocX = t.get("ll-x"); // lower left location of row of
											// balls
				this.upperLocY = t.get("ll-y");
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

	/**
	 * Get corner returns the x,y and theta coordinate of the corner the EV3
	 * will be starting in.
	 * 
	 * @return An array of doubles where (x coord, y coord, theta)
	 */
	public double[] getCorner() {
		int id = corner;
		double[] result = new double[3];
		if (id == 1) {
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
		}
		if (id == 2) {
			result[0] = 11 * tile;
			result[1] = 0;
			result[2] = Math.toRadians(90);
		}
		if (id == 3) {
			result[0] = 11 * tile;
			result[1] = 11 * tile;
			result[2] = Math.toRadians(180);
		}
		if (id == 4) {
			result[0] = 0;
			result[1] = 11 * tile;
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
		if (player == 0) {
			return true;
		} else {
			return false;
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
		loc[1] = tile * lowerLocY + 11.43;
		return loc;
	}
}
