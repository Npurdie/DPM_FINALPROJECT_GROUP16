package ev3Utilities;

/**
* UltrasonicController
*/
public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
