package j2w.team.core.plugin;

/**
 * @创建人 sky
 * @创建时间 15/11/9
 * @类描述 结果拦截器
 */
public interface J2WResponseInterceptor {

	/**
	 * 结果拦截
	 * 
	 * @param name
	 *            方法名
	 * @param object
	 *            结果
	 */
	void httpInterceptorResults(String name, Object object);
}
