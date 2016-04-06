package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/** This object coordinates the Defender case of the competition */
public class Defender {
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double width;
	private double wheelRadius;
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;

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
			USLocalizer uslocalizer, LightLocalizer lightlocalizer) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.width = width;
		this.wheelRadius = wheelRadius;
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
	}

	/**
	 * Initializes the defence sequence
	 */
	public void startDefense() {
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);

		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();

		// travel to location where the balls are held
		navigator.travelTo(navigator.tile * 6, navigator.tile * 6, true);
		lsl.doLocalization();
	}
}
