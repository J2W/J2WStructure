package j2w.team.modules.download;

import android.net.Uri;

/**
 * @创建人 sky
 * @创建时间 15/4/3 上午11:46
 * @类描述 下载请求
 */
public class J2WDownloadRequest extends J2WBaseRequest {

	/**
	 * 初始化
	 * 
	 * @param uri
	 *            地址
	 */
	public J2WDownloadRequest(Uri uri) {
		if (uri == null) {
			throw new NullPointerException();
		}

		String scheme = uri.getScheme();
		if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
			throw new IllegalArgumentException("下载地址只能是  HTTP/HTTPS 开头！ uri : " + uri);
		}
		setDownloadState(J2WDownloadManager.STATUS_PENDING);
		downloadUrl = uri;
	}

	/**
	 * 下载事件
	 */
	private J2WDownloadListener	j2WDownloadListener;

	/**
	 * 下载后的文件路径名
	 */
	private Uri					downloadUrl;

	/**
	 * 下载URL
	 */

	private Uri					destinationUrl;

	/**
	 * 获取下载事件
	 *
	 * @return 事件
	 */
	public J2WDownloadListener getJ2WDownloadListener() {
		return j2WDownloadListener;
	}

	/**
	 * 下载地址
	 * 
	 * @return 地址
	 */
	public Uri getDownloadUrl() {
		return downloadUrl;
	}

	/**
	 * 下载后目标地址
	 * 
	 * @return 地址
	 */
	public Uri getDestinationUrl() {
		return destinationUrl;
	}

	/**
	 * 设置目标地址
	 * 
	 * @param destinationUrl
	 */
	public J2WDownloadRequest setDestinationUrl(Uri destinationUrl) {
		this.destinationUrl = destinationUrl;
        return this;
	}

	/**
	 * 设置下载事件
	 * 
	 * @param j2WDownloadListener
	 *            事件
	 * @return
	 */
	public J2WDownloadRequest setJ2WDownloadListener(J2WDownloadListener j2WDownloadListener) {
		this.j2WDownloadListener = j2WDownloadListener;
		return this;
	}

}
