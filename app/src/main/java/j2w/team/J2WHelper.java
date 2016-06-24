package j2w.team;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import j2w.team.core.J2WIBiz;
import j2w.team.core.J2WICommonBiz;
import j2w.team.core.SynchronousExecutor;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.J2WModulesManage;
import j2w.team.modules.contact.J2WIContact;
import j2w.team.modules.download.J2WDownloadManager;
import j2w.team.modules.file.J2WFileCacheManage;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.methodProxy.J2WMethods;
import j2w.team.modules.screen.J2WScreenManager;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.modules.systemuihider.J2WSystemUiHider;
import j2w.team.modules.threadpool.J2WThreadPoolManager;
import j2w.team.modules.toast.J2WToast;

/**
 * Created by sky on 15/1/28. helper 管理
 */
public class J2WHelper {

	protected volatile static J2WModulesManage	mJ2WModulesManage	= null;

	/**
	 * 单例模式-初始化J2WHelper
	 *
	 * @param j2WModulesManage
	 *            Modules
	 */
	public static void with(J2WModulesManage j2WModulesManage) {
		mJ2WModulesManage = j2WModulesManage;
	}

	/**
	 * 获取管理
	 * 
	 * @param <M>
	 * @return
	 */
	protected static <M> M getManage() {
		return (M) mJ2WModulesManage;
	}

	/**
	 * 获取启动管理器
	 * 
	 * @param eClass
	 * @param <D>
	 * @return
	 */
	public static <D extends J2WIDisplay> D display(Class<D> eClass) {
		return structureHelper().display(eClass);
	}

	/**
	 * 获取业务
	 * 
	 * @param service
	 * @param <B>
	 * @return
	 */
	public static final <B extends J2WIBiz> B biz(Class<B> service) {
		return structureHelper().biz(service);
	}

	/**
	 * 业务是否存在
	 * 
	 * @param service
	 * @param <B>
	 * @return true 存在 false 不存在
	 */
	public static final <B extends J2WIBiz> boolean isExist(Class<B> service) {
		return structureHelper().isExist(service);
	}

	/**
	 * 获取业务
	 * 
	 * @param service
	 * @param <B>
	 * @return
	 */
	public static final <B extends J2WIBiz> List<B> bizList(Class<B> service) {
		return structureHelper().bizList(service);
	}

	/**
	 * 公用
	 * 
	 * @param service
	 * @param <B>
	 * @return
	 */
	public static final <B extends J2WICommonBiz> B common(Class<B> service) {
		return structureHelper().common(service);
	}

	/**
	 * 获取网络
	 * 
	 * @param httpClazz
	 * @param <H>
	 * @return
	 */
	public static final <H> H http(Class<H> httpClazz) {
		return structureHelper().http(httpClazz);

	}

	/**
	 * 获取实现类
	 * 
	 * @param implClazz
	 * @param <P>
	 * @return
	 */
	public static final <P> P impl(Class<P> implClazz) {
		return structureHelper().impl(implClazz);
	}

	/**
	 * 获取方法代理
	 * 
	 * @return
	 */
	public static final J2WMethods methodsProxy() {
		return mJ2WModulesManage.getJ2WMethods();
	}

	/**
	 * 获取全局上下文
	 *
	 * @return
	 */
	public static J2WApplication getInstance() {
		return mJ2WModulesManage.getJ2WApplication();
	}

	/**
	 * 获取EventBus
	 *
	 * @return
	 */
	public static final EventBus eventBus() {
		return mJ2WModulesManage.getBus();
	}

	/**
	 * 获取网络适配器
	 *
	 * @return
	 */
	public static final J2WRestAdapter httpAdapter() {
		return mJ2WModulesManage.getJ2WRestAdapter();
	}

	/**
	 * 结构管理器
	 * 
	 * @return 管理器
	 */
	public static final J2WStructureIManage structureHelper() {
		return mJ2WModulesManage.getJ2WStructureManage();
	}

	/**
	 * activity管理器
	 *
	 * @return 管理器
	 */
	public static final J2WScreenManager screenHelper() {
		return mJ2WModulesManage.getJ2WScreenManager();
	}

	/**
	 * J2WThreadPoolManager 线程池管理器
	 */

	public static final J2WThreadPoolManager threadPoolHelper() {
		return mJ2WModulesManage.getJ2WThreadPoolManager();
	}

	/**
	 * MainLooper 主线程中执行
	 *
	 * @return
	 */
	public static final SynchronousExecutor mainLooper() {
		return mJ2WModulesManage.getSynchronousExecutor();
	}

	/**
	 * 下载器工具
	 *
	 * @return
	 */
	public static final J2WDownloadManager downloader() {
		return mJ2WModulesManage.getJ2WDownloadManager();
	}

	/**
	 * 下载器工具 - 控制线程数量
	 *
	 * @return
	 */
	public static final J2WDownloadManager downloader(int threadPoolSize) {
		return mJ2WModulesManage.getJ2WDownloadManager(threadPoolSize);
	}

	/**
	 * 控制状态栏和标题栏
	 * 
	 * @param activity
	 * @param anchorView
	 * @param flags
	 * @return
	 */
	public static final J2WSystemUiHider systemHider(AppCompatActivity activity, View anchorView, int flags) {
		return mJ2WModulesManage.getJ2WSystemUiHider(activity, anchorView, flags);
	}

	/**
	 * Toast 提示信息
	 * 
	 * @return
	 */
	public static final J2WToast toast() {
		return mJ2WModulesManage.getJ2WToast();
	}

	/**
	 * 通讯录管理器
	 * 
	 * @return
	 */
	public static final J2WIContact contact() {
		return mJ2WModulesManage.getContactManage();
	}

	/**
	 * 提交Event
	 *
	 * @param object
	 */
	public static final void eventPost(final Object object) {
		boolean isMainLooper = isMainLooperThread();

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

	/**
	 * 判断是否是主线程
	 * 
	 * @return true 子线程 false 主线程
	 */
	public static final boolean isMainLooperThread() {
		return Looper.getMainLooper().getThread() != Thread.currentThread();
	}

	/**
	 * 文件缓存管理器
	 * 
	 * @return
	 */
	public static final J2WFileCacheManage fileCacheManage() {
		return mJ2WModulesManage.getJ2WFileCacheManage();
	}

}
