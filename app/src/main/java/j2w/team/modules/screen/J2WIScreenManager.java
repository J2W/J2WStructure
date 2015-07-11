package j2w.team.modules.screen;

import android.support.v4.app.FragmentActivity;

/**
 * Created by sky on 15/1/27.fragmentactivity管理器
 */
public interface J2WIScreenManager {

	/**
	 * 获取当前活动的activity
	 *
	 * @return
	 */
	public FragmentActivity currentActivity();

	/**
	 * 入栈
	 *
	 * @param activity
	 */
	public void pushActivity(FragmentActivity activity);

	/**
	 * 出栈
	 *
	 * @param activity
	 */
	public void popActivity(FragmentActivity activity);

	/**
	 * 退出堆栈中所有Activity, 当前的Activity除外
	 *
	 * @param clazz
	 *            当前活动窗口
	 */
	public void popAllActivityExceptMain(Class clazz);
}
