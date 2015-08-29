package j2w.team.modules.download;

import com.squareup.okhttp.Response;

/**
 * @创建人 sky
 * @创建时间 15/4/7 上午9:59
 * @类描述 上传事件回调
 */
public interface J2WUploadListener<T> {

	/**
	 * 成功
	 *
	 * @param id
	 *            请求ID
	 * @param t
	 *            响应结果
	 */
	void onUploadComplete(int id, T t);

	/**
	 * 失败
	 *
	 * @param id
	 *            请求ID
	 * @param errorCode
	 *            错误编码
	 * @param errorMessage
	 *            错误信息
	 */
	void onUploadFailed(int id, int errorCode, String errorMessage);

	/**
	 * 进度回调
	 *
	 * @param id
	 *            请求ID
	 * @param totalBytes
	 *            总字节
	 * @param downloadedBytes
	 *            下载字节
	 * @param progress
	 *            进度
	 */
	void onUploadProgress(int id, long totalBytes, long downloadedBytes, int progress);
}
