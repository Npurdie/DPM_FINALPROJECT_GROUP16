package ev3Tests;

import ev3Utilities.Launcher;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class SooperTest {
	private static final EV3LargeRegulatedMotor launcherMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
	private static final EV3LargeRegulatedMotor scooperMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));

	public static void main(String[] args) {
		Launcher launcher = new Launcher(scooperMotor, launcherMotor);
		while (true)	{ //continue until exit(0)
			int option = 0;
			while (option == 0)								// wait for a button press to start
				option = Button.waitForAnyPress();
			
			//this code is written with the goal of changing the motor's speed while running without re-uploading code
			//this allows us to adjust the exact range of the catapult to the environmental conditions(ex battery level)
			switch(option) {
			case Button.ID_LEFT:	//left button allows us to decrease speed by 10
				launcher.lowerScooper();
				break;
				
			case Button.ID_RIGHT:	//right button allows to increase speed by 10
				launcher.raiseScooper();
				break;
			case Button.ID_ENTER:	//center button launches ball
				launcher.shootBall(3);
				break;
			case Button.ID_ESCAPE:	//exit program
				System.exit(0);	
			default:
				break;
			}
		}
	}
}
