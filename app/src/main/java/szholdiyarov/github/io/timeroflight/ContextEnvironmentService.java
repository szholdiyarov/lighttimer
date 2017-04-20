package szholdiyarov.github.io.timeroflight;

import android.content.Context;
import android.os.Handler;

import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the implementation which should be used by activities or fragments(BaseDialogs etc. ) to help with multithreading.
 * The example where this will be a good fit:
 * 1) "A" runs some time-consuming operation on Thread 1
 * 2) "B" is the activity which started "A" operation and it went to the background state(onDestroyView()). It wants a result from A to be setted in textView.
 * 3) Now, instead of passing the result to "B" directly(which will cause a crash NPE), "A" calls ExecutionContext.execute()
 * which in turn will execute the code when context comes available(or never if context is destroyed and not recreated).
 * <p>
 * Usage: Create variable in your BaseFragment and in onCreateView() method initialize the service:
 * 	execContext = new ExecutionContextService(new Handler(Looper.getMainLooper()), getContext());
 *  Properly save state of service in your lifecycle callbacks: execContext.resume(), execContext.pause(), execContext.destroy()
 *  To run the code: execContext.execute(Runnable runnable)
 */
public class ContextEnvironmentService implements ContextEnvironment {
	private final Handler handler;
	private final Context context;
	
	private volatile State state; // state read/write must precede pendingTasks
	private final Queue<Runnable> pendingTasks = new LinkedList<>();
	
	
	public ContextEnvironmentService(Handler handler, Context context) {
		this.handler = handler;
		this.context = context;
		state = State.PAUSED;
	}
	
	
	public void pause() {
		state = State.PAUSED;
	}
	
	
	public void resume() {
		state = State.RESUMED;
		runPending();
	}
	
	
	public void destroy() {
		state = State.DESTROYED;
		pendingTasks.clear();
	}
	
	
	@Override
	public boolean execute(Runnable task) {
		switch (state) {
			case RESUMED:
				handler.post(task);
				return true;
			case PAUSED:
				pendingTasks.add(task);
				return true;
			default:
				return false;
		}
	}
	
	
	@Override
	public Context getContext() {
		return context;
	}
	
	
	@Override
	public boolean isActive() {
		return state == State.RESUMED || state == State.PAUSED;
	}
	
	
	private enum State {
		RESUMED,
		PAUSED,
		DESTROYED
	}
	
	
	private void runPending() {
		if (state == State.RESUMED && !pendingTasks.isEmpty()) {
			final Runnable task = pendingTasks.poll();
			task.run();
			handler.post(new Runnable() {
				@Override
				public void run() {
					runPending();
				}
			});
		}
	}
}
