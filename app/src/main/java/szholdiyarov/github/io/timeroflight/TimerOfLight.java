package szholdiyarov.github.io.timeroflight;

import android.os.Bundle;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by szholdiyarov on 20.04.17.
 * zholdiyarov@gmail.com
 *
 * TODO: Add description
 */
public class TimerOfLight {
	private static final String PARAM_SMS_TIMER_DEADLINE = "SmsTimer#TimerDeadline";
	
	private final AtomicInteger timerCountdown;
	private final int timerCountdownStartingPoint;
	
	private TimerListener listener;
	private ContextEnvironment executionContext;
	
	private TimerUtil.TimerFutureWrapper timerFuture;
	private Runnable listenerNotifier = new Runnable() {
		@Override
		public void run() {
			if (timerFuture.isCancelled()) {
				listener.onTerminate();
			} else {
				listener.onUpdate(timerCountdown.get());
			}
		}
	};
	private Runnable timerRunnable = new Runnable() {
		@Override
		public void run() {
			if (timerCountdown.compareAndSet(0, timerCountdown.decrementAndGet())) {
				stop();
			}
			executionContext.execute(listenerNotifier);
		}
	};
	
	
	TimerOfLight(ContextEnvironment context, int timerCountdownStartingPoint, TimerListener listener) {
		this.listener = listener;
		this.executionContext = context;
		this.timerCountdownStartingPoint = timerCountdownStartingPoint;
		timerCountdown = new AtomicInteger(timerCountdownStartingPoint);
	}
	
	
	private void start() {
		timerFuture = TimerUtil.scheduleEvery(0, 1, TimeUnit.SECONDS, timerRunnable);
	}
	
	
	private void stop() {
		if (timerFuture != null) {
			timerFuture.cancel();
		}
		timerCountdown.set(timerCountdownStartingPoint);
	}
	
	
	private void restore(int timerCountdown) {
		this.timerCountdown.set(timerCountdown);
		start();
	}
	
	private void saveState(Bundle bundle) {
		if (!timerFuture.isCancelled()) {
			bundle.putLong(PARAM_SMS_TIMER_DEADLINE, SystemClock.uptimeMillis() + TimeUnit.SECONDS.toMillis(timerCountdown.intValue()));
		}
	}
	
	
	private void restoreState(Bundle savedInstanceState) {
		long millisRemaining = savedInstanceState.getLong(PARAM_SMS_TIMER_DEADLINE) - SystemClock.uptimeMillis();
		int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisRemaining);
		if (seconds > 0) {                // continue
			timerCountdown.set(seconds);
			start();
		} else { // no needed to restart
			executionContext.execute(listenerNotifier);
		}
	}
	
	
	public int getRemainingSeconds() {
		return timerCountdown.intValue();
	}
	
	
	public boolean isFinished() {
		return timerFuture.isCancelled();
	}
}
