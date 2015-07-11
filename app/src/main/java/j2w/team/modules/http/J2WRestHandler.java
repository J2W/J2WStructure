package j2w.team.modules.http;

import com.squareup.okhttp.Request;

import java.lang.reflect.Method;
import java.util.Map;

import j2w.team.common.log.L;
import j2w.team.common.utils.proxy.BaseHandler;

/**
 * Created by sky on 15/2/24. 动态代理 - 网络层
 */
public class J2WRestHandler extends BaseHandler {

	private final Map<Method, J2WMethodInfo>	methodDetailsCache;

	private final J2WRestAdapter				j2WRestAdapter;

	private final String						tag;

	public J2WRestHandler(J2WRestAdapter j2WRestAdapter, Map<Method, J2WMethodInfo> methodDetailsCache, String tag) {
		super("");
		this.j2WRestAdapter = j2WRestAdapter;
		this.methodDetailsCache = methodDetailsCache;
		this.tag = tag;
	}

	@SuppressWarnings("unchecked")//
	@Override public Object invoke(Object proxy, Method method, final Object[] args) throws Throwable {
		// 如果是实现类 直接执行方法
		if (method.getDeclaringClass() == Object.class) {
			L.tag("J2W-Method");
			L.i("直接执行: " + method.getName());
			return method.invoke(this, args);
		}

		// 获取方法
		J2WMethodInfo methodInfo = J2WRestAdapter.getMethodInfo(methodDetailsCache, method);
		// 创建请求
		Request request = j2WRestAdapter.createRequest(methodInfo, tag, args);

		switch (methodInfo.executionType) {
			case SYNC:
				return j2WRestAdapter.invokeSync(methodInfo, request);// 执行
			case ASYNC:
				j2WRestAdapter.invokeAsync(methodInfo, request, (J2WCallback) args[args.length - 1]);
				return null;
			default:
				throw new IllegalStateException("未知的反应类型: " + methodInfo.executionType);
		}
	}

}
