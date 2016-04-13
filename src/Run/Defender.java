package Run;

import ev3Localization.LightLocalizer;
import ev3Localization.USLocalizer;
import ev3Navigation.Navigation;
import ev3Odometer.Odometer;
import ev3Utilities.LightPoller;
import ev3Utilities.LightSensorDerivative;
import ev3Utilities.UltrasonicPoller;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

/** This object coordinates the Defender case of the competition */
public class Defender {
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private double width;
	private double wheelRadius;
	private Odometer odometer;
	private Navigation navigator;
	private USLocalizer usl;
	private LightLocalizer lsl;
	private LightPoller lightPoller;
	private UltrasonicPoller USPoller;

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
	 * The Defender stores a reference to the left motor, right motor, the EV3's
	 * width, wheel radius , odometer, light poller, navigator, us localizer and
	 * the light localizer. This class calls the appropriate methods and objects
	 * to complete the attacker portion of the challenge
	 * 
	 * @param leftMotor
	 *            The left motor object
	 * @param rightMotor
	 *            The right motor object
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
	 */
	public Defender(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor, double width,
			double wheelRadius, Odometer odometer, LightPoller lightPoller, Navigation navigator,
			USLocalizer uslocalizer, LightLocalizer lightlocalizer, int cornerID, double[] cornerLoc, double[] ballLoc, double goalWidth,
			double defLine, double forwLine, UltrasonicPoller USPoller) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.width = width;
		this.wheelRadius = wheelRadius;
		this.odometer = odometer;
		this.navigator = navigator;
		this.usl = uslocalizer;
		this.lsl = lightlocalizer;
		this.lightPoller = lightPoller;
		this.cornerID = cornerID;
		this.cornerLoc = cornerLoc;
		this.ballLoc = ballLoc;
		this.goalWidth = goalWidth;
		this.defLine = defLine;
		this.forwLine = forwLine;
		this.USPoller = USPoller;
	}

	/**
	 * Initializes the defence sequence
	 */
	public void startDefense() {
		test();
	//	localize();
	//	navigate();
	//	stopUltraSonicSensors();
	//	defend();
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
     		navigator.travelTo(0*navigator.tile, (largeCoord-1)*navigator.tile, true);
     		lsl.doLocalization(0*navigator.tile, (largeCoord-1)*navigator.tile);
    
     		navigator.travelTo((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine, false);
    		lsl.doLocalization((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine);
     		
     		Sound.beep();
     		     
    		break;
    	case 2:
    		
    		navigator.travelTo((largeCoord-1)*navigator.tile, (largeCoord-1)*navigator.tile, true);
     		lsl.doLocalization((largeCoord-1)*navigator.tile, (largeCoord-1)*navigator.tile);
    
     		navigator.travelTo((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine, false);
    		lsl.doLocalization((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine);
     		
    		Sound.beep();
     		     
    		break;
    	case 3:
    	   
    		navigator.travelTo((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine, false);
    		lsl.doLocalization((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine);
     		
     		Sound.beep();
     		     
    		break;
    	case 4:
     	    
    		navigator.travelTo((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine, false);
    		lsl.doLocalization((largeCoord+1)/2*navigator.tile,(largeCoord+2)*navigator.tile - defLine);
     		Sound.beep();
     		     
    		break;
    	
    	}
    }
    
    private void stopUltraSonicSensors(){
    	USPoller.turnOffSensors();
   
    }
    
    private void defend(){
    	navigator.travelTo((largeCoord-1)/2*navigator.tile+goalWidth/2, (largeCoord+2)*navigator.tile - defLine , false);
    	navigator.turnTo(Math.toRadians(0));
    	int i = 0;
    	navigator.travelBackwardDistance(10, 200);
    	
    	while(true){
    		
    		if(i>3){
    		i=0;
    		fixDefense();
    		}
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		navigator.travelBackwardDistance(goalWidth-22, 200);
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		navigator.travelForwardDistance(goalWidth-22, 200);
    		
    		i++;
 
    	}
    	
    	
    }
    
    private void fixDefense(){
   		navigator.travelTo((largeCoord+1)/2*navigator.tile, (largeCoord+2)*navigator.tile - defLine, false);
		lsl.doLocalization((largeCoord+1)/2*navigator.tile, (largeCoord+2)*navigator.tile - defLine);
    	
    	navigator.travelTo((largeCoord-1)/2*navigator.tile+goalWidth/2, (largeCoord+2)*navigator.tile - defLine , false);
    	navigator.turnTo(Math.toRadians(0));
    }
    
    private void test(){
    	localize();
    	for(int i=0; i<12; i++){
    	navigator.travelForwardDistance(2*navigator.tile, 400);	
       	navigator.turnTo(odometer.getTheta() + Math.toRadians(90));
    	}
    }
       }
