package j2w.team.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午7:39
 * @类描述 服务
 */
public abstract class J2WService<D extends J2WIDisplay> extends Service {

	/** 显示调度对象 **/
	private D					display		= null;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackBiz	= null;

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 初始化数据
	 *
	 * 数据
	 */
	protected abstract void initData();

	/**
	 * 运行
	 */
	protected abstract void running();

	@Override public void onCreate() {
		super.onCreate();
		/** 初始化业务 **/
		attachBiz();
		/** 初始化 **/
		initData();
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		running();
		return START_NOT_STICKY;
	}

	/**
	 * 销毁
	 */
	@Override public void onDestroy() {
		super.onDestroy();
		detachBiz();
	}

	/**
	 * 获取业务
	 *
	 * @param biz
	 *            泛型
	 * @param <B>
	 * @return
	 */
	public <B extends J2WIBiz> B biz(Class<B> biz) {
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
		Object obj = stackBiz.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createBiz(biz, this, display);
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}

	/**
	 * 业务初始化
	 */
	synchronized final void attachBiz() {
		if (stackBiz == null) {
			stackBiz = new HashMap<>();
		}
		/** 创建业务类 **/
		if (display == null) {
			display = J2WBizUtils.createDisplay(this);
		}
	}

	/**
	 * 业务分离
	 */
	synchronized final void detachBiz() {
		for (Object b : stackBiz.values()) {
			J2WIBiz j2WIBiz = (J2WIBiz) b;
			if (j2WIBiz != null) {
				j2WIBiz.detach();
			}
		}
		if (stackBiz != null) {
			stackBiz.clear();
			stackBiz = null;
		}
		display = null;
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		return display;
	}

	public <E extends J2WIDisplay> E display(Class<E> e) {
		return (E) display;
	}
}