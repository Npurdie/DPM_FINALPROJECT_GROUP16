package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import ev3Utilities.ParseWifi;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import ev3Utilities.Launcher;

/** This object coordinates the Attacker case of the competition */
public class Attacker {

	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private EV3LargeRegulatedMotor clawMotor, launcherMotor;
	private double width;
	private double wheelRadius;
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;
	private Launcher launcher;
	private ParseWifi pw;

	//Wifi variables
	private int cornerID;
	private double[] cornerLoc;
	private double[] ballLoc;
	private double goalWidth;
	private double defLine;
	private double forwLine;
	
	
	//Field Parameter  (7 = BETA DEMO 11 = FINAL DEMO)
	public static final double largeCoord = 7;
	/**
	 * The Attacker stores a reference to the left motor, right motor, claw
	 * motor, launcher motor, the EV3's width, wheel radius , odometer, light
	 * poller, navigator, us localizer, light localizer and the launcher. This
	 * class calls the appropriate methods and objects to complete the attacker
	 * portion of the challenge
	 *
	 * @param leftMotor
	 *            The left motor object
	 * @param rightMotor
	 *            The right motor object
	 * @param clawmotor
	 *            The motor that actuates the claw
	 * @param launcherMotor
	 *            The motor that shoots the balls
	 * @param width
	 *            The width of the EV3's chassis
	 * @param wheelRadius
	 *            The radius of the EV3's wheels
	 * @param odometer
	 *            The Odometer
	 * @param lightPoller
	 *            The light poller
	 * @param navigator
	 *            The Navigator
	 * @param UsLocalizer
	 *            The ultrasonic localizer
	 * @param lightlocalizer
	 *            The light lozalizer
	 * @param launcher
	 *            The launcher class
	 */

	public Attacker(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			EV3LargeRegulatedMotor clawMotor, EV3LargeRegulatedMotor launcherMotor, double width, double wheelRadius,
			Odometer odometer, LightPoller lightPoller, Navigation navigator, USLocalizer uslocalizer,
			LightLocalizer lightlocalizer, Launcher launcher,int cornerID, double[] cornerLoc, double[] ballLoc, double goalWidth,
			double defLine, double forwLine) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.width = width;
		this.wheelRadius = wheelRadius;
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
		this.launcher = launcher;
		this.cornerID = cornerID;
		this.cornerLoc = cornerLoc;
		this.ballLoc = ballLoc;
		this.goalWidth = goalWidth;
		this.defLine = defLine;
		this.forwLine = forwLine;
		
	}

	/**
	 * Initializes the attack sequence
	 */
	public void startAttack() {
	
		localize();
		navigate();
		retrieveBall(ballLoc[0], ballLoc[1]);
		shootBalls();
	}

	private void setOdometryValues(double[] cornerValues) {
		odometer.setX(cornerValues[0]);
		odometer.setY(cornerValues[1]);
		odometer.setTheta(cornerValues[2]);

	}
	
    private void localize(){
		odometer.start();
		lightPoller.start();
		navigator.setLSL(lsl);
		// perform the ultrasonic sensor localization
		usl.doLocalization();

		LightSensorDerivative lsd = new LightSensorDerivative(odometer, lightPoller, lsl);
		lsd.start();

		// perform the light sensor localization
		lsl.doLocalization();
		
		while(!lsl.lslDONE){
		}
		//Initialize odometry readings to localized coordinates.
		setOdometryValues(this.cornerLoc);
    
    }
    
    //Navigates from initial localization to the attacker's zone and finally towards the balls.
    private void navigate(){
    	    	
    	switch(cornerID){
    	case 1:
     		navigator.travelTo((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile , true);
     		lsl.doLocalization((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile);
    		
     		Sound.beep();
     		
     		navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
    		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
    	
    		navigator.travelTo(ballLoc[0] - navigator.tile, ballLoc[1], true);
    		lsl.doLocalization(ballLoc[0] - navigator.tile, ballLoc[1]);
    		break;
    	case 2:
    		navigator.travelTo((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile , true);
     		lsl.doLocalization((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile);
     		
     		Sound.beep();
     		
     		navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
    		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
    	
    		navigator.travelTo(ballLoc[0] - navigator.tile, ballLoc[1], true);
    		lsl.doLocalization(ballLoc[0] - navigator.tile, ballLoc[1]);
    		break;
    	case 3:
    		navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
    		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
    	
    		navigator.travelTo((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile , true);
     		lsl.doLocalization((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile);
     		
     		Sound.beep();
     		
     		navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
    		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
    	
    			
       		navigator.travelTo(ballLoc[0] - navigator.tile, ballLoc[1], true);
    		lsl.doLocalization(ballLoc[0] - navigator.tile, ballLoc[1]);    		
    		break;
    	case 4:
    		navigator.travelTo(0*navigator.tile, 0*navigator.tile , true);
    		lsl.doLocalization(0*navigator.tile, 0*navigator.tile);
    		
    		navigator.travelTo((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile , true);
     		lsl.doLocalization((largeCoord+1)/2*navigator.tile, forwLine - 2*navigator.tile);
     		
     		Sound.beep();
     		
     		navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
    		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
    		
    		navigator.travelTo(ballLoc[0] - navigator.tile, ballLoc[1], true);
    		lsl.doLocalization(ballLoc[0] - navigator.tile, ballLoc[1]);
    		break;
    	
    	}
    }
    //Retrieves balls
    private void retrieveBall(double x, double y){
		navigator.travelTo(x - 25, y + 23 , false);
		navigator.turnTo(0);
		navigator.travelForwardDistance(15.5, 100);
		launcher.lowerScooper();
		navigator.travelForwardDistance(4, 50);
		launcher.raiseScooper();
		navigator.travelBackwardDistance(20,250);
		navigator.travelTo(ballLoc[0] - navigator.tile, ballLoc[1], true);
		lsl.doLocalization(ballLoc[0] - navigator.tile, ballLoc[1]);
    }
        
    private void shootBalls(){
    	navigator.travelTo((largeCoord-1)*navigator.tile, 0*navigator.tile, true);
		lsl.doLocalization((largeCoord-1)*navigator.tile, 0*navigator.tile);
		navigator.travelTo((largeCoord+1)/2*navigator.tile, defLine - 2*navigator.tile, true);
		lsl.doLocalization((largeCoord+1)/2*navigator.tile, defLine - 2*navigator.tile);
		navigator.shootDirection((largeCoord-1)/2*navigator.tile, 11*navigator.tile);
		launcher.shootBall(3);
    }
}
