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

/**
 * @创建人 sky
 * @创建时间 15/9/6 下午5:20
 * @类描述 广播接收器
 */
public abstract class J2WReceiver<D extends J2WIDisplay> extends BroadcastReceiver {

	/** 显示调度对象 **/
	private D					display;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackBiz	= null;

	@Override public void onReceive(Context context, Intent intent) {
		attachBiz();
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
			obj = J2WBizUtils.createBiz(biz, this);
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}

	/**
	 * 业务初始化
	 */
	private synchronized final void attachBiz() {
		if (stackBiz == null) {
			stackBiz = new HashMap<>();
		}
		/** 创建业务类 **/
		if (display == null) {
			Class displayClass = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
			display = (D) J2WBizUtils.createDisplayNotView(displayClass, J2WHelper.getInstance());
		}
	}

	/**
	 * 业务分离
	 */
	private synchronized final void detachBiz() {
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
		if (display == null) {
			display.detach();
			display = null;
		}
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		return display;
	}

}