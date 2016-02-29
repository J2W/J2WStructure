package j2w.team.receiver;

import android.content.BroadcastReceiver;

import j2w.team.J2WHelper;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/9/6 下午5:20
 * @类描述 广播接收器
 */
public abstract class J2WReceiver extends BroadcastReceiver {

	/**
	 * 获取View
	 * 
	 * @return
	 */
	public J2WActivity getView() {
		return J2WHelper.screenHelper().getCurrentActivity();
	}

}