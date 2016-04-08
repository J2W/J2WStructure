package j2w.team.modules.structure;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.J2WBiz;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.log.L;
import j2w.team.structure.R;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */

public class J2WStructureManage implements J2WStructureIManage {

	private final ConcurrentHashMap<Class<?>, Object>							stackDisplay;

	private final ConcurrentHashMap<Class<?>, Object>							stackHttp;

	private final ConcurrentHashMap<Class<?>, Object>							stackBiz;

	private final ConcurrentHashMap<Class<?>, Object>							stackImpl;

	private final ConcurrentHashMap<Class<?>, SimpleArrayMap<Integer, Object>>	statckRepeatBiz;

	public J2WStructureManage() {
		/** 初始化集合 **/
		stackHttp = new ConcurrentHashMap<>();
		stackBiz = new ConcurrentHashMap<>();
		stackDisplay = new ConcurrentHashMap<>();
		stackImpl = new ConcurrentHashMap<>();
		statckRepeatBiz = new ConcurrentHashMap<>();

	}

	@Override public void attach(Object view) {
		Class bizClass = J2WAppUtil.getSuperClassGenricType(view.getClass(), 0);
		J2WCheckUtils.validateServiceInterface(bizClass);
		Object impl = getImplClass(bizClass, view);
		Object proxy = J2WHelper.methodsProxy().create(bizClass, impl);

		SimpleArrayMap stack = statckRepeatBiz.get(bizClass);
		if (stack == null) {
			stack = new SimpleArrayMap();
		}
		stack.put(view.hashCode(), proxy);
		statckRepeatBiz.put(bizClass, stack);
	}

	@Override public void detach(Object view) {
		Class bizClass = J2WAppUtil.getSuperClassGenricType(view.getClass(), 0);
		J2WCheckUtils.validateServiceInterface(bizClass);

		SimpleArrayMap stack = statckRepeatBiz.get(bizClass);

		if (stack != null) {
			J2WIBiz j2WIBiz = (J2WIBiz) stack.get(view.hashCode());
			if (j2WIBiz != null) {
				j2WIBiz.detach();
			}
		}
		stack.remove(view.hashCode());
		if (stack.size() < 1) {
			statckRepeatBiz.remove(bizClass);
		}
	}

	/**
	 * 获取实现类
	 *
	 * @param service
	 * @param <D>
	 * @return
	 */
	@Override public <D> Object getImplClass(@NotNull Class<D> service, Object ui) {
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
			// 如果是业务类
			if (o instanceof J2WBiz) {
				((J2WBiz) o).initUI(ui);
			}
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

	@Override public <D extends J2WIDisplay> D display(Class<D> displayClazz) {
		J2WCheckUtils.checkNotNull(displayClazz, "display接口不能为空");
		J2WCheckUtils.validateServiceInterface(displayClazz);

		if (stackDisplay.get(displayClazz) == null) {
			Object display = createMainLooper(displayClazz, getImplClass(displayClazz, null));
			stackDisplay.put(displayClazz, display);
			return (D) display;
		} else {
			return (D) stackDisplay.get(displayClazz);
		}
	}

	@Override public <B extends J2WIBiz> B biz(Class<B> biz) {
		J2WCheckUtils.checkNotNull(biz, "biz接口不能为空～");
		SimpleArrayMap stack = statckRepeatBiz.get(biz);
		if (stack != null) {
			J2WIBiz j2WIBiz = (J2WIBiz) stack.valueAt(0);
			return (B) j2WIBiz;
		}
		return null;
	}

	@Override public <B extends J2WIBiz> B biz(Object view, Class<B> bizClazz) {
		SimpleArrayMap stack = statckRepeatBiz.get(bizClazz);
		if (stack != null) {
			if (stack.size() == 1) {
				return biz(bizClazz);
			}
			return (B) stack.get(view.hashCode());
		}
		return null;
	}

	@Override public <B extends J2WIBiz> B common(Class<B> service) {
		J2WCheckUtils.checkNotNull(service, "biz接口不能为空～");
		J2WCheckUtils.validateServiceInterface(service);
		if (stackBiz.get(service) == null) {
			Object impl = getImplClass(service, null);
			B b = J2WHelper.methodsProxy().create(service, impl);
			stackBiz.put(service, b);
			return b;
		} else {
			return (B) stackBiz.get(service);
		}
	}

	@Override public <B extends J2WIBiz> List<B> bizList(Class<B> service) {
		SimpleArrayMap stack = statckRepeatBiz.get(service);
		if (stack == null) {
			return null;
		}
		List list = new ArrayList();

		int count = stack.size();

		for (int i = 0; i < count; i++) {
			list.add(stack.valueAt(i));
		}
		return list;
	}

	@Override public <H> H http(Class<H> httpClazz) {
		J2WCheckUtils.checkNotNull(httpClazz, "http接口不能为空");
		J2WCheckUtils.validateServiceInterface(httpClazz);
		if (stackHttp.get(httpClazz) == null) {
			Object http = J2WHelper.httpAdapter().create(httpClazz);
			stackHttp.put(httpClazz, http);
			return (H) http;
		} else {
			return (H) stackHttp.get(httpClazz);
		}
	}

	@Override public <P> P impl(Class<P> implClazz) {
		J2WCheckUtils.checkNotNull(implClazz, "impl接口不能为空");
		J2WCheckUtils.validateServiceInterface(implClazz);
		if (stackImpl.get(implClazz) == null) {
			Object impl = J2WHelper.methodsProxy().createImpl(implClazz, getImplClass(implClazz, null));
			stackImpl.put(implClazz, impl);
			return (P) impl;
		} else {
			return (P) stackImpl.get(implClazz);
		}
	}

	@Override public <T> T createMainLooper(Class<T> service, final Object ui) {
		J2WCheckUtils.validateServiceInterface(service);
		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, new InvocationHandler() {

			@Override public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
				// 如果有返回值 - 直接执行
				if (!method.getReturnType().equals(void.class)) {
					return method.invoke(ui, args);
				}
				// 如果是主线程 - 直接执行
				if (!J2WHelper.isMainLooperThread()) {// 子线程
					return method.invoke(ui, args);
				}
				Runnable runnable = new Runnable() {

					@Override public void run() {
						try {
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