package j2w.team.common.utils.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * Created by sky on 15/2/7. 动态代理-handler
 */
public abstract class BaseHandler<T> implements InvocationHandler {

	protected T	t	= null;

	private BaseHandler() {}

	public BaseHandler(T t) {
		this.t = t;
	}
}
