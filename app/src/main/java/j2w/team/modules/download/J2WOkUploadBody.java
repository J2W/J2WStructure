package j2w.team.modules.download;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.internal.Util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import j2w.team.common.log.L;
import j2w.team.modules.http.converter.GsonConverter;
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

	private final GsonConverter		gsonConverter;

	public J2WOkUploadBody(J2WUploadRequest j2WUploadRequest, J2WUploadListener listener) {
		this.file = j2WUploadRequest.getJ2WUploadBody().file;
		totalSize = file.length();
		this.j2WUploadRequest = j2WUploadRequest;
		this.listener = listener;
		this.gsonConverter = new GsonConverter();

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
		RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM).addPart(headers, this)
				.addPart(headers, gsonConverter.toBody(j2WUploadRequest.getJ2WUploadBody().body, j2WUploadRequest.getJ2WUploadBody().body.getClass())).build();
		return requestBody;
	}
}
