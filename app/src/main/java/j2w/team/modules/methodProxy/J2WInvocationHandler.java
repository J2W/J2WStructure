package j2w.team.modules.methodProxy;

import java.lang.reflect.InvocationHandler;

import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.NotCacheMethods;

/**
 * @创建人 sky
 * @创建时间 16/1/8
 * @类描述
 */
public abstract class J2WInvocationHandler<T> implements InvocationHandler {

	Object	impl;

	boolean	isNotCacheMethed;

	public J2WInvocationHandler(Object impl) {
		this.impl = impl;
		NotCacheMethods notCacheMethods = impl.getClass().getAnnotation(NotCacheMethods.class);
		isNotCacheMethed = notCacheMethods != null;
		J2WCheckUtils.checkNotNull(impl, "接口没有设置@Impl(class)，请设置～");
	}
}
