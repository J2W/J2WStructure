package j2w.team.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @创建人 sky
 * @创建时间 16/3/5
 * @类描述
 */
public interface J2WBizRun {

	/**
	 * 执行
	 * 
	 * @param method
	 * @param impl
	 * @param args
	 */
	Object invoke(Method method, Object impl, Object... args) throws IllegalAccessException, InvocationTargetException;
}
