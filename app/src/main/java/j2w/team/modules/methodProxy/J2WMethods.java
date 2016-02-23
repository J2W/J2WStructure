package j2w.team.modules.methodProxy;

import android.support.v4.util.SimpleArrayMap;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Stack;

import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.plugin.J2WActivityInterceptor;
import j2w.team.core.plugin.J2WEndInterceptor;
import j2w.team.core.plugin.J2WErrorInterceptor;
import j2w.team.core.plugin.J2WFragmentInterceptor;
import j2w.team.core.plugin.J2WHttpErrorInterceptor;
import j2w.team.core.plugin.J2WStartInterceptor;

/**
 * @创建人 sky
 * @创建时间 16/1/5
 * @类描述 方法代理处理
 */
public final class J2WMethods {

	final SimpleArrayMap<Method, J2WMethod>		methodHandlerCache;

	final Stack<String>							stack;

	final J2WActivityInterceptor				j2WActivityInterceptor;

	final J2WFragmentInterceptor				j2WFragmentInterceptor;

	final ArrayList<J2WStartInterceptor>		j2WStartInterceptor;		// 方法开始拦截器

	final ArrayList<J2WEndInterceptor>			j2WEndInterceptor;			// 方法结束拦截器

	final ArrayList<J2WErrorInterceptor>		j2WErrorInterceptor;		// 方法错误拦截器

	final ArrayList<J2WHttpErrorInterceptor>	j2WHttpErrorInterceptor;	// 方法错误拦截器

	public J2WMethods(J2WActivityInterceptor j2WActivityInterceptor, J2WFragmentInterceptor j2WFragmentInterceptor, ArrayList<J2WStartInterceptor> j2WStartInterceptor,
			ArrayList<J2WEndInterceptor> j2WEndInterceptor, ArrayList<J2WErrorInterceptor> j2WErrorInterceptor, ArrayList<J2WHttpErrorInterceptor> j2WHttpErrorInterceptor) {
		this.methodHandlerCache = new SimpleArrayMap<>();
		this.stack = new Stack<>();
		this.j2WEndInterceptor = j2WEndInterceptor;
		this.j2WStartInterceptor = j2WStartInterceptor;
		this.j2WErrorInterceptor = j2WErrorInterceptor;
		this.j2WHttpErrorInterceptor = j2WHttpErrorInterceptor;
		this.j2WActivityInterceptor = j2WActivityInterceptor;
		this.j2WFragmentInterceptor = j2WFragmentInterceptor;
	}

