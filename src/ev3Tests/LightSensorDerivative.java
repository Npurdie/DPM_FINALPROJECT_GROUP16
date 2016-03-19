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
	private LightPoller leftLP;
	private LightPoller rightLP;

	/**
	 * This object adjusts the value odometer by tracking the grid lines crossed
	 *
	 * @param odometer
	 *            The Odometer
	 */
	public LightSensorDerivative(Odometer odometer, LightPoller leftLP,
			LightPoller rightLP) {
		this.odometer = odometer;
		this.leftLP = leftLP;
		this.rightLP = rightLP;

	}

	/**
	 * Run method
	 */
	public void run() {
		// long correctionStart, correctionEnd;
		Stack<Double> leftDerivStack = new Stack<Double>();
		Stack<Double> rightDerivStack = new Stack<Double>();

		leftDerivStack.push(leftLP.getReflection());
		rightDerivStack.push(rightLP.getReflection());

		while (true) {
			// compare current reflection with the reflection in the stack
			double oldLReflection = leftDerivStack.pop();
			double oldRReflection = rightDerivStack.pop();

			double currLReflection = leftLP.getReflection();
			double currRReflection = rightLP.getReflection();

			leftDerivStack.push(currLReflection);
			rightDerivStack.push(currRReflection);

			double leftDiff = oldLReflection - currLReflection;
			double rightDiff = oldRReflection - currRReflection;

			/*if (leftDiff > 0.15) {
				Sound.beep();
			}*/
			if (rightDiff > 0.15) {
				Sound.buzz();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}

		}
	}

}