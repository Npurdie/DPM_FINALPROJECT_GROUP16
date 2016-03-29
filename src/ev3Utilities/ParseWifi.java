package ev3Utilities;

import java.io.IOException;
import java.util.HashMap;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class ParseWifi {
	
	private static final String SERVER_IP = "192.168.0.101"; //"localhost";
	private static final int TEAM_NUMBER = 16;
	private static TextLCD LCD = LocalEV3.get().getTextLCD();
	private static int player, ballColor, corner, lowerLocX, lowerLocY, upperLocX, upperLocY, d1, d2, w1;
	private final double tile = 30.48;
	
	
	public ParseWifi()	{
		// WIFI
		WifiConnection conn = null;
		try {
			conn = new WifiConnection(SERVER_IP, TEAM_NUMBER);
		} catch (IOException e) {
			LCD.drawString("Connection failed", 0, 8);
		}
		
		LCD.clear();
		if (conn != null){
			HashMap<String,Integer> t = conn.StartData;
			if (t == null) {
				LCD.drawString("Failed to read transmission", 0, 5);
			} else {
				player = t.get("Role"); // OFFENCE DEFENSE ?
				ballColor = t.get("BC");
				corner = t.get("SC");
				lowerLocX = t.get("ll-x");	//lower left location of row of balls
				upperLocY = t.get("ll-y");
				upperLocX = t.get("ur-x");
				upperLocY = t.get("ur-y");
				w1 = t.get("w1");
				d1 = t.get("d1");
				d2 = t.get("d2");
			
			}
		} else {
			LCD.drawString("Connection failed", 0, 5);
		}
	}
	public double[] getCorner()	{
		int id = corner;
		double[] result = new double[3];
		if (id == 1)	{
			result[0] = 0;
			result[1] = 0;
			result[2] = 0;
		}
		if (id == 2)	{
			result[0] = 11*tile;
			result[1] = 0;
			result[2] = Math.toRadians(90);
		}
		if (id == 3)	{
			result[0] = 11*tile;
			result[1] = 11*tile;
			result[2] = Math.toRadians(180);
		}
		if (id == 4)	{
			result[0] = 0;
			result[1] = 11*tile;
			result[2] = Math.toRadians(270);
		}
		return result;
	}

}
