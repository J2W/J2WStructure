package j2w.team.modules.methodProxy;

import java.lang.reflect.InvocationHandler;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 16/1/8
 * @类描述
 */
public abstract class J2WInvocationHandler<T> implements InvocationHandler {

	Object	impl;

	public J2WInvocationHandler(Object impl) {
		this.impl = impl;
		J2WCheckUtils.checkNotNull(impl, "接口没有设置@Impl(class)，请设置～");
	}
}
