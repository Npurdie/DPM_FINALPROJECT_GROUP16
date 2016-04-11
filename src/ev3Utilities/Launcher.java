package ev3Utilities;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

/**
 * This class contains all methods necessary for manipulating the launcher motor
 * and the claw motor to effectively manipulate and shoot balls.
 */
public class Launcher {
	private EV3LargeRegulatedMotor scooperMotor;
	private EV3LargeRegulatedMotor launcherMotor;
	private int ACCELERATION = 600;
	private int SCOOPE_SPEED = 270;
	private int SHOOT_SPEED = 200;

	/**
	 * The launcher object stores a reference to both the claw motor and the
	 * launcher motor.
	 * 
	 * @param scooperMotor
	 * @param launcherMotor
	 */
	public Launcher(EV3LargeRegulatedMotor scooperMotor, EV3LargeRegulatedMotor launcherMotor) {
		this.scooperMotor = scooperMotor;
		this.launcherMotor = launcherMotor;

		scooperMotor.setAcceleration(ACCELERATION);
		launcherMotor.setAcceleration(ACCELERATION);
		scooperMotor.setSpeed(SCOOPE_SPEED);
		launcherMotor.setSpeed(SHOOT_SPEED);
		scooperMotor.rotate(-160);
	}

	/**
	 * This method lowers the claw by a set angle.
	 * 
	 * @param angle
	 *            The angle in degrees by which to rotate the claw.
	 */
	public void lowerScooper(int angle) {
		scooperMotor.setSpeed(SCOOPE_SPEED);
		scooperMotor.rotate(angle);
	}

	/**
	 * This method raises the claw by a set angle.
	 * 
	 * @param angle
	 *            The angle in degrees by which to rotate the claw.
	 */
	public void raiseScooper(int angle) {
		scooperMotor.rotate(-angle);
	}

	/**
	 * This method raises the claw by 160 degrees, it's full range of motion.
	 */
	public void lowerScooper() {
		scooperMotor.setSpeed(SCOOPE_SPEED);
		scooperMotor.rotate(140);
	}

	/**
	 * This method raises the claw by 160 degrees, it's full range of motion.
	 */
	public void raiseScooper() {
		scooperMotor.setSpeed(80);
		scooperMotor.rotate(-70);
		scooperMotor.setSpeed(100);
		scooperMotor.rotate(-100);
	}

	/**
	 * This method shoots the balls out of the claw. It will repeat the shoot
	 * sequence the number of times passed as an argument.
	 * 
	 * @param numberOfBalls
	 *            The number of times to repeat the shoot sequence.
	 */
	public void shootBall(int numberOfBalls) {
		for (int i = 0; i < numberOfBalls; i++) {
			lowerScooper();
			launcherMotor.rotate(-1080);
			raiseScooper(-140);
		}
	}
}