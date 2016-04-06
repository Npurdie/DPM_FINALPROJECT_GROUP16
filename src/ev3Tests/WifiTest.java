package ev3Tests;


import java.io.IOException;
import java.util.HashMap;

import Run.Attacker;
import ev3Utilities.ParseWifi;
import ev3Utilities.WifiConnection;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;

public class WifiTest {
	// example call of the transmission protocol
	// The print function is just for debugging to make sure data is received
	// correctly

	// *** INSTRUCTIONS ***
	// There are two variables to set manually on the EV3 client:
	// 1. SERVER_IP: the IP address of the computer running the server
	// application
	// 2. TEAM_NUMBER: your project team number

	private static TextLCD LCD = LocalEV3.get().getTextLCD();

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		ParseWifi pw = new ParseWifi();
		System.out.println("Corner : " + pw.getCorner());
		System.out.println("Role : " + pw.getRole());
		System.out.println("Ball location : " + pw.getBallLoc());
	}
}
