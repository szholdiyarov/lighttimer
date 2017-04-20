package szholdiyarov.github.io.timeroflight;

/**
 * Created by szholdiyarov on 20.04.17.
 * zholdiyarov@gmail.com
 * TODO: Add description
 */
public interface TimerListener {
	void onUpdate(long seconds);
	
	void onTerminate();
}
