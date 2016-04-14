package ev3Tests;

import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.Launcher;
import ev3Utilities.UltrasonicPoller;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class BallPlatformCompatibilityTest {
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
	public static final double LIGHTSENSOR_WIDTH = 12.5;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		@SuppressWarnings("resource") // Because we don't bother to close this
										// resource
		SensorModes usSensorF = new EV3UltrasonicSensor(usPort1);
		SensorModes usSensorR = new EV3UltrasonicSensor(usPort2);
		SensorModes usSensorL = new EV3UltrasonicSensor(usPort3);
		SampleProvider usValueF = usSensorF.getMode("Distance");
		SampleProvider usValueR = usSensorR.getMode("Distance");
		SampleProvider usValueL = usSensorL.getMode("Distance");
		float[] usDataF = new float[usValueF.sampleSize()]; // colorData is the
															// buffer in which
															// data are returned
		float[] usDataR = new float[usValueR.sampleSize()];
		float[] usDataL = new float[usValueL.sampleSize()];
		UltrasonicPoller usPoller = new UltrasonicPoller(usValueF, usValueR, usValueL, usDataF, usDataR, usDataL);
		usPoller.start();

		// colorData is the buffer in which data are returned

		// setup the odometer, display and navigation
		Odometer odo = new Odometer(leftMotor, rightMotor, WHEEL_RADIUS, TRACK);
		Navigation navigator = new Navigation(leftMotor, rightMotor, WHEEL_RADIUS, TRACK, odo, usPoller);

		Launcher launcher = new Launcher(clawMotor, launcherMotor);
		launcher.lowerScooper();
		navigator.travelForwardDistance(4, 50);
		launcher.raiseScooper();
		launcher.shootBall(3);

	}

}
