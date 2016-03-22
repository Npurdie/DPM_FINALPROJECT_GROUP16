package ev3Tests;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Driver {
	private static final int FORWARD_SPEED = 200;
	private static final int ROTATE_SPEED = 100;

	public static void driveSquare(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double leftRadius,
			double rightRadius, double width, int numTiles) {
		// reset the motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {
				leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		for (int i = 0; i < 4; i++) {
			// drive forward two tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(leftRadius, 30.48 * numTiles),
					true);
			rightMotor.rotate(convertDistance(rightRadius, 30.48 * numTiles),
					false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(leftRadius, width, 90.0), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, 90.0), false);
		}
	}

	public static void driveStraight(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double leftRadius,
			double rightRadius, double width, int numTiles) {

		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {
				leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		// drive forward two tiles
		leftMotor.setSpeed(FORWARD_SPEED);
		rightMotor.setSpeed(FORWARD_SPEED);

		leftMotor.rotate(convertDistance(leftRadius, 30.48 * numTiles), true);
		rightMotor
				.rotate(convertDistance(rightRadius, 30.48 * numTiles), false);

	}

	public static void driveCircle(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double leftRadius,
			double rightRadius, double width) {
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {
				leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(2000);
		}

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}

		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);

		leftMotor.rotate(convertAngle(leftRadius, width, 360.0), true);
		rightMotor.rotate(-convertAngle(rightRadius, width, 360.0), false);
		
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		
		leftMotor.forward();
		rightMotor.forward();

	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}
