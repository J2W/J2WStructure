package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/1/6
 * @类描述 执行前拦截
 */
public interface J2WStartInterceptor {

	<T> void intercept(Class<T> service, Method method);

	J2WStartInterceptor	NONE	= new J2WStartInterceptor() {

									@Override public <T> void intercept(Class<T> service, Method method) {
									}
								};

}
