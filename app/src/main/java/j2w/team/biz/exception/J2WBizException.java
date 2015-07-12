package j2w.team.biz.exception;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午1:10
 * @类描述
 */
public class J2WBizException extends RuntimeException {

	public J2WBizException() {}

	public J2WBizException(String detailMessage) {
		super(detailMessage);
	}

	public J2WBizException(String message, Throwable cause) {
		super(message, cause);
	}

	public J2WBizException(Throwable cause) {
		super((cause == null ? null : cause.toString()), cause);
	}
}