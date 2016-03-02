package j2w.team.modules.appconfig;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;

import j2w.team.modules.log.L;
import j2w.team.common.utils.J2WCheckUtils;

/**
 * Created by sky on 9/10/14. 临时信息存储
 */
public abstract class J2WProperties {

	/**
	 * 获取TAG标记 *
	 */
	public abstract String initTag();

	/**
	 * 从那里打开文件*
	 */
	public abstract int initType();

	/**
	 * 获取文件路径 *
	 */
	private File					propertyFilePath;

	/**
	 * 编码格式
	 */
	private static final String		DEFAULT_CODE			= "utf-8";

	private static final String		DEFAUT_ANNOTATION_VALUE	= "";

	/**
	 * 默认文件名 *
	 */
	private String					mPropertiesFileName;

	/**
	 * 默认文件后缀名 *
	 */
	private static final String		EXTENSION				= ".properties";

	/**
	 * 配置文件工具类 *
	 */
	private java.util.Properties	mProperties				= new java.util.Properties();

	/**
	 * 类型 *
	 */
	public static final int			OPEN_TYPE_ASSETS		= 1;							// 打开asset文件夹下的文件

	public static final int			OPEN_TYPE_DATA			= 2;							// 打开应用程序data文件夹下的文件

	/**
	 * 成功回调
	 */
	private PropertyCallback		propertyCallback;

	private Context					context;

	private J2WProperties() {}

	/**
	 * 构造函数
	 */
	public J2WProperties(Context context) {
		this(context, "config");
	}

	public J2WProperties(Context context, String propertiesFileName) {
		this.context = context;
		propertyFilePath = getPropertyFilePath();
		mPropertiesFileName = propertiesFileName;
		switch (initType()) {
			case OPEN_TYPE_ASSETS:
				Resources mResources = context.getResources();
				openAssetProperties(mResources);
				break;
			case OPEN_TYPE_DATA:
				openDataProperties();
				break;
		}
	}

	/**
	 * 获取存储路径
	 * 
	 * @return
	 */
	public File getPropertyFilePath() {
		return context.getFilesDir();// 存储到/DATA/DATA/
	}

	/**
	 * asset文件夹下的文件 *
	 */
	private void openAssetProperties(Resources resources) {
		synchronized (mProperties) {
			try {
				InputStream inputStream = resources.getAssets().open(mPropertiesFileName + EXTENSION);
				L.tag(initTag());
				L.i("openProperties() 路径:" + mPropertiesFileName + EXTENSION);
				mProperties.load(inputStream);
				loadPropertiesValues();
			} catch (IOException e) {
				L.tag(initTag());
				L.e("openAssetProperties失败:" + e.toString());
			}
		}
	}

	/**
	 * data文件夹下的文件 *
	 */
	private void openDataProperties() {
		synchronized (mProperties) {
			InputStream in = null;
			try {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(mPropertiesFileName);
				stringBuilder.append(EXTENSION);
				File file = new File(propertyFilePath, stringBuilder.toString());
				/** 处理配置文件的变化 **/
				if (!file.exists()) {
					L.tag("J2WProperties create file ");
				}
				in = new BufferedInputStream(new FileInputStream(file));
				L.tag(initTag());
				L.i("openDataProperties() 路径:" + propertyFilePath + "/" + stringBuilder.toString());
				mProperties.load(in);
			} catch (Exception e) {
				L.tag(initTag());
				L.e("openDataProperties失败:" + e.toString());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException ex) {
						L.e("" + ex);
					}
				}
			}

