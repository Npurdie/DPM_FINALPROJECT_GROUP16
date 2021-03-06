package ev3Navigation;

import ev3WallFollow.PController;

import java.util.ArrayList;
import java.util.Collections;

import ev3Odometer.Odometer;
import ev3Utilities.UltrasonicPoller;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This object detects unexpected objects to help the EV3 avoid collisions
 * 
 * @author Nick Purdie
 * @version 1.0
 * @since 2016-03-16
 */
public class CollisionAvoidance {

	private Odometer odometer;
	private UltrasonicPoller ultraSonicPoller;
	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	
	/**
	 * collision avoidance stores a reference to the odometer, ultrasonic
	 * poller, left motor, right motor, the EV3's wheel radius and chassis
	 * width.
	 *
	 * @param odometer
	 *            The Odometer
	 * @param ultrasonicPoller
	 *            The Ultrasonic Poller
	 * @param leftMotor
	 *            The left motor instance
	 * @param rightMotor
	 *            The right motor instance
	 * @param wheelRadius
	 *            The radius of the EV3's wheels
	 * @param wheelBase
	 *            The width of the EV3's chassis
	 */
	public CollisionAvoidance(Odometer odometer, UltrasonicPoller ultraSonicPoller, EV3LargeRegulatedMotor leftMotor,
			EV3LargeRegulatedMotor rightMotor, double wheelRadius, double wheelBase) {
		this.odometer = odometer;
		this.ultraSonicPoller = ultraSonicPoller;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}

	/**
	 * This method checks if an object is in the path of the EV3
	 *
	 * @param distance
	 *            The maximum distance of an object that is determined to be in
	 *            the trajectory of the EV3
	 * @return A boolean where true = object detected, false = object not
	 *         detected
	 */
	public boolean detectedObject(int distance) {
		ArrayList<Integer> detectedDistanceF = new ArrayList<Integer>();
		ArrayList<Integer> detectedDistanceL = new ArrayList<Integer>();
		
		int distanceF;
		int distanceL;
		int medianF = 200;
		int medianL = 200;
		
		for(int i=0;i<25;i++){
			distanceF = ultraSonicPoller.getForwardUsDistance();
			distanceL = ultraSonicPoller.getLeftUSDistance();
			
			if(distanceF > 200){
				distanceF = medianF;
			}
			if(distanceL > 200){
				distanceF = medianL;
			}
			
			detectedDistanceF.add(distanceF);
			detectedDistanceL.add(distanceL);

			medianF = calculateMedian(detectedDistanceF);
			medianL = calculateMedian(detectedDistanceL);
		
			}
		
		if (medianF < distance || medianL < distance) {
			return true;
		}
		return false;
	}

	/**
	 * This method return the object distance while also returning 0 if there is
	 * nothing detected
	 *
	 * @return A double that represents the distance of the object detected in
	 *         centimeters
	 */
	public int getForwardDistance() {
		int distance = ultraSonicPoller.getForwardUsDistance();
		return distance;
	}

	/**
	 * This method return the distance detected by the right ultrasonic sensor.
	 *
	 * @return A double that represents the distance of the object detected in
	 *         centimeters
	 */
	public int getRightDistance() {
		int distance = ultraSonicPoller.getRightUsDistance();
		return distance;
	}

	/**
	 * This method allows the EV3 to avoid an object while traveling to the
	 * desired coordinate
	 *
	 **/
	// REMOVE X AND Y ??
	public void avoidObject(int bandCenter, int bandWidth) {
		double theta = odometer.getTheta();
		PController wallFollower = new PController(leftMotor, rightMotor, bandCenter, bandWidth);
		while (!stopWallFollow(theta)) {
			wallFollower.processUSData(getRightDistance());
			try {
				Thread.sleep(30);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * This method help the wallFollower method determine when to stop
	 * wallfollowing. This method returns true when the opposite angle of the
	 * wallfollw
	 *
	 * @return A double that represents the distance of the object detected in
	 *         centimeters
	 */
	public boolean stopWallFollow(double theta) {
		if (theta >= Math.toRadians(180)) {
			theta = theta - Math.toRadians(130);
		} else if (theta < Math.toRadians(180)) {
			theta = theta + Math.toRadians(130);
		}
		if ((odometer.getTheta() > (theta - Math.toRadians(50)))
				&& (odometer.getTheta() < (theta + Math.toRadians(50)))) {
			return true;
		}
		return false;
	}
	
	public int calculateMedian(ArrayList<Integer> array){
		ArrayList<Integer> tempArray= new ArrayList<Integer>(array);
	
		Collections.sort(tempArray);
		int middle = (int)((Math.ceil((double)tempArray.size()/2.0))-1);
		int median = tempArray.get(middle);
		
		return median;
			
		}
		
	
	
}
