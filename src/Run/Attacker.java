package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import ev3Utilities.ParseWifi;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import ev3Utilities.Launcher;

/** This object coordinates the Attacker case of the competition */
public class Attacker {

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3LargeRegulatedMotor clawMotor, launcherMotor;
	private double width;
	private double wheelRadius;
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;
	private double[] ballLoc;
	private Launcher launcher;
	private ParseWifi pw;

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
	public Attacker(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor clawMotor, EV3LargeRegulatedMotor launcherMotor, double width, double wheelRadius,
			Odometer odometer, LightPoller lightPoller, Navigation navigator, USLocalizer uslocalizer,
			LightLocalizer lightlocalizer, Launcher launcher) {// , ParseWifi
																// pw) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.width = width;
		this.wheelRadius = wheelRadius;
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
		this.launcher = launcher;
		// this.pw = pw;
		// this.ballLoc = ballLoc;
	}

	/**
	 * Initializes the attack sequence
	 */
	public void startAttack() {
		Sound.beep();
		Sound.beep();
		Sound.beep();
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);

		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();
		while(!lsl.lslDONE){
		}
		setOdometryValues(new double[] { navigator.tile*0, navigator.tile*0, Math.toRadians(0) });
		// setOdometryValues(pw.getCorner());
		
		// ------------------------------
		// DEMO CODE (for video)
		navigator.travelTo(navigator.tile*4, navigator.tile*4, true);
		lsl.doLocalization(navigator.tile*4, navigator.tile*4);
		navigator.travelTo(navigator.tile*5 - 25, navigator.tile*4 + 22 , false);
		navigator.turnTo(0);
		navigator.travelForwardDistance(15.5, 100);
		launcher.lowerScooper();
		navigator.travelForwardDistance(2.5, 50);
		launcher.lowerScooper(2);
		navigator.travelForwardDistance(2.5, 50);
		launcher.lowerScooper(2);
		launcher.raiseScooper();
		navigator.travelBackwardDistance(20,250);
		navigator.turnTo(Math.toRadians(245));
		launcher.shootBall(3);
		
		
		
		
//		navigator.travelTo(navigator.tile*5, navigator.tile*5, true);
//		lsl.doLocalization(navigator.tile*5, navigator.tile*5);
//		navigator.travelTo(navigator.tile*4, navigator.tile*0, false);
//		lsl.doLocalization(navigator.tile*4,navigator.tile*0);
//		navigator.travelTo(navigator.tile*5 - 25, navigator.tile*0 + 23 , false);
//		navigator.turnTo(0);
//		navigator.travelForwardDistance(15.5, 100);
//		launcher.lowerScooper();
//		navigator.travelForwardDistance(4, 50);
//		launcher.raiseScooper();
//		navigator.travelBackwardDistance(20,250);
//		navigator.shootDirection(3, 3);
		
		// travel to location where the balls are held
		// navigator.travelTo(ballLoc[0],ballLoc[1],true);
		// double[] corner = lsl.pickCorner();
		// lsl.doLocalization(corner[0],corner[1]);
		// navigator.travelTo(ballLoc[0],ballLoc[1],false);
	//	navigator.travelTo(navigator.tile*0, navigator.tile*5, false);
	//	navigator.travelTo(navigator.tile*5, navigator.tile*5, false);
	//	navigator.travelTo(navigator.tile*5, navigator.tile*0, false);
	//	navigator.travelTo(navigator.tile*2, navigator.tile*2, false);
	//	lsl.doLocalization(navigator.tile*2, navigator.tile*2);
	
		/*
		navigator.travelTo(navigator.tile - 25, 23 , false);
		navigator.turnTo(0);
		navigator.travelForwardDistance(17.5, 100);
		launcher.lowerScooper();
		navigator.travelForwardDistance(5, 50);
		launcher.raiseScooper();
		navigator.travelBackwardDistance(20,250);
		navigator.shootDirection(0, 3);
	*/
	//	launcher.shootBall(3);
	}

	private void setOdometryValues(double[] cornerValues) {
		odometer.setX(cornerValues[0]);
		odometer.setY(cornerValues[1]);
		odometer.setTheta(cornerValues[2]);

	}
}
