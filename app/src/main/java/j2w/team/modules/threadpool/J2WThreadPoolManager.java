package j2w.team.modules.threadpool;

import java.util.concurrent.ExecutorService;

import j2w.team.common.log.L;

/**
 * Created by sky on 15/2/20.调度
 */
public final class J2WThreadPoolManager {

	/** 线程服务-网络线程池 **/
	private J2WHttpExecutorService			j2WHttpExecutorService;

	/** 线程服务-并行工作线程池 **/
	private J2WWorkExecutorService			j2WWorkExecutorService;

	/** 线程服务-串行工作线程池 **/
	private J2WSingleWorkExecutorServiece	j2WSingleWorkExecutorServiece;

	public synchronized ExecutorService getHttpExecutorService() {
		if (j2WHttpExecutorService == null) {
			j2WHttpExecutorService = new J2WHttpExecutorService();
		}
		return j2WHttpExecutorService;
	}

	public synchronized ExecutorService getSingleWorkExecutorService() {
		if (j2WSingleWorkExecutorServiece == null) {
			j2WSingleWorkExecutorServiece = new J2WSingleWorkExecutorServiece();
		}
		return j2WSingleWorkExecutorServiece;
	}

	public synchronized ExecutorService getWorkExecutorService() {
		if (j2WWorkExecutorService == null) {
			j2WWorkExecutorService = new J2WWorkExecutorService();
		}
		return j2WWorkExecutorService;
	}

	public synchronized void finish() {
		L.tag("J2WThreadPoolManager");
		L.i("finish()");
		if (j2WHttpExecutorService != null) {
			L.tag("J2WThreadPoolManager");
			L.i("j2WHttpExecutorService.shutdown()");
			j2WHttpExecutorService.shutdown();
			j2WHttpExecutorService = null;
		}
		if (j2WSingleWorkExecutorServiece != null) {
			L.tag("J2WThreadPoolManager");
			L.i("j2WSingleWorkExecutorServiece.shutdown()");
			j2WSingleWorkExecutorServiece.shutdown();
			j2WSingleWorkExecutorServiece = null;
		}
		if (j2WWorkExecutorService != null) {
			L.tag("J2WThreadPoolManager");
			L.i("j2WWorkExecutorService.shutdown()");
			j2WWorkExecutorService.shutdown();
			j2WWorkExecutorService = null;
		}
	}
}
