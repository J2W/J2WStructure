package j2w.team.modules.structure;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.SimpleArrayMap;
import android.view.KeyEvent;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.J2WIBiz;
import j2w.team.core.J2WICommonBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.log.L;
import j2w.team.modules.methodProxy.J2WProxy;
import j2w.team.structure.R;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */

public class J2WStructureManage implements J2WStructureIManage {

	private final ConcurrentHashMap<Class<?>, Object>										stackDisplay;

	private final ConcurrentHashMap<Class<?>, Object>										stackHttp;

	private final ConcurrentHashMap<Class<?>, Object>										stackCommonBiz;

	private final ConcurrentHashMap<Class<?>, Object>										stackImpl;

	private final ConcurrentHashMap<Class<?>, SimpleArrayMap<Integer, J2WStructureModel>>	statckRepeatBiz;

	public J2WStructureManage() {
		/** 初始化集合 **/
		stackHttp = new ConcurrentHashMap<>();
		stackCommonBiz = new ConcurrentHashMap<>();
		stackDisplay = new ConcurrentHashMap<>();
		stackImpl = new ConcurrentHashMap<>();
		statckRepeatBiz = new ConcurrentHashMap<>();

	}

	@Override public synchronized void attach(J2WStructureModel view) {
		synchronized (statckRepeatBiz) {
			SimpleArrayMap<Integer, J2WStructureModel> stack = statckRepeatBiz.get(view.getService());
			if (stack == null) {
				stack = new SimpleArrayMap();
			}
			stack.put(view.key, view);
			statckRepeatBiz.put(view.getService(), stack);
		}
	}

	@Override public void detach(J2WStructureModel view) {
		synchronized (statckRepeatBiz) {
			SimpleArrayMap<Integer, J2WStructureModel> stack = statckRepeatBiz.get(view.getService());
			if (stack != null) {
				J2WStructureModel j2WStructureModel = stack.get(view.key);
				if (j2WStructureModel == null) {
					return;
				}
				stack.remove(j2WStructureModel.key);
				if (stack.size() < 1) {
					statckRepeatBiz.remove(view.getService());
				}
				j2WStructureModel.clearAll();
			}
		}
		synchronized (stackImpl) {
			stackImpl.clear();
		}
		synchronized (stackDisplay) {
			stackDisplay.clear();
		}
		synchronized (stackCommonBiz) {
			stackCommonBiz.clear();
		}
		synchronized (stackHttp) {
			stackHttp.clear();
		}
	}

