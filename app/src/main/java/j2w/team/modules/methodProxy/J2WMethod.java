package j2w.team.modules.methodProxy;

import java.lang.reflect.Method;

import j2w.team.J2WHelper;
import j2w.team.core.J2WRunnable;
import j2w.team.core.plugin.J2WEndInterceptor;
import j2w.team.core.plugin.J2WErrorInterceptor;
import j2w.team.core.plugin.J2WStartInterceptor;
import j2w.team.modules.http.J2WMethodInfo;
import j2w.team.modules.log.L;
import j2w.team.modules.threadpool.Background;
import j2w.team.modules.threadpool.BackgroundType;
import j2w.team.modules.threadpool.J2WRepeat;

/**
 * @创建人 sky
 * @创建时间 16/1/5
 * @类描述 代理方法-执行
 */
public final class J2WMethod {

	// 执行方法
	public static final int	TYPE_INVOKE_EXE							= 0;

	// 执行后台方法
	public static final int	TYPE_INVOKE_BACKGROUD_HTTP_EXE			= 1;

	public static final int	TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE	= 2;

	public static final int	TYPE_INVOKE_BACKGROUD_WORK_EXE			= 3;

	static J2WMethod createMethod(J2WMethods j2WMethods, Method method, Class service) {
		// 默认不可重复
		boolean isRepeat = false;
		// 默认方法执行
		int type = TYPE_INVOKE_EXE;
		// 键值
		String key = J2WMethodInfo.getMethodString(method, method.getParameterTypes());
		// 实现类
		Object impl = j2WMethods.getImplClass(service);
		// 是否重复
		J2WRepeat j2WRepeat = method.getAnnotation(J2WRepeat.class);
		if (j2WRepeat != null && j2WRepeat.value()) {
			isRepeat = true;
		}

		Background background = method.getAnnotation(Background.class);

		if (background != null) {
			BackgroundType backgroundType = background.value();

			switch (backgroundType) {
				case HTTP:
					type = TYPE_INVOKE_BACKGROUD_HTTP_EXE;
					break;
				case SINGLEWORK:
					type = TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE;
					break;
				case WORK:
					type = TYPE_INVOKE_BACKGROUD_WORK_EXE;
					break;
			}
		}

		return new J2WMethod(key, impl, method, type, isRepeat, j2WMethods, service);
	}

	public <T> T invoke(Object[] args) {
		T result = null;
		if (!isRepeat) {
			if (j2WMethods.stack.search(key) != -1) { // 如果存在什么都不做
				L.tag("J2W-Method");
				L.i("该方法正在执行 - %s", key);
				return result;
			}
			j2WMethods.stack.push(key); // 入栈
		}

		if (type == TYPE_INVOKE_EXE) {
			try {
				// 业务拦截器 - 前
				for (J2WStartInterceptor item : j2WMethods.j2WStartInterceptor) {
					item.intercept(service, method);
				}
				result = (T) method.invoke(impl, args);// 执行
				// 业务拦截器 - 后
				for (J2WEndInterceptor item : j2WMethods.j2WEndInterceptor) {
					item.intercept(service, method);
				}
			} catch (Throwable throwable) {
				throwable.printStackTrace();
				// 业务错误拦截器
				for (J2WErrorInterceptor item : j2WMethods.j2WErrorInterceptor) {
					item.methodError(service, method, throwable);
				}
			} finally {
				j2WMethods.stack.remove(key); // 出栈
			}
		} else {
			if (isRepeat) {
				methodRunnable = new MethodRunnable();
			}
			methodRunnable.setArgs(args);
			switch (type) {
				case TYPE_INVOKE_BACKGROUD_HTTP_EXE:
					J2WHelper.threadPoolHelper().getHttpExecutorService().execute(methodRunnable);
					break;
				case TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE:
					J2WHelper.threadPoolHelper().getSingleWorkExecutorService().execute(methodRunnable);
					break;
				case TYPE_INVOKE_BACKGROUD_WORK_EXE:
					J2WHelper.threadPoolHelper().getWorkExecutorService().execute(methodRunnable);
					break;
			}
		}

		return result;
	}

	private class MethodRunnable extends J2WRunnable {

		Object[]	objects;

		public MethodRunnable() {
			super(key);
		}

		public void setArgs(Object[] objects) {
			this.objects = objects;
		}

		@Override protected void execute() {
			exeMehtod(objects);
		}
	}

	private void exeMehtod(Object[] objects) {
		try {
			// 业务拦截器 - 前
			for (J2WStartInterceptor item : j2WMethods.j2WStartInterceptor) {
				item.intercept(service, method);
			}

			method.invoke(impl, objects);// 执行
			// 业务拦截器 - 后
			for (J2WEndInterceptor item : j2WMethods.j2WEndInterceptor) {
				item.intercept(service, method);
			}

		} catch (Throwable throwable) {
			throwable.printStackTrace();
			// 业务处理拦截器
			// 业务错误拦截器
			for (J2WErrorInterceptor item : j2WMethods.j2WErrorInterceptor) {
				item.methodError(service, method, throwable);
			}
		} finally {
			j2WMethods.stack.remove(key);// 出栈
		}
	}

	int				type;

	Object			impl;

	boolean			isRepeat;

	String			key;

	Method			method;

	MethodRunnable	methodRunnable;

	J2WMethods		j2WMethods;

	Class			service;

	/**
	 * 构造函数
	 *
	 * @param key
	 * @param impl
	 * @param method
	 * @param type
	 *            执行类型
	 * @param isRepeat
	 * @param j2WMethods
	 * @param service
	 */
	public J2WMethod(String key, Object impl, Method method, int type, boolean isRepeat, J2WMethods j2WMethods, Class service) {
		this.key = key;
		this.impl = impl;
		this.type = type;
		this.isRepeat = isRepeat;
		this.j2WMethods = j2WMethods;
		this.method = method;
		this.service = service;
		if (type != TYPE_INVOKE_EXE) {
			this.methodRunnable = new MethodRunnable();
		}
	}

}
