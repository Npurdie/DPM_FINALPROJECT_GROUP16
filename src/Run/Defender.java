package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Defender {
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double width;
	private double wheelRadius;
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;

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
