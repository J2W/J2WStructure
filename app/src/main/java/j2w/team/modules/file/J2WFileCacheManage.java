package j2w.team.modules.file;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import j2w.team.J2WHelper;
import j2w.team.modules.log.L;

/**
 * @创建人 sky
 * @创建时间 16/5/25 下午8:12
 * @类描述
 */
public class J2WFileCacheManage {

	private final String	ENCODING	= "utf8";

	private final String	FILE_SUFFIX	= ".txt";

	public String			BASE_CACHE_PATH;

	private final String	TAG			= "CACHE_UTILS";

	public void configureCache(Context context) {
		BASE_CACHE_PATH = context.getApplicationInfo().dataDir + File.separator + "files" + File.separator + "CacheUtils";

		if (new File(BASE_CACHE_PATH).mkdirs()) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.d(TAG, BASE_CACHE_PATH + " created.");
			}
		}
	}

	private String pathForCacheEntry(String name) {
		return BASE_CACHE_PATH + File.separator + name + FILE_SUFFIX;
	}

	private <T> List<Map<String, T>> dataMapsFromJson(String dataString) {
		if (StringUtils.isEmpty(dataString)) return new ArrayList<Map<String, T>>();

		try {
			Type listType = new TypeToken<List<Map<String, T>>>() {}.getType();
			return J2WGsonHelper.buildGson().fromJson(dataString, listType);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.d(TAG, "failed to read json" + e.toString());
			}
			return new ArrayList<Map<String, T>>();
		}
	}

	private <T> String dataMapstoJson(List<Map<String, T>> dataMaps) {
		try {
			return J2WGsonHelper.buildGson().toJson(dataMaps);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.d(TAG, "failed to write json" + e.toString());
			}
			return "[]";
		}
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @return the content of the file, null if there is no such file
	 */
	public String readFile(String fileName) {
		try {
			return IOUtils.toString(new FileInputStream(pathForCacheEntry(fileName)), ENCODING);
		} catch (IOException e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.d(TAG, "read cache file failure" + e.toString());
			}
			return null;
		}
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @param fileContent
	 *            the content of the file
	 */
	public void writeFile(String fileName, String fileContent) {
		try {
			IOUtils.write(fileContent, new FileOutputStream(pathForCacheEntry(fileName)), ENCODING);
		} catch (IOException e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.d(TAG, "write cache file failure" + e.toString());
			}
		}
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @param dataMaps
	 *            the map list you want to store
	 */
	public <T> void writeDataMapsFile(String fileName, List<Map<String, T>> dataMaps) {
		writeFile(fileName, dataMapstoJson(dataMaps));
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @return the map list you previous stored, an empty {@link List} will be
	 *         returned if there is no such file
	 */
	public <T> List<Map<String, T>> readDataMapsFile(String fileName) {
		return dataMapsFromJson(readFile(fileName));
	}

	private <T> T objectFromJson(String dataString, Type t) {
		try {
			return J2WGsonHelper.buildGson().fromJson(dataString, t);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.v(TAG, "failed to read json" + e.toString());
			}
			return null;
		}
	}

	private <T> T objectFromJson(String dataString, Class<T> t) {
		try {
			return J2WGsonHelper.buildGson().fromJson(dataString, t);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.v(TAG, "failed to read json" + e.toString());
			}
			return null;
		}
	}

	private <T> String objectToJson(T o) {
		try {
			return J2WGsonHelper.buildGson().toJson(o);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				Log.v(TAG, "failed to write json" + e.toString());
			}
			return null;
		}
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @param object
	 *            the object you want to store
	 * @param <T>
	 *            a class extends from {@link Object}
	 */
	public <T> void writeObjectFile(String fileName, T object) {
		writeFile(fileName, objectToJson(object));
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @return the {@link T} type object you previous stored
	 */
	public <T> T readObjectFile(String fileName, Type type) {
		return objectFromJson(readFile(fileName), type);
	}

	public <T> T readObjectFile(String fileName, Class<T> clazz) {
		return objectFromJson(readFile(fileName), clazz);
	}

	private <T> Map<String, T> dataMapFromJson(String dataString) {
		if (StringUtils.isEmpty(dataString)) return new HashMap<String, T>();

		try {
			Type t = new TypeToken<Map<String, T>>() {}.getType();
			return J2WGsonHelper.buildGson().fromJson(dataString, t);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				L.v(TAG, "failed to read json" + e.toString());
			}
			return new HashMap<String, T>();
		}
	}

	private <T> String dataMaptoJson(Map<String, T> dataMap) {
		try {
			return J2WGsonHelper.buildGson().toJson(dataMap);
		} catch (Exception e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				L.v(TAG, "failed to write json" + e.toString());
			}
			return "{}";
		}
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @param dataMap
	 *            the map data you want to store
	 */
	public <T> void writeDataMapFile(String fileName, Map<String, T> dataMap) {
		writeFile(fileName, dataMaptoJson(dataMap));
	}

	/**
	 * @param fileName
	 *            the name of the file
	 * @return the map data you previous stored
	 */
	public <T> Map<String, T> readDataMapFile(String fileName) {
		return dataMapFromJson(readFile(fileName));
	}

	/**
	 * delete the file with fileName
	 * 
	 * @param fileName
	 *            the name of the file
	 */
	public void deleteFile(String fileName) {
		try {
			FileUtils.forceDelete(new File(pathForCacheEntry(fileName)));
		} catch (IOException e) {
			if (J2WHelper.getInstance().isLogOpen()) {
				L.v(TAG, "not delete " + e.toString());
			}
		}
	}

	/**
	 * check if there is a cache file with fileName
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return true if the file exits, false otherwise
	 */
	public boolean hasCache(String fileName) {
		return new File(pathForCacheEntry(fileName)).exists();
	}
}
