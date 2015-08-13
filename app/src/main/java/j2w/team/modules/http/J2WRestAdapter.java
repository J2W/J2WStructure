package j2w.team.modules.http;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import j2w.team.common.log.L;
import j2w.team.common.utils.proxy.DynamicProxyUtils;
import j2w.team.modules.http.converter.GsonConverter;
import j2w.team.modules.http.converter.J2WConverter;
import j2w.team.view.model.J2WConstants;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/2/24. 网络适配器
 */
public class J2WRestAdapter {

	// 缓存
	private final Map<Class<?>, Map<Method, J2WMethodInfo>>	serviceMethodInfoCache	= new LinkedHashMap<Class<?>, Map<Method, J2WMethodInfo>>();

	// 端点地址
	final J2WEndpoint										j2WEndpoint;

	// 转换器
	private J2WConverter									converter;

	// 拦截器
	private J2WRequestInterceptor							requestInterceptor;

	// OKHTTP
	private final OkHttpClient								client;

	// 错误
	private J2WErrorHandler									errorHandler;

	/**
	 * 构造器
	 *
	 * @param j2WEndpoint
	 *            端点地址
	 */
	public J2WRestAdapter(OkHttpClient client, J2WEndpoint j2WEndpoint, J2WConverter converter, J2WRequestInterceptor requestInterceptor, J2WErrorHandler errorHandler) {
		// OKHTTP
		this.client = client;
		// 端点地址
		this.j2WEndpoint = j2WEndpoint;
		// 数据转换器
		this.converter = converter;
		// 拦截器
		this.requestInterceptor = requestInterceptor;
		// 错误
		this.errorHandler = errorHandler;
	}

	/**
	 * 创建代理
	 *
	 * @param service
	 * @param <T>
	 * @return
	 */
	public <T> T create(Class<T> service) {
		// 验证是否是接口
		DynamicProxyUtils.validateServiceClass(service);
		// 验证是否继承其他接口
		DynamicProxyUtils.validateInterfaceServiceClass(service);
		// 创建动态代理-网络层
		J2WRestHandler j2WRestHandler = new J2WRestHandler(this, getMethodInfoCache(service));
		// 创建代理类并返回
		return DynamicProxyUtils.newProxyInstance(service.getClassLoader(), new Class<?>[] { service }, j2WRestHandler);
	}

	/**
	 * 取消请求
	 *
	 * @param requestCode
	 */
	public void cancel(String requestCode) {
		client.cancel(requestCode);
	}

	/**
	 * 取消请求
	 *
	 * @param service
	 *            接口
	 * @param methodName
	 *            方法名称
	 * @param <T>
	 *            泛型
	 */
	public <T> void cancel(Class<T> service, String methodName) {
		if (service == null) {
			return;
		}
		L.tag("J2WRestAdapter");
		L.i("取消 cancel(Class<T> service) 接口名 :" + service.getSimpleName());
		Method[] methods = service.getMethods();

		for (Method method : methods) {
			if (method.getName().equals(methodName)) {
				String methodString = J2WMethodInfo.getMethodString(method, method.getParameterTypes());
				client.cancel(methodString);
			}
		}
	}

	/**
	 * 取消该接口下的所有请求
	 *
	 * @param service
	 *            接口
	 * @param <T>
	 */
	public <T> void cancel(Class<T> service) {
		if (service == null) {
			return;
		}
		L.tag("J2WRestAdapter");
		L.i("取消 cancel(Class<T> service) 接口名 :" + service.getSimpleName());
		Method[] methods = service.getMethods();

		for (Method method : methods) {
			String methodString = J2WMethodInfo.getMethodString(method, method.getParameterTypes());
			client.cancel(methodString);
		}
	}

	/**
	 * 取消所有请求
	 */
	public void cancelAll() {
		for (Class<?> clazz : serviceMethodInfoCache.keySet()) {
			cancel(clazz);
		}
	}

	/**
	 * 执行同步请求
	 *
	 * @param methodInfo
	 * @param request
	 * @return
	 * @throws Throwable
	 */
	Object invokeSync(J2WMethodInfo methodInfo, Request request) throws Throwable {
		try {
			Call call = client.newCall(request);
			// 发送请求
			Response response = call.execute();
			// 拿到结果调用结果处理方法
			return createResult(methodInfo, response);
		} catch (IOException e) {
			throw handleError(J2WError.networkFailure(request.urlString(), e));
		} catch (J2WError error) {
			throw handleError(error);
		}
	}

