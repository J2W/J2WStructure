package j2w.team.biz;

import android.support.v7.app.ActionBarActivity;

import com.google.common.base.Preconditions;

import java.lang.reflect.InvocationHandler;

import j2w.team.J2WHelper;
import j2w.team.common.log.L;
import j2w.team.common.utils.AppUtils;
import j2w.team.common.utils.proxy.DynamicProxyUtils;
import j2w.team.common.utils.proxy.J2WBizHandler;
import j2w.team.view.J2WActivity;

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
	public static final <I extends J2WIBiz, B extends J2WBiz, V extends ActionBarActivity, D extends J2WIDisplay> I createBiz(Class<I> iBiz, V iView, D iDisplay) {
		Preconditions.checkNotNull(iView, "View层实体类不能为空～");
		I interfaceBiz;
		B implBiz;
		Class clazz;
		try {
			Preconditions.checkNotNull(iBiz, "业务类型不能为空");
			// 检查
			DynamicProxyUtils.validateServiceClass(iBiz);
			// 获取注解
			Impl impl = iBiz.getAnnotation(Impl.class);
			Preconditions.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			Preconditions.checkNotNull(clazz, "Biz类为空～");
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
	public static final <B extends J2WBiz, V extends ActionBarActivity> Object createUI(Class ui, V iView, B biz) {
		Object obj = null;
		try{
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
		}catch (Exception e){
			return obj;
		}
		return obj;
	}

	/**
	 * 创建Display类
	 *
	 * @return
	 */
	public static final <T extends J2WIDisplay, D extends J2WDisplay, V extends J2WActivity> T createDisplay(V iView) {
		T iDisplay;
		D implDisplay;
		Class clazz;
		Class<Object> displayClass = null;
		try {
			// 获取当前类的泛型类
			displayClass = AppUtils.getSuperClassGenricType(iView.getClass(), 0);

			// 获取Application的泛型类
			Preconditions.checkNotNull(displayClass, "View第二个泛型类不能为空～");
			// 获取注解
			Impl impl = displayClass.getAnnotation(Impl.class);
			Preconditions.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			Preconditions.checkNotNull(clazz, "Display实现类类为空～");
			/** 创建类Display **/
			implDisplay = (D) clazz.newInstance();
			/** 赋值给接口 **/
			iDisplay = (T) implDisplay;
			iDisplay.initDisplay(iView);
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
	public static final <T extends J2WIDisplay> T createDisplay(Object display,J2WActivity j2WActivity, J2WBiz j2WBiz) {
		Preconditions.checkNotNull(display, "biz层 display 不能为空~～");
		Preconditions.checkNotNull(j2WActivity, "biz层 activity不能为空～");
		Preconditions.checkNotNull(j2WBiz, "biz层 业务实体类不能为空～");
		T iDisplay;
		/** 初始化业务类 **/
		((T) display).initDisplay(j2WActivity);
		/** 动态代理 - 线程系统 **/
		iDisplay = DynamicProxyUtils.newProxyUI(((T) display), j2WBiz);
		return iDisplay;
	}
}
