package j2w.team.modules.download;

import java.nio.charset.Charset;

/**
 * @创建人 sky
 * @创建时间 15/4/8 下午2:28
 * @类描述 数据类型
 */
public class J2WContentType {

	public static final J2WContentType	APPLICATION_ATOM_XML		= create("application/atom+xml", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	APPLICATION_FORM_URLENCODED	= create("application/x-www-form-urlencoded", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	APPLICATION_JSON			= create("application/json", J2WDownloadConstants.UTF_8);

	public static final J2WContentType	APPLICATION_OCTET_STREAM	= create("application/octet-stream", (Charset) null);

	public static final J2WContentType	APPLICATION_SVG_XML			= create("application/svg+xml", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	APPLICATION_XHTML_XML		= create("application/xhtml+xml", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	APPLICATION_XML				= create("application/xml", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	MULTIPART_FORM_DATA			= create("multipart/form-data", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	TEXT_HTML					= create("text/html", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	TEXT_PLAIN					= create("text/plain", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	TEXT_XML					= create("text/xml", J2WDownloadConstants.ISO_8859_1);

	public static final J2WContentType	IMAGE_PNG					= create("image/png", (Charset) null);

	public static final J2WContentType	IMAGE_JPG					= create("image/jpeg", (Charset) null);

	public static final J2WContentType	WILDCARD					= create("*/*", (Charset) null);

	public static final J2WContentType	DEFAULT_TEXT				= TEXT_PLAIN;

	public static final J2WContentType	DEFAULT_FILE				= MULTIPART_FORM_DATA;

	/**
	 * 创建类型
	 * 
	 * @param mimeType
	 * @param charset
	 * @return
	 */
	public static J2WContentType create(final String mimeType, final Charset charset) {
		return new J2WContentType(mimeType, charset);
	}

	private final String	mimeType;

	private final Charset	charset;

	J2WContentType(final String mimeType, final Charset charset) {
		this.mimeType = mimeType;
		this.charset = charset;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public Charset getCharset() {
		return this.charset;
	}
}
