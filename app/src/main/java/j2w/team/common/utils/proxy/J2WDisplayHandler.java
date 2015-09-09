package j2w.team.common.utils.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import android.os.Looper;

import j2w.team.J2WHelper;
import j2w.team.biz.J2WBiz;

/**
 * Created by sky on 15/2/7.动态代理 - 业务层
 */
public final class J2WDisplayHandler<T> extends BaseHandler<T> {

	Object			methodReturn;

	CountDownLatch	countDownLatch;

	public J2WDisplayHandler(T t) {
		super(t);
	}

	@Override public synchronized Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		// 判断是否在主线程
		if (J2WHelper.isMainLooperThread()) {
			this.countDownLatch = new CountDownLatch(1);
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					try {
						methodReturn = method.invoke(t, args);
					} catch (Throwable throwable) {
						throwable.printStackTrace();
					} finally {
						countDownLatch.countDown();
					}
				}
			});
			countDownLatch.await();
			return methodReturn;
		} else {
			return method.invoke(t, args);
		}
	}

}
