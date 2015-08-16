package j2w.team.modules;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import de.greenrobot.event.EventBus;
import j2w.team.J2WApplication;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.modules.contact.ContactManage;
import j2w.team.common.utils.looper.SynchronousExecutor;
import j2w.team.modules.download.J2WDownloadManager;
import j2w.team.modules.fresco.FrescoTools;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.screen.J2WScreenManager;
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

	private J2WRestAdapter.Builder	j2WRestAdapterBuilder;	// 网络适配器编辑器

	private J2WScreenManager		j2WScreenManager;		// Activity堆栈管理

	private J2WThreadPoolManager	j2WThreadPoolManager;	// 线程池管理

	private SynchronousExecutor		synchronousExecutor;	// 主线程

	private J2WDownloadManager		j2WDownloadManager;	// 下载和上传管理

	private J2WRestAdapter			mJ2WRestAdapter;		// 网络适配器

	private FrescoTools				frescoTools;			// 图片架构

	private ImagePipelineConfig		imagePipelineConfig;	// frescoTools图片架构配置

	private J2WToast				j2WToast;				// 提示信息

	private ContactManage			contactManage;			// 通讯录

	public J2WModulesManage(J2WApplication j2WApplication) {
		this.mJ2WApplication = J2WCheckUtils.checkNotNull(j2WApplication, "Application初始化失败");
	}

	public J2WApplication getJ2WApplication() {
		return this.mJ2WApplication;
	}

	public void setJ2WRestAdapter(J2WRestAdapter j2WRestAdapter) {
		this.mJ2WRestAdapter = j2WRestAdapter;
	}

	public void setImagePipelineConfig(ImagePipelineConfig imagePipelineConfig) {
		this.imagePipelineConfig = imagePipelineConfig;
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

	public J2WRestAdapter.Builder getJ2WRestAdapterBuilder() {
		if (j2WRestAdapterBuilder == null) {
			j2WRestAdapterBuilder = new J2WRestAdapter.Builder();
		}
		return j2WRestAdapterBuilder;
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

	public FrescoTools getFrescoTools() {
		if (frescoTools == null) {
			frescoTools = new FrescoTools(mJ2WApplication, imagePipelineConfig);
		}
		return frescoTools;
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
}