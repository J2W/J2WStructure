package j2w.team.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureModel;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午7:39
 * @类描述 服务
 */
public abstract class J2WService<B extends J2WIBiz> extends Service {

	J2WStructureModel j2WStructureModel;

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
		j2WStructureModel = new J2WStructureModel(this);
		J2WHelper.structureHelper().attach(j2WStructureModel);
		/** 初始化 **/
		initData();
	}

	public <D extends J2WIDisplay> D display(Class<D> eClass) {
		return j2WStructureModel.display(eClass);
	}

	public B biz() {
		return (B) j2WStructureModel.getJ2WProxy().proxy;
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		if (j2WStructureModel.getService().equals(service)) {
			return (C) j2WStructureModel.getJ2WProxy().proxy;
		}
		return J2WHelper.structureHelper().biz(service);
	}

	@Override public void onDestroy() {
		super.onDestroy();
		J2WHelper.structureHelper().detach(j2WStructureModel);
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		running(intent, flags, startId);
		return START_NOT_STICKY;
	}

}