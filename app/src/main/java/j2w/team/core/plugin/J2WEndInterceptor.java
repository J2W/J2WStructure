package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 执行结束拦截
 */
public interface J2WEndInterceptor {

	<T> void interceptEnd(String viewName, Class<T> service, Method method, int interceptor, Object[] objects);

}
