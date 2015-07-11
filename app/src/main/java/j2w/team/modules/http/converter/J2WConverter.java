package j2w.team.modules.http.converter;

import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by sky on 15/2/23. 转换器
 */
public interface J2WConverter {

	/**
	 * 将一个HTTP响应体指定类型的对象。
	 * 
	 * @param body
	 *            请求结果体
	 * @param type
	 *            类型
	 * @return 返回对应类型
	 * @throws IOException
	 */
	Object fromBody(ResponseBody body, Type type) throws IOException;

	/**
	 * 将对象转换为HTTP传输适当的表示
	 * 
	 * @param object
	 *            对象实例转换.
	 * @return 指定的对象表示为字节
	 */
	RequestBody toBody(Object object, Type type);
}
