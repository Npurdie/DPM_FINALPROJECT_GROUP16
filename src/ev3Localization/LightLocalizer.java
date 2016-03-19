package ev3Localization;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;

/** This object gives the EV3 the ability to localize using the light sensor. */
public class LightLocalizer {

	private Odometer odometer;
	private SampleProvider colorSensorL;
	private SampleProvider colorSensorR;
	private float[] colorDataL;
	private float[] colorDataR;
	private Navigation navigator;
	private static int TURN_SPEED = 80;
	private final double lightThreshHold = 0.265;	//for detecting grid lines
	private final double sensorPosition = 12.5;	//distance from robot center to light sensor
	
	/**
	 * The light localizer stores a reference to the Odometer, the Navigator, the colour sensor's sample provider
	 * and an array containing the color data.
	 *
	 * @param odometer The Odometer
	 * @param colorSensorL The SampleProvider for the left color sensor
	 * @param colorSensorR The SampleProvider for the right color sensor
	 * @param colorDataL The color data array for the left color sensor
	 * @param colorDataR The color data array for the right color sensor
	 * @param navigation The Navigator
	 */
	public LightLocalizer(Odometer odometer, SampleProvider colorSensorL, SampleProvider colorSensorR, float[] colorDataL, float[] colorDataR, Navigation navigation) {
		this.odometer = odometer;
		this.colorSensorL = colorSensorL;
		this.colorSensorR = colorSensorR;
		this.colorDataL = colorDataL;
		this.colorDataR = colorDataR;
		this.navigator = navigation;
	}

	/**
	* Perform the localization. The ev3 will travel to the point (0,0) and rotate
	* while keeping track of the angles at which is encounters grid lines. This will
	* allow the ev3 to accurately determine it's current location as well as heading.
	* 
	* @param x The x coordinate of the location to perform the light localization
	* @param y The y coordinate of the location to perform the light localization
	*/
	public void doLocalization(double x, double y) {
		
		navigator.travelTo(x,y);
		navigator.turnTo(Math.toRadians(90));
		
		while (!detectedGridLineL() || !detectedGridLineR())	{
			navigator.travelForwards(50);
		}
		navigator.stopMotors();
		Sound.beep();
		
		if (detectedGridLineL())	{
			while (!detectedGridLineR())	{
				navigator.rotateRightWheel(50);
			}
			navigator.stopMotors();
			Sound.beep();
		}
		
		if (!detectedGridLineR())	{
			while (!detectedGridLineL())	{
				navigator.rotateLeftWheel(50);
			}
			navigator.stopMotors();
			Sound.beep();
		}
		
		odometer.setX(0);	//recalculate and set x and y position
		odometer.setY(12.5);
		
		odometer.setTheta(0); //calculate and set theta
		
	}
	
	/**
	* This method samples the left colour sensor and returns true if when it encounters one.
	*
	* @return Boolean = true if a grid line is detected. False otherwise
	*/
	private boolean detectedGridLineL()	{
		colorSensorL.fetchSample(colorDataL, 0);
		if (colorDataL[0] < lightThreshHold)	{
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	* This method samples the right colour sensor and returns true if when it encounters one.
	*
	* @return Boolean = true if a grid line is detected. False otherwise
	*/
	private boolean detectedGridLineR()	{
		colorSensorR.fetchSample(colorDataR, 0);
		if (colorDataR[0] < lightThreshHold)	{
			return true;
		}
		else {
			return false;
		}
	}

}
