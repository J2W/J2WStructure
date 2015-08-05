package j2w.team.modules.download;

import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * @创建人 sky
 * @创建时间 15/4/3 上午11:52
 * @类描述 下载管理器接口
 */
public interface J2WIDownloadMagnager {

	/**
	 * 目前正在等待状态
	 */
	int	STATUS_PENDING				= 1 << 0;

	/**
	 * 等待状态.
	 */
	int	STATUS_STARTED				= 1 << 1;

	/**
	 * 联网状态
	 */
	int	STATUS_CONNECTING			= 1 << 2;

	/**
	 * 运行状态.
	 */
	int	STATUS_RUNNING				= 1 << 3;

	/**
	 * 完成状态
	 */
	int	STATUS_SUCCESSFUL			= 1 << 4;

	/**
	 * 失败状态.
	 */
	int	STATUS_FAILED				= 1 << 5;

	/**
	 * 失败状态 - 没有找到
	 */
	int	STATUS_NOT_FOUND			= 1 << 6;

	/**
	 * 错误代码时下载内容到目标文件
	 */
	int	ERROR_FILE_ERROR			= 1001;

	/**
	 * 错误代码被接收时的HTTP代码下载管理器不能处理
	 */
	int	ERROR_UNHANDLED_HTTP_CODE	= 1002;

	/**
	 * 错误的接受或者处理的代码错误
	 */
	int	ERROR_HTTP_DATA_ERROR		= 1004;

	/**
	 * 重定向过多
	 */
	int	ERROR_TOO_MANY_REDIRECTS	= 1005;

	/**
	 * 文件大小未知.
	 */
	int	ERROR_DOWNLOAD_SIZE_UNKNOWN	= 1006;

	/**
	 * URI格式不正确.
	 */
	int	ERROR_MALFORMED_URI			= 1007;

	/**
	 * 取消请求.
	 */
	int	ERROR_DOWNLOAD_CANCELLED	= 1008;

	/**
	 * 添加请求
	 * 
	 * @param request
	 *            请求指令
	 * @return 请求ID
	 */
	int add(J2WBaseRequest request);

	/**
	 * 取消请求
	 * 
	 * @param downloadId
	 *            请求ID
	 * @return int
	 */
	int cancel(int downloadId);

	/**
	 * 取消所有请求
	 */
	void cancelAll();

	/**
	 * 查询请求指令
	 * 
	 * @param downloadId
	 *            请求ID
	 * @return
	 */
	int query(int downloadId);

	/**
	 * 释放
	 */
	void release();

	/**
	 * 下载 默认路径 /storage/emulated/0/Android/data/APP包
	 * 
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            文件名
	 * @param j2WDownloadListener
	 *            事件
	 */
	int download(String url, String fileName, J2WDownloadListener j2WDownloadListener);

	/**
	 * 下载
	 * 
	 * @param url
	 *            下载地址
	 * @param destination
	 *            路径
	 * @param fileName
	 *            文件名
	 * @param j2WDownloadListener
	 *            事件
	 */
	int download(String url, String destination, String fileName, J2WDownloadListener j2WDownloadListener);

	/**
	 * 下载
	 * 
	 * @param downloadUri
	 *            下载地址
	 * @param destinationUri
	 *            路径
	 * @param j2WDownloadListener
	 *            事件
	 */
	int download(Uri downloadUri, Uri destinationUri, J2WDownloadListener j2WDownloadListener);

	/**
	 * 上传
	 *
	 * @param uploadUri
	 *            上传地址
	 * @param j2WUploadHeader
	 * @param j2WUploadBody
	 *            请求体
	 * @param j2WUploadListener
	 *            上传事件
	 * @return
	 */
	int upload(Uri uploadUri, J2WUploadHeader j2WUploadHeader, J2WUploadBody j2WUploadBody, J2WUploadListener j2WUploadListener);

	/**
	 * 上传
	 *
	 * @param uploadUri
	 *            上传地址
	 * @param j2WUploadHeaders
	 *            上传头信息
	 * @param j2WUploadBody
	 *            上传体
	 * @param j2WContentType
	 *            类型
	 * @param j2WUploadListener
	 *            事件
	 * @return
	 */
	int upload(Uri uploadUri, List<J2WUploadHeader> j2WUploadHeaders, J2WUploadBody j2WUploadBody, J2WContentType j2WContentType, J2WUploadListener j2WUploadListener);

	/**
	 * 上传
	 *
	 * @param uploadUri
	 *            上传地址
	 * @param file
	 *            文件
	 * @param j2WUploadListener
	 *            事件
	 * @return
	 */
	int upload(String uploadUri, File file, J2WUploadListener j2WUploadListener);

	/**
	 * 上传
	 *
	 * @param uploadUri
	 *            上传地址
	 * @param file
	 *            文件
	 * @param j2WUploadHeader
	 *            请求头信息
	 * @param j2WUploadListener
	 *            事件
	 * @return
	 */
	int upload(String uploadUri, File file, J2WUploadHeader j2WUploadHeader, J2WUploadListener j2WUploadListener);

	/**
	 * 上传
	 *
	 * @param uploadUri
	 *            上传地址
	 * @param j2WUploadHeaders
	 *            请求头信息 数组
	 * @param file
	 *            文件
	 * @param j2WUploadListener
	 *            事件
	 * @return
	 */
	int upload(String uploadUri, List<J2WUploadHeader> j2WUploadHeaders, File file, J2WUploadListener j2WUploadListener);
}
