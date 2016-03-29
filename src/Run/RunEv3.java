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

public class RunEv3 {

	// Static Resources:
	// Left motor connected to outputA
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	private static final EV3LargeRegulatedMotor clawMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
	private static final EV3LargeRegulatedMotor launcherMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final Port usPort1 = LocalEV3.get().getPort("S4");	
	private static final Port usPort2 = LocalEV3.get().getPort("S3");
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.7;
	public static final double LIGHTSENSOR_WIDTH = 12.5;
	
	public static void main(String[] args) {
		
		//Setup ultrasonic sensor 
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		@SuppressWarnings("resource")							    	// Because we don't bother to close this resource
		SensorModes usSensorF = new EV3UltrasonicSensor(usPort1);
		SensorModes usSensorL = new EV3UltrasonicSensor(usPort2);
		SampleProvider usValueF = usSensorF.getMode("Distance");
		SampleProvider usValueL = usSensorL.getMode("Distance");	
		float[] usDataF = new float[usValueF.sampleSize()];				// colorData is the buffer in which data are returned
		float[] usDataL = new float[usValueL.sampleSize()];
		UltrasonicPoller usPoller = new UltrasonicPoller(usValueF, usValueL, usDataF, usDataL);
		usPoller.start();
		
		//Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");
		float[] colorData = new float[colorValue.sampleSize()];			// colorData is the buffer in which data are returned
		
		// setup the odometer, display and navigation
		Odometer odo = new Odometer(leftMotor,rightMotor,WHEEL_RADIUS,TRACK);
		Navigation navigator = new Navigation(leftMotor,rightMotor,WHEEL_RADIUS,TRACK,odo, usPoller);
		USLocalizer usl = new USLocalizer(odo, usValueF, usDataF,navigator);
		LightPoller lightPoller = new LightPoller(colorValue, colorData);
		LCDInfo lcd = new LCDInfo(odo, lightPoller);
		LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData, navigator);
		
		Launcher launcher = new Launcher(clawMotor, launcherMotor);
		
		//ParseWifi pw = new ParseWifi();
		
		//int buttonChoice = Button.waitForAnyPress();
		
		//if (buttonChoice == Button.ID_DOWN) {

	/*	if(pw.getRole()){
			Defender defender = new Defender(leftMotor, rightMotor, TRACK, WHEEL_RADIUS, odo, lightPoller, navigator, usl, lsl);
			defender.startDefense();
		} else { */
			Attacker attacker = new Attacker(leftMotor, rightMotor, clawMotor, launcherMotor, TRACK, WHEEL_RADIUS, odo, lightPoller, navigator, usl, lsl,launcher);
			attacker.startAttack();
//		}

			
			
				
		//}
			
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);		
	}
}
