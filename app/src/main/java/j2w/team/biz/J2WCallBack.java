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
	 * @param t
	 *            结果
	 */
	void onSuccess(T t);

	/**
	 * 失败
	 * 
	 * @param t
	 *            结果
	 */
	void onFailure(T t);
}
