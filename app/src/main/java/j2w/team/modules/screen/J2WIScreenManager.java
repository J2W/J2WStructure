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
	<T extends FragmentActivity> T currentActivity();

	/**
	 * 入栈
	 *
	 * @param activity
	 */
	void pushActivity(FragmentActivity activity);

	/**
	 * 出栈
	 *
	 * @param activity
	 */
	void popActivity(FragmentActivity activity);

	/**
	 * 退出堆栈中所有Activity, 当前的Activity除外
	 *
	 * @param clazz
	 *            当前活动窗口
	 */
	void popAllActivityExceptMain(Class clazz);

	/**
	 * 退出堆栈中所有activity,登陆activity 除掉
	 */
	void popAllActivityExceptionLoginActivity(Class logoin);

	/**
	 * 退出程序
	 */
	void logout();

	/**
	 * 判断是否存在
	 * 
	 * @param name
	 * @return true 存在 false 不存在
	 */
	boolean isUI(String name);

	<T> T getView(String name);

	void pushView(String name, Object object);

	void popView(String name);

}
