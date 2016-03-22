package ev3Tests;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class WheelRadiusTest {
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(
			LocalEV3.get().getPort("D"));
	public static final double WHEEL_RADIUS = 2.05;
	public static final double TRACK = 15.7;

	public static void main(String[] args) {
		int buttonChoice = Button.waitForAnyPress();
		while (true) {
			if (buttonChoice == Button.ID_DOWN) {

				(new Thread() {
					public void run() {
						Driver.driveStraight(leftMotor, rightMotor,
								WHEEL_RADIUS, WHEEL_RADIUS, TRACK, 3);
					}
				}).start();
			}
			if (Button.waitForAnyPress() == Button.ID_ESCAPE) {
				System.exit(0);
			}
		}

	}
}
