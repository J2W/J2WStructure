package j2w.team.common.utils.proxy;

import android.os.Looper;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import j2w.team.common.log.L;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WBiz;

/**
 * Created by sky on 15/2/7.动态代理 - 业务层
 */
public final class J2WBizHandler<T> extends BaseHandler<T> {

	J2WBiz			j2WBiz;

	Object			methodReturn;

	CountDownLatch	countDownLatch;

	public J2WBizHandler(T t, J2WBiz j2WBiz) {
		super(t);
		this.j2WBiz = j2WBiz;
	}

	@Override public synchronized Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		// 判断是否在主线程
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();
		if (isMainLooper) {
			this.countDownLatch = new CountDownLatch(1);
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					try {
						if (!checkUI()) {
							return;
						}
						methodReturn = method.invoke(t, args);
					} catch (Throwable throwable) {
						throwable.printStackTrace();
						if (j2WBiz != null) {
							j2WBiz.methodCodingError(method.getName(), throwable);
						}
					} finally {
						countDownLatch.countDown();
					}
				}
			});
			countDownLatch.await();
		} else {
			try {
				if (!checkUI()) {
					return null;
				}
				methodReturn = method.invoke(t, args);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				if (j2WBiz != null) {
					j2WBiz.methodCodingError(method.getName(), throwable);
				}
			}
		}
		return methodReturn;

	}

	/**
	 * 检查UI是否被销毁
	 */
	private boolean checkUI() {
		if (j2WBiz == null || t == null) {
			return false;
		}

		boolean isUI = j2WBiz.checkUI();

		if (!isUI) {
			j2WBiz.detachUI();
			j2WBiz = null;
			t = null;
		}
		return isUI;
	}
}
