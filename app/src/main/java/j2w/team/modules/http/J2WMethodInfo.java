package j2w.team.modules.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import j2w.team.modules.http.annotations.Body;
import j2w.team.modules.http.annotations.GET;
import j2w.team.modules.http.annotations.HEAD;
import j2w.team.modules.http.annotations.Header;
import j2w.team.modules.http.annotations.Headers;
import j2w.team.modules.http.annotations.Path;
import j2w.team.modules.http.annotations.POST;
import j2w.team.modules.http.annotations.Query;
import j2w.team.modules.http.annotations.QueryMap;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by sky on 15/2/13. 解析方法上的注解
 */
public final class J2WMethodInfo {

	/**
	 * 枚举类型
	 */
	public enum ExecutionType {
		ASYNC, SYNC
	}

	private static final String		PARAM				= "[a-zA-Z][a-zA-Z0-9_-]*";

	private static final Pattern	PARAM_NAME_REGEX	= Pattern.compile(PARAM);

	private static final Pattern	PARAM_URL_REGEX		= Pattern.compile("\\{(" + PARAM + ")\\}");

	// 方法属性
	final Method					method;

	// 枚举类型
	public final ExecutionType		executionType;

	// 响应类型
	Type							responseObjectType;

	// 请求方法
	String							requestMethod;

	// 请求参数名称集合
	Set<String>						requestUrlParamNames;

	// 请求url
	String							requestUrl;

	// 请求查询
	String							requestQuery;

	// 请求头信息
	com.squareup.okhttp.Headers		headers;

	// 请求头信息内容类型
	String							contentTypeHeader;

	// 请求参数注解
	Annotation[]					requestParamAnnotations;

	Type							requestObjectType;

	J2WMethodInfo(Method method) {
		this.method = method;
		// 拿到返回类型
		executionType = parseResponseType();
		// 解析方法上的注解
		parseMethodAnnotations();
		// 解析方法参数
		parseParameters();
	}

	/**
	 * 解析方法上的注解
	 */

	private void parseMethodAnnotations() {
		for (Annotation methodAnnotation : method.getAnnotations()) {
			Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
			if (annotationType == GET.class) {
				parseHttpMethodAndPath("GET", ((GET) methodAnnotation).value());
			} else if (annotationType == HEAD.class) {
				parseHttpMethodAndPath("HEAD", ((HEAD) methodAnnotation).value());
			} else if (annotationType == POST.class) {
				parseHttpMethodAndPath("POST", ((POST) methodAnnotation).value());
			} else if (annotationType == Headers.class) {
				String[] headersToParse = ((Headers) methodAnnotation).value();
				if (headersToParse.length == 0) {
					throw methodError("@Headers 空的.");
				}
				headers = parseHeaders(headersToParse);
			}
		}
		if (requestMethod == null) {
			throw methodError("给方法注解( @GET, @POST).");
		}
	}

	/**
	 * 解析参数
	 */
	private void parseParameters() {
		Type[] methodParameterTypes = method.getGenericParameterTypes();
		// 活的参数类型注解数组
		Annotation[][] methodParameterAnnotationArrays = method.getParameterAnnotations();
		// 获取长度
		int count = methodParameterAnnotationArrays.length;
		if (executionType == ExecutionType.ASYNC) { // 如果异步
			count -= 1; // 回调是最后一个参数的时候没有同步方法
		}
		boolean gotBody = false;
		// 生成
		Annotation[] requestParamAnnotations = new Annotation[count];

		for (int i = 0; i < count; i++) {
			Type methodParameterType = methodParameterTypes[i];
			Annotation[] methodParameterAnnotations = methodParameterAnnotationArrays[i];
			if (methodParameterAnnotations != null) {
				for (Annotation methodParameterAnnotation : methodParameterAnnotations) {
					Class<? extends Annotation> methodAnnotationType = methodParameterAnnotation.annotationType();

					if (methodAnnotationType == Path.class) {
						String name = ((Path) methodParameterAnnotation).value();
						validatePathName(i, name);
					} else if (methodAnnotationType == Query.class) {
						// 什么都不做
					} else if (methodAnnotationType == QueryMap.class) {
						if (!Map.class.isAssignableFrom(Types.getRawType(methodParameterType))) {
							throw parameterError(i, "@QueryMap 参数类型必须map.");
						}
					} else if (methodAnnotationType == Body.class) {
						requestObjectType = methodParameterType;
						gotBody = true;
					} else if (methodAnnotationType == Header.class) {
						//  什么都不做
					} else {
						continue;
					}
					requestParamAnnotations[i] = methodParameterAnnotation;
				}
			}

			if (requestParamAnnotations[i] == null) {
				throw parameterError(i, "没有发现注解.");
			}
		}

		this.requestParamAnnotations = requestParamAnnotations;
	}

