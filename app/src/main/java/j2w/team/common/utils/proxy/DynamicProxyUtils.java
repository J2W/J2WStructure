package j2w.team.common.utils.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import j2w.team.biz.J2WBiz;

/**
 * Created by sky on 15/1/27. 通用动态代理类
 */
public final class DynamicProxyUtils {

	/**
	 * 生产代理类
	 *
	 * @param loader
	 *            类加载器
	 * @param interfaces
	 *            接口数组
	 * @param invocationHandler
	 *            代理方法
	 * @param <T>
	 *            类型
	 * @return 返回代理类
	 */
	public static <T> T newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler invocationHandler) {
		return (T) Proxy.newProxyInstance(loader, interfaces, invocationHandler);
	}

	/**
	 * 代理类 - 日志系统
	 *
	 * @param d
	 * @param <D>
	 * @return
	 */
	public static <D> D newProxyLogSystem(D d) {
		// 获取Classloader
		ClassLoader loader = d.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = d.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = d.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WLogSystemHandler<>(d);
		// 获取代理接口
		D b = newProxyInstance(loader, interfaces, invocationHandler);
		return b;
	}

	/**
	 * 代理类 - 线程系统
	 *
	 * @param d
	 * @param <D>
	 * @return
	 */
	public static <D> D newProxySyncSystem(D d) {
		// 获取Classloader
		ClassLoader loader = d.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = d.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = d.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WSyncHandler<>(d, d.getClass());
		// 获取代理接口
		D b = newProxyInstance(loader, interfaces, invocationHandler);
		return b;
	}

	/**
	 * 代理类 - 视图
	 *
	 * @param d
	 * @param <D>
	 * @return
	 */
	public static <D> D newProxyUI(D d, J2WBiz j2WBiz) {
		// 获取Classloader
		ClassLoader loader = d.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = d.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = d.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WBizHandler<>(d, j2WBiz);
		// 获取代理接口
		D b = newProxyInstance(loader, interfaces, invocationHandler);
		return b;
	}

	/**
	 * 代理类 - 视图
	 *
	 * @param d
	 * @param <D>
	 * @return
	 */
	public static <D> D newProxyDisplay(D d, J2WBiz j2WBiz) {
		// 获取Classloader
		ClassLoader loader = d.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = d.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = d.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WDisplayHandler<>(d, j2WBiz);
		// 获取代理接口
		D b = newProxyInstance(loader, interfaces, invocationHandler);
		return b;
	}

	/**
	 * 代理类 - Service视图
	 *
	 * @param d
	 * @param <D>
	 * @return
	 */
	public static <D> D newProxyServiceUI(D d) {
		// 获取Classloader
		ClassLoader loader = d.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = d.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = d.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WServiceHandler<>(d);
		// 获取代理接口
		D b = newProxyInstance(loader, interfaces, invocationHandler);
		return b;
	}

	/**
	 * 验证类 - 判断是否是一个接口
	 *
	 * @param service
	 * @param <T>
	 */
	public static <T> void validateServiceClass(Class<T> service) {
		if (service == null || !service.isInterface()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(service);
			stringBuilder.append("，该类不是接口！");
			throw new IllegalArgumentException(stringBuilder.toString());
		}
	}

	/**
	 * 验证类 - 判断是否继承其他接口
	 *
	 * @param service
	 * @param <T>
	 */
	public static <T> void validateInterfaceServiceClass(Class<T> service) {
		if (service.getInterfaces().length > 0) {
			throw new IllegalArgumentException("接口不能继承其它接口");
		}

	}

	/**
	 * 代理类 - 触发间接方法
	 *
	 * @param v
	 * @param <V>
	 * @return
	 */
	public static <V> V newProxyImpl(V v, J2WBiz j2WBiz) {
		// 获取Classloader
		ClassLoader loader = v.getClass().getClassLoader();
		// 获得接口数组
		Class<?>[] interfaces = v.getClass().getInterfaces();
		// 如果没有实现接口，获取父类接口
		if (interfaces.length == 0) {
			interfaces = v.getClass().getSuperclass().getInterfaces();
		}
		// 获得Handler - 这里可以替换成其他代理方法
		InvocationHandler invocationHandler = new J2WInterceptorHandler<>(v, j2WBiz);
		// 获取代理接口
		V vi = newProxyInstance(loader, interfaces, invocationHandler);
		return vi;
	}
}
