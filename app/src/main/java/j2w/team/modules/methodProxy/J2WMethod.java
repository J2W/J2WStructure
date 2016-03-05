package j2w.team.modules.methodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import j2w.team.J2WHelper;
import j2w.team.core.J2WBizRunAnnotation;
import j2w.team.core.J2WRunnable;
import j2w.team.core.exception.J2WNotUIPointerException;
import j2w.team.core.plugin.BizEndInterceptor;
import j2w.team.core.plugin.J2WErrorInterceptor;
import j2w.team.core.plugin.J2WHttpErrorInterceptor;
import j2w.team.core.plugin.BizStartInterceptor;
import j2w.team.modules.http.J2WError;
import j2w.team.modules.log.L;
import j2w.team.modules.threadpool.BackgroundType;

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

	static J2WMethod createBizMethod(String key,Object impl, Method method, Class service) {
		// 是否重复
		boolean isRepeat = parseRepeat(method);
		// 拦截方法标记
		int interceptor = parseInterceptor(method);
		// 判断是否是子线程
		int type = parseBackground(method);

		boolean j2WBizRunAnnotation = parseRunAnnotation(impl.getClass());

		return new J2WMethod(key, interceptor, method, type, isRepeat, j2WBizRunAnnotation, service);
	}

	private static boolean parseRunAnnotation(Class method) {
		J2WBizRunAnnotation j2WRepeat = (J2WBizRunAnnotation) method.getAnnotation(J2WBizRunAnnotation.class);
		if (j2WRepeat != null) {
			return true;
		} else {
			return false;
		}
	}

	private static boolean parseRepeat(Method method) {

		Repeat j2WRepeat = method.getAnnotation(Repeat.class);
		if (j2WRepeat != null && j2WRepeat.value()) {
			return true;
		} else {
			return false;
		}
	}

	private static int parseBackground(Method method) {
		int type = TYPE_INVOKE_EXE;
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

		return type;
	}

	private static int parseInterceptor(Method method) {
		// 拦截方法标记
		Interceptor interceptorClass = method.getAnnotation(Interceptor.class);
		if (interceptorClass != null) {
			return interceptorClass.value();
		} else {
			return 0;
		}
	}

	public <T> T invoke(final Object impl, final Object[] args) throws InterruptedException {
		T result = null;
		if (!isRepeat) {
			if (isExe) { // 如果存在什么都不做
				L.tag("J2W-Method");
				L.i("该方法正在执行 - %s", key);
				return result;
			}
			isExe = true;
		}
		this.impl = impl;
		this.implName = impl.getClass().getName();

		switch (type) {
			case TYPE_INVOKE_EXE:
				defaultMethod(args);
				result = (T) backgroundResult;
				break;
			default:
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
				break;
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
			defaultMethod(objects);
		}
	}

	private void defaultMethod(Object[] objects) {
		try {
			exeMethod(method, impl, objects);
		} catch (Throwable throwable) {
			exeError(method, throwable);
		} finally {
			isExe = false;
		}
	}

	public void exeMethod(Method method, Object impl, Object[] objects) throws InvocationTargetException, IllegalAccessException {
		// 业务拦截器 - 前
		for (BizStartInterceptor item : J2WHelper.methodsProxy().bizStartInterceptor) {
			item.interceptStart(implName, service, method, interceptor, objects);
		}
		backgroundResult = method.invoke(impl, objects);// 执行
		// 业务拦截器 - 后
		for (BizEndInterceptor item : J2WHelper.methodsProxy().bizEndInterceptor) {
			item.interceptEnd(implName, service, method, interceptor, objects, backgroundResult);
		}
	}

	public void exeError(Method method, Throwable throwable) {
		if (J2WHelper.getInstance().isLogOpen()) {
			throwable.printStackTrace();
		}
		if (throwable.getCause() instanceof J2WError) {
			// 网络错误拦截器
			for (J2WHttpErrorInterceptor item : J2WHelper.methodsProxy().j2WHttpErrorInterceptor) {
				item.methodError(service, method, interceptor, (J2WError) throwable.getCause());
			}
		} else if (throwable.getCause() instanceof J2WNotUIPointerException) {
			// 忽略
			return;
		} else {
			// 业务错误拦截器
			for (J2WErrorInterceptor item : J2WHelper.methodsProxy().j2WErrorInterceptor) {
				item.interceptorError(implName, service, method, interceptor, throwable);
			}
		}
	}

	public boolean  getRunAnnottation(){
		return j2WBizRunAnnotation;
	}

	boolean			j2WBizRunAnnotation;

	int				type;

	Object			impl;

	String			implName;

	boolean			isRepeat;

	String			key;

	Method			method;

	MethodRunnable	methodRunnable;

	Class			service;

	int				interceptor;

	Object			backgroundResult;

	boolean			isExe;

	/**
	 * 构造函数
	 *
	 * @param key
	 * @param interceptor
	 * @param method
	 * @param type
	 *            执行类型
	 * @param isRepeat
	 * @param service
	 */
	public J2WMethod(String key, int interceptor, Method method, int type, boolean isRepeat, boolean j2WBizRunAnnotation, Class service) {
		this.key = key;
		this.interceptor = interceptor;
		this.type = type;
		this.isRepeat = isRepeat;
		this.method = method;
		this.service = service;
		this.j2WBizRunAnnotation = j2WBizRunAnnotation;
		if (type == TYPE_INVOKE_BACKGROUD_HTTP_EXE || type == TYPE_INVOKE_BACKGROUD_SINGLEWORK_EXE || type == TYPE_INVOKE_BACKGROUD_WORK_EXE) {
			this.methodRunnable = new MethodRunnable();
		}
	}
}
