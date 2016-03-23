package ev3Localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;
import ev3Odometer.Odometer;
import ev3Navigation.Navigation;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;

public class LightLocalizer {
	private Odometer odometer;
	private Navigation navigator;
	private static int TURN_SPEED = 80;
	private final double sensorPosition = 12.5;	//distance from robot center to light sensor
	private boolean gridLine = false;
	
	public LightLocalizer(Odometer odometer, SampleProvider colorSensor, float[] colorData, Navigation navigation) {
		this.odometer = odometer;
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
		
		dx = gridLines[3] - gridLines[1];	//calculate dx
		dy = gridLines[2] - gridLines[0];	//calculate dy
		
		odometer.setX(-(sensorPosition * Math.cos(dy)/2));	//recalculate and set x and y position
		odometer.setY(-(sensorPosition * Math.cos(dx)/2));
		
		odometer.setTheta(odometer.getTheta() + (gridLines[3]-Math.PI) - (Math.abs(gridLines[3]-gridLines[1])/2) - (Math.PI/2)); //calculate and set theta
		navigator.travelTo(0,0);
		navigator.turnTo(0); //finished localization
		
	}
	
	public void foundGridLine()	{
		gridLine = true;
	}

}