	/**
	 * 验证方法参数注解名称
	 *
	 * @param index
	 * @param name
	 */
	private void validatePathName(int index, String name) {
		if (!PARAM_NAME_REGEX.matcher(name).matches()) {
			throw parameterError(index, "@Path 参数名称必须匹配 %s. 名称： %s", PARAM_URL_REGEX.pattern(), name);
		}
		// 验证URL替换的名字实际上是URL路径。
		if (!requestUrlParamNames.contains(name)) {
			throw parameterError(index, "URL \"%s\" 不包含\"{%s}\".", requestUrl, name);
		}
	}

	/**
	 * 解析请求头
	 *
	 * @param headers
	 * @return
	 */
	com.squareup.okhttp.Headers parseHeaders(String[] headers) {
		com.squareup.okhttp.Headers.Builder builder = new com.squareup.okhttp.Headers.Builder();
		for (String header : headers) {
			int colon = header.indexOf(':');
			if (colon == -1 || colon == 0 || colon == header.length() - 1) {
				throw methodError("@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header);
			}
			String headerName = header.substring(0, colon);
			String headerValue = header.substring(colon + 1).trim();
			if ("Content-Type".equalsIgnoreCase(headerName)) {
				contentTypeHeader = headerValue;
			} else {
				builder.add(headerName, headerValue);
			}
		}
		return builder.build();
	}

	/**
	 * 解析方法和参数
	 *
	 * @param method
	 * @param path
	 */
	private void parseHttpMethodAndPath(String method, String path) {
		if (requestMethod != null) {
			throw methodError("只能有一个HTTP方法 %s and %s.", requestMethod, method);
		}
		if (path == null || path.length() == 0 || path.charAt(0) != '/') {
			throw methodError("URL 路径  \"%s\" 必须要从 '/'.", path);
		}

		String url = path;
		String query = null;
		int question = path.indexOf('?');
		if (question != -1 && question < path.length() - 1) {
			url = path.substring(0, question);
			query = path.substring(question + 1);

			// 确保查询字符串没有任何命名参数
			Matcher queryParamMatcher = PARAM_URL_REGEX.matcher(query);
			if (queryParamMatcher.find()) { //
				throw methodError("URL 查询字符串 \"%s\" 不得有替代功能块. 对于动态查询参数的使用 @Query.", query);
			}
		}

		Set<String> urlParams = parsePathParameters(path);

		requestMethod = method;
		requestUrl = url;
		requestUrlParamNames = urlParams;
		requestQuery = query;

	}

	/**
	 * 解析Path参数
	 * 
	 * @param path
	 * @return
	 */
	static Set<String> parsePathParameters(String path) {
		// 通过正则表达式 查询
		Matcher m = PARAM_URL_REGEX.matcher(path);
		Set<String> patterns = new LinkedHashSet<String>();
		while (m.find()) {
			patterns.add(m.group(1));
		}
		return patterns;
	}

	/**
	 * 获取返回类型
	 * 
	 * @return
	 */
	private ExecutionType parseResponseType() {
		Type returnType = method.getGenericReturnType(); // 获取方法的返回类型

		Type lastArgType = null; // 参数类型

		Class<?> lastArgClass = null; // 结果类型

		Type[] parameterTypes = method.getGenericParameterTypes(); // 获得一个方法参数数组

		if (parameterTypes.length > 0) {
			Type typeToCheck = parameterTypes[parameterTypes.length - 1]; // 获取最后一个参数类型
			lastArgType = typeToCheck;
			if (typeToCheck instanceof ParameterizedType) { // 判断是否是普通参数
				typeToCheck = ((ParameterizedType) typeToCheck).getRawType();// 返回类型参数
			}
			if (typeToCheck instanceof Class) { // 判断是否是实现类-回调接口的实现类
				lastArgClass = (Class<?>) typeToCheck;// 返回类
			}
		}

		boolean hasReturnType = returnType != void.class; // 返回类型 是否为NULL
		boolean hasCallback = lastArgClass != null && J2WCallback.class.isAssignableFrom(lastArgClass);// 判断是否是回调接接口

		if (hasReturnType && hasCallback) {
			throw methodError("返回类型和回调接口不能同时存在！");
		}
		if (!hasReturnType && !hasCallback) {
			throw methodError("返回类型和回调接口必须要有一个！");
		}

		if (hasReturnType) {// 如果有返回类型 - 同步类型
			responseObjectType = returnType;
			return ExecutionType.SYNC;
		}
		// 获取父类的类型
		lastArgType = Types.getSupertype(lastArgType, Types.getRawType(lastArgType), J2WCallback.class);
		if (lastArgType instanceof ParameterizedType) { // 判断是否是参数类型
			responseObjectType = getParameterUpperBound((ParameterizedType) lastArgType);// 获取绑定的参数类型
			return ExecutionType.ASYNC;
		}

		throw methodError("最后一个参数必须是Callback<x> or Callback<? super X>");
	}

	/**
	 * 获取参数类型
	 * 
	 * @param type
	 * @return
	 */
	private static Type getParameterUpperBound(ParameterizedType type) {
		Type[] types = type.getActualTypeArguments();// 返回参数数组
		for (int i = 0; i < types.length; i++) {
			Type paramType = types[i];
			if (paramType instanceof WildcardType) { // 接口类型
				types[i] = ((WildcardType) paramType).getUpperBounds()[0];
			}
		}
		return types[0];
	}

	/**
	 * 自定义异常 - 方法错误
	 * 
	 * @param message
	 * @param args
	 * @return
	 */
	private RuntimeException methodError(String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
		}
		return new IllegalArgumentException(method.getDeclaringClass().getSimpleName() + "." + method.getName() + ": " + message);
	}