	/**
	 * 获取实现类
	 *
	 * @param service
	 * @param <D>
	 * @return
	 */
	private <D> Object getImplClass(@NotNull Class<D> service) {
		validateServiceClass(service);
		try {
			// 获取注解
			Impl impl = service.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			Class clazz = Class.forName(impl.value().getName());
			Constructor c = clazz.getDeclaredConstructor();
			c.setAccessible(true);
			J2WCheckUtils.checkNotNull(clazz, "业务类为空～");
			/** 创建类 **/
			Object o = c.newInstance();
			return o;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(service) + "，没有找到业务类！");
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(service) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(service) + "，访问权限异常！");
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(String.valueOf(service) + "，没有找到构造方法！");
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(String.valueOf(service) + "，反射异常！");
		}
	}

	/**
	 * 验证类 - 判断是否是一个接口
	 *
	 * @param service
	 * @param <T>
	 */
	private <T> void validateServiceClass(Class<T> service) {
		if (service == null || !service.isInterface()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(service);
			stringBuilder.append("，该类不是接口！");
			throw new IllegalArgumentException(stringBuilder.toString());
		}
	}

	@Override public <B extends J2WIBiz> B biz(Class<B> biz) {
		SimpleArrayMap<Integer, J2WStructureModel> stack = statckRepeatBiz.get(biz);
		if (stack == null) {
			return createNullService(biz);
		}
		J2WStructureModel j2WStructureModel = stack.valueAt(0);
		if (j2WStructureModel == null) {
			return createNullService(biz);
		}
		if (j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
			return createNullService(biz);
		}
		return (B) j2WStructureModel.getJ2WProxy().proxy;
	}

	@Override public <B extends J2WIBiz> boolean isExist(Class<B> biz) {
		SimpleArrayMap<Integer, J2WStructureModel> stack = statckRepeatBiz.get(biz);
		if (stack == null) {
			return false;
		}
		J2WStructureModel j2WStructureModel = stack.valueAt(0);
		if (j2WStructureModel == null) {
			return false;
		}
		if (j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
			return false;
		}
		return true;
	}

	@Override public <D extends J2WIDisplay> D display(Class<D> displayClazz) {
		D display = (D) stackDisplay.get(displayClazz);
		if (display == null) {
			synchronized (stackDisplay) {
				if (display == null) {
					J2WCheckUtils.checkNotNull(displayClazz, "display接口不能为空");
					J2WCheckUtils.validateServiceInterface(displayClazz);
					Object impl = getImplClass(displayClazz);
					J2WProxy j2WProxy = J2WHelper.methodsProxy().createDisplay(displayClazz, impl);
					stackDisplay.put(displayClazz, j2WProxy.proxy);
					display = (D) j2WProxy.proxy;
				}
			}
		}
		return display;
	}

	@Override public <B extends J2WICommonBiz> B common(Class<B> service) {
		B b = (B) stackCommonBiz.get(service);
		if (b == null) {
			synchronized (stackCommonBiz) {
				if (b == null) {
					J2WCheckUtils.checkNotNull(service, "biz接口不能为空～");
					J2WCheckUtils.validateServiceInterface(service);
					Object impl = getImplClass(service);
					J2WProxy j2WProxy = J2WHelper.methodsProxy().create(service, impl);
					stackCommonBiz.put(service, j2WProxy.proxy);
					b = (B) j2WProxy.proxy;
				}
			}
		}
		return b;
	}

	@Override public <B extends J2WIBiz> List<B> bizList(Class<B> service) {
		SimpleArrayMap<Integer, J2WStructureModel> stack = statckRepeatBiz.get(service);
		List list = new ArrayList();
		if (stack == null) {
			return list;
		}
		int count = stack.size();
		for (int i = 0; i < count; i++) {
			J2WStructureModel j2WStructureModel = stack.valueAt(i);
			if (j2WStructureModel == null || j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
				list.add(createNullService(service));
			} else {
				list.add(j2WStructureModel.getJ2WProxy().proxy);
			}
		}
		return list;
	}

	@Override public <H> H http(Class<H> httpClazz) {
		H http = (H) stackHttp.get(httpClazz);
		if (http == null) {
			synchronized (stackHttp) {
				if (http == null) {
					J2WCheckUtils.checkNotNull(httpClazz, "http接口不能为空");
					J2WCheckUtils.validateServiceInterface(httpClazz);
					http = J2WHelper.httpAdapter().create(httpClazz);
					stackHttp.put(httpClazz, http);
				}
			}
		}
		return http;
	}

	@Override public <P> P impl(Class<P> implClazz) {
		P impl = (P) stackImpl.get(implClazz);
		if (impl == null) {
			synchronized (stackImpl) {
				if (impl == null) {
					J2WCheckUtils.checkNotNull(implClazz, "impl接口不能为空");
					J2WCheckUtils.validateServiceInterface(implClazz);
					impl = J2WHelper.methodsProxy().createImpl(implClazz, getImplClass(implClazz));
					stackImpl.put(implClazz, impl);
				}
			}
		}
		return impl;
	}

	@Override public <T> T createMainLooper(Class<T> service, final Object ui) {
		J2WCheckUtils.validateServiceInterface(service);
		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {

			@Override public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
				// 如果有返回值 - 直接执行
				if (!method.getReturnType().equals(void.class)) {
                    if(ui == null){
                        return null;
                    }
					return method.invoke(ui, args);
				}

				// 如果是主线程 - 直接执行
				if (!J2WHelper.isMainLooperThread()) {// 子线程
                    if(ui == null){
                        return null;
                    }
					return method.invoke(ui, args);
				}
				Runnable runnable = new Runnable() {

					@Override public void run() {
						try {
                            if(ui == null){
                                return;
                            }
							method.invoke(ui, args);
						} catch (Exception throwable) {
							if (J2WHelper.getInstance().isLogOpen()) {
								throwable.printStackTrace();
							}
							return;
						}
					}
				};
				J2WHelper.mainLooper().execute(runnable);
				return null;
			}
		});
	}

	public <U> U createNullService(final Class<U> service) {
		J2WCheckUtils.validateServiceInterface(service);
		return (U) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {

			@Override public Object invoke(Object proxy, Method method, Object... args) throws Throwable {

				if (J2WHelper.getInstance().isLogOpen()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("UI被销毁,回调接口继续执行");
					stringBuilder.append("方法[");
					stringBuilder.append(method.getName());
					stringBuilder.append("]");
					L.tag(service.getSimpleName());
					L.i(stringBuilder.toString());
				}

				if (method.getReturnType().equals(int.class) || method.getReturnType().equals(long.class) || method.getReturnType().equals(float.class) || method.getReturnType().equals(double.class)
						|| method.getReturnType().equals(short.class)) {
					return 0;
				}

				if (method.getReturnType().equals(boolean.class)) {
					return false;
				}
				if (method.getReturnType().equals(byte.class)) {
					return Byte.parseByte(null);
				}
				if (method.getReturnType().equals(char.class)) {
					return ' ';
				}
				return null;
			}
		});
	}

	@Override public boolean onKeyBack(int keyCode, FragmentManager fragmentManager, J2WActivity bj2WActivity) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			int idx = fragmentManager.getBackStackEntryCount();
			if (idx > 1) {
				FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(idx - 1);
				Object view = fragmentManager.findFragmentByTag(entry.getName());
				if (view instanceof J2WFragment) {
					return ((J2WFragment) view).onKeyBack();
				}
			} else {

				Object view = fragmentManager.findFragmentById(R.id.j2w_home);
				if (view instanceof J2WFragment) {
					return ((J2WFragment) view).onKeyBack();
				}
			}
			if (bj2WActivity != null) {
				return bj2WActivity.onKeyBack();
			}
		}
		return false;
	}

	@Override public void printBackStackEntry(FragmentManager fragmentManager) {
		if (J2WHelper.getInstance().isLogOpen()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[");
			for (Fragment fragment : fragmentManager.getFragments()) {
				if (fragment != null) {
					stringBuilder.append(",");
					stringBuilder.append(fragment.getClass().getSimpleName());
				}
			}
			stringBuilder.append("]");
			stringBuilder.deleteCharAt(1);
			L.tag("Activity FragmentManager:");
			L.i(stringBuilder.toString());
		}
	}
}