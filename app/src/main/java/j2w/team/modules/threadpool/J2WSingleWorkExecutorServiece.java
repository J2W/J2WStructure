package j2w.team.modules.threadpool;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sky on 15/2/20.
 */
class J2WSingleWorkExecutorServiece extends ThreadPoolExecutor {

	J2WSingleWorkExecutorServiece() {
		super(0, 1, 60, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), J2WThreadPoolUtils.threadFactory("J2WSingleWork Dispatcher", true));
	}
}
