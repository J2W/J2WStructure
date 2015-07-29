package com.squareup.picasso;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import j2w.team.common.log.L;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/2/19. Picasso工具
 */
public final class PicassoTools {

	/**
	 * 单例 *
	 */
	private static final PicassoTools	singleton	= new PicassoTools();

	/**
	 * 单例 *
	 */
	public static PicassoTools getInstance() {
		return singleton;
	}

	static Picasso			picasso				= null;

	static OkHttpDownloader	okHttpDownloader	= null;

	/**
	 * 初始化和获取
	 *
	 * @return
	 */
	public Picasso with() {
		if (picasso == null) {
			synchronized (Picasso.class) {
				if (picasso == null) {
					picasso = new Builder().build();
				}
			}
		}
		return picasso;
	}

	/**
	 * 加载数据源
	 *
	 * @param file
	 *            文件
	 * @return
	 */
	public RequestCreator load(File file) {
		return with().load(file);
	}

	/**
	 * 加载数据源
	 *
	 * @param path
	 *            路径
	 * @return
	 */
	public RequestCreator load(String path) {
		return with().load(path);
	}

	/**
	 * 加载数据源
	 *
	 * @param resourceId
	 *            资源文件
	 * @return
	 */
	public RequestCreator load(int resourceId) {
		return with().load(resourceId);
	}

	/**
	 * 加载数据源
	 *
	 * @param uri
	 * @return
	 */
	public RequestCreator load(Uri uri) {
		return with().load(uri);
	}

    /**
     * 是否打开debug 标记
     * @param bool
     */
    public void isOpenDebug(boolean bool){
        with().setIndicatorsEnabled(bool);
    }

	/**
	 * 清空内存缓存-不清空磁盘缓存
	 */
	public void clearCache() {
		if (picasso == null) {
			okHttpDownloader = null;
			return;
		}
		// 清空缓存-内存
		picasso.cache.clear();
		picasso.shutdown();
		okHttpDownloader = null;
		picasso = null;
	}

	public void deleteCahce(String uri) {
		// okHttpDownloader.getClient().
	}

	/**
	 * 清空磁盘缓存
	 */
	public void removeDiskCache() {
		clearCache();
		// 清空缓存-sdcard
		try {
			okHttpDownloader.getClient().getCache().evictAll();
		} catch (IOException e) {
			L.e("removeDiskCache() 异常");
		}
	}

    public static class Builder {

		/**
		 * 缓存路径 *
		 */
		final static String	CACHE_PATH			= ".j2w_base/img_cache/";

		/**
		 * 缓存大小 *
		 */
		final static int	DISK_CACHE_MAX_SIZE	= 200 * 1024 * 1024;

		private File		file;

		private void defaults() {
			// 创建文件
			if (file == null) {
				if (J2WAppUtil.isSDCardState()) {
					file = new File(Environment.getExternalStorageDirectory(), CACHE_PATH);
				} else {
					file = new File(J2WHelper.getInstance().getApplicationContext().getCacheDir(), CACHE_PATH);
				}
				if (!file.exists()) {
					file.mkdirs();
				}
				Log.i("PicassoTools", file.getPath());
			}
			// 创建okhttp下载器
			if (okHttpDownloader == null) {
				okHttpDownloader = new OkHttpDownloader(file, DISK_CACHE_MAX_SIZE);
			}
			// 创建picasso
			if (picasso == null) {
				Picasso.Builder builder = new Picasso.Builder(J2WHelper.getInstance().getApplicationContext());
				builder.downloader(okHttpDownloader);
				picasso = builder.build();
			}
		}

		public Picasso build() {
			defaults();
			return picasso;
		}
	}
}
