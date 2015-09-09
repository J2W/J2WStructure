package j2w.team.biz;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.biz.exception.J2WBizException;
import j2w.team.biz.exception.J2WHTTPException;
import j2w.team.biz.exception.J2WUINullPointerException;
import j2w.team.common.log.L;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.http.J2WError;
import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/1. 中央处理器
 */
public abstract class J2WBiz<T extends J2WIDisplay> implements J2WIBiz {

	public static final String	METHOD_ERROR	= "methodError";	// 错误方法

	public static final String	METHOD_CHECKUI	= "checkUI";		// UI检查方法

	private J2WView				j2WView;

	private Object				callback;

	private Map<String, Object>	stack			= null;

	private boolean				isUI;

	private T					display;

	/**
	 * 初始化 - 业务
	 *
	 * @param iView
	 *            view层引用
	 */
	@Override public void initBiz(J2WView iView) {
		this.j2WView = iView;
		initMap();
	}

	@Override public void initBiz(Object callback) {
		this.callback = callback;
		initMap();
	}

	private void initMap() {
		isUI = true;
		stack = new HashMap<>();
		Class displayClass = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
		checkNotNull(displayClass, "请指定display～");
		if (callback != null) {
			display = (T) J2WBizUtils.createDisplayNotView(displayClass, J2WHelper.getInstance());
		} else {
			display = (T) J2WBizUtils.createDisplayBiz(displayClass, j2WView);
		}
	}

	@Override public void detach() {
		isUI = false;
		if (stack != null) {
			stack.clear();
			stack = null;
		}
		if (display != null) {
			display.detach();
			display = null;
		}
		j2WView = null;
		callback = null;
	}

	/**
	 * 获取网络
	 */
	protected <H> H http(Class<H> hClass) {
		checkNotNull(hClass, "请指定View接口～");
		Object obj = stack.get(hClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.httpAdapter().create(hClass, this);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(hClass.getSimpleName(), obj);
		}
		return (H) obj;
	}

	/**
	 * 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
	 *
	 * @return
	 */
	protected T display() {
		return display;
	}

	/**
	 * 根据接口获取实现类
	 * 
	 * @param inter
	 * @param <I>
	 * @return
	 */
	protected <I> I createImpl(Class<I> inter) {
		checkNotNull(inter, "请指定View接口～");
		Object obj = stack.get(inter.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createImpl(inter, this);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(inter.getSimpleName(), obj);
		}
		return (I) obj;
	}

	/**
	 * View层 回调引用
	 * 
	 * @param ui
	 * @param <U>
	 * @return
	 */
	@Override public <U> U ui(Class<U> ui) {
		checkNotNull(ui, "请指定View接口～");

		Object obj = stack.get(ui.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createUI(ui, callback == null ? j2WView.getView() : callback, this);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(ui.getSimpleName(), obj);
		}
		return (U) obj;
	}

	/**
	 * 检查UI
	 *
	 * @return
	 */
	@Override public boolean checkUI() {
		return isUI;
	}

	/**
	 * 拦截器
	 */
	@Override public void interceptorImpl(Class clazz) {
		L.i("拦截器IMPL:" + clazz);
	}

	/**
	 * 拦截器
	 */
	@Override public void interceptorHttp(String name, Object object) {
		L.i("名称:" + name + " 拦截器HTTP:" + object);
	}

	/**
	 * 检查是否为空
	 * 
	 * @param reference
	 * 
	 * @param errorMessageTemplate
	 * @return
	 */
	protected <T> void checkNotNull(T reference, String errorMessageTemplate) {
		J2WCheckUtils.checkNotNull(reference, errorMessageTemplate);

	}

	/**
	 * 检查是否为空
	 * 
	 * @param reference
	 * 
	 * @param errorMessageTemplate
	 * @return
	 */
	private <T> void checkUINotNull(T reference, String errorMessageTemplate) {
		J2WCheckUtils.checkUINotNull(reference, errorMessageTemplate);

	}

	/**
	 * 检查参数
	 * 
	 * @param expression
	 * @param errorMessageTemplate
	 */
	protected void checkArgument(boolean expression, String errorMessageTemplate) {
		J2WCheckUtils.checkArgument(expression, errorMessageTemplate);
	}

	/**
	 * 检查是否越界
	 *
	 * @param index
	 * @param size
	 * @param desc
	 */
	protected void checkPositionIndex(int index, int size, String desc) {
		J2WCheckUtils.checkPositionIndex(index, size, desc);
	}

	/**
	 * 抛异常
	 */
	protected void throwBizHttp(String msg) {
		throw new J2WHTTPException(msg);
	}

	/**
	 * 错误处理
	 * 
	 * @param methodName
	 * @param throwable
	 */
	public final void methodError(String methodName, Throwable throwable) {
		if (throwable.getCause() instanceof J2WError) {
			if ("Canceled".equals(throwable.getCause().getMessage())) {
				errorCancel();
			} else {
				methodHttpError(methodName, (J2WError) throwable.getCause());
			}
		} else if (throwable.getCause() instanceof J2WBizException) {
			checkError(methodName, (J2WBizException) throwable.getCause());
		} else {
			methodCodingError(methodName, throwable.getCause());
		}
	}

	/** 检查异常 **/
	@Override public void checkError(String methodName, J2WBizException j2WBizException) {
		if (j2WBizException instanceof J2WUINullPointerException) {
			return;
		}
		j2WBizException.printStackTrace();
	}

	/** 网络异常 **/
	@Override public final void methodHttpError(String methodName, J2WError j2WError) {
		if (j2WError.getKind() == J2WError.Kind.NETWORK) { // 请求发送前，网络问题
			errorNetWork();
		} else if (j2WError.getKind() == J2WError.Kind.HTTP) {// 请求响应后，网络错误
			errorHttp();
		} else if (j2WError.getKind() == J2WError.Kind.UNEXPECTED) {// 意外错误
			errorUnexpected();
		}
	}

	/** 发送请求前取消 **/
	@Override public void errorCancel() {}

	/** 发送请求前错误 **/
	@Override public void errorNetWork() {}

	/** 请求得到响应后错误 **/
	@Override public void errorHttp() {}

	/** 请求或者响应 意外错误 **/
	@Override public void errorUnexpected() {}

	/** 编码异常 **/
	@Override public void methodCodingError(String methodName, Throwable throwable) {}

}
