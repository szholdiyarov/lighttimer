# lighttimer

I found that very often I need to run a timer which is updating every second(until specified deadline) and returns this update 
in the main thread so that a developer should not worry about udating a textView for example. 
This creates addition challenges which are usually solved in the same way. 

This library is designed to solve such problem. The class which you need to use is the StopwatchTimer.java.

You need to:

1. In your BaseActivity or BaseFragment initialize ContextEnvironmentService(new Handler(Looper.getMainLooper()), this).
2. Call contextEnvironment.onPause() in your onPause callback etc.
3. Simply call StopwatchTimer(ContextEnvironment context, int timerCountdownStartingPoint, StopwatchTimerListener listener) where you need a timer.


