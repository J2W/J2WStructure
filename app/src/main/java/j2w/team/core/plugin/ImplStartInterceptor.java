package j2w.team.core.plugin;

import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/3/1
 * @类描述
 */
public interface ImplStartInterceptor {
    <T> void interceptStart(String viewName, Class<T> service, Method method, Object[] objects);

}
