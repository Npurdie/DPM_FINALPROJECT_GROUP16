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
	private static int TURN_SPEED = 175;
	private static int FORWARDSPEED = 400;
	private final double sensorPosition = 12.5; // distance from robot center to
												// light sensor
	private boolean gridLine = false;
	private double finalAngleOffset = Math.toRadians(9);

	public LightLocalizer(Odometer odometer, SampleProvider colorSensor,
			float[] colorData, Navigation navigation) {
		this.odometer = odometer;
		this.navigator = navigation;
	}

	//second version of light localization that skips finding a gridline intersection
	public void doLocalization(double x, double y) {
		double gridLines[] = new double[4]; // stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;
		
		navigator.travelTo(x, y,false);
		navigator.turnTo(Math.toRadians(45));
		gridLine = false;

		for (int i = 0; i < gridLines.length; i++) { // for every line
			while (!gridLine) {
				navigator.turnRight(TURN_SPEED);
			}
			gridLines[i] = odometer.getTheta();
			// turn 10 degrees to make sure the same line is not picked up on
			// next iteration
			Sound.beep();
			navigator.turnTo(odometer.getTheta() - Math.toRadians(10));
			gridLine = false;
		}

		if (gridLines[0] < Math.PI) { // wrap-around angle
			gridLines[0] += 2 * Math.PI;
		}

		dx = gridLines[1] - gridLines[3]; // calculate dx
		dy = gridLines[0] - gridLines[2]; // calculate dy

		odometer.setX(-1 * (sensorPosition * Math.cos(dx / 2))); // recalculate
																	// and set x
																	// and y
																	// position
		odometer.setY(-1 * (sensorPosition * Math.cos(dy / 2)));

		odometer.setTheta(odometer.getTheta() - (gridLines[3])
				- ((gridLines[1] - gridLines[3]) / 2) + (Math.PI) - finalAngleOffset); // calculate
																	// and set
																	// theta
		navigator.travelTo(0, 0,false);
		navigator.turnTo(0); // finished localization
		
		odometer.setX(x);
		odometer.setY(y);

	}
	
	public void doLocalization() {
		double gridLines[] = new double[4]; // stores the angles of all 4 lines
		double dx = 0;
		double dy = 0;

		while (!gridLine) {
			navigator.travelForwards(FORWARDSPEED);
		}
		
		navigator.travelBackwardDistance(sensorPosition);

		navigator.turnTo(Math.toRadians(45));
		gridLine = false;

		for (int i = 0; i < gridLines.length; i++) { // for every line
			while (!gridLine) {
				navigator.turnRight(TURN_SPEED);
			}
			gridLines[i] = odometer.getTheta();
			// turn 10 degrees to make sure the same line is not picked up on
			// next iteration
			Sound.beep();
			navigator.turnTo(odometer.getTheta() - Math.toRadians(10));
			gridLine = false;
		}

		if (gridLines[0] < Math.PI) { // wrap-around angle
			gridLines[0] += 2 * Math.PI;
		}

		dx = gridLines[1] - gridLines[3]; // calculate dx
		dy = gridLines[0] - gridLines[2]; // calculate dy

		odometer.setX(-1 * (sensorPosition * Math.cos(dx / 2))); // recalculate
																	// and set x
																	// and y
																	// position
		odometer.setY(-1 * (sensorPosition * Math.cos(dy / 2)));

		odometer.setTheta(odometer.getTheta() - (gridLines[3])
				- ((gridLines[1] - gridLines[3]) / 2) + (Math.PI) - finalAngleOffset); // calculate
																	// and set
																	// theta
		navigator.travelTo(0, 0,false);
		navigator.turnTo(0); // finished localization

	}
	
	public double[] pickCorner()	{
		double[] corner = new double[2];
		double currX = odometer.getX();
		double currY = odometer.getY();
		double tile = navigator.tile;
		corner[0] = currX - currX%tile;
		corner[1] = currY - currY%tile;
		
		return corner;
	}

	public void foundGridLine() {
		gridLine = true;
	}

}