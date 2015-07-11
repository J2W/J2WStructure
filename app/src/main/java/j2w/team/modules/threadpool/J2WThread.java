package j2w.team.modules.threadpool;

import android.os.Process;
import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created by sky on 15/2/20. 自定义线程
 */
class J2WThread extends Thread {

	public J2WThread(Runnable runnable, String name) {
		super(runnable, name);
	}

	@Override public void run() {
		// 设置线程优先级-标准后台线程
		Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND);
		super.run();
	}
}
