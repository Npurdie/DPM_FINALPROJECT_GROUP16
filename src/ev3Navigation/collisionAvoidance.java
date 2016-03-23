package ev3Navigation;


import ev3Odometer.Odometer;
import ev3Utilities.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

/**
* This object detects unexpected objects to help the EV3 avoid collisions
* @author Nick Purdie
* @version 1.0
* @since   2016-03-16
*/
public class collisionAvoidance	{

	//--------- User defined variables ------------
	private double TURNSPEED = 100;
	private double ACC = 1000;
	//---------------------------------------------

	private Odometer odometer;
	private SampleProvider colorValue;
	private UltrasonicPoller ultraSonicPoller;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;

	private double wheelRadius;
	private double wheelBase;

	private Object lock = new Object();
	
	/**
	* collision avoidance stores a reference to the odometer, ultrasonic poller, left motor, right motor, the EV3's wheel radius and chassis width.
	*
	* @param odometer The Odometer
	* @param ultrasonicPoller The Ultrasonic Poller
	* @param leftMotor The left motor instance
	* @param rightMotor The right motor instance
	* @param wheelRadius The radius of the EV3's wheels
	* @param wheelBase The width of the EV3's chassis
	*/
	public collisionAvoidance(Odometer odometer, UltrasonicPoller ultraSonicPoller, EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius, double wheelBase)	{
		this.odometer = odometer;
		this.ultraSonicPoller  = ultraSonicPoller;
		this.wheelBase = wheelBase;
		this.wheelRadius = wheelRadius;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	/**
	* This method checks if an object is in the path of the EV3
	*
	* @param distance The maximum distance of an object that is determined to be in the trajectory of the EV3
	* @return A boolean where true = object detected, false = object not detected
	*/
	public boolean detectedObject(int distance)	{
		if( ultraSonicPoller.getForwardUsDistance() < distance)
		{
			return true;
		}
		return false;
	}

	/**
	* This method return the object distance while also returning 0 if there is nothing detected
	*
	* @return A double that represents the distance of the object detected in centimeters
	*/
	public double getObjectDistance()	{
		int distance = ultraSonicPoller.getForwardUsDistance();
		if(distance < 255)
			return distance;
		return 0.0;
	}

	/**
	* This method allows the EV3 to avoid an object while travelling to the desired coordinate
	*
	* @param x The x coordinate of the desired location
	* @param y The y coordinate of the desired location
	*/
	public void avoidObject(double x, double y)	{
		
	}
}

