package j2w.team.biz;

import j2w.team.biz.exception.J2WBizException;
import j2w.team.modules.http.J2WError;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/7. 业务
 */
public interface J2WIBiz {

	/**
	 * 初始化
	 * 
	 * @param iView
	 */
	void initBiz(J2WView iView);

	void initBiz(Object callback);

	/**
	 * 清空
	 */
	void detach();

	void detachUI();

	/**
	 * 拦截器
	 * 
	 * @param clazz
	 */
	void interceptorImpl(Class clazz);

	/**
	 * 网络拦截器
	 * 
	 * @param name
	 * @param object
	 */
	void interceptorHttp(String name, Object object);

	/**
	 * View层 回调引用
	 *
	 * @param ui
	 * @return
	 */
	<U> U ui(Class<U> ui);

	/**
	 * 检查UI
	 *
	 * @return
	 */
	boolean checkUI();

	/**
	 * 异常捕捉
	 * 
	 * @param methodName
	 *            方法名称
	 * @param throwable
	 *            异常
	 */
	void methodError(String methodName, Throwable throwable);

	/**
	 * 检查异常
	 * 
	 * @param methodName
	 * @param j2WBizException
	 */
	void checkError(String methodName, J2WBizException j2WBizException);

	/**
	 * 网络异常
	 * 
	 * @param methodName
	 *            方法名称
	 * @param j2WError
	 *            网络异常
	 */
	void methodHttpError(String methodName, J2WError j2WError);

	void errorNetWork(); // 发送请求前错误

	void errorHttp(); // 请求得到响应后错误

	void errorUnexpected();// 请求或者响应 意外错误

	void errorCancel();// 取消请求

	/** 编码异常 **/
	/**
	 * 编码异常
	 * 
	 * @param methodName
	 *            方法名称
	 * @param throwable
	 *            异常
	 */
	void methodCodingError(String methodName, Throwable throwable);
}
