package ev3Tests;

import ev3Odometer.Odometer;
import ev3Odometer.LCDInfo;
import ev3Navigation.Navigation;
import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class localizationTest {
	// Static Resources:
		// Left motor connected to outputA
		// Right motor connected to output D
		// Ultrasonic sensor port connected to input S1
		// Color sensor port connected to input S2
		private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
		private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
		private static final Port usPort1 = LocalEV3.get().getPort("S4");	
		private static final Port usPort2 = LocalEV3.get().getPort("S3");
		private static final Port colorPortL = LocalEV3.get().getPort("S1");
		private static final Port colorPortR = LocalEV3.get().getPort("S2");	
		public static final double WHEEL_RADIUS = 2.2;
		public static final double TRACK = 15.37;
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
			
			//Setup color sensor
			// 1. Create a port object attached to a physical port (done above)
			// 2. Create a sensor instance and attach to port
			// 3. Create a sample provider instance for the above and initialize operating mode
			// 4. Create a buffer for the sensor data
			SensorModes colorSensorL = new EV3ColorSensor(colorPortL);
			SensorModes colorSensorR = new EV3ColorSensor(colorPortR);
			SampleProvider colorValueL = colorSensorL.getMode("Red");
			SampleProvider colorValueR = colorSensorR.getMode("Red");
			float[] colorDataL = new float[colorValueL.sampleSize()];			// colorData is the buffer in which data are returned
			float[] colorDataR = new float[colorValueR.sampleSize()];
			
			// setup the odometer and display and navigation
			Odometer odo = new Odometer(leftMotor,rightMotor,WHEEL_RADIUS,TRACK);
			odo.start();
			//LCDInfo lcd = new LCDInfo(odo);
			Navigation navigator = new Navigation(leftMotor,rightMotor,WHEEL_RADIUS,TRACK,odo, false);
			
			// perform the ultrasonic localization
			//USLocalizer usl = new USLocalizer(odo, usValueF, usDataF, USLocalizer.LocalizationType.FALLING_EDGE,navigator);
			//usl.doLocalization();
			
			//Button.waitForAnyPress();
			
			// perform the light sensor localization
			LightLocalizer lsl = new LightLocalizer(odo, colorValueL,colorValueR, colorDataL, colorDataR, navigator);
			lsl.doLocalization(0,0);			
			
			while (Button.waitForAnyPress() != Button.ID_ESCAPE);
			System.exit(0);		
		}
}
