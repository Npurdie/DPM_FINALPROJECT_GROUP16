package ev3Utilities;

public interface UltrasonicController {

	public void processUSData(int distance);

	public int readUSDistance();
}
