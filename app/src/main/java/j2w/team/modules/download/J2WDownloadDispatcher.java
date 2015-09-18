package j2w.team.modules.download;

import android.os.Process;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import j2w.team.common.log.L;
import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.modules.http.converter.GsonConverter;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;

public class J2WDownloadDispatcher extends Thread {

	/** 下载队列请求服务. */
	private final BlockingQueue<J2WBaseRequest>	mQueue;

	/** 通知调度死亡 */
	private volatile boolean					mQuit									= false;

	/** 请求 */
	private J2WBaseRequest						mRequest;

	/** 数据流缓冲大小 */
	public final int							BUFFER_SIZE								= 4096;

	/** 记录重定向多少次. */
	private int									mRedirectionCount						= 0;

	/** 最大重定向数量 */
	public final int							MAX_REDIRECTS							= 5;

	/** 网络底层协议 **/
	OkHttpClient								okHttpClient;

	/**
	 * 网络响应
	 */
	private final int							HTTP_REQUESTED_RANGE_NOT_SATISFIABLE	= 416;		// 资源范围

	private final int							HTTP_TEMP_REDIRECT						= 307;		// 重定向

	private long								mContentLength;									// 内容长度

	private long								mCurrentBytes;										// 下载字节

	boolean										shouldAllowRedirects					= true;	// 是否开启重定向

	GsonConverter								converter;

	/**
	 * 初始化调度器
	 * 
	 * @param queue
	 *            队列
	 * @param okHttpClient
	 *            网路底层协议
	 */
	public J2WDownloadDispatcher(BlockingQueue<J2WBaseRequest> queue, OkHttpClient okHttpClient) {
		mQueue = queue;
		this.okHttpClient = okHttpClient;
		converter = new GsonConverter();
	}

	/**
	 * 执行
	 */
	@Override public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

