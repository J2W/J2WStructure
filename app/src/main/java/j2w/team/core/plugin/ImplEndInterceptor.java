package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/3/1
 * @类描述
 */
public interface ImplEndInterceptor {
    <T> void interceptEnd(String viewName, Class<T> service, Method method, Object[] objects, Object backgroundResult);
}
