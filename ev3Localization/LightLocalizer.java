package ev3Localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;

public class LightLocalizer {
	private Odometer odometer;
	private SampleProvider colorSensor;
	private LightPoller lp;
	private float[] colorData;
	private Navigation navigator;
	private static int TURN_SPEED = 80;
	private final double lightThreshHold = 0.4;	//for detecting grid lines
	private final double sensorPosition = 12.5;	//distance from robot center to light sensor
	private boolean gridLine = false;
	
	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData, Navigation navigation) {
		this.odometer = odometer;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.navigator = navigation;
	}
	
	public void doLocalization(int x, int y) {
		double gridLines [] = new double[4];	//stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;

		navigator.travelTo(x,y);
		navigator.turnTo(Math.toRadians(45));
		gridLine = false;
		
		for (int i=0; i< gridLines.length; i++)	{	//for every line
			while (!gridLine)	{
				navigator.turnRight(TURN_SPEED);
			}
			gridLines [i] = odometer.getTheta();
			//turn 10 degrees to make sure the same line is not picked up on next iteration
			navigator.turnTo(odometer.getTheta() - Math.toRadians(10));
			gridLine = false;
		}
		navigator.stopMotors();
		
		if (gridLines[0] < Math.PI)	{	//wrap-around angle
			gridLines[0] += 2*Math.PI;
		}
		
		dx = gridLines[1] - gridLines[3];	//calculate dx
		dy = gridLines[0] - gridLines[2];	//calculate dy
		
		odometer.setX(sensorPosition * Math.cos(dx)/2);	//recalculate and set x and y position
		odometer.setY(sensorPosition * Math.cos(dx)/2);
		
		odometer.setTheta(odometer.getTheta() + Math.PI-(gridLines[1] - gridLines[3])/2 - gridLines[3]); //calculate and set theta
		navigator.travelTo(0,0);
		navigator.turnTo(0); //finished localization
		
	}
	
	public void foundGridLine()	{
		gridLine = true;
	}

}
