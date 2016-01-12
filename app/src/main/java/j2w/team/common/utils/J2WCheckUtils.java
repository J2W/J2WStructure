package j2w.team.common.utils;

import j2w.team.core.exception.J2WArgumentException;
import j2w.team.core.exception.J2WIndexOutOfException;
import j2w.team.core.exception.J2WNullPointerException;
import j2w.team.core.exception.J2WUINullPointerException;

/**
 * @创建人 sky
 * @创建时间 15/7/15 上午10:05
 * @类描述 检查
 */
public final class J2WCheckUtils {

	/**
	 * 验证
	 *
	 * @param service
	 * @param <T>
	 */
	public static <T> void validateServiceInterface(Class<T> service) {
		if (!service.isInterface()) {
			throw new IllegalArgumentException("该类不是接口");
		}
	}

	/**
	 * 检查是否为空
	 *
	 * @param reference
	 *
	 * @param errorMessageTemplate
	 * @return
	 */
	public static <T> T checkNotNull(T reference, String errorMessageTemplate) {
		if (reference == null) {
			throw new J2WNullPointerException(errorMessageTemplate);
		}
		return reference;
	}

	/**
	 * 检查是否为空
	 *
	 * @param reference
	 *
	 * @param errorMessageTemplate
	 * @return
	 */
	public static <T> T checkUINotNull(T reference, String errorMessageTemplate) {
		if (reference == null) {
			throw new J2WUINullPointerException(errorMessageTemplate);
		}
		return reference;
	}

	/**
	 * 检查参数
	 *
	 * @param expression
	 * @param errorMessageTemplate
	 */
	public static void checkArgument(boolean expression, String errorMessageTemplate) {
		if (!expression) {
			throw new J2WArgumentException(errorMessageTemplate);
		}
	}

	/**
	 * 检查是否越界
	 *
	 * @param index
	 * @param size
	 * @param desc
	 */
	public static void checkPositionIndex(int index, int size, String desc) {
		if (index < 0 || index > size) {
			throw new J2WIndexOutOfException(desc);
		}
	}

	/**
	 * 判断是否相同
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean equal(Object a, Object b) {
		return a == b || (a != null && a.equals(b));
	}

	/**
	 * 判断是否为空
	 *
	 * @param text
	 * @return
	 */
	public static boolean isEmpty(CharSequence text) {
		return null == text || text.length() == 0;
	}
}