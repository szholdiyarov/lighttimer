package szholdiyarov.github.io.timeroflight.timer;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by szholdiyarov on 20.04.17.
 * zholdiyarov@gmail.com
 * <p>
 * TODO: Add description
 */
public class TimerUtil {
    private static final String TAG = "StopwatchTimerUtil";
    private static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(2);

    private TimerUtil() {
    }


    public static TimerFutureWrapper scheduleAfter(long delay, TimeUnit unit, Runnable task) {
        if (delay < 0) {
            Log.d(TAG, "Something wrong. Do not pass negative delay value.");
            delay = 0;
        }

        final Future<?> future = SCHEDULER.schedule(task, delay, unit);
        return new TimerFutureWrapper(future);
    }


    public static TimerFutureWrapper scheduleAt(long scheduleMillis, Runnable task) {
        return scheduleAfter(scheduleMillis - SystemClock.uptimeMillis(), TimeUnit.MILLISECONDS, task);
    }


    public static TimerFutureWrapper scheduleEvery(@NonNull long initialDelay, @NonNull long delay, @NonNull TimeUnit unit, @NonNull Runnable task) {
        final Future<?> future = SCHEDULER.scheduleWithFixedDelay(task, initialDelay, delay, unit);
        return new TimerFutureWrapper(future);
    }

}


