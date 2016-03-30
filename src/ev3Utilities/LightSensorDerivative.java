package ev3Utilities;

import java.util.Stack;

import ev3Localization.LightLocalizer;
import ev3Odometer.Odometer;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;

public class LightSensorDerivative extends Thread {
	private Odometer odometer;
	private LightPoller lightPoller;
	private boolean foundLine = false;
	private LightLocalizer lsl;

	/**
	 * This object adjusts the value odometer by tracking the grid lines crossed
	 *
	 * @param odometer
	 *            The Odometer
	 */
	public LightSensorDerivative(Odometer odometer, LightPoller lightPoller, LightLocalizer lsl) {
		this.odometer = odometer;
		this.lightPoller = lightPoller;
		this.lsl = lsl;

	}

	/**
	 * Run method
	 */
	public void run() {
		// long correctionStart, correctionEnd;
		Stack<Double> leftDerivStack = new Stack<Double>();

		leftDerivStack.push(lightPoller.getReflection());

		while (true) {
			// compare current reflection with the reflection in the stack
			double oldLReflection = leftDerivStack.pop();

			double currLReflection = lightPoller.getReflection();

			leftDerivStack.push(currLReflection);

			double leftDiff = oldLReflection - currLReflection;

			if (leftDiff > 0.12) { //originally 0.15. tried 0.2 (no detection)
				lsl.foundGridLine();
			}

			try {
				Thread.sleep(25);
			} catch (InterruptedException e) {

			}

		}
	}
	
	public boolean lineDetected()	{
		if (foundLine)	{
			foundLine = false;
			return true;
		}
		return false;
	}

}