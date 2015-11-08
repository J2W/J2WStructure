package j2w.team.modules.http;

import com.squareup.okhttp.Response;

/**
 * Created by sky on 15/2/23.
 */
public interface J2WHttpCallback<T> {

	/**
	 * 成功
	 * 
	 * @param t
	 *            成功类型
	 * @param response
	 *            响应结果
	 */
	void success(T t, Response response);

	/**
	 * 失败
	 * 
	 * @param error
	 *            错误
	 */
	void failure(J2WError error);
}
