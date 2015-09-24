package j2w.team.modules.download;

import com.squareup.okhttp.Headers;

import java.io.File;
import java.util.List;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/4/8 上午11:10
 * @类描述 头部信息 - 仅仅支持一种 还需要完善
 */
public class J2WUploadBody<T> {

	public static final String		CONTENT_DISPOSITION	= "Content-Disposition";

	public String					headerName;									// key

	public String					headerValue;									// 头信息value

	public File						file;											// 文件

	private final Headers.Builder	headers				= new Headers.Builder();

	public List<J2WFromData>		j2wFromData;								// 表单

	public Headers getHeader() {
		if (isDisposition()) {
			final StringBuilder buffer = new StringBuilder();
			buffer.append("form-data; name=\"");
			buffer.append(this.headerValue);
			buffer.append("\"");
			if (file != null && !J2WCheckUtils.isEmpty(file.getName())) {
				buffer.append("; filename=\"");
				buffer.append(this.file.getName());
				buffer.append("\"");
			}
			headerValue = buffer.toString();
		}
		headers.add(headerName, headerValue);
		return headers.build();
	}

	private boolean isDisposition() {
		return CONTENT_DISPOSITION.equals(headerName) ? true : false;
	}

}
