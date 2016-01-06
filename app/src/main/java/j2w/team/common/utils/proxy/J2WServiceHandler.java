package j2w.team.common.utils.proxy;

import java.lang.reflect.Method;

import j2w.team.modules.log.L;

/**
 * Created by sky on 15/2/7.动态代理 - 业务层
 */
public final class J2WServiceHandler<T> extends BaseHandler<T> {

	public J2WServiceHandler(T t) {
		super(t);
	}

	@Override public synchronized Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getName().equals("context") || method.getName().startsWith("intent") || method.getName().equals("initDisplay") || method.getName().equals("detach")) {
			return method.invoke(t, args);
		} else {
			L.tag("Display");
			L.i("提示: 无界面调用Display()." + method.getName() + "方法，该方法不会被执行!,只有content()和intent前缀方法会被执行");
			return null;
		}
	}
}
