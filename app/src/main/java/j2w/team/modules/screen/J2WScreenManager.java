package j2w.team.modules.screen;

import android.os.*;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Stack;

import j2w.team.common.log.L;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/1/26. fragmentactivity管理器
 */
public class J2WScreenManager implements J2WIScreenManager {

	/**
	 * FragmentActivity堆栈 单例模式
	 */
	private static final Stack<FragmentActivity>	fragmentActivities	= new Stack<>();

	/**
	 * 获取当前活动的activity
	 *
	 * @return
	 */
	@Override public FragmentActivity currentActivity() {
		if (fragmentActivities.size() == 0) {
			L.i("FragmentActivity堆栈 size = 0");
			return null;
		}
		FragmentActivity fragmentActivity = fragmentActivities.peek();
		return fragmentActivity;
	}

	/**
	 * 入栈
	 *
	 * @param activity
	 */
	@Override public void pushActivity(FragmentActivity activity) {
		if (activity == null) {
			L.e("传入的参数为空!");
			return;
		}
		fragmentActivities.add(activity);
	}

	/**
	 * 出栈
	 *
	 * @param activity
	 */
	@Override public void popActivity(FragmentActivity activity) {
		if (activity == null) {
			L.e("传入的参数为空!");
			return;
		}
		activity.finish();
		fragmentActivities.remove(activity);
		activity = null;
	}

	/**
	 * 退出堆栈中所有Activity, 当前的Activity除外
	 *
	 * @param clazz
	 *            当前活动窗口
	 */
	@Override public void popAllActivityExceptMain(Class clazz) {
		while (true) {
			FragmentActivity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(clazz)) {
				break;
			}

			popActivity(activity);
		}
	}

	/**
	 * 退出堆栈中所有activity ,栈顶activity除外
	 */
	@Override public void popAllActivityExceptionLoginActivity(Class login) {
		while (true) {
			int size = fragmentActivities.size();
			if (size == 0) {
				return;
			}
			FragmentActivity pop = fragmentActivities.pop();
			if (!pop.getClass().equals(login)) {
				pop.finish();
			}
		}
	}
}
