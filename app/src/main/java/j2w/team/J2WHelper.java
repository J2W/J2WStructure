package j2w.team;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.PicassoTools;

import de.greenrobot.event.EventBus;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.J2WIBiz;
import j2w.team.core.SynchronousExecutor;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.J2WModulesManage;
import j2w.team.modules.contact.J2WIContact;
import j2w.team.modules.download.J2WDownloadManager;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.methodProxy.J2WMethods;
import j2w.team.modules.screen.J2WIScreenManager;
import j2w.team.modules.systemuihider.J2WSystemUiHider;
import j2w.team.modules.threadpool.J2WThreadPoolManager;
import j2w.team.modules.toast.J2WToast;
import j2w.team.view.J2WActivity;

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
	 * 创建接口代理
	 * 
	 * @param service
	 *            接口
	 * @param <T>
	 * @return
	 */
	public static final <T> T createBiz(Class<T> service) {
		return methodsProxy().createBiz(service);
	}

	/**
	 * 创建UI接口代理
	 * 
	 * @param service
	 * @param <T>
	 * @return
	 */
	public static final <T> T createUI(Class<T> service) {
		return methodsProxy().createUI(service);
	}

	/**
	 * Display接口代理
	 * 
	 * @param service
	 * @param <T>
	 * @return
	 */
	public static final <T> T createDisplay(Class<T> service) {
		return methodsProxy().createDisplay(service);
	}

	public static  <D extends J2WIDisplay> D display(Class<D> eClass) {
		J2WActivity j2WActivity = mJ2WModulesManage.getJ2WScreenManager().currentActivity();
		return (D) j2WActivity.display(eClass);
	}

	public static final <B> B BIZ(Class<B> service) {
		J2WCheckUtils.checkNotNull(service, "请指定业务接口～");
		Object obj = mJ2WModulesManage.getStatck().get(service.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.createBiz(service);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			mJ2WModulesManage.getStatck().put(service.getSimpleName(), obj);
		}
		return (B) obj;
	}

	/**
	 * 获取视图
	 * 
	 * @param viewName
	 * @param <T>
	 * @return
	 */
	public static final <T> T UI(String viewName) {
		return screenHelper().getView(viewName);
	}

	/**
	 * 判断视图存在不存在
	 * 
	 * @param viewName
	 * @return true 存在 false 不存在
	 */
	public static final boolean isUI(String viewName) {
		return screenHelper().isUI(viewName);
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
	 * activity管理器
	 *
	 * @return 管理器
	 */
	public static final J2WIScreenManager screenHelper() {
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
	 * Picasso工具
	 *
	 * @return picasso
	 */
	public static final PicassoTools picassoHelper() {
		return mJ2WModulesManage.getPicassoTools();
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
}
