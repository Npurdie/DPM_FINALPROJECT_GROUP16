package ev3Utilities;

/**
* UltrasonicController
*
* @author Nick Purdie
* @version 1.0
* @since   2016-03-16
*/
public interface UltrasonicController {
	
	public void processUSData(int distance);
	
	public int readUSDistance();
}
