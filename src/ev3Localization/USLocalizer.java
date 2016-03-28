package ev3Localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import java.util.Arrays;
import ev3Navigation.Navigation;

/**
 * This object gives the EV3 the ability to localize using the Ultrasonic Sensor
 */
public class USLocalizer {
	
	private static int TURN_SPEED = 400;
	private static int maxDist = 40; // sensor max dist
	private static int dist = 30; // distance from wall for angle calculation
	private final double tile = 30.48; // final variable that keeps track of
										// tile size
	private final double sensorPosition = 3; // distance between sensor and the
												// center of robot

	private Odometer odometer;
	private SampleProvider usSensor;
	private float[] usData;
	private Navigation navigator;

	/**
	 * The Ultrasonic localizer stores a reference to the Odometer, the
	 * ultrasonic sensor sample provider, a float array of ultrasonic data, the
	 * Navigator and the type of localization to be performed.
	 *
	 * @param odometer
	 *            The Odometer
	 * @param navigator
	 *            The Navigator
	 * @param usSensor
	 *            The SampleProvider
	 * @param usData
	 *            The ultrasonic sensor data array
	 * @param locType
	 *            The type of localization to be performed
	 */
	public USLocalizer(Odometer odometer, SampleProvider usSensor, float[] usData, Navigation navigator) {
		this.odometer = odometer;
		this.usSensor = usSensor;
		this.usData = usData;
		this.navigator = navigator;
	}

	/**
	 * Perform the localization. The EV3 will either perform falling edge
	 * localization or rising edge localization by rotating in the corner of the
	 * grid, using the walls to determine it's heading and approximate location.
	 */
	public void doLocalization() {
		
		initializeRobot();
		
		double latchA, latchB; // angle of wall A and Wall B


		while (getFilteredData(10) > dist) {
			navigator.turnRight(TURN_SPEED);
		}
		latchA = (odometer.getTheta());
		Sound.beep();
		navigator.turnLeft(TURN_SPEED); // again turn left for a few second to
										// avoid latching on to the same wall
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		// keep rotating until the robot sees a wall, then latch the angle
		while (getFilteredData(10) > dist) {
			navigator.turnLeft(TURN_SPEED);
		}
		latchB = odometer.getTheta();
		Sound.beep();
		double theta = odometer.getTheta() + calcAngle(latchA, latchB); // new
																		// theta
																		// is
																		// calculated
		odometer.setPosition(new double[] { 0.0, 0.0, theta }, new boolean[] { true, true, true }); // set
																									// theta
		navigator.turnTo(Math.toRadians(45)); // turn to face 0 for demo
		
	}

	/**
	 * Returns value that must be added to the EV3's heading to be accurately
	 * oriented.
	 *
	 * @param a
	 *            The first angle detected while performing the US localization
	 * @param b
	 *            The second angle detected while performing the us localization
	 * @return A double that represents the correction required to the heading
	 */
	private double calcAngle(double a, double b) {
		if (a <= b) {
			return Math.toRadians(45) - (a + b) / 2; // 40 was changed based on
														// experimentation
		} else {
			return Math.toRadians(225) - (a + b) / 2; // 220 was changed base on
														// experimentation
		}
	}

	/**
	 * Returns value that must be added to the EV3's heading to be accurately
	 * oriented.
	 *
	 * @param f
	 *            The sample size of the filtered data to collect
	 * @return A float that represents the filtered average reading of the
	 *         ultrasonic sensor
	 */
	private float getFilteredData(int f) { // functions returns average distance
											// of filter size f
		float data[] = new float[f];
		for (int i = 0; i < f; i++) { // fill an array of size f with US
										// distances
			// sleep between polling
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			usSensor.fetchSample(usData, 0);
			float distance = usData[0] * 100;
			if (distance > maxDist) {
				usData[0] = maxDist;
			}
			data[i] = usData[0] * 100;
		}
		Arrays.sort(data); // sort array
		double median; // calculate median
		if (data.length % 2 == 0)
			median = ((double) data[data.length / 2] + (double) data[data.length / 2 - 1]) / 2;
		else
			median = (double) data[data.length / 2];

		return (float) median;
	}

	public void initializeRobot(){
		// If robot starts off facing wall, rotate clockwise until it no longer sees a wall.
		if (getFilteredData(10) <= maxDist) {
			while (getFilteredData(10) <= maxDist - 3) {
				navigator.turnRight(TURN_SPEED);
			}
			// Once it reaches initial position, 
			// turn right by 35 degrees to avoid picking up wall again
			navigator.turnTo(odometer.getTheta() - Math.toRadians(35));
		} else
		// If the robot is initially not facing any wall.
		{
			while (getFilteredData(10) >= maxDist - 3) {
				navigator.turnLeft(TURN_SPEED);
			}
			// Once it reaches initial position, 
			// turn right by 35 degrees to avoid picking up wall again
			navigator.turnTo(odometer.getTheta() - Math.toRadians(35));
			
		}
			
	}
}