	/**
	 * 自定义异常 - 参数错误
	 * 
	 * @param index
	 * @param message
	 * @param args
	 * @return
	 */
	private RuntimeException parameterError(int index, String message, Object... args) {
		return methodError(message + " (parameter #" + (index + 1) + ")", args);
	}

	/**
	 * Okhttp 读取结果集
	 * 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	static Response readBodyToBytesIfNecessary(Response response) throws IOException {
		final ResponseBody body = response.body();
		if (body == null) {
			return response;
		}

		BufferedSource source = body.source();
		final Buffer buffer = new Buffer();
		buffer.writeAll(source);
		source.close();

		return response.newBuilder().body(new ResponseBody() {

			@Override public MediaType contentType() {
				return body.contentType();
			}

			@Override public long contentLength() {
				return buffer.size();
			}

			@Override public BufferedSource source() {
				return buffer.clone();
			}
		}).build();
	}

	/**
	 * 获取带参数的方法名
	 *
	 * @return
	 */
	public static final String getMethodString(Method method, Class[] classes) {
		boolean bool = false;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(method.getName());
		stringBuilder.append("(");
		for (Class clazz : classes) {
			stringBuilder.append(clazz.getSimpleName());
			stringBuilder.append(",");
			bool = true;
		}
		if (bool) {
			stringBuilder = stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
		}
		stringBuilder.append(")");
		return stringBuilder.toString();
	}
}
