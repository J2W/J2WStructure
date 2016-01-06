package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 执行结束拦截
 */
public interface J2WEndInterceptor {

	<T> void intercept(T service, Method method);

	J2WEndInterceptor	NONE	= new J2WEndInterceptor() {

									@Override public <T> void intercept(T service, Method method) {

									}
								};
}
