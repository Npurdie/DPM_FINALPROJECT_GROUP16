package ev3Utilities;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Launcher {
	private EV3LargeRegulatedMotor scooperMotor;
	private EV3LargeRegulatedMotor launcherMotor;
	private int ACCELERATION = 50;
	private int SCOOPE_SPEED = 30;
	private int SHOOT_SPEED = 50;
	
	public Launcher(EV3LargeRegulatedMotor scooperMotor, EV3LargeRegulatedMotor launcherMotor)	{
		this.scooperMotor = scooperMotor;
		this.launcherMotor = launcherMotor;
		
		scooperMotor.setAcceleration(ACCELERATION);
		launcherMotor.setAcceleration(ACCELERATION);
		scooperMotor.setSpeed(SCOOPE_SPEED);
		launcherMotor.setSpeed(SHOOT_SPEED);
		scooperMotor.rotate(70);
	}
	
	public void lowerScooper()	{
		scooperMotor.rotate(-70);
	}
	public void raiseScooper()	{
		scooperMotor.rotate(70);
	}
	
	public void shootBall(int numberOfBalls)	{
		for (int i=0; i< numberOfBalls; i++)	{
			raiseScooper();
			lowerScooper();
			launcherMotor.rotate(360);
		}
	}
}
