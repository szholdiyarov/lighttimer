package szholdiyarov.github.io.timeroflight.timer;

/**
 * Created by szholdiyarov on 20.04.17.
 * zholdiyarov@gmail.com
 * TODO: Add description
 */
public interface StopwatchTimerListener {
	void onUpdate(long seconds);
	
	void onTerminate();
}
