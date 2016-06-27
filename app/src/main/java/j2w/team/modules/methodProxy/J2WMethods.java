package j2w.team.modules.methodProxy;

import android.os.Build;
import android.os.Looper;
import android.os.Trace;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.J2WIBiz;
import j2w.team.core.plugin.DisplayEndInterceptor;
import j2w.team.core.plugin.DisplayStartInterceptor;
import j2w.team.core.plugin.J2WActivityInterceptor;
import j2w.team.core.plugin.ImplEndInterceptor;
import j2w.team.core.plugin.BizEndInterceptor;
import j2w.team.core.plugin.J2WErrorInterceptor;
import j2w.team.core.plugin.J2WFragmentInterceptor;
import j2w.team.core.plugin.J2WHttpErrorInterceptor;
import j2w.team.core.plugin.ImplStartInterceptor;
import j2w.team.core.plugin.BizStartInterceptor;

/**
 * @创建人 sky
 * @创建时间 16/1/5
 * @类描述 方法代理处理
 */
public final class J2WMethods {

	final J2WActivityInterceptor				j2WActivityInterceptor;

	final J2WFragmentInterceptor				j2WFragmentInterceptor;

	final ArrayList<BizStartInterceptor>		bizStartInterceptor;		// 方法开始拦截器

	final DisplayStartInterceptor				displayStartInterceptor;	// 方法开始拦截器

	final ArrayList<BizEndInterceptor>			bizEndInterceptor;			// 方法结束拦截器

	final DisplayEndInterceptor					displayEndInterceptor;		// 方法结束拦截器

	private ArrayList<ImplStartInterceptor>		implStartInterceptors;		// 方法开始拦截器

	private ArrayList<ImplEndInterceptor>		implEndInterceptors;		// 方法结束拦截器

	final ArrayList<J2WErrorInterceptor>		j2WErrorInterceptor;		// 方法错误拦截器

	final ArrayList<J2WHttpErrorInterceptor>	j2WHttpErrorInterceptor;	// 方法错误拦截器

	public J2WMethods(J2WActivityInterceptor j2WActivityInterceptor, J2WFragmentInterceptor j2WFragmentInterceptor, ArrayList<BizStartInterceptor> bizStartInterceptor,
			DisplayStartInterceptor displayStartInterceptor, ArrayList<BizEndInterceptor> bizEndInterceptor, DisplayEndInterceptor displayEndInterceptor,
			ArrayList<ImplStartInterceptor> implStartInterceptors, ArrayList<ImplEndInterceptor> implEndInterceptors, ArrayList<J2WErrorInterceptor> j2WErrorInterceptor,
			ArrayList<J2WHttpErrorInterceptor> j2WHttpErrorInterceptor) {
		this.bizEndInterceptor = bizEndInterceptor;
		this.displayEndInterceptor = displayEndInterceptor;
		this.displayStartInterceptor = displayStartInterceptor;
		this.bizStartInterceptor = bizStartInterceptor;
		this.j2WErrorInterceptor = j2WErrorInterceptor;
		this.implStartInterceptors = implStartInterceptors;
		this.implEndInterceptors = implEndInterceptors;
		this.j2WHttpErrorInterceptor = j2WHttpErrorInterceptor;
		this.j2WActivityInterceptor = j2WActivityInterceptor;
		this.j2WFragmentInterceptor = j2WFragmentInterceptor;
	}

