package j2w.team.modules.threadpool;

import java.lang.reflect.Method;

import j2w.team.core.J2WRunnable;

/**
 * Created by sky on 15/2/20.
 */
public abstract class J2WAsyncCall extends J2WRunnable {

	public String	mehtodName;

	public J2WRepeat j2WRepeat;

	public Method	method;

	public Method	methodError;

	public Object[] args;

	public J2WAsyncCall(String methodName, J2WRepeat j2WRepeat, Method method, Method methodError,Object[] args) {
		super("J2W Method Name %s", methodName);
		this.mehtodName = methodName;
		this.j2WRepeat = j2WRepeat;
		this.method = method;
		this.methodError = methodError;
		this.args = args;
	}
}
