package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 错误拦截
 */
public interface J2WErrorInterceptor {

	<T> void methodError(T service, Method method, Throwable throwable);

	J2WErrorInterceptor	NONE	= new J2WErrorInterceptor() {

									@Override public <T> void methodError(T service, Method method, Throwable throwable) {

									}
								};

}
