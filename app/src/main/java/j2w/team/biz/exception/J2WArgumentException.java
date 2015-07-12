package j2w.team.biz.exception;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午1:12
 * @类描述 参数异常
 */
public class J2WArgumentException extends J2WBizException {

	public J2WArgumentException() {}

	public J2WArgumentException(String detailMessage) {
		super(detailMessage);
	}

	public J2WArgumentException(String message, Throwable cause) {
		super(message, cause);
	}

	public J2WArgumentException(Throwable cause) {
		super((cause == null ? null : cause.toString()), cause);
	}
}