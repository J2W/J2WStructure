package j2w.team.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.modules.structure.J2WStructureManage;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午7:39
 * @类描述 服务
 */
public abstract class J2WService<B extends J2WIBiz> extends Service {

	@Nullable @Override public IBinder onBind(Intent intent) {
		return null;
	}

	private J2WStructureIManage<B>	j2WStructureIManage;

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
		/** 初始化结构 **/
		j2WStructureIManage = new J2WStructureManage();
		/** 初始化业务 **/
		j2WStructureIManage.attachService(this);
		/** 初始化 **/
		initData();
	}

	public B biz() {
		return j2WStructureIManage.getBiz();
	}

	public <C> C biz(Class<C> service) {
		return j2WStructureIManage.getBiz(service, this.getClass().getInterfaces()[0]);
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public <N extends J2WIDisplay> N display(Class<N> eClass) {
		return j2WStructureIManage.display(eClass);
	}

	@Override public void onDestroy() {
		super.onDestroy();
		j2WStructureIManage.detachService(this);
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		running(intent, flags, startId);
		return START_NOT_STICKY;
	}

	public J2WStructureIManage getStructureManage() {
		return j2WStructureIManage;
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