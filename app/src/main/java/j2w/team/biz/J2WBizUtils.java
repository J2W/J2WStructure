package j2w.team.biz;

import j2w.team.common.utils.AppUtils;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.proxy.DynamicProxyUtils;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;

/**
 * Created by sky on 15/2/18.业务工具类
 */
public final class J2WBizUtils {

	/**
	 * 创建业务类 获取动态代理业务层
	 *
	 * @param iView
	 * @param <D>
	 *            业务
	 * @param <V>
	 *            视图
	 * @return
	 */
	public static final <I extends J2WIBiz, B extends J2WBiz, V, D extends J2WIDisplay> I createBiz(Class<I> iBiz, V iView, D iDisplay) {
		J2WCheckUtils.checkNotNull(iView, "View层实体类不能为空～");
		I interfaceBiz;
		B implBiz;
		Class clazz;
		try {
			J2WCheckUtils.checkNotNull(iBiz, "业务类型不能为空");
			// 检查
			DynamicProxyUtils.validateServiceClass(iBiz);
			// 获取注解
			Impl impl = iBiz.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			J2WCheckUtils.checkNotNull(clazz, "Biz类为空～");
			/** 创建类BIZ **/
			implBiz = (B) clazz.newInstance();
			/** 初始化业务类 **/
			implBiz.initPresenter(iView, iDisplay);
			/** 赋值给接口 **/
			interfaceBiz = (I) implBiz;
			/** 动态代理 - 线程系统 **/
			interfaceBiz = DynamicProxyUtils.newProxySyncSystem(interfaceBiz);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(iView) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(iView) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(iView) + "，访问权限异常！");
		}
		return interfaceBiz;
	}

	/**
	 * 创建View层类
	 *
	 * @return
	 */
	public static final <B extends J2WBiz, V> Object createUI(Class ui, V iView, B biz) {
		Object obj = null;
		try {
			// 获得接口数组
			Class<?>[] interfaces = iView.getClass().getInterfaces();
			// 如果没有实现接口，获取父类接口
			if (interfaces.length == 0) {
				interfaces = iView.getClass().getSuperclass().getInterfaces();
			}
			for (Class clazz : interfaces) {
				if (clazz.getSimpleName().equals(ui.getSimpleName())) {
					obj = DynamicProxyUtils.newProxyUI(iView, biz);
					break;
				}
			}
		} catch (Exception e) {
			return obj;
		}
		return obj;
	}

	/**
	 * 创建Display类
	 *
	 * @return
	 */
	public static final <T extends J2WIDisplay, D extends J2WDisplay, V> T createDisplay(V iView) {
		T iDisplay;
		D implDisplay;
		Class clazz;
		Class<Object> displayClass = null;
		try {
			// 获取当前类的泛型类
			displayClass = AppUtils.getSuperClassGenricType(iView.getClass(), 0);

			// 获取Application的泛型类
			J2WCheckUtils.checkNotNull(displayClass, "View第二个泛型类不能为空～");
			// 获取注解
			Impl impl = displayClass.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			J2WCheckUtils.checkNotNull(clazz, "Display实现类类为空～");
			/** 创建类Display **/
			implDisplay = (D) clazz.newInstance();
			/** 赋值给接口 **/
			iDisplay = (T) implDisplay;
			if (iView instanceof J2WFragment) {
				iDisplay.initDisplay((J2WActivity) ((J2WFragment) iView).getActivity());
			} else {
				iDisplay.initDisplay((J2WActivity) iView);
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(displayClass) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(displayClass) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(displayClass) + "，访问权限异常！");
		}
		return iDisplay;
	}

	/**
	 * 创建Display类
	 *
	 * @return
	 */
	public static final <T extends J2WIDisplay> T createDisplay(Object display, Object obj, J2WBiz j2WBiz) {
		J2WCheckUtils.checkNotNull(display, "biz层 display 不能为空~～");
		J2WCheckUtils.checkNotNull(obj, "biz层 activity或fragment不能为空～");
		J2WCheckUtils.checkNotNull(j2WBiz, "biz层 业务实体类不能为空～");
		T iDisplay;
		/** 初始化业务类 **/
		if (obj instanceof J2WFragment) {
			((T) display).initDisplay((J2WActivity) ((J2WFragment) obj).getActivity());
		} else {
			((T) display).initDisplay((J2WActivity) obj);
		}
		/** 动态代理 - UI **/
		iDisplay = DynamicProxyUtils.newProxyUI(((T) display), j2WBiz);
		return iDisplay;
	}
}
