/*
 * Odometer.java
 */

package ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This object keeps tracks of the rotation of both wheels to perpetually keep
 * track of x and y coordinates as well as heading
 */
public class Odometer extends Thread {
	// robot position
	private double x, y, theta, totalDistance;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;
	private static final double PI = Math.PI;

	// lock object for mutual exclusion
	private Object lock;

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private static int lastTachoL; // most recent Tacho L
	private static int lastTachoR; // most recent Tacho R
	private static int currTachL; // current L
	private static int currTachR; // current R
	private double wheelBase; // wheelbase
	private double wheelRadius; // wheel radius

	/**
	 * The Navigator stores a reference to the left motor, right motor,
	 * wheelRadius, chassis width, odometer and collision avoidance
	 *
	 * @param leftMotor
	 *            The left motor object
	 * @param rightMotor
	 *            The right motor object
	 * @param wheelRadius
	 *            The radius of the EV3's wheels
	 * @param wheelBase
	 *            The width of the EV3's chassis
	 */
	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double wheelRadius,
			double wheelBase) {
		x = 0.0;
		y = 0.0;
		theta = Math.PI / 2;
		lock = new Object();
		this.leftMotor = leftMotor; // passed motor object from main
		this.rightMotor = rightMotor; //
		this.wheelBase = wheelBase; // passed wheelbase from main so it can be
									// changed in one place
		this.wheelRadius = wheelRadius; // passed wheel radius from main so it
										// can be changed in one place
		totalDistance = 0.0;
	}

	/** Run the odometer */
	public void run() {
		long updateStart, updateEnd;
		leftMotor.resetTachoCount();
		lastTachoL = leftMotor.getTachoCount();
		rightMotor.resetTachoCount();
		lastTachoR = leftMotor.getTachoCount();

		while (true) {
			double rwDist, lwDist, deltaD, deltaT = 0; // initialize
																// necessary
																// variables
			updateStart = System.currentTimeMillis();

			currTachL = leftMotor.getTachoCount(); // get current tach values
			currTachR = rightMotor.getTachoCount();

			rwDist = Math.PI * wheelRadius * (currTachR - lastTachoR) / 180; // compare
																				// to
																				// last
																				// tach
																				// values
																				// +
																				// find
																				// distance
																				// travel
			lwDist = Math.PI * wheelRadius * (currTachL - lastTachoL) / 180;

			lastTachoR = currTachR; // for next iteration
			lastTachoL = currTachL;

			deltaD = 0.5 * (rwDist + lwDist); // average dist of 2 wheels
			deltaT = (rwDist - lwDist) / wheelBase; // opposite / adjacent

			synchronized (lock) {
				// update theta
				theta += deltaT;
				// bounds of theta
				if (theta < 0) {
					theta += 2 * PI;
				}
				if (theta > 2 * PI) {
					theta -= 2 * PI;
				}
				// update distance as well as x and y coordinates
				totalDistance = totalDistance + deltaD;
				setX(x + deltaD * Math.cos(theta));
				setY(y + deltaD * Math.sin(theta));
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	/**
	 * Obtain the current position of the EV3
	 *
	 * @param position
	 *            Passes the position array to the method.
	 * @param update
	 *            Array indicates which parameters to update
	 *            (x=[0],y=[1],theta=[2])
	 */
	public void getPosition(double[] position, boolean[] update) {
		synchronized (lock) { // ensure that the values don't change while the
								// odometer is running
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = Math.toDegrees(theta);
		}
	}

	/**
	 * Obtain the current x coordinate
	 *
	 * @return A double that represents the EV3's current x coordinate
	 */
	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	/**
	 * Obtain the current y coordinate
	 *
	 * @return A double that represents the EV3's current y coordinate
	 */
	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	/**
	 * Obtain the current heading
	 *
	 * @return A double that represents the EV3's current theta(heading)
	 */
	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	/**
	 * Obtain the total distance traveled by the EV3
	 *
	 * @return A double that represents the total distance traveled by the EV3
	 */
	public double getDistance() {
		double result;

		synchronized (lock) {
			result = totalDistance;
		}

		return result;
	}

	/**
	 * Set the EV3's total distance.
	 *
	 * @param d
	 *            The distance to set the EV3's totalDistance to.
	 */
	public void setDistance(double d) {
		synchronized (lock) {
			this.totalDistance = d;
		}
	}

	/**
	 * Set the current position of the EV3
	 *
	 * @param position
	 *            Array containing the x,y and theta values to be updated
	 * @param update
	 *            Array indicates which parameters to update
	 *            (x=[0],y=[1],theta=[2])
	 */
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	/**
	 * Set the EV3's x coordinate
	 *
	 * @param x1
	 *            The value to which to set the EV3's current x coordinate
	 */
	public void setX(double x1) {
		synchronized (lock) {
			this.x = x1;
		}
	}

	/**
	 * Set the EV3's y coordinate
	 *
	 * @param y1
	 *            The value to which to set the EV3's current y coordinate
	 */
	public void setY(double y1) {
		synchronized (lock) {
			this.y = y1;
		}
	}

	/**
	 * Set the EV3's heading
	 *
	 * @param theta1
	 *            The value to which to set the EV3's current theta(heading)
	 */
	public void setTheta(double theta1) {
		synchronized (lock) {
			this.theta = theta1;
		}
	}
}