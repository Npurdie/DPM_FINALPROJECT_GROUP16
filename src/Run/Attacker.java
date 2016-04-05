package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
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

	/**
	 * The Attacker stores a reference to the left motor, right motor, claw motor, launcher motor, the EV3's width, wheel radius
	 * , odometer, light poller, navigator, us localizer, light localizer and the launcher. This class calls the appropriate
	 * methods and objects to complete the attacker portion of the challenge
	 *
	 * @param leftMotor The left motor object
	 * @param rightMotor The right motor object
	 * @param clawmotor The motor that actuates the claw
	 * @param launcherMotor The motor that shoots the balls
	 * @param width The width of the EV3's chassis
	 * @param wheelRadius The radius of the EV3's wheels
	 * @param odometer The Odometer
	 * @param lightPoller The light poller
	 * @param navigator The Navigator
	 * @param UsLocalizer The ultrasonic localizer
	 * @param lightlocalizer The light lozalizer
	 * @param launcher The launcher class
	 */
	public Attacker(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor clawMotor, EV3LargeRegulatedMotor launcherMotor, double width, double wheelRadius,
			Odometer odometer, LightPoller lightPoller, Navigation navigator, USLocalizer uslocalizer,
			LightLocalizer lightlocalizer, Launcher launcher) {
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
		//this.ballLoc = ballLoc;
	}

	/**
	 * Initializes the attack sequence
	 */
	public void startAttack() {
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);

		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();
		
		leftMotor.setAcceleration(2000);
		rightMotor.setAcceleration(2000);
		// perform the light sensor localization
		lsl.doLocalization();
		odometer.setX(0);
		odometer.setY(0);
		odometer.setTheta(0);

		//travel to location where the balls are held
//		navigator.travelTo(ballLoc[0],ballLoc[1],true);
//		double[] corner = lsl.pickCorner();
//		lsl.doLocalization(corner[0],corner[1]);
//		navigator.travelTo(ballLoc[0],ballLoc[1],false);
		leftMotor.setAcceleration(1000);
		rightMotor.setAcceleration(1000);
				
		navigator.travelTo(navigator.tile * 5, navigator.tile * 5, true);
		lsl.doLocalization(navigator.tile * 5, navigator.tile * 5);
		navigator.travelTo(navigator.tile * 6 - 25, navigator.tile * 5 + 11.43, false);
		navigator.turnTo(0);
		launcher.lowerScooper();
		navigator.travelForwardDistance(25,80);
		launcher.raiseScooper();
		navigator.shootDirection(0, 3);
		launcher.shootBall(3);
	}

}
