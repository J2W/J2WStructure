package j2w.team;

import android.os.Looper;

import com.squareup.picasso.PicassoTools;

import de.greenrobot.event.EventBus;
import j2w.team.common.utils.looper.SynchronousExecutor;
import j2w.team.modules.download.J2WDownloadManager;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.screen.J2WIScreenManager;
import j2w.team.modules.screen.J2WScreenManager;
import j2w.team.modules.threadpool.J2WThreadPoolManager;

/**
 * Created by sky on 15/1/28. helper 管理
 */
public class J2WHelper {

	/**
	 * 单例模式-握有application
	 */
	private volatile static J2WApplication	mJ2WApplication	= null;

	/**
	 * 单例模式 - 获取application
	 *
	 * @return
	 */
	public static J2WApplication getInstance() {
		return mJ2WApplication;
	}

	/**
	 * 单例模式-初始化application
	 *
	 * @param j2WApplication
	 *            系统上下文
	 */
	static void with(J2WApplication j2WApplication) {
		if (mJ2WApplication == null) {
			synchronized (J2WHelper.class) {
				if (mJ2WApplication == null) {
					mJ2WApplication = j2WApplication;
				}
			}
		}
	}

	/**
	 * 单例模式 - EventBus
	 */
	private volatile static EventBus bus	= EventBus.getDefault();

	/**
	 * 获取EventBus
	 *
	 * @return
	 */
	public static EventBus eventBus() {
		return bus;
	}

	/**
	 * 单例模式-握有 网络适配器
	 */
	private volatile static J2WRestAdapter	mJ2WRestAdapter;

	/**
	 * 获取网络适配器
	 *
	 * @return
	 */
	public static J2WRestAdapter httpAdapter() {
		return mJ2WRestAdapter;
	}

	/**
	 * 单例模式-初始化网络适配器
	 *
	 * @param j2WRestAdapter
	 *            网络适配器
	 */
	static void createRestAdapter(J2WRestAdapter j2WRestAdapter) {
		if (mJ2WRestAdapter == null) {
			synchronized (J2WHelper.class) {
				if (mJ2WRestAdapter == null) {
					mJ2WRestAdapter = j2WRestAdapter;
				}
			}
		}
	}

	/**
	 * 生成器
	 **/
	static final J2WRestAdapter.Builder	j2WRestAdapterBuilder	= new J2WRestAdapter.Builder();

	/**
	 * 网络适配器-生成器
	 *
	 * @return
	 */
	public static final J2WRestAdapter.Builder j2WRestBuilder() {
		return j2WRestAdapterBuilder;
	}

	/**
	 * activity管理器
	 */
	static final J2WScreenManager	j2WScreenManager	= new J2WScreenManager();

	/**
	 * activity管理器
	 *
	 * @return 管理器
	 */
	public static final J2WIScreenManager screenHelper() {
		return j2WScreenManager;
	}

	/**
	 * J2WThreadPoolManager 线程池管理器
	 */

	public static final J2WThreadPoolManager threadPoolHelper() {
		return J2WThreadPoolManager.getInstance();
	}

	/**
	 * MainLooper 主线程中执行
	 *
	 * @return
	 */
	public static final SynchronousExecutor mainLooper() {
		return SynchronousExecutor.getInstance();
	}

	/**
	 * 下载器工具
	 *
	 * @return
	 */
	public static final J2WDownloadManager downloader() {
		return J2WDownloadManager.getInstance();
	}

	/**
	 * Picasso工具
	 *
	 * @return picasso
	 */
	public static PicassoTools picassoHelper() {
		return PicassoTools.getInstance();
	}

	/**
	 * 提交Event
	 *
	 * @param object
	 */
	public static void eventPost(final Object object) {
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();

		if (isMainLooper) {
			mainLooper().execute(new Runnable() {

				@Override public void run() {
					eventBus().post(object);
				}
			});
		} else {
			eventBus().post(object);
		}
	}
}
