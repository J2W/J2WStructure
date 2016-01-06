package j2w.team.common.utils.proxy;

import java.lang.reflect.Method;

import j2w.team.modules.log.L;

/**
 * Created by sky on 15/1/27.动态代理-日志系统
 */
public final class J2WLogSystemHandler<T> extends BaseHandler<T> {

	public J2WLogSystemHandler(T t) {
		super(t);
	}

	@Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		/** 打印准备 **/
		long startTime = 0;
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(method.getName());
		stringBuffer.append("方法-开始执行:");
		startTime = System.currentTimeMillis();
		L.tag("J2W-Method");
		L.i(stringBuffer.toString());
		/** 执行方法 **/
		Object object = method.invoke(t, args);
		/** 打印结束 **/
		stringBuffer = new StringBuffer();
		stringBuffer.append(method.getName());
		stringBuffer.append("方法-结束执行!执行时间:");
		long endTime = System.currentTimeMillis();
		stringBuffer.append((endTime - startTime));
		stringBuffer.append("毫秒");
		L.tag("J2W-Method");
		L.i(stringBuffer.toString());
		return object;
	}
}
