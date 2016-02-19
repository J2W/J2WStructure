package j2w.team.core.plugin;

import java.lang.reflect.Method;

import j2w.team.modules.http.J2WError;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 网络错误异常
 */
public interface J2WHttpErrorInterceptor {

	<T> void methodError(Class<T> service, Method method, int interceptor, J2WError j2WError);
}
