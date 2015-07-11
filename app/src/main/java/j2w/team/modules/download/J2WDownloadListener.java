package j2w.team.modules.download;

/**
 * @创建人 sky
 * @创建时间 15/4/3 下午2:13
 * @类描述 进度回调接口
 */
public interface J2WDownloadListener {

	/**
	 * 成功
	 * 
	 * @param id
	 *            请求ID
	 */
	void onDownloadComplete(int id);

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
	void onDownloadFailed(int id, int errorCode, String errorMessage);

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
	void onDownloadProgress(int id, long totalBytes, long downloadedBytes, int progress);
}
