package j2w.team.core.exception;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午1:13
 * @类描述 空指针异常
 */
public class J2WNullPointerException extends J2WBizException {

	public J2WNullPointerException() {}

	public J2WNullPointerException(String detailMessage) {
		super(detailMessage);
	}
}