	/**
	 * 执行异步请求
	 *
	 * @param methodInfo
	 * @param request
	 * @param callback
	 */
	void invokeAsync(final J2WMethodInfo methodInfo, final Request request, final J2WCallback callback) {
		Call call = client.newCall(request);
		call.enqueue(new com.squareup.okhttp.Callback() {

			@Override public void onFailure(Request request, IOException e) {
				callFailure(callback, J2WError.networkFailure(request.urlString(), e));
			}

			@Override public void onResponse(Response response) {
				try {
					Object result = createResult(methodInfo, response);
					callResponse(callback, result, response);
				} catch (J2WError error) {
					callFailure(callback, error);
				}
			}
		});

	}

	/**
	 * 正确回调
	 *
	 * @param callback
	 * @param result
	 * @param response
	 */
	private void callResponse(final J2WCallback callback, final Object result, final Response response) {
		// 主线程执行
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				try {
					callback.success(result, response);
				} catch (Throwable throwable) {
					L.tag("J2W-Method");
					L.i("方法执行失败");
					return;
				}
			}
		});
	}

	/**
	 * 错误回调
	 *
	 * @param callback
	 * @param error
	 */
	private void callFailure(final J2WCallback callback, J2WError error) {
		Throwable throwable = handleError(error);
		if (throwable != error) {// 如果是自定义异常
			Response response = error.getResponse();
			if (response != null) {
				error = J2WError.unexpectedError(response, throwable);
			} else {
				error = J2WError.unexpectedError(error.getUrl(), throwable);
			}
		}
		final J2WError finalError = error;
		// 主线程执行
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				try {
					callback.failure(finalError);
				} catch (Throwable throwable) {
					L.tag("J2W-Method");
					L.i("方法执行失败");
					return;
				}
			}
		});
	}

	/**
	 * 错误操作
	 *
	 * @param error
	 * @return
	 */
	private Throwable handleError(J2WError error) {
		L.tag("J2W-Method");
		L.i("handleError(error)");
		Throwable throwable = errorHandler.handleError(error);
		if (throwable == null) {
			return new IllegalStateException("错误处理程序返回空.", error);
		}
		return throwable;
	}

	/**
	 * 创建结果
	 *
	 * @param methodInfo
	 * @param response
	 * @return
	 */
	private Object createResult(J2WMethodInfo methodInfo, Response response) {
		try {
			return parseResult(methodInfo, response);
		} catch (J2WError error) {
			throw error;
		} catch (IOException e) {
			throw J2WError.networkError(response, e);
		} catch (Throwable t) {
			throw J2WError.unexpectedError(response, t);
		}
	}

	/**
	 * 解析结果集
	 *
	 * @param methodInfo
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private Object parseResult(J2WMethodInfo methodInfo, Response response) throws IOException {
		// 获取结果类型
		Type type = methodInfo.responseObjectType;

		// 拿到响应编号
		int statusCode = response.code();

		// 判断
		if (statusCode < 200 || statusCode >= 300) {
			response = J2WMethodInfo.readBodyToBytesIfNecessary(response);
			throw J2WError.httpError(response, converter, type);
		}

		// 如果是结果集
		if (type.equals(Response.class)) {
			return response;
		}

		// 读取响应体
		ResponseBody body = response.body();
		if (body == null) {
			return null;
		}
		// 捕获请求异常
		ExceptionCatchingRequestBody wrapped = new ExceptionCatchingRequestBody(body);
		try {
			return converter.fromBody(wrapped, type);
		} catch (RuntimeException e) {
			if (wrapped.threwException()) {
				throw wrapped.getThrownException();
			}
			throw e;
		}
	}

	/**
	 * 创建请求
	 *
	 * @param methodInfo
	 *            方法信息
	 * @param args
	 *            参数
	 * @return
	 */
	Request createRequest(J2WMethodInfo methodInfo, String requestTag, Object[] args) {
		// 获取url
		String serverUrl = j2WEndpoint.url(requestTag);
		// 编辑请求
		J2WRequestBuilder requestBuilder = new J2WRequestBuilder(serverUrl, methodInfo, converter);
		// 设置参数
		requestBuilder.setArguments(args);
		// 交给拦截器
		requestInterceptor.intercept(requestBuilder);

		return requestBuilder.build(requestTag);
	}

	/**
	 * 获取方法信息
	 *
	 * @param cache
	 * @param method
	 * @return
	 */
	public static J2WMethodInfo getMethodInfo(Map<Method, J2WMethodInfo> cache, Method method) {
		synchronized (cache) {
			J2WMethodInfo methodInfo = cache.get(method);
			if (methodInfo == null) {
				methodInfo = new J2WMethodInfo(method);
				cache.put(method, methodInfo);
			}
			return methodInfo;
		}
	}

	/**
	 * 从缓存里获取方法
	 *
	 * @param service
	 * @return
	 */
	Map<Method, J2WMethodInfo> getMethodInfoCache(Class<?> service) {
		synchronized (serviceMethodInfoCache) {
			Map<Method, J2WMethodInfo> methodInfoCache = serviceMethodInfoCache.get(service);
			if (methodInfoCache == null) {
				methodInfoCache = new LinkedHashMap<Method, J2WMethodInfo>();
				serviceMethodInfoCache.put(service, methodInfoCache);
			}
			return methodInfoCache;
		}
	}

	/**
	 * 生成器
	 */
	public static class Builder {

		/**
		 * 超时
		 */
		private int						timeOut;

		/**
		 * 端点地址
		 */
		private J2WEndpoint				endpoint;

		/**
		 * 网络协议
		 */
		private OkHttpClient			client;

		/**
		 * 请求拦截器
		 */
		private J2WRequestInterceptor	requestInterceptor;

		/**
		 * 转换器
		 */
		private J2WConverter			converter;

		/**
		 * 错误处理程序
		 */
		private J2WErrorHandler			errorHandler;

		/**
		 * 端点地址
		 */
		public Builder setEndpoint(String url) {
			return setEndpoint(J2WEndpoint.createFixed(url));
		}

		/**
		 * 端点地址
		 */
		public Builder setEndpoint(J2WEndpoint endpoint) {
			if (endpoint == null) {
				throw new NullPointerException("端点地址不能为空.");
			}
			this.endpoint = endpoint;
			return this;
		}

		/**
		 * 设置拦截器
		 */
		public Builder setRequestInterceptor(J2WRequestInterceptor requestInterceptor) {
			if (requestInterceptor == null) {
				throw new NullPointerException("请求拦截器不得空.");
			}
			this.requestInterceptor = requestInterceptor;
			return this;
		}

		/**
		 * 设置超时
		 * 
		 * @param timeOut
		 *            超时
		 * @return
		 */
		public Builder setTimeOut(int timeOut) {
			this.timeOut = timeOut;
			return this;
		}

		/**
		 * 设置转换器
		 */
		public Builder setConverter(J2WConverter converter) {
			if (converter == null) {
				throw new NullPointerException("转换器不能为空.");
			}
			this.converter = converter;
			return this;
		}

		/**
		 * 设置错误处理
		 */
		public Builder setErrorHandler(J2WErrorHandler errorHandler) {
			if (errorHandler == null) {
				throw new NullPointerException("错误处理handler不能为空");
			}
			this.errorHandler = errorHandler;
			return this;
		}

		/**
		 * 设置网络模块
		 */
		public Builder setHttpClient(OkHttpClient okHttpClient) {
			if (okHttpClient == null) {
				throw new NullPointerException("错误处理网络服务不能为空");
			}
			this.client = okHttpClient;
			return this;
		}

		/**
		 * 创建网络适配器 推荐使用单例模式
		 *
		 * @return
		 */
		public J2WRestAdapter build() {
			// 地址不能为空
			if (endpoint == null) {
				throw new IllegalArgumentException("端点地址不能为空.");
			}
			// 默认值
			ensureSaneDefaults();
			return new J2WRestAdapter(client, endpoint, converter, requestInterceptor, errorHandler);
		}

		/**
		 * 获取默认值
		 */
		private void ensureSaneDefaults() {
			// 默认超时
			if (timeOut == 0) {
				timeOut = J2WConstants.DEFAULT_TIME_OUT;
			}
			// 转换器-默认使用Gson
			if (converter == null) {
				converter = new GsonConverter();
			}
			// 网络协议-默认使用okhttp
			if (client == null) {
				OkHttpClient okHttpClient = new OkHttpClient();
				okHttpClient.setConnectTimeout(timeOut, TimeUnit.SECONDS);// 连接超时
				okHttpClient.setReadTimeout(timeOut, TimeUnit.SECONDS);// 读取超时
				okHttpClient.setWriteTimeout(timeOut, TimeUnit.SECONDS);// 写入超时
				client = okHttpClient;
			}
			// 错误处理程序-默认什么都不做
			if (errorHandler == null) {
				errorHandler = J2WErrorHandler.DEFAULT;
			}
			// 拦截器-默认什么都不做
			if (requestInterceptor == null) {
				requestInterceptor = J2WRequestInterceptor.NONE;
			}
		}
	}
}
