package j2w.team.modules.http.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import j2w.team.modules.log.L;
import j2w.team.common.utils.J2WGsonUtils;

/**
 * Created by sky on 15/2/23.
 */
public class GsonConverter implements J2WConverter {

	protected Gson		gson;

	protected Charset	charset;

	protected MediaType	mediaType;

	public GsonConverter() {
		this(new GsonBuilder().create());
	}

	public GsonConverter(Gson gson) {
		this(gson, Charset.forName("UTF-8"));
	}

	public GsonConverter(Gson gson, Charset charset) {
		if (gson == null) throw new NullPointerException("gson == null");
		if (charset == null) throw new NullPointerException("charset == null");
		this.gson = gson;
		this.charset = charset;
		this.mediaType = MediaType.parse("application/json; charset=" + charset.name());
	}

	@Override public Object fromBody(ResponseBody body, Type type) throws IOException {
		return J2WGsonUtils.readBody(gson, charset, body, type);
	}

	@Override public RequestBody toBody(Object object, Type type) {
		String json = gson.toJson(object, type);
		L.tag("J2W-HTTP");
		L.i("请求体:mediaType :" + mediaType + ", json : " + json);
		return RequestBody.create(mediaType, json);
	}
}
