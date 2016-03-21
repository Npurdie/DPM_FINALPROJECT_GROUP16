package ev3Tests;

import java.util.Stack;

import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;

public class LightSensorDerivative extends Thread {
	private Odometer odometer;
	private LightPoller lightPoller;

	/**
	 * This object adjusts the value odometer by tracking the grid lines crossed
	 *
	 * @param odometer
	 *            The Odometer
	 */
	public LightSensorDerivative(Odometer odometer, LightPoller lightPoller) {
		this.odometer = odometer;
		this.lightPoller = lightPoller;

	}

	/**
	 * Run method
	 */
	public void run() {
		// long correctionStart, correctionEnd;
		Stack<Double> derivStack = new Stack<Double>();

		derivStack.push(lightPoller.getReflection());

		while (true) {
			// compare current reflection with the reflection in the stack
			double oldReflection = derivStack.pop();

			double currReflection = lightPoller.getReflection();

			derivStack.push(currReflection);

			double diff = oldReflection - currReflection;

			if (diff > 0.15) {
				Sound.beep();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}

		}
	}

}