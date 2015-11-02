package j2w.team.biz;

import android.content.Context;
import android.support.v4.app.Fragment;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.proxy.DynamicProxyUtils;
import j2w.team.display.J2WDisplay;
import j2w.team.display.J2WIDisplay;
import j2w.team.receiver.J2WReceiver;
import j2w.team.service.J2WService;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/18.业务工具类
 */
public final class J2WBizUtils {

	/**
	 * 创建业务类 获取动态代理业务层
	 *
	 * @return
	 */
	public static final <I extends J2WIBiz> I createBiz(Class<I> iBiz, Object object) {
		I interfaceBiz;
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
			interfaceBiz = (I) clazz.newInstance();
			if (object instanceof J2WView) {
				interfaceBiz.initBiz((J2WView) object);
			} else {
				interfaceBiz.initBiz(object);
			}
			/** 动态代理 - 线程系统 **/
			interfaceBiz = DynamicProxyUtils.newProxySyncSystem(interfaceBiz);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(iBiz) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(iBiz) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(iBiz) + "，访问权限异常！");
		}
		return interfaceBiz;
	}

	/**
	 * 创建Display类 View层
	 *
	 * @return
	 */
	public static final <D extends J2WIDisplay> D createDisplay(Class<D> iDisplay) {
		D interfaceDisplay;
		Class clazz;
		try {
			J2WCheckUtils.checkNotNull(iDisplay, "Display接口不能为空～");
			// 检查
			DynamicProxyUtils.validateServiceClass(iDisplay);
			// 获取注解
			Impl impl = iDisplay.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			J2WCheckUtils.checkNotNull(clazz, "Display实现类类为空～");
			/** 创建类Display **/
			interfaceDisplay = (D) clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，访问权限异常！");
		}
		return interfaceDisplay;
	}

	/**
	 * 创建Display类 Biz层
	 *
	 * @return
	 */
	public static final <D extends J2WIDisplay> D createDisplayBiz(Class<D> iDisplay, J2WView j2WView) {
		D interfaceDisplay;
		Class clazz;
		try {
			J2WCheckUtils.checkNotNull(iDisplay, "Display接口不能为空～");
			// 检查
			DynamicProxyUtils.validateServiceClass(iDisplay);
			// 获取注解
			Impl impl = iDisplay.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			J2WCheckUtils.checkNotNull(clazz, "Display实现类类为空～");
			/** 创建类Display **/
			interfaceDisplay = (D) clazz.newInstance();
			interfaceDisplay.initDisplay(j2WView);
			interfaceDisplay = DynamicProxyUtils.newProxyDisplay((interfaceDisplay));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，访问权限异常！");
		}
		return interfaceDisplay;
	}

	/**
	 * 创建Display类 Biz层
	 *
	 * @return
	 */
	public static final <D extends J2WIDisplay> D createDisplayNotView(Class<D> iDisplay, Context context) {
		D interfaceDisplay;
		Class clazz;
		try {
			J2WCheckUtils.checkNotNull(iDisplay, "Display接口不能为空～");
			// 检查
			DynamicProxyUtils.validateServiceClass(iDisplay);
			// 获取注解
			Impl impl = iDisplay.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			/** 加载类 **/
			clazz = Class.forName(impl.value().getName());
			J2WCheckUtils.checkNotNull(clazz, "Display实现类类为空～");
			/** 创建类Display **/
			interfaceDisplay = (D) clazz.newInstance();
			interfaceDisplay.initDisplay(context);
			interfaceDisplay = DynamicProxyUtils.newProxyServiceUI((interfaceDisplay));
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(iDisplay) + "，访问权限异常！");
		}
		return interfaceDisplay;
	}

	/**
	 * 创建View层类 - 动态代理
	 *
	 * @return
	 */
	public static final <B extends J2WBiz, V> Object createUI(Class ui, V iView, B biz) {
		Object obj = null;
		try {
			if (iView instanceof J2WFragment) {
				J2WFragment j2WFragment = (J2WFragment) iView;
				final Fragment targetFragment = j2WFragment.getTargetFragment();
				if (targetFragment != null) {
					iView = (V) targetFragment;
				} else {
					if (j2WFragment.isTargetActivity()) {
						iView = (V) j2WFragment.getActivity();
					}
				}
			} else if (iView instanceof J2WDialogFragment) {
				J2WDialogFragment j2WDialogFragment = (J2WDialogFragment) iView;
				final Fragment targetFragment = j2WDialogFragment.getTargetFragment();
				if (targetFragment != null) {
					iView = (V) targetFragment;
				} else {
					if (j2WDialogFragment.isTargetActivity()) {
						iView = (V) j2WDialogFragment.getActivity();
					}
				}
			}

			if (iView == null) {
				return obj;
			}

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
	 * 创建View层类 - 获取接口
	 *
	 * @return
	 */
	public static final <V> J2WCallBack createCallBack(V iView) {
		J2WCallBack j2WCallBack = null;
		if (iView instanceof J2WFragment) {
			J2WFragment j2WFragment = (J2WFragment) iView;
			final Fragment targetFragment = j2WFragment.getTargetFragment();
			if (targetFragment != null) {
				iView = (V) targetFragment;
			} else {
				if (j2WFragment.isTargetActivity()) {
					iView = (V) j2WFragment.getActivity();
				}
			}
		} else if (iView instanceof J2WDialogFragment) {
			J2WDialogFragment j2WDialogFragment = (J2WDialogFragment) iView;
			final Fragment targetFragment = j2WDialogFragment.getTargetFragment();
			if (targetFragment != null) {
				iView = (V) targetFragment;
			} else {
				if (j2WDialogFragment.isTargetActivity()) {
					iView = (V) j2WDialogFragment.getActivity();
				}
			}
		}

		if (iView instanceof J2WCallBack) {
			j2WCallBack = (J2WCallBack) iView;
		}

		return j2WCallBack;
	}

	/**
	 * 根据接口创建 实现类
	 * 
	 * @param vClass
	 * @param <V>
	 * @return
	 */
	public static final <V> V createImpl(Class<V> vClass, J2WBiz j2WBiz) {
		V v;
		Class clazz = null;
		try {
			J2WCheckUtils.checkNotNull(vClass, "接口不能为空～");
			// 获取注解
			Impl impl = vClass.getAnnotation(Impl.class);
			J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
			clazz = Class.forName(impl.value().getName());
			v = (V) clazz.newInstance();
			/** 动态代理 - 线程系统 **/
			v = DynamicProxyUtils.newProxyImpl(v, j2WBiz);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(String.valueOf(clazz) + "，没有找到业务类！");
		} catch (java.lang.InstantiationException e) {
			throw new IllegalArgumentException(String.valueOf(clazz) + "，实例化异常！");
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(String.valueOf(clazz) + "，访问权限异常！");
		}
		return v;
	}
}
