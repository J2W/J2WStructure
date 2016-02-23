package j2w.team.modules.methodProxy;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.J2WBiz;
import j2w.team.core.NotCacheMethods;

/**
 * @创建人 sky
 * @创建时间 16/1/8
 * @类描述
 */
public abstract class J2WInvocationHandler<T> implements InvocationHandler {

	public static final int	TYPE_BIZ	= 0;

	public static final int	TYPE_UI		= 1;

	public static final int	TYPE_DISPLA	= 2;

	final int				type;

	Object					impl;

	boolean					isNotCacheMethed;

	public J2WInvocationHandler(Class<T> service, int type, Object ui) {
		this.type = type;
		switch (type) {
			case TYPE_BIZ:
			case TYPE_DISPLA:
				impl = getImplClass(service, ui);
				break;
			case TYPE_UI:
				impl = getUIClass(service, ui);
				break;
		}
		NotCacheMethods notCacheMethods = impl.getClass().getAnnotation(NotCacheMethods.class);
		isNotCacheMethed = notCacheMethods == null ? false : true;
		J2WCheckUtils.checkNotNull(impl, "接口没有设置@Impl(class)，请设置～");
	}

	<D> Object getUIClass(@NotNull Class<D> service, Object ui) {
		validateServiceClass(service);
		if (ui != null) {
			return ui;
		}
		// 获取注解
		Impl impl = service.getAnnotation(Impl.class);
		J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
		/** 加载类 **/
		Object view = J2WHelper.screenHelper().getView(impl.value().getName());
		J2WCheckUtils.checkNotNull(view, "UI类为空～");
		/** 创建类BIZ **/
		return view;
	}

	/**
	 * 获取实现类
	 *
	 * @param service
	 * @param <D>
	 * @return
	 */
	<D> Object getImplClass(@NotNull Class<D> service, Object ui) {
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
		} catch (java.lang.InstantiationException e) {
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

}
