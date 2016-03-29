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

	public void startAttack() {
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);

		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();

		//travel to location where the balls are held
//		navigator.travelTo(ballLoc[0],ballLoc[1],true);
//		double[] corner = lsl.pickCorner();
//		lsl.doLocalization(corner[0],corner[1]);
//		navigator.travelTo(ballLoc[0],ballLoc[1],false);
		navigator.travelTo(navigator.tile * 4, navigator.tile * 5, true);
		lsl.doLocalization(navigator.tile * 4, navigator.tile * 5);
		navigator.travelTo(navigator.tile * 5 - 10, navigator.tile * 5 + 11.43, false);
		navigator.turnTo(0);
		launcher.lowerScooper();
		navigator.travelForwardDistance(10,80);
		launcher.raiseScooper();
		navigator.shootDirection(0, 3);
		launcher.shootBall(3);
	}

}
