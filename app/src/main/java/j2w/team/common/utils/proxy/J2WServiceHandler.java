package j2w.team.common.utils.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;

import android.os.Looper;

import j2w.team.J2WHelper;
import j2w.team.biz.J2WBiz;
import j2w.team.common.log.L;

/**
 * Created by sky on 15/2/7.动态代理 - 业务层
 */
public final class J2WServiceHandler<T> extends BaseHandler<T> {

	public J2WServiceHandler(T t) {
		super(t);
	}

	@Override public synchronized Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getName().equals("context") || method.getName().startsWith("intent")) {
			return method.invoke(t, args);
		} else {
			L.i("J2WService");
			L.i("Service无法获取Activity,所以导致Display无效,除了context()方法以外");
			return null;
		}
	}
}
