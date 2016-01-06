package j2w.team.modules;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.squareup.picasso.PicassoTools;

import de.greenrobot.event.EventBus;
import j2w.team.J2WApplication;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.modules.methodProxy.J2WMethods;
import j2w.team.modules.contact.ContactManage;
import j2w.team.common.utils.looper.SynchronousExecutor;
import j2w.team.modules.download.J2WDownloadManager;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.log.L;
import j2w.team.modules.screen.J2WScreenManager;
import j2w.team.modules.systemuihider.J2WSystemUiHider;
import j2w.team.modules.threadpool.J2WThreadPoolManager;
import j2w.team.modules.toast.J2WToast;

/**
 * @创建人 sky
 * @创建时间 15/8/5 下午3:17
 * @类描述 Modules管理
 */
public class J2WModulesManage {

	private final J2WApplication	mJ2WApplication;		// 全局上下文

	private EventBus				bus;					// 事件总线

	private J2WScreenManager		j2WScreenManager;		// Activity堆栈管理

	private J2WThreadPoolManager	j2WThreadPoolManager;	// 线程池管理

	private SynchronousExecutor		synchronousExecutor;	// 主线程

	private J2WDownloadManager		j2WDownloadManager;	// 下载和上传管理

	private J2WRestAdapter			mJ2WRestAdapter;		// 网络适配器

	private J2WToast				j2WToast;				// 提示信息

	private ContactManage			contactManage;			// 通讯录

	private PicassoTools			picassoTools;			// 图片加载器

	private J2WSystemUiHider		j2WSystemUiHider;		// 标题栏和状态栏控制

	private L.DebugTree				debugTree;				// 打印信息

	private J2WMethods				j2WMethods;			// 方法代理

	public J2WModulesManage(J2WApplication j2WApplication) {
		this.mJ2WApplication = J2WCheckUtils.checkNotNull(j2WApplication, "Application初始化失败");
	}

	public J2WApplication getJ2WApplication() {
		return this.mJ2WApplication;
	}

	public void initJ2WRestAdapter(J2WRestAdapter j2WRestAdapter) {
		this.mJ2WRestAdapter = j2WRestAdapter;
	}

	public void initLog(boolean logOpen) {
		if (logOpen) {
			if (debugTree == null) {
				debugTree = new L.DebugTree();
			}
			L.plant(debugTree);
		}
	}

	public void initMehtodProxy(J2WMethods methodInterceptor) {
		j2WMethods = methodInterceptor;
	}

	public J2WMethods getJ2WMethods() {
		return j2WMethods;
	}

	public J2WRestAdapter getJ2WRestAdapter() {
		return this.mJ2WRestAdapter;
	}

	public EventBus getBus() {
		if (bus == null) {
			bus = EventBus.getDefault();
		}
		return bus;
	}

	public J2WScreenManager getJ2WScreenManager() {
		if (j2WScreenManager == null) {
			j2WScreenManager = new J2WScreenManager();
		}
		return j2WScreenManager;
	}

	public J2WThreadPoolManager getJ2WThreadPoolManager() {
		if (j2WThreadPoolManager == null) {
			j2WThreadPoolManager = new J2WThreadPoolManager();
		}
		return j2WThreadPoolManager;
	}

	public SynchronousExecutor getSynchronousExecutor() {
		if (synchronousExecutor == null) {
			synchronousExecutor = new SynchronousExecutor();
		}
		return synchronousExecutor;
	}

	public J2WDownloadManager getJ2WDownloadManager() {
		if (j2WDownloadManager == null) {
			j2WDownloadManager = new J2WDownloadManager();
		}
		return j2WDownloadManager;
	}

	public J2WDownloadManager getJ2WDownloadManager(int threadPoolSize) {
		if (j2WDownloadManager == null) {
			j2WDownloadManager = new J2WDownloadManager(threadPoolSize);
		}
		return j2WDownloadManager;
	}

	public J2WToast getJ2WToast() {
		if (j2WToast == null) {
			j2WToast = new J2WToast(mJ2WApplication);
		}
		return j2WToast;
	}

	public ContactManage getContactManage() {
		if (contactManage == null) {
			contactManage = new ContactManage(mJ2WApplication);
		}
		return contactManage;
	}

	public PicassoTools getPicassoTools() {
		if (picassoTools == null) {
			picassoTools = new PicassoTools();
		}
		return picassoTools;
	}

	public J2WSystemUiHider getJ2WSystemUiHider(AppCompatActivity activity, View anchorView, int flags) {
		if (j2WSystemUiHider == null) {
			j2WSystemUiHider = J2WSystemUiHider.getInstance(activity, anchorView, flags);
		}
		return j2WSystemUiHider;
	}
}