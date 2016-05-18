package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import ev3Utilities.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/** This object coordinates the Defender case of the competition */
public class Defender {
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;
	private int cornerID;
	private double[] cornerLoc;
	private double goalWidth;
	public static final double largeCoord = 11;

	/**
	 * The Defender stores a reference to the left motor, right motor, the EV3's
	 * width, wheel radius , odometer, light poller, navigator, us localizer and
	 * the light localizer. This class calls the appropriate methods and objects
	 * to complete the attacker portion of the challenge
	 * 
	 * @param leftMotor
	 *            The left motor object
	 * @param rightMotor
	 *            The right motor object
	 * @param width
	 *            The width of the EV3's chassis
	 * @param wheelRadius
	 *            The radius of the EV3's wheels
	 * @param odometer
	 *            The Odometer
	 * @param lightPoller
	 *            The light poller
	 * @param navigator
	 *            The Navigator
	 * @param UsLocalizer
	 *            The ultrasonic localizer
	 * @param lightlocalizer
	 *            The light lozalizer
	 */
	public Defender(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double width,
			double wheelRadius, Odometer odometer, LightPoller lightPoller, Navigation navigator,
			USLocalizer uslocalizer, LightLocalizer lightlocalizer, int cornerID, double[] cornerLoc, double[] ballLoc,
			double goalWidth, double defLine, double forwLine, UltrasonicPoller USPoller) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
		this.cornerID = cornerID;
		this.cornerLoc = cornerLoc;
		this.goalWidth = goalWidth;
	}

	/**
	 * Initializes the defence sequence
	 */
	public void startDefense() {
		localize();
		navigate();
		defend();
	}

	/**
	 * Sets the odometer to the appropriate values depending on the corner it starts in.
	 * @param cornerValues The x,y and theta values of the corner the ev3 is starting in.
	 */
	private void setOdometryValues(double[] cornerValues) {
		odometer.setX(cornerValues[0]);
		odometer.setY(cornerValues[1]);
		odometer.setTheta(cornerValues[2]);

	}

	/**
	 * Inities the ev3's localizing sequence.
	 */
	private void localize() {
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);
		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();

		while (!lsl.lslDONE) {
		}
		// Initialize odometry readings to localized coordinates.
		setOdometryValues(this.cornerLoc);

	}

	/**
	 * Navigates from initial localization to the attacker's zone and finally
	 * towards the balls.
	 */
	private void navigate() {

		switch (cornerID) {
		case 1:
			navigator.travelTo(2 * navigator.tile, (largeCoord - 2) * navigator.tile, true);
			lsl.doLocalization(2 * navigator.tile, (largeCoord - 2) * navigator.tile);

			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile, false);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile);

			Sound.beep();

			break;
		case 2:

			navigator.travelTo((largeCoord - 3) * navigator.tile, (largeCoord - 2) * navigator.tile, true);
			lsl.doLocalization((largeCoord - 3) * navigator.tile, (largeCoord - 2) * navigator.tile);

			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile, false);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile);

			Sound.beep();

			break;
		case 3:
			navigator.travelTo(8*navigator.tile, 9*navigator.tile, false);
			lsl.doLocalization(8*navigator.tile, 9*navigator.tile);
			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile, false);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile);

			Sound.beep();

			break;
		case 4:

			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile, false);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile);
			Sound.beep();

			break;

		}
	}

	/**
	 * Initializes the defend algorithm. This effectively consists of going back and forth
	 * in front of the goal area.
	 */
	private void defend() {
		odometer.setDistance(0);
		navigator.travelTo((largeCoord - 1) / 2 * navigator.tile + goalWidth / 2, (largeCoord - 3) * navigator.tile,
				false);
		odometer.setDistance(0);
		navigator.turnTo(Math.toRadians(0));
		int i = 0;
		navigator.travelBackwardDistance(10, 200);
		odometer.setDistance(0);
		while (true) {

			if (i > 3) {
				i = 0;
				fixDefense();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			navigator.travelBackwardDistance(goalWidth - 22, 200);
			odometer.setDistance(0);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			navigator.travelForwardDistance(goalWidth - 22, 200);
			odometer.setDistance(0);
			i++;

		}

	}

	private void fixDefense() {
		navigator.travelTo((largeCoord - 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile, false);
		lsl.doLocalization((largeCoord - 1) / 2 * navigator.tile, (largeCoord - 3) * navigator.tile);

		navigator.travelTo((largeCoord - 1) / 2 * navigator.tile + goalWidth / 2, (largeCoord - 3) * navigator.tile,
				false);
		navigator.turnTo(Math.toRadians(0));
	}

}
