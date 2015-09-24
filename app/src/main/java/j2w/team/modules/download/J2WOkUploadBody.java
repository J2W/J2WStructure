package j2w.team.modules.download;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import j2w.team.common.log.L;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * @创建人 sky
 * @创建时间 15/4/7 上午9:36
 * @类描述 Okhttp 请求体
 */
public class J2WOkUploadBody extends RequestBody {

	private static final int		SEGMENT_SIZE	= 2048; // okio.Segment.SIZE

	private final File				file;					// 上传的问题件

	private final J2WUploadListener	listener;				// 事件

	private long					totalSize;				// 待上传文件总大小

	private final J2WUploadRequest	j2WUploadRequest;		// 请求

	public J2WOkUploadBody(J2WUploadRequest j2WUploadRequest, J2WUploadListener listener) {
		this.file = j2WUploadRequest.getJ2WUploadBody().file;
		if(file != null){
			totalSize = file.length();
		}
		this.j2WUploadRequest = j2WUploadRequest;
		this.listener = listener;
	}

	@Override public long contentLength() {
		return totalSize;
	}

	@Override public MediaType contentType() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(j2WUploadRequest.getJ2WContentType().getMimeType());
		if (j2WUploadRequest.getJ2WContentType().getCharset() != null) {
			buffer.append("; charset=");
			buffer.append(j2WUploadRequest.getJ2WContentType().getCharset());
		}
		return MediaType.parse(buffer.toString());
	}

	@Override public void writeTo(BufferedSink sink) throws IOException {
		Source source = null;
		try {

			source = Okio.source(file);
			long total = 0;
			long read;

			while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
				if (j2WUploadRequest.isCanceled()) {
					L.i("取消的请求Id " + j2WUploadRequest.getRequestId());
					return;
				}
				total += read;
				sink.flush();
				int progres = (int) (100 * total / totalSize);
				this.listener.onUploadProgress(j2WUploadRequest.getRequestId(), totalSize, total, progres);// 进度
			}
		} finally {
			Util.closeQuietly(source);
		}
	}

	/**
	 * 建造
	 * 
	 * @return
	 */
	public RequestBody build() {
		// 请求头信息
		Headers headers = j2WUploadRequest.getJ2WUploadBody().getHeader();
		MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
		if(file != null){
			multipartBuilder.addPart(headers, this);
		}
		List<J2WFromData> j2WHeadersBeans = j2WUploadRequest.getJ2WUploadBody().j2wFromData;
		if (j2WHeadersBeans != null && j2WHeadersBeans.size() > 0) {
			int count = j2WUploadRequest.getJ2WUploadBody().j2wFromData.size();
			for (int i = 0; i < count; i++) {
				multipartBuilder.addFormDataPart(j2WHeadersBeans.get(i).key,j2WHeadersBeans.get(i).value);
			}
		}
		RequestBody requestBody = multipartBuilder.build();
		return requestBody;
	}
}
