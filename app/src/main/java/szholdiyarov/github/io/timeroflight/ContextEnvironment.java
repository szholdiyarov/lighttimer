package szholdiyarov.github.io.timeroflight;

import android.content.Context;

/**
 * This is a contract which should be used by activities or fragments(BaseDialogs etc. ) to help with multithreading.
 * The example where this will be a good fit:
 * 1) "A" runs some time-consuming operation on Thread 1
 * 2) "B" is the activity which started "A" operation and it went to the background state(onDestroyView()). It wants a result from A to be setted in textView.
 * 3) Now, instead of passing the result to "B" directly(which will cause a crash NPE), "A" calls ExecutionContext.execute()
 * which in turn will execute the code when context comes available(or never if context is destroyed and not recreated).
 */
public interface ContextEnvironment {
	
	boolean execute(Runnable task);
	
	Context getContext();
	
	boolean isActive();
}