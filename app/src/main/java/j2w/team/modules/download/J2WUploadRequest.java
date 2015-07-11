package j2w.team.modules.download;

import android.net.Uri;

import com.squareup.okhttp.Headers;

import j2w.team.common.utils.J2WTextUtils;


/**
 * @创建人 sky
 * @创建时间 15/4/3 下午3:25
 * @类描述 上传请求
 */
public class J2WUploadRequest extends J2WBaseRequest {

	private Uri					uploadUri;

	private J2WUploadListener	j2WUploadListener;

	Headers.Builder				headers;

	J2WContentType				j2WContentType;

	J2WUploadBody				j2WUploadBody;

	/**
	 * 初始化
	 *
	 * @param uri
	 *            地址
	 * @param j2WUploadBody
	 *            请求体
	 * @param j2WContentType
	 *            类型
	 */
	public J2WUploadRequest(Uri uri, J2WUploadBody j2WUploadBody, J2WContentType j2WContentType) {
		if (J2WTextUtils.isEmpty(j2WUploadBody.headerName) || J2WTextUtils.isEmpty(j2WUploadBody.headerValue)) {
			throw new IllegalArgumentException("文件体头信息不能为空！");
		}
		if (j2WUploadBody.file == null) {
			throw new NullPointerException();
		}

		if (!j2WUploadBody.file.exists()) {
			throw new IllegalArgumentException("文件不存在！");
		}

		String scheme = uri.getScheme();
		if (scheme == null || (!scheme.equals("http") && !scheme.equals("https"))) {
			throw new IllegalArgumentException("上传地址只能是  HTTP/HTTPS 开头！ uri : " + uri);
		}
		setDownloadState(J2WDownloadManager.STATUS_PENDING);
		this.uploadUri = uri;
		this.j2WUploadBody = j2WUploadBody;
		this.headers = new Headers.Builder();
		this.j2WContentType = j2WContentType;
	}

	/**
	 * 添加头信息
	 *
	 * @param headerName
	 * @param headerValue
	 * @return
	 */
	public J2WUploadRequest addHeader(String headerName, String headerValue) {
		headers.add(headerName, headerValue);
		return this;
	}

	/**
	 * 添加头信息
	 *
	 * @param headerName
	 * @param headerValue
	 * @return
	 */
	public J2WUploadRequest addHeaderBody(String headerName, String headerValue) {
		headers.add(headerName, headerValue);
		return this;
	}

	/**
	 * 返回请求头信息
	 * 
	 * @return
	 */
	public Headers getHeaders() {
		return headers.build();
	}

	/**
	 * 返回请求体
	 * 
	 * @return
	 */
	public J2WUploadBody getJ2WUploadBody() {
		return j2WUploadBody;
	}

	/**
	 * 返回类型
	 * 
	 * @return
	 */
	public J2WContentType getJ2WContentType() {
		return j2WContentType;
	}

	/**
	 * 上传地址
	 *
	 * @return 地址
	 */
	public Uri getUploadUrl() {
		return uploadUri;
	}

	/**
	 * 获取上传事件
	 *
	 * @return 事件
	 */
	public J2WUploadListener getJ2WUploadListener() {
		return j2WUploadListener;
	}

	/**
	 * 设置下载事件
	 *
	 * @param j2WUploadListener
	 *            事件
	 * @return
	 */
	public J2WUploadRequest setJ2WUploadListener(J2WUploadListener j2WUploadListener) {
		this.j2WUploadListener = j2WUploadListener;
		return this;
	}
}
