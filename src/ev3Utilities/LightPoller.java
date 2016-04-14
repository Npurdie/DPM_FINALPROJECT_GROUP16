package ev3Utilities;

import lejos.robotics.SampleProvider;

/**
 * The class provides the methods to access data from the light sensor
 */
public class LightPoller extends Thread {
	private SampleProvider ls;
	private float[] lsData;
	private double lsReflection;
	private Object lock;

	/**
	 * @param ls
	 *            The Sample Provider
	 * @param lsData
	 *            Float array of the Light Sensor Data
	 */
	public LightPoller(SampleProvider ls, float[] lsData) {
		this.ls = ls;
		this.lsData = lsData;
		lock = new Object();
	}

	/**
	 * Run method
	 */
	public void run() {
		double reflection;
		while (true) {
			ls.fetchSample(lsData, 0); // acquire data
			reflection = lsData[0];
			try {
				Thread.sleep(25);
			} catch (Exception e) {
			} // Poor man's timed sampling
			synchronized (lock) {
				lsReflection = reflection;
			}
		}
	}

	/**
	 * Return the the light sensor reflection when polled
	 *
	 * @return A double representing the reflection polled
	 */
	public double getReflection() {
		double reflection;
		synchronized (lock) {
			reflection = this.lsReflection;
		}
		return reflection;
	}
}
