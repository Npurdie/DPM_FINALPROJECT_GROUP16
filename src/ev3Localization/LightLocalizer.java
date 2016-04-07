package ev3Localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;

/**
 * This object contains all methods necessary for the EV3 to localize around a
 * grid intersection using the light sensor
 */
public class LightLocalizer {
	private Odometer odometer;
	private Navigation navigator;
	private static int TURN_SPEED = 200;
	private static int FORWARDSPEED = 400;
	private final double sensorPosition = 12.5; // distance from robot center to
												// light sensor
	private boolean gridLine = false;
	private double finalAngleOffset = Math.toRadians(4);

	/**
	 * The LightLocalizer stores a reference to the Odometer, the Color sensor's
	 * sample provider, the color data array and the navigator
	 *
	 * @param odometer
	 *            The Odometer
	 * @param coloSensor
	 *            The color sensor's sample provider
	 * @param wheelRadius
	 *            The radius of the EV3's wheels
	 * @param coloData
	 *            The float array of data from the color sensor
	 * @param navigation
	 *            The navigator object
	 */
	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData, Navigation navigation) {
		this.odometer = odometer;
		this.navigator = navigation;
	}

	/**
	 * doLocalization performs the localization using the light sensor. It will
	 * travel to the point specified and rotate 360 degrees. It will correct its
	 * odometer and always end facing angle 0.
	 *
	 * @param x
	 *            The x location of where to perform the localization.
	 * @param y
	 *            The y location of where to perform the localization.
	 */
	public void doLocalization(double x, double y) {
		
		Sound.beep();
				
		double gridLines[] = new double[4]; // stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;

		navigator.travelTo(x, y, false);
		navigator.turnTo(Math.toRadians(45), TURN_SPEED);
		gridLine = false;

		for (int i = 0; i < gridLines.length; i++) { // for every line
			while (!gridLine) {
				navigator.turnRight(TURN_SPEED);
			}
			gridLines[i] = odometer.getTheta();
			// turn 10 degrees to make sure the same line is not picked up on
			// next iteration
			Sound.beep();
			navigator.turnBy(10, TURN_SPEED);
			
			gridLine = false;
		}
		if (gridLines[0] < Math.PI) { // wrap-around angle
			gridLines[0] += 2 * Math.PI;
		}

		dx = gridLines[1] - gridLines[3]; // calculate dx
		dy = gridLines[0] - gridLines[2]; // calculate dy

		odometer.setX(-1 * (sensorPosition * Math.cos(dx / 2))); // recalculate
																	// and set x
																	// and y
																	// position
		odometer.setY(-1 * (sensorPosition * Math.cos(dy / 2)));

		odometer.setTheta(odometer.getTheta() - (gridLines[3]) - ((gridLines[1] - gridLines[3]) / 2) + (Math.PI)
				- finalAngleOffset); // calculate
		// and set
		// theta
		navigator.travelTo(0, 0, false);
		navigator.stopMotors();
		navigator.turnTo(Math.toRadians(0), TURN_SPEED); // finished
															// localization
		navigator.stopMotors();
		odometer.setTheta(0);
		gridLine = false;

		odometer.setX(x);
		odometer.setY(y);
		odometer.setDistance(0);

		Sound.twoBeeps();

	}

	/**
	 * Method overloading of class doLocalization that takes no parameters. This
	 * method performs the localization in place.
	 */
	public void doLocalization() {
		
		Sound.beep();
		
		double gridLines[] = new double[4]; // stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;

		while (!gridLine) {
			navigator.travelForwards(FORWARDSPEED);
		}

		navigator.travelBackwardDistance(sensorPosition,FORWARDSPEED);

		navigator.turnTo(Math.toRadians(45), TURN_SPEED);
		gridLine = false;

		for (int i = 0; i < gridLines.length; i++) { // for every line
			while (!gridLine) {
				navigator.turnRight(TURN_SPEED);
			}
			gridLines[i] = odometer.getTheta();
			// turn 10 degrees to make sure the same line is not picked up on
			// next iteration
			Sound.beep();
			navigator.turnBy(10, TURN_SPEED);
			gridLine = false;
		}
		if (gridLines[0] < Math.PI) { // wrap-around angle
			gridLines[0] += 2 * Math.PI;
		}

		dx = gridLines[1] - gridLines[3]; // calculate dx
		dy = gridLines[0] - gridLines[2]; // calculate dy

		odometer.setX(-1 * (sensorPosition * Math.cos(dx / 2))); // recalculate
																	// and set x
																	// and y
																	// position
		odometer.setY(-1 * (sensorPosition * Math.cos(dy / 2)));

		odometer.setTheta(odometer.getTheta() - (gridLines[3]) - ((gridLines[1] - gridLines[3]) / 2) + (Math.PI)
				- finalAngleOffset); // calculate
		// and set
		// theta
		navigator.travelTo(0, 0, false);
		navigator.stopMotors();
		navigator.turnTo(Math.toRadians(0), TURN_SPEED); // finished
															// localization
		navigator.stopMotors();
		odometer.setTheta(0);
		odometer.setDistance(0);

		Sound.twoBeeps();
	}

	/**
	 * pickCorner returns the closest corner of the tile the ev3 is located in.
	 * This is used to find the fastest way to localize.
	 *
	 * @param obstacleCorner
	 *            The integer ID of a corner to avoid returning because an
	 *            object covers it. (0 = ll, 1 = lr, 2 = ur, 3 = ul)
	 */
	public double[] pickCorner() {
		double[][] corner = { new double[2], new double[2], new double[2], new double[2] };
		double currX = odometer.getX();
		double currY = odometer.getY();
		double tile = navigator.tile;
		corner[0][0] = currX - currX % tile;
		corner[0][1] = currY - currY % tile;
		corner[1][0] = currX - currX % tile + tile;
		corner[1][1] = currY - currY % tile;
		corner[2][0] = currX - currX % tile + tile;
		corner[2][1] = currY - currY % tile + tile;
		corner[3][0] = currX - currX % tile;
		corner[3][1] = currY - currY % tile + tile;

		double smallest = tile;
		int foo = 0;
		for (int i = 0; i < 4; i++) {
			double temp = Math.sqrt(Math.pow((currX - corner[i][0]), 2) + Math.pow(currY - corner[i][1], 2));
			if (temp < smallest) {
				smallest = temp;
				foo = i;
			}
		}
		return corner[foo];
	}
	
	/**
	 * localize in corner passed as int
	 *
	 * @param obstacleCorner
	 *            The integer ID of a corner to localize in  (0 = ll, 1 = lr, 2 = ur, 3 = ul)
	 */
/*	public void pickCorner(double [] corner) {
		double currX = odometer.getX();
		double currY = odometer.getY();
		double tile = navigator.tile;
		corner[0] = currX - currX % tile;
		corner[1] = currY - currY % tile;
		//return corner;
		
		double[][] corner = { new double[2], new double[2], new double[2], new double[2] };
		double currX = odometer.getX();
		double currY = odometer.getY();
		double tile = navigator.tile;
		corner[0][0] = currX - currX % tile;
		corner[0][1] = currY - currY % tile;
		corner[1][0] = currX - currX % tile + tile;
		corner[1][1] = currY - currY % tile;
		corner[2][0] = currX - currX % tile + tile;
		corner[2][1] = currY - currY % tile + tile;
		corner[3][0] = currX - currX % tile;
		corner[3][1] = currY - currY % tile + tile;

		return corner[localizeCorner];
	}*/

	/**
	 * foundGridLine sets the gridLine field to true when called
	 */
	public void foundGridLine() {
		gridLine = true;
	}

}