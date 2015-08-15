package j2w.team.biz.exception;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午5:52
 * @类描述 网络业务逻辑异常
 */
public class J2WHTTPException extends J2WBizException {

	public J2WHTTPException() {}

	public J2WHTTPException(String detailMessage) {
		super(detailMessage);
	}

	public J2WHTTPException(String message, Throwable cause) {
		super(message, cause);
	}

	public J2WHTTPException(Throwable cause) {
		super((cause == null ? null : cause.toString()), cause);
	}
}