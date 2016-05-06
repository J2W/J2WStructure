package j2w.team.modules.http;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.ResponseBody;
import java.io.IOException;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * Created by sky on 15/2/24.异常捕获请求正文
 */
class ExceptionCatchingRequestBody extends ResponseBody {

	private final ResponseBody	delegate;

	private IOException			thrownException;

	ExceptionCatchingRequestBody(ResponseBody delegate) {
		this.delegate = delegate;
	}

	@Override public MediaType contentType() {
		return delegate.contentType();
	}

	@Override public long contentLength() {
		return delegate.contentLength();
	}

	@Override public BufferedSource source() {
		return Okio.buffer(new ForwardingSource(delegate.source()) {

			@Override public long read(Buffer sink, long byteCount) throws IOException {
				try {
					return super.read(sink, byteCount);
				} catch (IOException e) {
					thrownException = e;
					throw e;
				}
			}
		});
	}

	IOException getThrownException() {
		return thrownException;
	}

	boolean threwException() {
		return thrownException != null;
	}
}