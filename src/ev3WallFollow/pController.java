package ev3WallFollow;

import ev3Utilities.UltrasonicController;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
* The class pController allows the EV3 to follow walls and obstacles using a proporitonal control algorithm
*
* @author Nick Purdie
* @version 1.0
* @since   2016-03-16
*/
public class PController implements UltrasonicController	{
	
	private final int bandCenter, bandwidth;
	private final int motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance;
	private int filterControl;

	private final int PConstant = 10;
	private int correctionMax = 80;
	
	/**
	* The pController stores a reference to the left motor and right motor
	*
	* @param leftMotor The left motor instance
	* @param rightMotor The right motor instance
	*/
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(motorStraight);					// Initalize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}

		int error = distance - bandCenter;
		int delta = pValue(error);

		//continue straight, robot is within deadBand
		if (Math.abs(error) <= bandwidth) {		
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
			leftMotor.forward();
			rightMotor.forward();
		}

		//too close to wall
		else if (error < 0) {
			leftMotor.setSpeed(motorStraight+delta);
			rightMotor.setSpeed(motorStraight-delta);
			leftMotor.forward();
			rightMotor.forward();
		}

		//too far from wall
		if (error > bandwidth) {
			leftMotor.setSpeed(motorStraight -delta);
			rightMotor.setSpeed(motorStraight + delta);
			leftMotor.forward();
			rightMotor.forward();
		}
	}

	public int pValue(int error)	{
		error = Math.abs(error);
		int correction = PConstant*error;
		if (correction > motorStraight)	{
			return correctionMax;
		}
		return correction;
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

}