package j2w.team.biz;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.biz.exception.J2WBizException;
import j2w.team.biz.exception.J2WUINullPointerException;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.modules.http.J2WError;

/**
 * Created by sky on 15/2/1. 中央处理器
 */
public abstract class J2WBiz<T extends J2WIDisplay> implements J2WIBiz {

	private boolean				checkUI			= true;

	public static final String	METHOD_ERROR	= "methodError";	// 错误方法

	public static final String	METHOD_CHECKUI	= "checkUI";		// UI检查方法

	private Object				view;

	private T					display;

	/** View层 **/
	private Map<String, Object>	stack			= null;

	private Map<String, Class>	stackHttp		= null;

	/**
	 * 初始化 - 业务
	 *
	 * @param iView
	 *            view层引用
	 */
	void initPresenter(Object iView, Object object) {
		this.view = iView;
		this.stack = new HashMap<>();
		this.stackHttp = new HashMap<>();
		this.display = J2WBizUtils.createDisplay(object, iView, this);// 设置显示调度
	}

	/**
	 * 获取网络
	 */
	protected <H> H http(Class<H> hClass) {
		checkNotNull(hClass, "请指定View接口～");
		Object obj = stack.get(hClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.httpAdapter().create(hClass);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(hClass.getSimpleName(), obj);
			stackHttp.put(hClass.getSimpleName(), hClass);
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
	 * @param <S>
	 * @return
	 */
	protected <S> S createImpl(Class<S> inter) {
		checkNotNull(inter, "请指定View接口～");
		Object obj = stack.get(inter.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createImpl(inter);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(inter.getSimpleName(), obj);
		}
		return (S) obj;
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
			obj = J2WBizUtils.createUI(ui, view, this);
			checkUINotNull(obj, "View层没有实现该接口～");
			stack.put(ui.getSimpleName(), obj);
		}
		return (U) obj;
	}

	/**
	 * 消除引用
	 */
	public void detach() {
		checkUI = false;
		if(stackHttp != null){
			for (Map.Entry<String, Class> entry : stackHttp.entrySet()) {
				J2WHelper.httpAdapter().cancel(entry.getValue());
			}
			stackHttp.clear();
		}
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
		for (Map.Entry<String, Class> entry : stackHttp.entrySet()) {
			J2WHelper.httpAdapter().cancel(entry.getValue());
		}
		stackHttp.clear();
		stackHttp = null;
		stack.clear();
		stack = null;
		display = null;
		view = null;
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
