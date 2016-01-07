package j2w.team.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import j2w.team.J2WHelper;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午7:39
 * @类描述 服务
 */
public abstract class J2WService extends Service {

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
	protected abstract void running(Intent intent, int flags, int startId);

	@Override public void onCreate() {
		super.onCreate();
		/** 初始化 **/
		initData();
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		running(intent, flags, startId);
		return START_NOT_STICKY;
	}

	/**
	 * 获取View
	 *
	 * @return
	 */
	public J2WActivity getView() {
		return J2WHelper.screenHelper().currentActivity();
	}

	/**
	 * 获取View
	 *
	 * @param name
	 * @param <T>
	 * @return
	 */
	public <T> T getView(String name) {
		return J2WHelper.screenHelper().getView(name);
	}

}