	/**
	 * 创建 BIZ
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */
	public <T> J2WProxy create(final Class<T> service, Object impl) {
		J2WCheckUtils.validateServiceInterface(service);

		final J2WProxy j2WProxy = new J2WProxy();
		j2WProxy.impl = impl;
		j2WProxy.proxy = Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new J2WInvocationHandler() {

			@Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
				// 如果有返回值 - 直接执行
				if (!method.getReturnType().equals(void.class)) {
					return method.invoke(j2WProxy.impl, args);

				}

				J2WMethod j2WMethod = loadJ2WMethod(j2WProxy, method, service);
				// 开始
				if (!J2WHelper.getInstance().isLogOpen()) {
					return j2WMethod.invoke(j2WProxy.impl, args);
				}
				enterMethod(method, args);
				long startNanos = System.nanoTime();

				Object result = j2WMethod.invoke(j2WProxy.impl, args);

				long stopNanos = System.nanoTime();
				long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
				exitMethod(method, result, lengthMillis);

				return result;
			}
		});

		return j2WProxy;
	}

	/**
	 * 创建 Display
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */
	public <T> J2WProxy createDisplay(final Class<T> service, Object impl) {
		J2WCheckUtils.validateServiceInterface(service);

		final J2WProxy j2WProxy = new J2WProxy();
		j2WProxy.impl = impl;
		j2WProxy.proxy = Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new J2WInvocationHandler() {

			@Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
				// 如果有返回值 - 直接执行
				if (!method.getReturnType().equals(void.class)) {
					return method.invoke(j2WProxy.impl, args);

				}
				J2WMethod j2WMethod = loadDisplayJ2WMethod(j2WProxy, method, service);
				// 开始
				if (!J2WHelper.getInstance().isLogOpen()) {
					return j2WMethod.invoke(j2WProxy.impl, args);
				}
				enterMethod(method, args);
				long startNanos = System.nanoTime();

				Object result = j2WMethod.invoke(j2WProxy.impl, args);

				long stopNanos = System.nanoTime();
				long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
				exitMethod(method, result, lengthMillis);

				return result;
			}
		});

		return j2WProxy;
	}

	private void enterMethod(Method method, Object... args) {
		Class<?> cls = method.getDeclaringClass();
		String methodName = method.getName();
		Object[] parameterValues = args;
		StringBuilder builder = new StringBuilder("\u21E2 ");
		builder.append(methodName).append('(');
		if (parameterValues != null) {
			for (int i = 0; i < parameterValues.length; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append(Strings.toString(parameterValues[i]));
			}
		}

		builder.append(')');

		if (Looper.myLooper() != Looper.getMainLooper()) {
			builder.append(" [Thread:\"").append(Thread.currentThread().getName()).append("\"]");
		}
		Log.v(cls.getSimpleName(), builder.toString());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			final String section = builder.toString().substring(2);
			Trace.beginSection(section);
		}
	}

	private void exitMethod(Method method, Object result, long lengthMillis) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			Trace.endSection();
		}
		Class<?> cls = method.getDeclaringClass();
		String methodName = method.getName();
		boolean hasReturnType = method.getReturnType() != void.class;

		StringBuilder builder = new StringBuilder("\u21E0 ").append(methodName).append(" [").append(lengthMillis).append("ms]");

		if (hasReturnType) {
			builder.append(" = ");
			builder.append(Strings.toString(result));
		}
		Log.v(cls.getSimpleName(), builder.toString());
	}

	/**
	 * 获取方法唯一标记
	 * 
	 * @param method
	 * @param classes
	 * @return
	 */
	private String getKey(Method method, Class[] classes) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(method.getName());
		stringBuilder.append("(");
		for (Class clazz : classes) {
			stringBuilder.append(clazz.getSimpleName());
			stringBuilder.append(",");
		}
		if (stringBuilder.length() > 2) {
			stringBuilder.deleteCharAt(stringBuilder.toString().length() - 1);
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

	/**
	 * 创建 IMPL
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */
	public <T> T createImpl(final Class<T> service, final Object impl) {
		J2WCheckUtils.validateServiceInterface(service);
		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new J2WInvocationHandler() {

			@Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
				// 业务拦截器 - 前
				for (ImplStartInterceptor item : J2WHelper.methodsProxy().implStartInterceptors) {
					item.interceptStart(impl.getClass().getName(), service, method, args);
				}
				Object backgroundResult;
				if (!J2WHelper.getInstance().isLogOpen()) {
					backgroundResult = method.invoke(impl, args);// 执行
				} else {
					enterMethod(method, args);
					long startNanos = System.nanoTime();
					backgroundResult = method.invoke(impl, args);// 执行
					long stopNanos = System.nanoTime();
					long lengthMillis = TimeUnit.NANOSECONDS.toMillis(stopNanos - startNanos);
					exitMethod(method, backgroundResult, lengthMillis);
				}

				// 业务拦截器 - 后
				for (ImplEndInterceptor item : J2WHelper.methodsProxy().implEndInterceptors) {
					item.interceptEnd(impl.getClass().getName(), service, method, args, backgroundResult);
				}
				return backgroundResult;
			}
		});
	}

	/**
	 * 获取拦截器
	 *
	 * @return
	 */
	public J2WActivityInterceptor activityInterceptor() {
		return j2WActivityInterceptor;
	}

	/**
	 * 获取拦截器
	 *
	 * @return
	 */
	public J2WFragmentInterceptor fragmentInterceptor() {
		return j2WFragmentInterceptor;
	}

	/**
	 * 加载接口
	 *
	 * @param j2WProxy
	 * @param method
	 * @param service
	 * @param <T>
	 * @return
	 */
	private <T> J2WMethod loadJ2WMethod(J2WProxy j2WProxy, Method method, Class<T> service) {
		synchronized (j2WProxy.methodCache) {
			String methodKey = getKey(method, method.getParameterTypes());
			J2WMethod j2WMethod = j2WProxy.methodCache.get(methodKey);
			if (j2WMethod == null) {
				j2WMethod = J2WMethod.createBizMethod(method, service);
				j2WProxy.methodCache.put(methodKey, j2WMethod);
			}
			return j2WMethod;
		}
	}

	/**
	 * 加载接口
	 *
	 * @param j2WProxy
	 * @param method
	 * @param service
	 * @param <T>
	 * @return
	 */
	private <T> J2WMethod loadDisplayJ2WMethod(J2WProxy j2WProxy, Method method, Class<T> service) {
		synchronized (j2WProxy.methodCache) {
			String methodKey = getKey(method, method.getParameterTypes());
			J2WMethod j2WMethod = j2WProxy.methodCache.get(methodKey);
			if (j2WMethod == null) {
				j2WMethod = J2WMethod.createDisplayMethod(method, service);
				j2WProxy.methodCache.put(methodKey, j2WMethod);
			}
			return j2WMethod;
		}
	}

	public static class Builder {

		private J2WActivityInterceptor				j2WActivityInterceptor;		// activity拦截器

		private J2WFragmentInterceptor				j2WFragmentInterceptor;		// activity拦截器

		private ArrayList<BizStartInterceptor>		j2WStartInterceptors;		// 方法开始拦截器

		private ArrayList<BizEndInterceptor>		bizEndInterceptors;			// 方法结束拦截器

		private ArrayList<ImplStartInterceptor>		implStartInterceptors;		// 方法开始拦截器

		private ArrayList<ImplEndInterceptor>		implEndInterceptors;		// 方法结束拦截器

		private ArrayList<J2WErrorInterceptor>		j2WErrorInterceptors;		// 方法错误拦截器

		private ArrayList<J2WHttpErrorInterceptor>	j2WHttpErrorInterceptors;	// 方法网络错误拦截器

		private DisplayStartInterceptor				displayStartInterceptor;	// 方法开始拦截器

		private DisplayEndInterceptor				displayEndInterceptor;		// 方法结束拦截器

		public void setActivityInterceptor(J2WActivityInterceptor j2WActivityInterceptor) {
			this.j2WActivityInterceptor = j2WActivityInterceptor;
		}

		public void setFragmentInterceptor(J2WFragmentInterceptor j2WFragmentInterceptor) {
			this.j2WFragmentInterceptor = j2WFragmentInterceptor;

		}

		public Builder addStartInterceptor(BizStartInterceptor bizStartInterceptor) {
			if (j2WStartInterceptors == null) {
				j2WStartInterceptors = new ArrayList<>();
			}
			if (!j2WStartInterceptors.contains(bizStartInterceptor)) {
				j2WStartInterceptors.add(bizStartInterceptor);
			}
			return this;
		}

		public Builder addEndInterceptor(BizEndInterceptor bizEndInterceptor) {
			if (bizEndInterceptors == null) {
				bizEndInterceptors = new ArrayList<>();
			}
			if (!bizEndInterceptors.contains(bizEndInterceptor)) {
				bizEndInterceptors.add(bizEndInterceptor);
			}
			return this;
		}

		public Builder setDisplayStartInterceptor(DisplayStartInterceptor displayStartInterceptor) {
			this.displayStartInterceptor = displayStartInterceptor;
			return this;
		}

		public Builder setDisplayEndInterceptor(DisplayEndInterceptor displayEndInterceptor) {
			this.displayEndInterceptor = displayEndInterceptor;
			return this;
		}

		public Builder addStartImplInterceptor(ImplStartInterceptor implStartInterceptor) {
			if (implStartInterceptors == null) {
				implStartInterceptors = new ArrayList<>();
			}
			if (!implStartInterceptors.contains(implStartInterceptor)) {
				implStartInterceptors.add(implStartInterceptor);
			}
			return this;
		}

		public Builder addEndImplInterceptor(ImplEndInterceptor implEndInterceptor) {
			if (implEndInterceptors == null) {
				implEndInterceptors = new ArrayList<>();
			}
			if (!implEndInterceptors.contains(implEndInterceptor)) {
				implEndInterceptors.add(implEndInterceptor);
			}
			return this;
		}

		public void addErrorInterceptor(J2WErrorInterceptor j2WErrorInterceptor) {
			if (j2WErrorInterceptors == null) {
				j2WErrorInterceptors = new ArrayList<>();
			}
			if (!j2WErrorInterceptors.contains(j2WErrorInterceptor)) {
				j2WErrorInterceptors.add(j2WErrorInterceptor);
			}
		}

		public void addHttpErrorInterceptor(J2WHttpErrorInterceptor j2WHttpErrorInterceptor) {
			if (j2WHttpErrorInterceptors == null) {
				j2WHttpErrorInterceptors = new ArrayList<>();
			}
			if (!j2WHttpErrorInterceptors.contains(j2WHttpErrorInterceptor)) {
				j2WHttpErrorInterceptors.add(j2WHttpErrorInterceptor);
			}
		}

		public J2WMethods build() {
			// 默认值
			ensureSaneDefaults();
			return new J2WMethods(j2WActivityInterceptor, j2WFragmentInterceptor, j2WStartInterceptors, displayStartInterceptor, bizEndInterceptors, displayEndInterceptor, implStartInterceptors,
					implEndInterceptors, j2WErrorInterceptors, j2WHttpErrorInterceptors);
		}

		private void ensureSaneDefaults() {
			if (j2WStartInterceptors == null) {
				j2WStartInterceptors = new ArrayList<>();
			}
			if (bizEndInterceptors == null) {
				bizEndInterceptors = new ArrayList<>();
			}
			if (j2WErrorInterceptors == null) {
				j2WErrorInterceptors = new ArrayList<>();
			}
			if (j2WHttpErrorInterceptors == null) {
				j2WHttpErrorInterceptors = new ArrayList<>();
			}
			if (j2WFragmentInterceptor == null) {
				j2WFragmentInterceptor = J2WFragmentInterceptor.NONE;
			}
			if (j2WActivityInterceptor == null) {
				j2WActivityInterceptor = J2WActivityInterceptor.NONE;
			}
			if (implStartInterceptors == null) {
				implStartInterceptors = new ArrayList<>();
			}
			if (implEndInterceptors == null) {
				implEndInterceptors = new ArrayList<>();
			}
		}

	}
}
