package Run;

import ev3Localization.*;
import ev3Navigation.*;
import ev3Odometer.*;
import ev3Utilities.*;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;
import java.io.IOException;
import java.util.HashMap;
import ev3Utilities.WifiConnection;

/**
 * This class is used to start the EV3. It is responsible for initializing all
 * objects and sensors as well as receiving all the game parameters over wifi.
 */
public class RunEv3 {

	// Static Resources:
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor launcherMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final Port usPort1 = LocalEV3.get().getPort("S4");
	private static final Port usPort2 = LocalEV3.get().getPort("S3");
	private static final Port usPort3 = LocalEV3.get().getPort("S2");
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.7;
	public static final double LIGHTSENSOR_WIDTH = 17.5; // It was 12.5

	/**
	 * Main method
	 */
	public static void main(String[] args) {

		leftMotor.setAcceleration(0);
		rightMotor.setAcceleration(0);
		clawMotor.setAcceleration(0);
		launcherMotor.setAcceleration(0);

		@SuppressWarnings("resource") // Because we don't bother to close this
										// resource
		SensorModes usSensorF = new EV3UltrasonicSensor(usPort1);
		SensorModes usSensorR = new EV3UltrasonicSensor(usPort2);
		SensorModes usSensorL = new EV3UltrasonicSensor(usPort3);
		SampleProvider usValueF = usSensorF.getMode("Distance");
		SampleProvider usValueR = usSensorR.getMode("Distance");
		SampleProvider usValueL = usSensorL.getMode("Distance");
		float[] usDataF = new float[usValueF.sampleSize()];
		float[] usDataR = new float[usValueR.sampleSize()];
		float[] usDataL = new float[usValueL.sampleSize()];
		UltrasonicPoller usPoller = new UltrasonicPoller(usValueF, usValueR, usValueL, usDataF, usDataR, usDataL);
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");
		float[] colorData = new float[colorValue.sampleSize()];

		usPoller.start();
		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		Navigation navigator = new Navigation(leftMotor, rightMotor, WHEEL_RADIUS, TRACK, odo, usPoller);
		USLocalizer usl = new USLocalizer(odo, usValueL, usDataL, navigator);
		LightPoller lightPoller = new LightPoller(colorValue, colorData);
		LCDInfo lcd = new LCDInfo(odo, lightPoller);
		LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData, navigator);
		Launcher launcher = new Launcher(clawMotor, launcherMotor);

		// Get wifi parameters
		ParseWifi pw = new ParseWifi();

		boolean attacker = pw.getRole();
		int cornerID = pw.getCornerID();
		double[] cornerLoc = pw.getCorner();
		double[] ballLoc = pw.getBallLoc();
		double goalWidth = pw.getGoalWidth();
		double defLine = pw.getDefenderLine();
		double forwLine = pw.getForwardLine();

		if (attacker) {
			Attacker attackerRun = new Attacker(leftMotor, rightMotor, clawMotor, launcherMotor, TRACK, WHEEL_RADIUS,
					odo, lightPoller, navigator, usl, lsl, launcher, cornerID, cornerLoc, ballLoc, goalWidth, defLine,
					forwLine);
			attackerRun.startAttack();
		} else {
			Defender defenderRun = new Defender(leftMotor, rightMotor, TRACK, WHEEL_RADIUS, odo, lightPoller, navigator,
					usl, lsl, cornerID, cornerLoc, ballLoc, goalWidth, defLine, forwLine, usPoller);
			defenderRun.startDefense();

		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}

}
