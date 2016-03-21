package ev3Tests;

import ev3Odometer.LCDInfo;
import ev3Odometer.Odometer;
import ev3Odometer.OdometryCorrection;
import ev3Tests.SquareDriver;
import ev3Utilities.LightPoller;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightSensorTest {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("D"));
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RADIUS = 2.08;
	public static final double TRACK = 15.7;
	public static final double LIGHTSENSOR_WIDTH = 12.5;

	public static void main(String[] args) {
		// test the motors going forward over the lines
		// get values from LightPoller
		// Store value into a stack (push into stack)
		// get value of reflection again
		// get the value of the reflection in the stack - take difference
		// between currentReflection and previousReflection
		// use the difference - update odometer?
		// pop old reflection out, push new reflection in
		
		@SuppressWarnings("resource")
		// Because we don't bother to close this resource
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");
		float[] colorData = new float[colorValue.sampleSize()]; 

		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		odo.start();

		LightPoller lightPoller = new LightPoller(colorValue, colorData);
		lightPoller.start();
		
		LightSensorDerivative lsDerivative = new LightSensorDerivative(odo, lightPoller);
		lsDerivative.start();

		LCDInfo lcd = new LCDInfo(odo, lightPoller);

		int buttonChoice = Button.waitForAnyPress();
		if (buttonChoice == Button.ID_DOWN) {

			(new Thread() {
				public void run() {
					SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS,
							WHEEL_RADIUS, TRACK, 2);
				}
			}).start();
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);

	}

}
