package j2w.team.modules.screen;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import j2w.team.J2WHelper;

/**
 * @创建人 sky
 * @创建时间 16/2/27
 * @类描述
 */
public class J2WScreenHolder {

	private FragmentActivity activity;

	private boolean		isLanding	= false;

	private boolean		isRunning	= true;

	private String		activityName;

	public J2WScreenHolder(FragmentActivity activity, boolean isLanding) {
		this.activity = activity;
		this.activityName = activity.getClass().getSimpleName();
		this.isLanding = isLanding;
		log(" 创建.");
	}

	public void pause() {
		this.isRunning = false;
		log(" 暂停.");
	}

	public void resume() {
		this.isRunning = true;
		log(" 运行.");
	}

	public FragmentActivity getActivity() {
		return activity;
	}

	public boolean isLanding() {
		return isLanding;
	}

	public void setLanding(boolean isLanding) {
		this.isLanding = isLanding;
		log(isLanding ? " 定位!" : " 没有定位!");
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void finish() {
		activity.finish();
		log(" 关闭.");
	}

	public void removed() {
		log(" 关闭.");
	}

	private void log(String message) {
		if (J2WHelper.getInstance().isLogOpen()) {
			Log.i("J2WActivityManager", activityName + message);
		}
	}

}
