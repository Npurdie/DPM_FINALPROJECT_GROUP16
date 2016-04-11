package ev3Utilities;

import lejos.robotics.SampleProvider;

/**
 * The ultrasonic Poller class return ultrasonic polling data
 */
public class UltrasonicPoller extends Thread {
	private SampleProvider forwardUS;
	private SampleProvider rightUS;
	private SampleProvider leftUS;
	private float[] forwardUSData;
	private float[] rightUSData;
	private float[] leftUSData;

	private int rightUSSensorDistance = 50;
	private int forwardUSSensorDistance = 50;
	private int leftUSSensorDistance = 50;
	private Object lock;

	/**
	 * @param us
	 *            The Sample Provider
	 * @param usData
	 *            Float array of the Ultrasonic Data
	 */
	public UltrasonicPoller(SampleProvider fUS, SampleProvider rUS, SampleProvider lUS, float[] fUSData,
			float[] rUSData, float[] lUSData) {
		this.forwardUS = fUS;
		this.rightUS = rUS;
		this.leftUS = lUS;
		this.forwardUSData = fUSData;
		this.rightUSData = rUSData;
		this.leftUSData = lUSData;
		lock = new Object();
	}

	// Sensors now return floats using a uniform protocol.
	/**
	 * Run method
	 */
	public void run() {
		int rDistance, fDistance, lDistance;
		while (true) {
			forwardUS.fetchSample(forwardUSData, 0); // acquire data
			rightUS.fetchSample(rightUSData, 0);
			leftUS.fetchSample(leftUSData, 0);
			fDistance = (int) (forwardUSData[0] * 100.0); // extract from
															// buffer, cast to
															// int
			rDistance = (int) (rightUSData[0] * 100.0);
			lDistance = (int) (leftUSData[0] * 100.0);
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
			synchronized (lock) {
				rightUSSensorDistance = rDistance;
				forwardUSSensorDistance = fDistance;
				leftUSSensorDistance = lDistance;
			}
		}
	}

	/**
	 * Return the right ultrasonic distance when polled
	 *
	 * @return An integer representing the distance polled
	 */
	public int getRightUsDistance() {
		int distance;
		synchronized (lock) {
			distance = rightUSSensorDistance;
		}
		return distance;
	}

	/**
	 * Return the forward ultrasonic distance when polled
	 *
	 * @return An integer representing the distance polled
	 */
	public int getForwardUsDistance() {
		int distance;
		synchronized (lock) {
			distance = forwardUSSensorDistance;
		}
		return distance;
	}

	public int getLeftUSDistance() {
		int distance;
		synchronized (lock) {
			distance = leftUSSensorDistance;
		}
		return distance;
	}
	
	public void turnOffSensors(){

	}

}