	/**
	 * 创建
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */
	<T> T create(final Class<T> service, int type, Object ui) {
		J2WCheckUtils.validateServiceInterface(service);
		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new J2WInvocationHandler(service, type, ui) {

			@Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
				J2WMethod j2WMethod = null;
				switch (type) {
					case J2WInvocationHandler.TYPE_BIZ:
						j2WMethod = loadJ2WMethod(method, service, isNotCacheMethed);
						break;
					case J2WInvocationHandler.TYPE_DISPLA:
						j2WMethod = loadJ2WDisplayMethod(method, service, isNotCacheMethed);
						break;
					case J2WInvocationHandler.TYPE_UI:
						j2WMethod = loadJ2WUIMethod(method, service, isNotCacheMethed);
						break;
				}
				return j2WMethod.invoke(impl, args);
			}
		});
	}

	public <T> T createUI(final Class<T> service, Object ui) {
		J2WCheckUtils.validateServiceInterface(service);
		return create(service, J2WInvocationHandler.TYPE_UI, ui);
	}

	public <T> T createDisplay(final Class<T> service) {
		J2WCheckUtils.validateServiceInterface(service);
		return create(service, J2WInvocationHandler.TYPE_DISPLA, null);
	}

	public <T> T createBiz(final Class<T> service, Object ui) {
		J2WCheckUtils.validateServiceInterface(service);
		return create(service, J2WInvocationHandler.TYPE_BIZ, ui);
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
	 * 加载UI接口
	 *
	 * @param method
	 * @param service
	 * @param notCache
	 * @param <T>
	 * @return
	 */
	private <T> J2WMethod loadJ2WUIMethod(Method method, Class<T> service, boolean notCache) {
		J2WMethod j2WMethod;
		if (notCache) {
			j2WMethod = J2WMethod.createDisplayMethod(this, method, service);
		} else {
			synchronized (methodHandlerCache) {
				j2WMethod = methodHandlerCache.get(method);
				if (j2WMethod == null) {
					j2WMethod = J2WMethod.createUIMethod(this, method, service);
					methodHandlerCache.put(method, j2WMethod);
				}
			}
		}
		return j2WMethod;
	}

	/**
	 * 加载display接口
	 *
	 * @param method
	 * @param service
	 * @param notCache
	 * @param <T>
	 * @return
	 */
	private <T> J2WMethod loadJ2WDisplayMethod(Method method, Class<T> service, boolean notCache) {
		J2WMethod j2WMethod;

		if (notCache) {
			j2WMethod = J2WMethod.createDisplayMethod(this, method, service);
		} else {
			synchronized (methodHandlerCache) {
				j2WMethod = methodHandlerCache.get(method);
				if (j2WMethod == null) {
					j2WMethod = J2WMethod.createDisplayMethod(this, method, service);
					methodHandlerCache.put(method, j2WMethod);
				}
			}
		}

		return j2WMethod;
	}

	/**
	 * 加载接口
	 *
	 * @param method
	 * @param service
	 * @param notCache
	 * @param <T>
	 * @return
	 */
	private <T> J2WMethod loadJ2WMethod(Method method, Class<T> service, boolean notCache) {
		J2WMethod j2WMethod;
		if (notCache) {
			j2WMethod = J2WMethod.createDisplayMethod(this, method, service);
		} else {
			synchronized (methodHandlerCache) {
				j2WMethod = methodHandlerCache.get(method);
				if (j2WMethod == null) {
					j2WMethod = J2WMethod.createBizMethod(this, method, service);
					methodHandlerCache.put(method, j2WMethod);
				}
			}
		}
		return j2WMethod;
	}

	public static class Builder {

		private J2WActivityInterceptor				j2WActivityInterceptor;	// activity拦截器

		private J2WFragmentInterceptor				j2WFragmentInterceptor;	// activity拦截器

		private ArrayList<J2WStartInterceptor>		j2WStartInterceptors;		// 方法开始拦截器

		private ArrayList<J2WEndInterceptor>		j2WEndInterceptors;		// 方法结束拦截器

		private ArrayList<J2WErrorInterceptor>		j2WErrorInterceptors;		// 方法错误拦截器

		private ArrayList<J2WHttpErrorInterceptor>	j2WHttpErrorInterceptors;	// 方法网络错误拦截器

		public void setActivityInterceptor(J2WActivityInterceptor j2WActivityInterceptor) {
			this.j2WActivityInterceptor = j2WActivityInterceptor;
		}

		public void setFragmentInterceptor(J2WFragmentInterceptor j2WFragmentInterceptor) {
			this.j2WFragmentInterceptor = j2WFragmentInterceptor;

		}

		public Builder addStartInterceptor(J2WStartInterceptor j2WStartInterceptor) {
			if (j2WStartInterceptors == null) {
				j2WStartInterceptors = new ArrayList<>();
			}
			if (!j2WStartInterceptors.contains(j2WStartInterceptor)) {
				j2WStartInterceptors.add(j2WStartInterceptor);
			}
			return this;
		}

		public Builder addEndInterceptor(J2WEndInterceptor j2WEndInterceptor) {
			if (j2WEndInterceptors == null) {
				j2WEndInterceptors = new ArrayList<>();
			}
			if (!j2WEndInterceptors.contains(j2WEndInterceptor)) {
				j2WEndInterceptors.add(j2WEndInterceptor);
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
			return new J2WMethods(j2WActivityInterceptor, j2WFragmentInterceptor, j2WStartInterceptors, j2WEndInterceptors, j2WErrorInterceptors, j2WHttpErrorInterceptors);
		}

		private void ensureSaneDefaults() {
			if (j2WStartInterceptors == null) {
				j2WStartInterceptors = new ArrayList<>();
			}
			if (j2WEndInterceptors == null) {
				j2WEndInterceptors = new ArrayList<>();
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
		}

	}
}
