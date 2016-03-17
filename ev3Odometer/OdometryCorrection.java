/* 
 * OdometryCorrection.java
 */
package ev3Odometer;

import lejos.hardware.Sound;
import lejos.hardware.port.SensorPort;
import lejos.robotics.SampleProvider;
import lejos.hardware.sensor.EV3ColorSensor;


/**
* This object adjusts the value of the odometer by tracking the grid lines crossed
*
* @author Nick Purdie
* @version 1.0
* @since   2016-03-16
*/
public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	private EV3ColorSensor cs;
	private SampleProvider colorValue;
	private float colorValues[] = new float[3];	//contains sample readings
	private int lineCounter = 0;
	private static final double foo = 7;	//distance of sensor from center of robot
	private static final double brightnessThreshold = 0.40;

	/**
	 * This object adhjusts the value  odomter by tracking the grid lines crossed
	 *
	 * @param odometer The Odometer
	 */
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		this.cs = new EV3ColorSensor(SensorPort.S1);
		this.colorValue = cs.getMode("Red");
	}

	/**
	* Run method
	*/
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			colorValue.fetchSample(colorValues, 0);	//fetch sample of light readings and store in colorValues[]
			if (colorValues[0] < brightnessThreshold)	{	//tested threshold that detects black lines
				lineCounter ++;
				Sound.beep();		//gives us feedback to make sure all lines are being detected
				correctOdometer();	//correct the odometer
			}
			
			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}

	/**
	* Correct the odometer. Done automatically when called
	*/
	private void correctOdometer(){
		
		if (lineCounter == 1 || lineCounter == 2 || lineCounter == 5 || lineCounter == 6)	{	//Y axis portions of the square
			if (lineCounter==1)
				odometer.setY(15.15-foo);
			if (lineCounter==2)
				odometer.setY(45.15-foo);
			if (lineCounter==5)
				odometer.setY(45.15+foo);
			if (lineCounter==6)
				odometer.setY(15.15+foo);	
		}
		else	{

			if (lineCounter== 3)	//X axis portions of the square
				odometer.setX(15.15-foo);
			if (lineCounter== 4)
				odometer.setX(45.15-foo);
			if (lineCounter== 7)
				odometer.setX(45.15+foo);
			if (lineCounter == 8)
				odometer.setX(15.15+foo);
		}
	}
}