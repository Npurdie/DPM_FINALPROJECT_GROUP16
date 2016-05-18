package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import ev3Utilities.Launcher;

/** This object coordinates the Attacker case of the competition */
public class Attacker {
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;
	private Launcher launcher;
	private boolean rightSide;

	// Wifi variables
	private int cornerID;
	private double[] cornerLoc;
	private double[] ballLoc;
	private double defLine;

	// Field Parameter (7 = BETA DEMO 11 = FINAL DEMO)
	public static final double largeCoord = 11;

	/**
	 * The Attacker stores a reference to the left motor, right motor, claw
	 * motor, launcher motor, the EV3's width, wheel radius , odometer, light
	 * poller, navigator, us localizer, light localizer and the launcher. This
	 * class calls the appropriate methods and objects to complete the attacker
	 * portion of the challenge
	 *
	 * @param leftMotor
	 *            The left motor object
	 * @param rightMotor
	 *            The right motor object
	 * @param clawmotor
	 *            The motor that actuates the claw
	 * @param launcherMotor
	 *            The motor that shoots the balls
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
	 * @param launcher
	 *            The launcher class
	 */
	public Attacker(EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor clawMotor,
			EV3LargeRegulatedMotor launcherMotor, double width,
			double wheelRadius, Odometer odometer, LightPoller lightPoller,
			Navigation navigator, USLocalizer uslocalizer,
			LightLocalizer lightlocalizer, Launcher launcher, int cornerID,
			double[] cornerLoc, double[] ballLoc, double goalWidth,
			double defLine, double forwLine) {
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
		this.launcher = launcher;
		this.cornerID = cornerID;
		this.cornerLoc = cornerLoc;
		this.ballLoc = ballLoc;
		this.defLine = defLine;

	}

	/**
	 * Initializes the attack sequence
	 */
	public void startAttack() {

		localize();
		navigate();
		if (ballLoc[0] > (largeCoord-1)/2) {
			rightSide = true;
		}
		else {
			rightSide = false;
		}
		retrieveBall(rightSide);
		
		shootBalls();
		navigator.travelTo(0, largeCoord*navigator.tile, false);
	}

	private void setOdometryValues(double[] cornerValues) {
		odometer.setX(cornerValues[0]);
		odometer.setY(cornerValues[1]);
		odometer.setTheta(cornerValues[2]);

	}
	
	/**
	 * Starts the ev3 localizing
	 */
	private void localize() {
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);
		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer,
				lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();

		while (!lsl.lslDONE) {
		}
		// Initialize odometry readings to localized coordinates.
		setOdometryValues(this.cornerLoc);

	}

	/**
	 * Starts the ev3 navigating.
	 * Contains a case that deals specifically with all four different start corners.
	 */
	private void navigate() {

		switch (cornerID) {
		case 1:
			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile, true);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile);

			Sound.beep();
			
			goToBalls();

			break;
		case 2:
			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile, true);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile);

			Sound.beep();
			
			goToBalls();

			break;
		case 3:
			navigator.travelTo((largeCoord - 2) * navigator.tile,
					0 * navigator.tile, false);
			lsl.doLocalization((largeCoord - 2) * navigator.tile,
					0 * navigator.tile);

			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile, false);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile);

			Sound.beep();
			
			goToBalls();
			
			break;
		case 4:
			navigator.travelTo(1 * navigator.tile, 0 * navigator.tile, true);
			lsl.doLocalization(1 * navigator.tile, 0 * navigator.tile);

			navigator.travelTo((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile, true);
			lsl.doLocalization((largeCoord + 1) / 2 * navigator.tile,
					2 * navigator.tile);

			Sound.beep();
			
			goToBalls();

			break;

		}
	}

	/**
	 * Navigates the ev3 to the location of the balls.
	 */
	public void goToBalls() {
		if (ballLoc[0] < ((largeCoord-1)/2) * navigator.tile) {
			navigator.travelTo(0 * navigator.tile, 0 * navigator.tile, false);
			lsl.doLocalization(0 * navigator.tile, 0 * navigator.tile);

			navigator.travelTo(ballLoc[0] + 2 * navigator.tile, ballLoc[1],
					true);
			lsl.doLocalization(ballLoc[0] + 2 * navigator.tile, ballLoc[1]);
		} else {
			navigator.travelTo((largeCoord-1) * navigator.tile, 0 * navigator.tile, true);
			lsl.doLocalization((largeCoord-1) * navigator.tile, 0 * navigator.tile);

			navigator.travelTo(ballLoc[0] - 1 * navigator.tile, ballLoc[1],
					false);
			lsl.doLocalization(ballLoc[0] - 1 * navigator.tile, ballLoc[1]);
		}
	}


	/**
	 * triggers the sequence that picks up the balls.
	 * @param rightSide This boolean indicates to the robot which side of the field the
	 * balls are located.
	 */
	private void retrieveBall(boolean rightSide) {
		if (!rightSide) {
			navigator.travelTo(ballLoc[0] + (2 * navigator.tile) - 5, ballLoc[1] + 23, false);
			navigator.turnTo(180);
			navigator.travelForwardDistance(14, 100);
			launcher.lowerScooper();
			navigator.travelForwardDistance(4, 50);
			launcher.raiseScooper();
			navigator.travelBackwardDistance(20, 250);
			navigator.travelTo(ballLoc[0] + 2 * navigator.tile, ballLoc[1],
					true);
			lsl.doLocalization(ballLoc[0] + 2 * navigator.tile, ballLoc[1]);
		} else {
			navigator.travelTo(ballLoc[0] - 25, ballLoc[1] + 23, false);
			navigator.turnTo(0);
			navigator.travelForwardDistance(14, 100);
			launcher.lowerScooper();
			navigator.travelForwardDistance(4, 50);
			launcher.raiseScooper();
			navigator.travelBackwardDistance(20, 250);
			navigator.travelTo(ballLoc[0] - 1 * navigator.tile, ballLoc[1],
					true);
			lsl.doLocalization(ballLoc[0] - 1 * navigator.tile, ballLoc[1]);
		}
	}

	/**
	 * Navigates the ev3 to the appropriate location and orientation for shooting the balls.
	 */
	private void shootBalls() {
		navigator.travelTo((largeCoord - 1) / 2 * navigator.tile, largeCoord
				* navigator.tile - (defLine+2*navigator.tile), false);
		navigator.shootDirection((largeCoord - 1) / 2 * navigator.tile,
				largeCoord * navigator.tile);
		launcher.shootBall(3);
	}
}