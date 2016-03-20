//package ev3Tests;
//
//import ev3Odometer.LCDInfo;
//import ev3Odometer.Odometer;
//import ev3Odometer.OdometryCorrection;
//import ev3Tests.SquareDriver;
//import ev3Utilities.LightPoller;
//import ev3Utilities.LightSensorDerivative;
//import lejos.hardware.Button;
//import lejos.hardware.ev3.LocalEV3;
//import lejos.hardware.motor.EV3LargeRegulatedMotor;
//import lejos.hardware.port.Port;
//import lejos.hardware.sensor.EV3ColorSensor;
//import lejos.hardware.sensor.SensorModes;
//import lejos.robotics.SampleProvider;
//
//public class LightSensorTest {
//
//	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
//			LocalEV3.get().getPort("A"));
//	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
//			LocalEV3.get().getPort("D"));
//	private static final Port colorPortL = LocalEV3.get().getPort("S1");
//	public static final double WHEEL_RADIUS = 2.08;
//	public static final double TRACK = 15.7;
//	public static final double LIGHTSENSOR_WIDTH = 12.5;
//
//	public static void main(String[] args) {
//		// test the motors going forward over the lines
//		// get values from LightPoller
//		// Store value into a stack (push into stack)
//		// get value of reflection again
//		// get the value of the reflection in the stack - take difference
//		// between currentReflection and previousReflection
//		// use the difference - update odometer?
//		// pop old reflection out, push new reflection in
//		
//		@SuppressWarnings("resource")
//		// Because we don't bother to close this resource
//		SensorModes colorSensorL = new EV3ColorSensor(colorPortL);
//		SampleProvider colorValueL = colorSensorL.getMode("Red");
//		float[] colorDataL = new float[colorValueL.sampleSize()]; 
//
//		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
//		odo.start();
//
//		LightPoller leftLP = new LightPoller(colorValueL, colorDataL);
//		leftLP.start();
//		
//		LightSensorDerivative lsDerivative = new LightSensorDerivative(odo, leftLP);
//		lsDerivative.start();
//
//		//LCDInfo lcd = new LCDInfo(odo, leftLP, rightLP);
//
//		int buttonChoice = Button.waitForAnyPress();
//		if (buttonChoice == Button.ID_DOWN) {
//
//			(new Thread() {
//				public void run() {
//					SquareDriver.drive(leftMotor, rightMotor, WHEEL_RADIUS,
//							WHEEL_RADIUS, TRACK, 2);
//				}
//			}).start();
//		}
//
//		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
//			;
//		System.exit(0);
//
//	}
//
//}
