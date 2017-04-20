package szholdiyarov.github.io.timeroflight;

import android.os.Bundle;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import szholdiyarov.github.io.timeroflight.context.ContextEnvironment;
import szholdiyarov.github.io.timeroflight.timer.StopwatchTimerListener;
import szholdiyarov.github.io.timeroflight.timer.TimerFutureWrapper;
import szholdiyarov.github.io.timeroflight.timer.TimerUtil;

/**
 * Created by szholdiyarov on 20.04.17.
 * zholdiyarov@gmail.com
 * <p>
 * TODO: Add description
 */
public class StopwatchTimer {
    private static final String PARAM_TIMER_DEADLINE = "Timer#TimerDeadline";

    private final AtomicInteger timerCountdown;
    private final int timerCountdownStartingPoint;

    private StopwatchTimerListener listener;
    private ContextEnvironment executionContext;

    private TimerFutureWrapper timerFuture;
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

    StopwatchTimer(ContextEnvironment context, int timerCountdownStartingPoint, StopwatchTimerListener listener) {
        this.listener = listener;
        this.executionContext = context;
        this.timerCountdownStartingPoint = timerCountdownStartingPoint;
        timerCountdown = new AtomicInteger(timerCountdownStartingPoint);
    }


    public void start() {
        timerFuture = TimerUtil.scheduleEvery(0, 1, TimeUnit.SECONDS, new Runnable() {
            @Override
            public void run() {
                if (timerCountdown.compareAndSet(0, timerCountdown.decrementAndGet())) {
                    stop();
                }
                executionContext.execute(listenerNotifier);
            }
        });
    }


    public void stop() {
        if (timerFuture != null) {
            timerFuture.cancel();
        }
        timerCountdown.set(timerCountdownStartingPoint);
    }


    public boolean saveState(Bundle bundle) {
        if (!timerFuture.isCancelled()) {
            bundle.putLong(PARAM_TIMER_DEADLINE, SystemClock.uptimeMillis() + TimeUnit.SECONDS.toMillis(timerCountdown.intValue()));
            return true;
        }
        return false;
    }


    public boolean restoreState(Bundle savedInstanceState) {
        long millisRemaining = savedInstanceState.getLong(PARAM_TIMER_DEADLINE) - SystemClock.uptimeMillis();
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millisRemaining);
        if (seconds > 0) {                // continue
            timerCountdown.set(seconds);
            start();
            return true;
        } else { // no needed to restart
            executionContext.execute(listenerNotifier);
            return false;
        }
    }


    public int getRemainingSeconds() {
        return timerCountdown.intValue();
    }


    public boolean isFinished() {
        return timerFuture.isCancelled();
    }
}
