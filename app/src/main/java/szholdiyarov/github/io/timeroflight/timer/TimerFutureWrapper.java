package szholdiyarov.github.io.timeroflight.timer;

import java.util.concurrent.Future;

/**
 * Created by szholdiyarov on 4/20/17.
 */

public class TimerFutureWrapper {
    private final Future<?> future;


    TimerFutureWrapper(Future<?> future) {
        this.future = future;
    }


    public void cancel() {
        future.cancel(false);
    }


    public boolean isCancelled() {
        return future.isCancelled();
    }
}