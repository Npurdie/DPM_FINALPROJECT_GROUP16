package ev3Utilities;
import lejos.robotics.SampleProvider;

/**
* The ultrasonic Poller class return ultrasonic polling data
*/
public class UltrasonicPoller extends Thread{
	private SampleProvider us;
	private UltrasonicController cont;
	private float[] usData;
	private int usSensorDistance = 50;
	private Object lock;
	
	/**
	 * @param us The Sample Provider
	 * @param usData Float array of the Ultrasonic Data
	 * @param cont The Ultrasonic Controller
	 */
	public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont) {
		this.us = us;
		this.cont = cont;
		this.usData = usData;
		lock = new Object();
	}

//  Sensors now return floats using a uniform protocol.
	/**
	* Run method
	*/
	public void run() {
		int distance;
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			//cont.processUSData(distance);						// now take action depending on value
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
			synchronized(lock)	{
				usSensorDistance = distance;
			}
		}
	}

	/**
	* Return the the ultrasonic distance when polled
	*
	* @return An integer representing the distance polled
	*/
	public int getUsDistance()	{
		int distance;
		synchronized(lock)	{
			distance = usSensorDistance;
		}
		return distance;
	}

}
