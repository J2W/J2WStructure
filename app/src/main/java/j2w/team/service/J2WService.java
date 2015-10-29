package j2w.team.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.modules.structure.J2WStructureManage;

/**
 * @创建人 sky
 * @创建时间 15/8/15 下午7:39
 * @类描述 服务
 */
public abstract class J2WService<D extends J2WIDisplay> extends Service {

	private J2WStructureIManage<D>	j2WStructureIManage;

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
		/** 初始化业务 **/
		j2WStructureIManage = new J2WStructureManage<>();
		j2WStructureIManage.attachService(this);
		/** 初始化 **/
		initData();
	}

	@Override public int onStartCommand(Intent intent, int flags, int startId) {
		running(intent, flags, startId);
		return START_NOT_STICKY;
	}

	/**
	 * 销毁
	 */
	@Override public void onDestroy() {
		super.onDestroy();
		if (j2WStructureIManage != null) {
			j2WStructureIManage.detachService(this);
			j2WStructureIManage = null;
		}
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
		return j2WStructureIManage.biz(biz);
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		j2WStructureIManage.getDisplay().initDisplay(J2WHelper.getInstance());
		return j2WStructureIManage.getDisplay();
	}

	public <N extends J2WIDisplay> N display(Class<N> eClass) {
		return j2WStructureIManage.display(eClass, J2WHelper.getInstance());
	}
}