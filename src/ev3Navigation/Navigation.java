package ev3Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.UltrasonicPoller;
import ev3Localization.LightLocalizer;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

/** This object contains all methods necessary for the EV3 to travel to coordinates on the grid */
public class Navigation extends Thread	{
	//-------- user defined--------------
	private final int FORWARDSPEED = 250;
	private final int TURNSPEED = 175;
	private final int ACCELERATION = 2016;
	private final double travelToError = 1.0;
	private final double travelAngleError = 1.0;
	private final double correctDistThreshold = 15;
	private final double correctAngleThreshold = 3;
	private final double recolalizeThreshold = 130;
	//----------------------------------

	//variables
	public final double tile = 30.48;
	private final double PI = Math.PI;
	private boolean isNavigating = false;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double wheelBase;
	private double wheelRadius;
	private UltrasonicPoller ultraSonicPoller;
	private LightLocalizer lsl;

	/**
	 * The Navigator stores a reference to the left motor, right motor, wheelRadius, chassis width, odometer and collision avoidance
	 *
	 * @param leftMotor The left motor object
	 * @param rightMotor The right motor object
	 * @param wheelRadius The radius of the EV3's wheels
	 * @param width The width of the EV3's chassis
	 * @param odometer The Odometer
	 * @param avoidCollisions Boolean warns the EV3 whether or not to attempt to avoid obstacles in it's path
	 */
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius, double width, Odometer odometer, UltrasonicPoller ultraSonicPoller)	{
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;	
		this.wheelBase = width;
		this.wheelRadius = wheelRadius;
		this.ultraSonicPoller = ultraSonicPoller;

		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
	}
	
	/**
	* Travel to the coordinate specified on the grid
	*
	* @param x The location in centimeters of the coordinate on the x axis
	* @param y The location in centimeters of the coordinate on the y axis
	*/
	public void travelTo(double x, double y, boolean avoidCollisions)	{
		this.isNavigating=true;
		CollisionAvoidance collisionAvoidance = new CollisionAvoidance(odometer, ultraSonicPoller, leftMotor, rightMotor, wheelRadius, wheelBase);
		while (Math.abs(x - odometer.getX()) > travelToError || Math.abs(y - odometer.getY()) > travelToError )	{
			if (avoidCollisions)	{
				if (collisionAvoidance.detectedObject(20))	{
					double[] currLoc = {odometer.getX(), odometer.getY()};
					double[] corner = lsl.pickCorner();
					lsl.doLocalization(corner[0],corner[1]);
					travelTo(currLoc[0],currLoc[1],false);
					turnTo(odometer.getTheta() + Math.toRadians(90));
					collisionAvoidance.avoidObject(3, 20);
				}
			}
			navigateTo(x,y);
			if (odometer.getDistance() < recolalizeThreshold)	{
				double[] corner = lsl.pickCorner();
				lsl.doLocalization(corner[0],corner[1]);
			}
		}
		Sound.beep();
		this.isNavigating=false;
		leftMotor.stop();
		rightMotor.stop();
	}

	/**
	* A helper method for travel to. This method handles making regular corrections to the EV3's heading
	* while it travels to the coordinate specified
	*
	* @param x The location in centimeters of the coordinate on the x axis
	* @param y The location in centimeters of the coordinate on the y axis
	*/
	private void navigateTo(double x, double y)	{
		double angle = findAngle(x - odometer.getX() , y - odometer.getY());

		if (Math.abs(Math.toDegrees(smallestAngle(angle, odometer.getTheta()))) > travelAngleError) {
			turnTo(angle);
		}
		else	{
			if (Math.sqrt(Math.pow(Math.abs(x - odometer.getX()),2) + Math.pow(Math.abs(y - odometer.getY()),2)) <= correctAngleThreshold) {
				leftMotor.setSpeed((int)(0.4*FORWARDSPEED));	//for small corrections, correct by only 20% of turn speed
				rightMotor.setSpeed((int)(0.4*FORWARDSPEED));
			}
			else	{
				leftMotor.setSpeed(FORWARDSPEED);		//travel straight
				rightMotor.setSpeed(FORWARDSPEED);
			}
			leftMotor.forward();
			rightMotor.forward();
		}
	}
	
	/**
	* When called, this method will make the EV3 turn to face the heading it was given
	*
	* @param theta The angle to which the EV3 will turn
	*/
	public void turnTo(double theta)	{		//robot turns to face this heading
		theta = theta % (2*PI);
		double error = theta - odometer.getTheta();
		double correction = smallestAngle(theta, odometer.getTheta());

		//makes small corrections slower
		if (Math.abs(error) <= Math.toRadians(correctDistThreshold)) {
			leftMotor.setSpeed((int)(TURNSPEED));
			rightMotor.setSpeed((int)(TURNSPEED));
		}
		else	{
			leftMotor.setSpeed(TURNSPEED);
			rightMotor.setSpeed(TURNSPEED);
		}

		leftMotor.rotate(-convertAngle(wheelRadius, wheelBase, Math.toDegrees(correction)), true);
		rightMotor.rotate(convertAngle(wheelRadius, wheelBase, Math.toDegrees(correction)), false);
	}
	
	/**
	* This method calculates the angle the EV3 would have to face in order to travel to a specified point
	*
	* @param dx The difference between the EV3's current location and the coordinate it is traveling to
	* @param dy The difference between the EV3's current location and the coordinate it is traveling to
	* @return A double the represents the angle the EV3 must turn to in order to travel to the desired
	* coordinate in a straight line
	*/
	private double findAngle(double dx, double dy)	{
		double finalAngle = 0.0;
		if (dx >= 0) {
			finalAngle =  Math.atan(dy/dx);
		}
		if (dx < 0 && dy >=0) {
			finalAngle = Math.atan(dy/dx) + PI;
		}
		if (dx < 0 && dy < 0) {
			finalAngle = Math.atan(dy/dx) - PI;
		}
		return finalAngle;
	}

	/**
	* This method minimizes the angle the EV3 must turn to face a new heading.
	*
	* @param fAngle The final angle the EV3 should face
	* @param currentAngle The angle the EV3 is currently facing
	* @return A double that represents the smallest angle of rotation
	*/
	private double smallestAngle(double fAngle, double currentAngle)	{
		double dTheta = fAngle - currentAngle;

		if (Math.abs(dTheta) <= PI) {
			return dTheta;
		}
		if (dTheta < -PI) {
			return (dTheta + 2*PI);
		}
		if (dTheta > PI) {
			return (dTheta - 2*PI);
		}
		return 0;
	}
	
	/**
	* This method returns a boolean that represents the current state of the EV3.
	* Specifically it indicates whether the EV3 is currently navigating towards a coordinate.
	*
	* @return Boolean is true if the EV3 is currently performing navigation, else returns false
	*/
	public boolean isNavigating()	{
		return isNavigating;
	}

	/**
	* This method sets both motors appropriately to make a left turn.
	*
	* @param turnSpeed The speed at which to perform the turn
	*/
	public void turnLeft(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.backward();
		rightMotor.forward();
	}

	/**
	* This method sets both motors appropriately to make a right turn.
	*
	* @param turnSpeed The speed at which to perform the turn
	*/
	public void turnRight(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.forward();
		rightMotor.backward();
	}

	/**
	* This method stops both motors.
	*/
	public void stopMotors(){
		leftMotor.setSpeed(0);
		rightMotor.setSpeed(0);
		
		leftMotor.forward();
		rightMotor.forward();
	}

	/**
	* This method sets both motors appropriately to travel backwards.
	*
	* @param turnSpeed The speed at which to perform travel.
	*/
	public void travelBackwards(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	/**
	* This method sets the right motor to rotate forward
	*
	* @param turnSpeed The speed at which to perform travel.
	*/
	public void rotateRightWheel(int turnSpeed)	{
		rightMotor.setSpeed(turnSpeed);
		rightMotor.forward();
	}
	
	/**
	* This method sets the left motor to rotate forward
	*
	* @param turnSpeed The speed at which to perform travel.
	*/
	public void rotateLeftWheel(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		leftMotor.forward();
	}
	
	/**
	* This method sets both motors appropriately to travel forwards.
	*
	* @param turnSpeed The speed at which to perform travel.
	*/
	public void travelForwards(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.forward();
		rightMotor.forward();
	}
	
	public void travelBackwardDistance(double distance) {
		leftMotor.setSpeed(TURNSPEED);
		rightMotor.setSpeed(TURNSPEED);
		leftMotor.rotate(-convertDistance(wheelRadius, distance), true);
		rightMotor.rotate(-convertDistance(wheelRadius, distance), false);
		
		stopMotors();
	}

	/**
	* This method converts the desired turn angle into the distance the left or right wheel has to rotate
	*
	* @param radius The EV3's wheel radius
	* @param width The EV3's chassis width
	* @param angle The desired turn angle
	*/
	private static int convertAngle(double radius, double width, double angle){
		return (int) ((180.0 * Math.PI * width * angle / 360.0) / (Math.PI * radius));
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	public void setLSL(LightLocalizer lsl)	{
		this.lsl = lsl;
	}
	
}
