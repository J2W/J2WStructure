package j2w.team.biz;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.common.utils.proxy.DynamicProxyUtils;
import j2w.team.modules.http.J2WError;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.view.J2WActivity;

/**
 * Created by sky on 15/2/1. 中央处理器
 */
public abstract class J2WBiz<T extends J2WIDisplay> implements J2WIBiz {

	private boolean				checkUI			= true;

	public static final String	METHOD_ERROR	= "methodError";	// 错误方法

	public static final String	METHOD_CHECKUI	= "checkUI";		// UI检查方法

	private J2WActivity			activity;

	private T					display;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackUI			= null;

	/**
	 * 初始化 - 业务
	 *
	 * @param iView
	 *            view层引用
	 */
	void initPresenter(Object iView, Object object) {
		this.activity = (J2WActivity) iView;
		this.stackUI = new HashMap<>();
		this.display = J2WBizUtils.createDisplay(object, activity, this);// 设置显示调度
	}

	/**
	 * 获取网络
	 */
	@Override public J2WRestAdapter http() {
		return J2WHelper.getHttpAdapter();
	}

	/**
	 * 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
	 *
	 * @param objects
	 *            参数
	 * @return
	 */
	public T display(Object... objects) {
		if (objects.length > 0) {
			display.setActivity(activity, objects);
		}
		return display;
	}

	/**
	 * View层 回调引用
	 * 
	 * @param ui
	 * @param <U>
	 * @return
	 */
	@Override public synchronized  <U> U ui(Class<U> ui) {
		Preconditions.checkNotNull(ui, "请指定View接口～");
		if (stackUI.get(ui.getSimpleName()) == null) {// 如果没有索索到
			stackUI.put(ui.getSimpleName(), DynamicProxyUtils.newProxyUI(activity, this));
		}
		return (U) stackUI.get(ui.getSimpleName());
	}

	/**
	 * 消除引用
	 */
	public void detach() {
		checkUI = false;
	}

	/**
	 * 检查UI
	 *
	 * @return
	 */
	@Override public boolean checkUI() {
		return checkUI;
	}

	/**
	 * 销毁UI
	 */
	@Override public void detachUI() {
		stackUI.clear();
		stackUI = null;
		activity = null;
		display = null;
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
		} else {
			methodCodingError(methodName, throwable.getCause());
		}
	}

	/** 网络异常 **/
	public final void methodHttpError(String methodName, J2WError j2WError) {
		if (j2WError.getKind() == J2WError.Kind.NETWORK) { // 请求发送前，网络问题
			errorNetWork();
		} else if (j2WError.getKind() == J2WError.Kind.HTTP) {// 请求响应后，网络错误
			errorHttp();
		} else if (j2WError.getKind() == J2WError.Kind.UNEXPECTED) {// 意外错误
			errorUnexpected();
		}
	}

	/** 发送请求前取消 **/
	public void errorCancel() {}

	/** 发送请求前错误 **/
	public void errorNetWork() {}

	/** 请求得到响应后错误 **/
	public void errorHttp() {}

	/** 请求或者响应 意外错误 **/
	public void errorUnexpected() {}

	/** 编码异常 **/
	public void methodCodingError(String methodName, Throwable throwable) {}

}
