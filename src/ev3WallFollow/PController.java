package ev3WallFollow;

import ev3Utilities.UltrasonicController;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * The class pController allows the EV3 to follow walls and obstacles using a
 * proportional control algorithm
 *
 * @author Nick Purdie
 * @version 1.0
 * @since 2016-03-16
 */
public class PController implements UltrasonicController {

	private final int bandCenter, bandwidth;
	private final int motorStraight = 200;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;

	private final int PConstant = 7;
	private int correctionMax = 75;

	/**
	 * The pController stores a reference to the left motor and right motor
	 *
	 * @param leftMotor
	 *            The left motor instance
	 * @param rightMotor
	 *            The right motor instance
	 */
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, int bandCenter,
			int bandwidth) {
		// Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight); // Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	 * This method processes the ultrasonic data by changing the speeds of the
	 * motors proportionally to the distance measured.
	 *
	 * @param distance
	 *            The distance polled by the ultrasonic sensor
	 */
	@Override
	public void processUSData(int distance) {
		int error = distance - bandCenter;
		int delta = pValue(error);

		// continue straight, robot is within deadBand
		if (Math.abs(error) <= bandwidth) {
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}

		// too close to wall
		else if (error < 0 && error>-3){
			rightMotor.setSpeed(motorStraight);
			leftMotor.setSpeed(motorStraight);
			leftMotor.backward();
			rightMotor.backward();
		}else if (error < 0) {
			rightMotor.setSpeed(motorStraight + delta);
			leftMotor.setSpeed(delta);
			leftMotor.backward();
			rightMotor.forward();
		}

		// too far from wall
		if (error > 0) {
			rightMotor.setSpeed(motorStraight - delta);
			leftMotor.setSpeed(motorStraight + delta);
			leftMotor.forward();
			rightMotor.forward();
		}

	}

	/**
	 * The pValue method calculates the proportional correction to be applied to
	 * the motors based on the error value and the p constant.
	 * 
	 * @param error
	 *            The error between the distance to the object and the optimal
	 *            band center.
	 * @return an integer correction to be added or subtracted from the motor
	 *         speed.
	 */
	public int pValue(int error) {
		error = Math.abs(error);
		int correction = PConstant * error;
		if (correction > correctionMax) {
			return correctionMax;
		}
		return correction;
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}