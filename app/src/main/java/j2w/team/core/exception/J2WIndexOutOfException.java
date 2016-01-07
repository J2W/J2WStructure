package j2w.team.core.exception;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午1:13
 * @类描述 状态异常
 */
public class J2WIndexOutOfException extends J2WBizException {

    public J2WIndexOutOfException() {}

    public J2WIndexOutOfException(String detailMessage) {
        super(detailMessage);
    }

    public J2WIndexOutOfException(String message, Throwable cause) {
        super(message, cause);
    }

    public J2WIndexOutOfException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}