			loadPropertiesValues();
		}
	}

	public void setPropertyCallback(PropertyCallback propertyCallback) {
		this.propertyCallback = propertyCallback;
	}

	/**
	 * 所有返回类型
	 */
	private int getInt(String key, int defaultValue) {
		String value = null;
		try {
			value = mProperties.getProperty(key);
			if (J2WCheckUtils.isEmpty(value)) {
				return 0;
			}
			return Integer.parseInt(mProperties.getProperty(key));
		} catch (Exception e) {
			L.tag(initTag());
			L.e("%s 解析失败, 解析类型 %s, 解析数据 %s ", key, "int", value);
			return defaultValue;
		}
	}

	private long getLong(String key, long defaultValue) {
		String value = null;
		try {
			value = mProperties.getProperty(key);
			if (J2WCheckUtils.isEmpty(value)) {
				return 0;
			}
			return Long.parseLong(mProperties.getProperty(key));
		} catch (Exception e) {
			L.tag(initTag());
			L.e("%s 解析失败, 解析类型 %s, 解析数据 %s ", key, "int", value);
			return defaultValue;
		}
	}

	private float getFloat(String key, float defaultValue) {
		String value = null;
		try {
			value = mProperties.getProperty(key);
			if (J2WCheckUtils.isEmpty(value)) {
				return 0;
			}
			return Float.parseFloat(mProperties.getProperty(key));
		} catch (Exception e) {
			L.tag(initTag());
			L.e("%s 解析失败, 解析类型 %s, 解析数据 %s ", key, "float", value);
			return defaultValue;
		}
	}

	private double getDouble(String key, double defaultValue) {
		String value = null;
		try {
			value = mProperties.getProperty(key);
			if (J2WCheckUtils.isEmpty(value)) {
				return 0;
			}
			return Double.parseDouble(mProperties.getProperty(key));
		} catch (Exception e) {
			L.tag(initTag());
			L.e("%s 解析失败, 解析类型 %s, 解析数据 %s ", key, "double", value);
			return defaultValue;
		}
	}

	private boolean getBoolean(String key, boolean defaultValue) {
		String value = null;
		try {
			value = mProperties.getProperty(key);
			if (J2WCheckUtils.isEmpty(value)) {
				return false;
			}
			return Boolean.parseBoolean(mProperties.getProperty(key));
		} catch (Exception e) {
			L.tag(initTag());
			L.e("%s 解析失败, 解析类型 %s, 解析数据 %s ", key, "boolean", value);
			return defaultValue;
		}
	}

	private String getString(String key, String defaultValue) {
		String result = null;

		switch (initType()) {
			case OPEN_TYPE_ASSETS:
				try {
					result = new String(mProperties.getProperty(key, defaultValue).getBytes("ISO-8859-1"), DEFAULT_CODE);
				} catch (UnsupportedEncodingException e) {
					L.tag(initTag());
					L.e("%s 解析失败, 解析类型  %s ", key, "String");
					return defaultValue;
				}
				break;
			case OPEN_TYPE_DATA:
				result = mProperties.getProperty(key, defaultValue);
				break;
		}

		return result;
	}

	/**
	 * 解析文件 - 赋值 * 获取所有的属性名 并赋值
	 */
	private void loadPropertiesValues() {
		synchronized (mProperties) {
			L.tag(initTag());
			L.i("loadPropertiesValues()-加载所有的值");
			Class<? extends J2WProperties> thisClass = this.getClass();
			Field[] fields = thisClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Property.class)) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Property annotation = field.getAnnotation(Property.class);

					if (annotation.value().equals(DEFAUT_ANNOTATION_VALUE)) {
						setFieldValue(field, fieldName);
					} else {
						setFieldValue(field, annotation.value());
					}
				}
			}
		}
	}

	/**
	 * 所有属性写入到properties里
	 */
	private void writePropertiesValues() {
		synchronized (mProperties) {
			L.tag(initTag());
			L.i("writePropertiesValues()-写入所有的值");
			Class<? extends J2WProperties> thisClass = this.getClass();
			Field[] fields = thisClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Property.class)) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Property annotation = field.getAnnotation(Property.class);
					if (annotation.value().equals(DEFAUT_ANNOTATION_VALUE)) {
						try {
							mProperties.put(fieldName, field.get(this) == null ? "" : String.valueOf(field.get(this)));
						} catch (IllegalAccessException e) {
							L.e("Properties写入错误:" + e.toString());
						}
					} else {
						try {

							mProperties.put(annotation.value(), field.get(this) == null ? "" : String.valueOf(field.get(this)));
						} catch (IllegalAccessException e) {
							L.e("Properties写入错误:" + e.toString());
						}
					}
				}
			}
		}
	}

	/**
	 * 所有属性写入到properties里
	 */
	private void writeDefaultPropertiesValues() {
		synchronized (mProperties) {
			L.tag(initTag());
			L.i("writePropertiesValues()-写入所有的值");
			Class<? extends J2WProperties> thisClass = this.getClass();
			Field[] fields = thisClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.isAnnotationPresent(Property.class)) {
					field.setAccessible(true);
					String fieldName = field.getName();
					Property annotation = field.getAnnotation(Property.class);
					if (annotation.value().equals(DEFAUT_ANNOTATION_VALUE)) {
						try {
							mProperties.put(fieldName, "");
							setFieldDefaultValue(field, fieldName);
						} catch (Exception e) {
							L.e("Properties写入错误:" + e.toString());
						}
					} else {
						try {
							mProperties.put(annotation.value(), "");
							setFieldDefaultValue(field, annotation.value());
						} catch (Exception e) {
							L.e("Properties写入错误:" + e.toString());
						}
					}
				}
			}
		}
	}

	/**
	 * 设置属性值 *
	 */
	private void setFieldValue(Field field, String propertiesName) {
		Object value = getPropertyValue(field.getType(), propertiesName);
		if (value == null) {
			return;
		}
		try {
			field.set(this, value);
		} catch (Exception e) {
			L.tag(initTag());
			L.e("setFieldValue失败 ， 属性名 %s 文件名 %s", field.getName(), propertiesName);
		}
	}

	/**
	 * 设置属性值 *
	 */
	private void setFieldDefaultValue(Field field, String propertiesName) {
		Object value = getPropertyDefaultValue(field.getType());
		if (value == null) {
			return;
		}
		try {
			field.set(this, value);
		} catch (Exception e) {
			L.tag(initTag());
			L.e("setFieldValue失败 ， 属性名 %s 文件名 %s", field.getName(), propertiesName);
		}
	}

	/**
	 * 获取类型 *
	 */
	private Object getPropertyValue(Class<?> clazz, String key) {
		if (clazz == String.class) {
			return getString(key, "");
		} else if (clazz == float.class || clazz == Float.class) {
			return getFloat(key, 0);
		} else if (clazz == double.class || clazz == Double.class) {
			return getDouble(key, 0);
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return getBoolean(key, false);
		} else if (clazz == int.class || clazz == Integer.class) {
			return getInt(key, 0);
		} else if (clazz == long.class || clazz == Long.class) {
			return getLong(key, 0);
		} else {
			return null;
		}
	}

	/**
	 * 获取类型 *
	 */
	private Object getPropertyDefaultValue(Class<?> clazz) {
		if (clazz == String.class) {
			return "";
		} else if (clazz == float.class || clazz == Float.class) {
			return 0;
		} else if (clazz == double.class || clazz == Double.class) {
			return 0;
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return false;
		} else if (clazz == int.class || clazz == Integer.class) {
			return 0;
		} else if (clazz == long.class || clazz == Long.class) {
			return 0;
		} else {
			return null;
		}
	}

	/**
	 * 提交
	 */
	public void commit() {
		synchronized (mProperties) {
			OutputStream out = null;
			try {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(mPropertiesFileName);
				stringBuilder.append(EXTENSION);
				File file = new File(propertyFilePath, stringBuilder.toString());
				if (!file.exists()) {
					file.createNewFile();
				}
				synchronized (mProperties) {
					out = new BufferedOutputStream(new FileOutputStream(file));
					writePropertiesValues();
					mProperties.store(out, "");
				}
				if (propertyCallback != null) {
					propertyCallback.onSuccess();
				}
			} catch (FileNotFoundException ex) {
				L.e("" + ex);
			} catch (IOException ex) {
				L.e("" + ex);
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException ex) {
						L.e("" + ex);
					}
				}
			}
		}
	}

	/**
	 * 提交
	 * 
	 * @param callback
	 */
	public void commit(PropertyCallback callback) {
		synchronized (mProperties) {
			OutputStream out = null;
			try {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(mPropertiesFileName);
				stringBuilder.append(EXTENSION);
				File file = new File(propertyFilePath, stringBuilder.toString());
				if (!file.exists()) {
					file.createNewFile();
				}
				synchronized (mProperties) {
					out = new BufferedOutputStream(new FileOutputStream(file));
					writePropertiesValues();
					mProperties.store(out, "");
				}
				if (callback != null) {
					callback.onSuccess();
				}

			} catch (FileNotFoundException ex) {
				L.e("" + ex);
			} catch (IOException ex) {
				L.e("" + ex);
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException ex) {
						L.e("" + ex);
					}
				}
			}
		}
	}

	/**
	 * 删除
	 */
	public void delete() {
		synchronized (mProperties) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(mPropertiesFileName);
			stringBuilder.append(EXTENSION);
			File file = new File(propertyFilePath, stringBuilder.toString());
			if (!file.exists()) {
				return;
			}
			// 删除
			file.delete();
		}
	}

	/**
	 * 清空文件内容
	 */
	public void clear() {
		synchronized (mProperties) {
			OutputStream out = null;
			try {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(mPropertiesFileName);
				stringBuilder.append(EXTENSION);
				File file = new File(propertyFilePath, stringBuilder.toString());
				if (!file.exists()) {
					return;
				}
				synchronized (mProperties) {
					out = new BufferedOutputStream(new FileOutputStream(file));
					writeDefaultPropertiesValues();
					mProperties.store(out, "");
				}
			} catch (FileNotFoundException ex) {
				L.e("" + ex);
			} catch (IOException ex) {
				L.e("" + ex);
			} finally {
				if (null != out) {
					try {
						out.close();
					} catch (IOException ex) {
						L.e("" + ex);
					}
				}
			}
		}
	}
}
