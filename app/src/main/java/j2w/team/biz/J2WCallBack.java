package j2w.team.biz;

/**
 * @创建人 sky
 * @创建时间 15/10/29 下午4:37
 * @接口描述 业务处理后回调
 */
public interface J2WCallBack<T> {

	/**
	 * 成功
	 * 
	 * @param code
	 *            编码
	 * @param t
	 *            结果
	 */
	void onSuccess(int code, T t);

	/**
	 * 失败
	 * 
	 * @param code
	 * @param msg
	 */
	void onFailure(int code, String msg);
}
