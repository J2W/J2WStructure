package j2w.team.common.utils.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Stack;

import j2w.team.biz.J2WBiz;
import j2w.team.common.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.modules.http.J2WMethodInfo;
import j2w.team.modules.threadpool.BackgroundType;
import j2w.team.modules.threadpool.Background;
import j2w.team.modules.threadpool.J2WAsyncCall;
import j2w.team.modules.threadpool.J2WRepeat;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/2/18.动态代理-线程系统
 */
public class J2WSyncHandler<T> extends BaseHandler<T> {

	Class			aClass;

	Stack<String>	stack;

	public J2WSyncHandler(T t, Class clazz) {
		super(t);
		aClass = clazz;
		stack = new Stack<>();
	}

	@Override public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
		Object returnObject = null;
		// 获取UI检查方法
		if (!checkUI()) {
			return null;
		}

		// 获得错误处理方法
		Method methodError = aClass.getMethod(J2WBiz.METHOD_ERROR, new Class[] { String.class, Throwable.class });

		Method oldMethod = aClass.getMethod(method.getName(), method.getParameterTypes());

		// 获得注解数组
		J2WRepeat j2WRepeat = method.getAnnotation(J2WRepeat.class);
		if (j2WRepeat == null) {
			j2WRepeat = oldMethod.getAnnotation(J2WRepeat.class);
		}
		Background background = method.getAnnotation(Background.class);
		if (background == null) {
			background = oldMethod.getAnnotation(Background.class);
		}

		String key = J2WMethodInfo.getMethodString(method, method.getParameterTypes());
		// 搜索
		if (j2WRepeat == null || !j2WRepeat.value()) { // 拦截
			if (stack.search(key) != -1) { // 如果存在什么都不做
				L.tag("J2W-Method");
				L.i("该方法正在执行 - 多次点击无效 : " + key);
				return returnObject;
			}
		}
		// 同步执行
		if (background == null) {
			try {
				if (j2WRepeat == null || !j2WRepeat.value()) { // 拦截
					stack.push(key); // 入栈
				}
				J2WCheckUtils.checkNotNull(t, "UI和BIZ已经被销毁~");
				returnObject = method.invoke(t, args);// 执行
				return returnObject;
			} catch (Throwable throwable) {
				try {
					return methodError.invoke(t, new Object[] { method.getName(), throwable });
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				}
			} finally {
				stack.remove(key);// 出栈
			}
		}
		// 获取后台线程池类型
		BackgroundType backgroundType = background.value();
		if (j2WRepeat == null || !j2WRepeat.value()) { // 拦截
			stack.push(key); // 入栈
		}
		// 生成执行任务
		SyncHandlerCall syncHandlerCall = new SyncHandlerCall(key, j2WRepeat, method, methodError, args);
		switch (backgroundType) {
			case HTTP:
				J2WHelper.threadPoolHelper().getHttpExecutorService().execute(syncHandlerCall);
				break;
			case SINGLEWORK:
				J2WHelper.threadPoolHelper().getSingleWorkExecutorService().execute(syncHandlerCall);
				break;
			case WORK:
				J2WHelper.threadPoolHelper().getWorkExecutorService().execute(syncHandlerCall);
				break;
		}
		return null;
	}

	/**
	 * 执行代码
	 */
	class SyncHandlerCall extends J2WAsyncCall {

		public SyncHandlerCall(String methodName, J2WRepeat j2WRepeat, Method method, Method methodError, Object[] args) {
			super(methodName, j2WRepeat, method, methodError, args);
		}

		@Override protected void execute() {
			try {
				// 获取UI检查方法
				if (!checkUI()) {
					return;
				}
				J2WCheckUtils.checkNotNull(t, "UI和BIZ已经被销毁~");
				super.method.invoke(t, args);// 执行
			} catch (Throwable e) {
				try {
					// 获取UI检查方法
					if (!checkUI()) {
						return;
					}
					J2WCheckUtils.checkNotNull(t, "UI和BIZ已经被销毁~");
					super.methodError.invoke(t, new Object[] { method.getName(), e });
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (InvocationTargetException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
			} finally {
				if (stack != null) {
					stack.remove(super.mehtodName);// 出栈
				}
			}
		}
	}

	/**
	 * 检查
	 * 
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 */
	private boolean checkUI() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		if (aClass == null || t == null || stack == null) {
			return false;
		}
		// 获取UI检查方法
		Method checkUI = aClass.getMethod(J2WBiz.METHOD_CHECKUI);
		boolean bool = (boolean) checkUI.invoke(t);
		if (!bool) {
			aClass = null;
			stack = null;
			t = null;
		}
		return bool;
	}
}
