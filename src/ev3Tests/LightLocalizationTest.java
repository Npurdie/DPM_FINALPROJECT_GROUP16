package ev3Tests;

import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import ev3Odometer.LCDInfo;
import ev3Navigation.Navigation;
import ev3Localization.LightLocalizer;
import lejos.hardware.*;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.*;
import lejos.robotics.SampleProvider;

public class LightLocalizationTest {
	// Static Resources:
	// Left motor connected to outputA
	// Right motor connected to output D
	// Ultrasonic sensor port connected to input S1
	// Color sensor port connected to input S2
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	// private static final Port usPort1 = LocalEV3.get().getPort("S4");
	// private static final Port usPort2 = LocalEV3.get().getPort("S3");
	private static final Port colorPort = LocalEV3.get().getPort("S1");
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.7;
	public static final double LIGHTSENSOR_WIDTH = 12.5;

	public static void main(String[] args) {
		// Setup color sensor
		// 1. Create a port object attached to a physical port (done above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize
		// operating mode
		// 4. Create a buffer for the sensor data
		SensorModes colorSensor = new EV3ColorSensor(colorPort);
		SampleProvider colorValue = colorSensor.getMode("Red");
		float[] colorData = new float[colorValue.sampleSize()]; // colorData is
																// the buffer in
																// which data
																// are returned

		// setup the odometer and display and navigation
		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		odo.start();
		Navigation navigator = new Navigation(leftMotor, rightMotor, WHEEL_RADIUS, TRACK, odo, null);

		// perform the light sensor localization
		LightPoller lightPoller = new LightPoller(colorValue, colorData);
		lightPoller.start();

		LCDInfo lcd = new LCDInfo(odo, lightPoller);

		LightLocalizer lsl = new LightLocalizer(odo, colorValue, colorData, navigator);
		LightSensorDerivative lsd = new LightSensorDerivative(odo, lightPoller, lsl);

		lsd.start();
		lsl.doLocalization();

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			;
		System.exit(0);
	}
}
