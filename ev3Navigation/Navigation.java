//Navigation.java

package ev3Navigation;

import ev3Odometer.Odometer;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;

public class Navigation extends Thread	{
	//-------- user defined--------------
	private final int FORWARDSPEED = 200;
	private final int TURNSPEED = 100;
	private final int ACCELERATION = 2000;
	private final double travelToError = 1.0;
	private final double travelAngleError = 1.0;
	private final double correctDistThreshold = 15;
	private final double correctAngleThreshold = 3;
	//----------------------------------

	//variables
	public final double tile = 30.48;
	private final double PI = Math.PI;
	private boolean isNavigating = false;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double wheelBase;
	private double wheelRadius;
	private boolean avoidCollisions;

	//The Constructor
	public Navigation(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius, double width, Odometer odometer, boolean avoidCollisions)	{
		this.odometer = odometer;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;	
		this.wheelBase = width;
		this.wheelRadius = wheelRadius;
		this.avoidCollisions = avoidCollisions;

		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
	}
	
	public void travelTo(double x, double y)	{
		this.isNavigating=true;
		while (Math.abs(x - odometer.getX()) > travelToError || Math.abs(y - odometer.getY()) > travelToError )	{
			if (avoidCollisions)	{
				//if (collisionAvoidance.detectedObject(30))	{
					//AVOID OBJECT
				//}
			}
			navigateTo(x,y);
		}
		Sound.beep();
		this.isNavigating=false;
		leftMotor.stop();
		rightMotor.stop();
	}

	private void navigateTo(double x, double y)	{
		double angle = findAngle(x - odometer.getX() , y - odometer.getY());
//		System.out.println();
//		System.out.println();
//		System.out.println("turnto " + angle);
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
	
	public void turnTo(double theta)	{		//robot turns to face this heading
		theta = theta % (2*PI);
		double error = theta - odometer.getTheta();
		double correction = smallestAngle(theta, odometer.getTheta());

		//makes small corrections slower
		if (Math.abs(error) <= Math.toRadians(correctDistThreshold)) {
			leftMotor.setSpeed((int)(0.2* TURNSPEED));
			rightMotor.setSpeed((int)(0.2* TURNSPEED));
		}
		else	{
			leftMotor.setSpeed(TURNSPEED);
			rightMotor.setSpeed(TURNSPEED);
		}

		leftMotor.rotate(-convertAngle(wheelRadius, wheelBase, Math.toDegrees(correction)), true);
		rightMotor.rotate(convertAngle(wheelRadius, wheelBase, Math.toDegrees(correction)), false);
	}
	
	private double findAngle(double dx, double dy)	{	//find angle to turn to
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

	//calculate shortest angle robot should turn
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
	
	public boolean isNavigating()	{
		return isNavigating;
	}

	public void turnLeft(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.backward();
		rightMotor.forward();
	}
	public void turnRight(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.forward();
		rightMotor.backward();
	}
	public void stopMotors(){
		leftMotor.stop();
		rightMotor.stop();
	}
	public void travelBackwards(int turnSpeed)	{
		leftMotor.setSpeed(turnSpeed);
		rightMotor.setSpeed(turnSpeed);
		leftMotor.backward();
		rightMotor.backward();
	}
	
	private static int convertAngle(double radius, double width, double angle){
		return (int) ((180.0 * Math.PI * width * angle / 360.0) / (Math.PI * radius));
	}
	
}
