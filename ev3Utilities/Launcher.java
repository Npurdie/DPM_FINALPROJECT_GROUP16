package ev3Utilities;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Launcher {
	private EV3LargeRegulatedMotor scooperMotor;
	private EV3LargeRegulatedMotor launcherMotor;
	private int ACCELERATION = 100;
	private int SCOOPE_SPEED = 90;
	private int SHOOT_SPEED = 200;
	
	public Launcher(EV3LargeRegulatedMotor scooperMotor, EV3LargeRegulatedMotor launcherMotor)	{
		this.scooperMotor = scooperMotor;
		this.launcherMotor = launcherMotor;
		
		scooperMotor.setAcceleration(ACCELERATION);
		launcherMotor.setAcceleration(ACCELERATION);
		scooperMotor.setSpeed(SCOOPE_SPEED);
		launcherMotor.setSpeed(SHOOT_SPEED);
		scooperMotor.rotate(-160);
	}
	public void lowerScooper(int angle)	{
		scooperMotor.rotate(angle);
	}
	public void raiseScooper(int angle)	{
		scooperMotor.rotate(-angle);
	}
	public void lowerScooper()	{
		scooperMotor.rotate(160);
	}
	public void raiseScooper()	{
		scooperMotor.rotate(-160);
	}
	
	public void shootBall(int numberOfBalls)	{
		for (int i=0; i< numberOfBalls; i++)	{
			lowerScooper(100);
			launcherMotor.rotate(-1080);
			raiseScooper(100);
		}
	}
}
