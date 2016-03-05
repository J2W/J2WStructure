package j2w.team.core;

import j2w.team.modules.methodProxy.J2WMethod;

/**
 * @创建人 sky
 * @创建时间 16/3/5
 * @类描述
 */
public interface J2WBizRun {

	/**
	 * 执行
	 * 
	 * @param j2WMethod
	 * @param impl
	 * @param args
	 */
	Object invoke(J2WMethod j2WMethod, Object impl, Object... args) throws Throwable;
}
