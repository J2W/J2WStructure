package j2w.team.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
 * @创建时间 15/9/6 下午5:20
 * @类描述 广播接收器
 */
public abstract class J2WReceiver<D extends J2WIDisplay> extends BroadcastReceiver {

	private J2WStructureIManage<D>	j2WStructureIManage;

	@Override public void onReceive(Context context, Intent intent) {
		j2WStructureIManage = new J2WStructureManage<>();
		j2WStructureIManage.attachReceiver(this);
	}

	protected void detach(){
		j2WStructureIManage.detachReceiver(this);
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