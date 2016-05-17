package ev3Localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;

/**
 * This object contains all methods necessary for the EV3 to localize around a
 * grid intersection using the light sensor
 */
public class LightLocalizer {
	private Odometer odometer;
	private Navigation navigator;
	private static int TURN_SPEED = 180;
	private static int FORWARDSPEED = 400;
	private final double sensorPosition = 15.8; // distance from robot center to
												// light sensor (used to be
												// 12.5)
	private boolean gridLine = false;
	private double finalAngleOffset = Math.toRadians(4);

	public boolean lslDONE = false;

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
			//navigator.turnBy(10, TURN_SPEED);

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

		navigator.travelBackwardDistance(sensorPosition, FORWARDSPEED);

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
			//navigator.turnBy(10, TURN_SPEED);
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
		odometer.setDistance(0);

		navigator.turnTo(0);

		Sound.twoBeeps();

		lslDONE = true;
	}

	/**
	 * foundGridLine sets the gridLine field to true when called
	 */
	public void foundGridLine() {
		gridLine = true;
	}

}