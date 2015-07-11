package j2w.team.modules.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sky on 15/3/20.
 */
public class J2WWorkExecutorService extends ThreadPoolExecutor {

	private static final int	DEFAULT_THREAD_COUNT	= 3;

	J2WWorkExecutorService() {
		super(DEFAULT_THREAD_COUNT, DEFAULT_THREAD_COUNT, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), J2WThreadPoolUtils.threadFactory("J2WWork Dispatcher", true));
	}
}
