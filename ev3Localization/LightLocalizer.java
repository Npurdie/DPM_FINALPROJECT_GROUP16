package ev3Localization;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;

/** This object gives the EV3 the ability to localize using the light sensor.
* @author Nick Purdie
* @version 1.0
* @since   2016-03-16
*/
public class LightLocalizer {

	private Odometer odometer;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigation navigator;
	private static int TURN_SPEED = 80;
	private final double lightThreshHold = 0.4;	//for detecting grid lines
	private final double sensorPosition = 7;	//distance from robot center to light sensor
	
	/**
	 * The light localizer stores a reference to the Odometer, the Navigator, the colour sensor's sample provider
	 * and an array containing the color data.
	 *
	 * @param odometer The Odometer
	 * @param navigation The Navigator
	 * @param colorsensor The SampleProvider
	 * @param colorData The color data array
	 */
	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData, Navigation navigation) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigation;
	}

	/**
	* Perform the localization. The ev3 will travel to the point (0,0) and rotate
	* while keeping track of the angles at which is encounters grid lines. This will
	* allow the ev3 to accurately determine it's current location as well as heading.
	*/
	public void doLocalization() {
		// drive to location listed in tutorial
		double gridLines [] = new double[4];	//stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;
		
		navigator.travelTo(0,0);
		navigator.turnTo(Math.toRadians(35));
		
		for (int i=0; i< gridLines.length; i++)	{	//for every line
			while (!detectedGridLine())	{
				navigator.turnRight(TURN_SPEED);
			}
			gridLines [i] = odometer.getTheta();
			Sound.beep();
			//turn 10 degrees to make sure the same line is not picked up on next iteration
			navigator.turnTo(odometer.getTheta() - Math.toRadians(10));	
		}
		navigator.stopMotors();
		
		if (gridLines[0] < Math.PI)	{	//wrap-around angle
			gridLines[0] += 2*Math.PI;
		}
		
		dy = gridLines[1] - gridLines[3];	//calculate dx
		dx = gridLines[0] - gridLines[2];	//calculate dy
		
		odometer.setX(sensorPosition * Math.cos(dy/2));	//recalculate and set x and y position
		odometer.setY(sensorPosition * Math.cos(dx/2));
		
		odometer.setTheta(odometer.getTheta() + gridLines[3]+ Math.toRadians(180) +dy/2); //calculate and set theta
		navigator.travelTo(0,0);
		navigator.turnTo(0); //finished localization
		
	}
	
	/**
	* This method samples the colour sensor and return true if when it encounters one.
	*
	* @return Boolean = true if a grid line is detected. False otherwise
	*/
	private boolean detectedGridLine()	{
		colorSensor.fetchSample(colorData, 0);
		if (colorData[0] < lightThreshHold)	{
			return true;
		}
		else {
			return false;
		}
	}

}
