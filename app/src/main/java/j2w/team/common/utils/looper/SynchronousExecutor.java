package j2w.team.common.utils.looper;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by sky on 15/2/16. 同步执行
 */
public class SynchronousExecutor implements Executor {

	private static SynchronousExecutor	synchronousExecutor	= new SynchronousExecutor();

	private final Handler				handler				= new Handler(Looper.getMainLooper());

	public static SynchronousExecutor getInstance() {
		return synchronousExecutor;
	}

	@Override public void execute(Runnable runnable) {
		handler.post(runnable);
	}
}