package j2w.team.modules.threadpool;

import java.util.concurrent.ThreadFactory;

/**
 * Created by sky on 15/2/20. 线程池工具类
 */
final class J2WThreadPoolUtils {

	/**
	 * 默认线程创建方式
	 * 
	 * @param name
	 *            名称
	 * @param daemon
	 *            true 表示守护线程 false 用户线程 说明：守护线程:主线程挂掉也跟着挂掉. 用户线程:主线程挂掉不会跟着挂掉
	 * @return
	 */
	public static ThreadFactory threadFactory(final String name, final boolean daemon) {
		return new ThreadFactory() {

			@Override public Thread newThread(Runnable runnable) {
				J2WThread result = new J2WThread(runnable, name);
				result.setDaemon(daemon);
				return result;
			}
		};
	}
}
