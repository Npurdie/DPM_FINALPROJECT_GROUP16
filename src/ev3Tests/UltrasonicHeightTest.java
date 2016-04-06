package ev3Tests;

import ev3Utilities.UltrasonicPoller;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class UltrasonicHeightTest {
	private static final Port usPort1 = LocalEV3.get().getPort("S4");

	public static void main(String[] args) {
		SensorModes usSensorF = new EV3UltrasonicSensor(usPort1);
		SampleProvider usValueF = usSensorF.getMode("Distance");
		float[] usDataF = new float[usValueF.sampleSize()];
		int buttonChoice = Button.waitForAnyPress();
		UltrasonicPoller usPoller = new UltrasonicPoller(usValueF, usDataF);
		usPoller.start();

		while (buttonChoice == Button.ID_DOWN) {
			System.out.println(usPoller.getUsDistance());
			if (usPoller.getUsDistance() < 30) {
				Sound.beep();
			}

			if (Button.waitForAnyPress() == Button.ID_ESCAPE) {
				System.exit(0);
			}
		}

	}

}
