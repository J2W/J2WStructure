package j2w.team.modules.http;

/**
 * Created by sky on 15/2/24. 端点地址
 */
public abstract class J2WEndpoint {

	/** 创建url端点地址. */
	public static J2WEndpoint createFixed(final String url) {
		checkNotNull(url, "url == null");
		return new J2WEndpoint() {

			@Override public String url(String clazzName) {
				return url;
			}
		};
	}

	public abstract String url(String clazzName);

	/**
	 * 检查是否为空
	 * 
	 * @param object
	 * @param message
	 * @param args
	 * @param <T>
	 * @return
	 */
	static <T> T checkNotNull(T object, String message, Object... args) {
		if (object == null) {
			throw new NullPointerException(String.format(message, args));
		}
		return object;
	}
}
