package j2w.team.modules.threadpool;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sky on 15/3/20.
 */
public class J2WWorkExecutorService extends ThreadPoolExecutor {

	J2WWorkExecutorService() {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}
}
