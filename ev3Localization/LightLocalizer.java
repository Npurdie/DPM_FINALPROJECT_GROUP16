package ev3Localization;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;

public class LightLocalizer {
	private Odometer odometer;
	private SampleProvider colorSensor;
	private float[] colorData;
	private Navigation navigator;
	private static int TURN_SPEED = 80;
	private final double lightThreshHold = 0.4;	//for detecting grid lines
	private final double sensorPosition = 7;	//distance from robot center to light sensor
	
	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData, Navigation navigation) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigation;
	}
	
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