		while (true) {
			try {
				mRequest = mQueue.take(); // 从队列中取出指令

				if (mRequest instanceof J2WDownloadRequest) { // 下载请求
					J2WDownloadRequest j2WDownloadRequest = (J2WDownloadRequest) mRequest;
					mRedirectionCount = 0; // 重定向清零
					L.i("请求ID = " + j2WDownloadRequest.getRequestId());
					j2WDownloadRequest.setDownloadState(J2WDownloadManager.STATUS_STARTED);// 设置状态
					executeDownload(j2WDownloadRequest, j2WDownloadRequest.getDownloadUrl().toString());
				} else if (mRequest instanceof J2WUploadRequest) { // 上传请求
					J2WUploadRequest j2WUploadRequest = (J2WUploadRequest) mRequest;
					mRedirectionCount = 0; // 重定向清零
					L.i("请求ID = " + j2WUploadRequest.getRequestId());
					j2WUploadRequest.setDownloadState(J2WDownloadManager.STATUS_STARTED);// 设置状态
					executeUpload(j2WUploadRequest, j2WUploadRequest.getUploadUrl().toString());
				} else {
					L.i("未知指令");
				}

			} catch (InterruptedException e) {
				if (mQuit) {
					if (mRequest instanceof J2WDownloadRequest) { // 下载请求
						J2WDownloadRequest j2WDownloadRequest = (J2WDownloadRequest) mRequest;
						j2WDownloadRequest.finish();
						updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
					} else if (mRequest instanceof J2WUploadRequest) { // 上传请求
						J2WUploadRequest j2WUploadRequest = (J2WUploadRequest) mRequest;
						j2WUploadRequest.finish();
						updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消上传");

					} else {
						L.i("未知指令");
					}
					return;
				}
				continue;
			}
		}
	}

	/**
	 * *******************************上传****************************************
	 */

	/**
	 * 执行上传
	 *
	 * @param j2WUploadRequest
	 *            下载请求
	 * @param uploadUrl
	 *            请求url
	 */
	private void executeUpload(J2WUploadRequest j2WUploadRequest, String uploadUrl) {
		URL url = null;
		try {
			url = new URL(uploadUrl);
		} catch (MalformedURLException e) {
			updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_MALFORMED_URI, "异常 : 不正确的地址");
			return;
		}
		updateUploadState(j2WUploadRequest, J2WDownloadManager.STATUS_CONNECTING);
		// 创建文件体
		J2WOkUploadBody j2WOkUploadBody = new J2WOkUploadBody(j2WUploadRequest, j2WUploadRequest.getJ2WUploadListener());
		// 请求
		Request request = new Request.Builder().tag(j2WUploadRequest.getRequestTag()).url(url).headers(j2WUploadRequest.getHeaders()).post(j2WOkUploadBody.build()).build();// 创建请求
		try {
			updateUploadState(j2WUploadRequest, J2WDownloadManager.STATUS_RUNNING);
			Response response = okHttpClient.newCall(request).execute();
			final int responseCode = response.code();

			L.i("请求编号:" + j2WUploadRequest.getRequestId() + ", 响应编号 : " + responseCode);

			switch (responseCode) {
				case HTTP_OK:
					if (j2WUploadRequest.isCanceled()) {
						j2WUploadRequest.finish();
						updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
						return;
					}
					if (!response.isSuccessful()) {
						updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_HTTP_DATA_ERROR, "成功响应,但上传失败！");
						return;
					}
					shouldAllowRedirects = false;
					postUploadComplete(j2WUploadRequest, response);// 上传成功
					return;
				case HTTP_MOVED_PERM:
				case HTTP_MOVED_TEMP:
				case HTTP_SEE_OTHER:
				case HTTP_TEMP_REDIRECT:
					while (mRedirectionCount++ < MAX_REDIRECTS && shouldAllowRedirects) {
						L.i("重定向 Id " + j2WUploadRequest.getRequestId());
						final String location = response.header("Location");
						executeUpload(j2WUploadRequest, location); // 执行下载
						continue;
					}

					if (mRedirectionCount > MAX_REDIRECTS) {
						updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_TOO_MANY_REDIRECTS, "重定向太多，导致下载失败,默认最多 5次重定向！");
						return;
					}
					break;
				case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
					updateUploadFailed(j2WUploadRequest, HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, response.message());
					break;
				case HTTP_UNAVAILABLE:
					updateUploadFailed(j2WUploadRequest, HTTP_UNAVAILABLE, response.message());
					break;
				case HTTP_INTERNAL_ERROR:
					updateUploadFailed(j2WUploadRequest, HTTP_INTERNAL_ERROR, response.message());
					break;
				default:
					updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_UNHANDLED_HTTP_CODE, "未处理的响应:" + responseCode + " 信息:" + response.message());
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			updateUploadFailed(j2WUploadRequest, J2WDownloadManager.ERROR_HTTP_DATA_ERROR, "故障");
		}
	}

	/**
	 * 更新状态 - 上传失败
	 *
	 * @param j2WUploadRequest
	 * @param errorCode
	 * @param errorMsg
	 */
	public void updateUploadFailed(J2WUploadRequest j2WUploadRequest, int errorCode, String errorMsg) {
		shouldAllowRedirects = false;
		j2WUploadRequest.setDownloadState(J2WDownloadManager.STATUS_FAILED);
		if (j2WUploadRequest.getJ2WUploadListener() != null) {
			postUploadFailed(j2WUploadRequest, errorCode, errorMsg);
			j2WUploadRequest.finish();
		}
	}

	/**
	 * 更新状态 - 链接
	 *
	 * @param j2WUploadRequest
	 * @param state
	 */
	public void updateUploadState(J2WUploadRequest j2WUploadRequest, int state) {
		j2WUploadRequest.setDownloadState(state);
	}

	/**
	 * 上传失败
	 *
	 * @param request
	 *            请求
	 * @param errorCode
	 *            错误编号
	 * @param errorMsg
	 *            错误信息
	 */
	public void postUploadFailed(final J2WUploadRequest request, final int errorCode, final String errorMsg) {
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				request.getJ2WUploadListener().onUploadFailed(request.getRequestId(), errorCode, errorMsg);
			}
		});
	}

	/**
	 * 上传成功
	 *
	 * @param request
	 *            请求
	 */
	public void postUploadComplete(final J2WUploadRequest request, final Response response) {
		try {

			final Class clazz = J2WAppUtil.getSuperClassGenricType(request.getJ2WUploadListener().getClass(), 0);

			final Object value = converter.fromBody(response.body(), clazz);
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					if (clazz == null) {
						request.getJ2WUploadListener().onUploadComplete(request.getRequestId(), response);
					} else {
						request.getJ2WUploadListener().onUploadComplete(request.getRequestId(), value);
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			J2WHelper.mainLooper().execute(new Runnable() {
				public void run() {
					request.getJ2WUploadListener().onUploadFailed(request.getRequestId(), 0, "上传成功-数据转换失败");
				}
			});
		}
	}

	/**
	 * *******************************下载****************************************
	 */
	/**
	 * 执行下载
	 * 
	 * @param j2WDownloadRequest
	 *            下载请求
	 * @param downloadUrl
	 *            请求url
	 */
	private void executeDownload(J2WDownloadRequest j2WDownloadRequest, String downloadUrl) {

		URL url = null;
		try {
			url = new URL(downloadUrl);
		} catch (MalformedURLException e) {
			updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_MALFORMED_URI, "异常 : 不正确的地址");
			return;
		}

		Request request = new Request.Builder().tag(j2WDownloadRequest.getRequestTag()).url(url).build();// 创建请求
		try {
			Response response = okHttpClient.newCall(request).execute();

			updateDownloadState(j2WDownloadRequest, J2WDownloadManager.STATUS_CONNECTING);

			final int responseCode = response.code();

			L.i("请求编号:" + j2WDownloadRequest.getRequestId() + ", 响应编号 : " + responseCode);

			switch (responseCode) {
				case HTTP_OK:
					shouldAllowRedirects = false;
					if (readResponseHeaders(j2WDownloadRequest, response) == 1) {
						transferData(j2WDownloadRequest, response);
					} else {
						updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_DOWNLOAD_SIZE_UNKNOWN, "服务端没有返回文件长度，长度不确定导致失败！");
					}
					return;
				case HTTP_MOVED_PERM:
				case HTTP_MOVED_TEMP:
				case HTTP_SEE_OTHER:
				case HTTP_TEMP_REDIRECT:
					while (mRedirectionCount++ < MAX_REDIRECTS && shouldAllowRedirects) {
						L.i("重定向 Id " + j2WDownloadRequest.getRequestId());
						final String location = response.header("Location");
						executeDownload(j2WDownloadRequest, location); // 执行下载
						continue;
					}

					if (mRedirectionCount > MAX_REDIRECTS) {
						updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_TOO_MANY_REDIRECTS, "重定向太多，导致下载失败,默认最多 5次重定向！");
						return;
					}
					break;
				case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
					updateDownloadFailed(j2WDownloadRequest, HTTP_REQUESTED_RANGE_NOT_SATISFIABLE, response.message());
					break;
				case HTTP_UNAVAILABLE:
					updateDownloadFailed(j2WDownloadRequest, HTTP_UNAVAILABLE, response.message());
					break;
				case HTTP_INTERNAL_ERROR:
					updateDownloadFailed(j2WDownloadRequest, HTTP_INTERNAL_ERROR, response.message());
					break;
				default:
					updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_UNHANDLED_HTTP_CODE, "未处理的响应:" + responseCode + " 信息:" + response.message());
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_HTTP_DATA_ERROR, "故障");
		}
	}

	/**
	 * 流的读取
	 * 
	 * @param j2WDownloadRequest
	 * @param conn
	 */
	private void transferData(J2WDownloadRequest j2WDownloadRequest, Response conn) {
		InputStream in = null;
		OutputStream out = null;
		cleanupDestination(j2WDownloadRequest);
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			in = conn.body().byteStream();

			File destinationFile = new File(j2WDownloadRequest.getDestinationUrl().getPath().toString());

			try {
				out = new FileOutputStream(destinationFile, true);
			} catch (IOException e) {
				e.printStackTrace();
				updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_FILE_ERROR, "路径转换文件时错误");
			}

			bis = new BufferedInputStream(in);
			bos = new BufferedOutputStream(out);

			transferData(j2WDownloadRequest, bis, bos);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				if (bos != null) bos.flush();
			} catch (IOException e) {
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 流的读取
	 * 
	 * @param j2WDownloadRequest
	 * @param in
	 * @param out
	 */
	private void transferData(J2WDownloadRequest j2WDownloadRequest, BufferedInputStream in, BufferedOutputStream out) {
		final byte data[] = new byte[BUFFER_SIZE];
		mCurrentBytes = 0;
		j2WDownloadRequest.setDownloadState(J2WDownloadManager.STATUS_RUNNING);
		L.i("内容长度: " + mContentLength + " 下载ID : " + j2WDownloadRequest.getRequestId());
		for (;;) {
			if (j2WDownloadRequest.isCanceled()) {
				L.i("取消的请求Id " + j2WDownloadRequest.getRequestId());
				mRequest.finish();
				updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_DOWNLOAD_CANCELLED, "取消下载");
				return;
			}
			int bytesRead = readFromResponse(j2WDownloadRequest, data, in);

			if (mContentLength != -1) {
				int progress = (int) ((mCurrentBytes * 100) / mContentLength);
				updateDownloadProgress(j2WDownloadRequest, progress, mCurrentBytes);
			}

			if (bytesRead == -1) {
				updateDownloadComplete(j2WDownloadRequest);
				return;
			} else if (bytesRead == Integer.MIN_VALUE) {
				return;
			}

			writeDataToDestination(j2WDownloadRequest, data, bytesRead, out);
			mCurrentBytes += bytesRead;
		}
	}

	/**
	 * 从响应体里读取
	 * 
	 * @param j2WDownloadRequest
	 * @param data
	 * @param entityStream
	 * @return
	 */
	private int readFromResponse(J2WDownloadRequest j2WDownloadRequest, byte[] data, BufferedInputStream entityStream) {
		try {
			return entityStream.read(data);
		} catch (IOException ex) {
			updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_HTTP_DATA_ERROR, "无法读取响应");
			return Integer.MIN_VALUE;
		}
	}

	/**
	 * 下载数据到目标文件
	 * 
	 * @param j2WDownloadRequest
	 * @param data
	 * @param bytesRead
	 * @param out
	 */
	private void writeDataToDestination(J2WDownloadRequest j2WDownloadRequest, byte[] data, int bytesRead, BufferedOutputStream out) {
		while (true) {
			try {
				out.write(data, 0, bytesRead);
				return;
			} catch (IOException ex) {
				updateDownloadFailed(j2WDownloadRequest, J2WDownloadManager.ERROR_FILE_ERROR, "写入目标文件时，IO异常错误");
			}
		}
	}

	/**
	 * 读取响应头信息
	 * 
	 * @param j2WDownloadRequest
	 * @param response
	 * @return
	 */
	private int readResponseHeaders(J2WDownloadRequest j2WDownloadRequest, Response response) {
		final String transferEncoding = response.header("Transfer-Encoding");

		if (transferEncoding == null) {
			mContentLength = getHeaderFieldLong(response, "Content-Length", -1);
		} else {
			L.i("长度无法确定的请求Id " + j2WDownloadRequest.getRequestId());
			mContentLength = -1;
		}

		if (mContentLength == -1 && (transferEncoding == null || !transferEncoding.equalsIgnoreCase("chunked"))) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * 读取头信息
	 * 
	 * @param conn
	 * @param field
	 * @param defaultValue
	 * @return
	 */
	public long getHeaderFieldLong(Response conn, String field, long defaultValue) {
		try {
			return Long.parseLong(conn.header(field));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * 清理目标文件
	 */
	private void cleanupDestination(J2WDownloadRequest j2WDownloadRequest) {
		L.i("目标文件路径 : " + j2WDownloadRequest.getDestinationUrl());
		File destinationFile = new File(j2WDownloadRequest.getDestinationUrl().getPath().toString());
		if (destinationFile.exists()) {
			destinationFile.delete();
		}
	}

	/**
	 * 更新状态 - 链接
	 * 
	 * @param j2WDownloadRequest
	 * @param state
	 */
	public void updateDownloadState(J2WDownloadRequest j2WDownloadRequest, int state) {
		j2WDownloadRequest.setDownloadState(state);
	}

	/**
	 * 更新状态 - 下载成功
	 * 
	 * @param j2WDownloadRequest
	 */
	public void updateDownloadComplete(J2WDownloadRequest j2WDownloadRequest) {
		j2WDownloadRequest.setDownloadState(J2WDownloadManager.STATUS_SUCCESSFUL);
		if (j2WDownloadRequest.getJ2WDownloadListener() != null) {
			postDownloadComplete(j2WDownloadRequest);
			j2WDownloadRequest.finish();
		}
	}

	/**
	 * 更新状态 - 下载失败
	 * 
	 * @param j2WDownloadRequest
	 * @param errorCode
	 * @param errorMsg
	 */
	public void updateDownloadFailed(J2WDownloadRequest j2WDownloadRequest, int errorCode, String errorMsg) {
		shouldAllowRedirects = false;
		j2WDownloadRequest.setDownloadState(J2WDownloadManager.STATUS_FAILED);
		cleanupDestination(j2WDownloadRequest);
		if (j2WDownloadRequest.getJ2WDownloadListener() != null) {
			postDownloadFailed(j2WDownloadRequest, errorCode, errorMsg);
			j2WDownloadRequest.finish();
		}
	}

	/**
	 * 更新状态 - 加载进度
	 * 
	 * @param j2WDownloadRequest
	 * @param progress
	 * @param downloadedBytes
	 */
	public void updateDownloadProgress(J2WDownloadRequest j2WDownloadRequest, int progress, long downloadedBytes) {
		if (j2WDownloadRequest.getJ2WDownloadListener() != null) {
			postProgressUpdate(j2WDownloadRequest, mContentLength, downloadedBytes, progress);
		}
	}

	/**
	 * 下载成功
	 *
	 * @param request
	 *            请求
	 */
	public void postDownloadComplete(final J2WDownloadRequest request) {
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				request.getJ2WDownloadListener().onDownloadComplete(request.getRequestId());
			}
		});
	}

	/**
	 * 下载失败
	 *
	 * @param request
	 *            请求
	 * @param errorCode
	 *            错误编号
	 * @param errorMsg
	 *            错误信息
	 */
	public void postDownloadFailed(final J2WDownloadRequest request, final int errorCode, final String errorMsg) {
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				request.getJ2WDownloadListener().onDownloadFailed(request.getRequestId(), errorCode, errorMsg);
			}
		});
	}

	/**
	 * 下载进度
	 *
	 * @param request
	 *            请求
	 * @param totalBytes
	 *            总字节数
	 * @param downloadedBytes
	 * @param progress
	 */
	public void postProgressUpdate(final J2WDownloadRequest request, final long totalBytes, final long downloadedBytes, final int progress) {
		J2WHelper.mainLooper().execute(new Runnable() {

			@Override public void run() {
				request.getJ2WDownloadListener().onDownloadProgress(request.getRequestId(), totalBytes, downloadedBytes, progress);
			}
		});
	}

	/**
	 * *******************************上传****************************************
	 */
	/**
	 * 退出
	 */
	public void quit() {
		mQuit = true;
		interrupt();
	}
}
