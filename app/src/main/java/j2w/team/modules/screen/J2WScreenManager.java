package j2w.team.modules.screen;

import android.support.v4.app.FragmentActivity;
import android.support.v4.util.SimpleArrayMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import j2w.team.modules.log.L;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/1/26. fragmentactivity管理器
 */
public class J2WScreenManager implements J2WIScreenManager {

	/**
	 * FragmentActivity堆栈 单例模式
	 */
	private final Stack<FragmentActivity>			fragmentActivities;

	private final SimpleArrayMap<String, Object>	viewMap;

	public J2WScreenManager() {
		fragmentActivities = new Stack<>();
		viewMap = new SimpleArrayMap<>();
	}

	/**
	 * 获取当前活动的activity
	 *
	 * @return
	 */
	@Override public <T extends FragmentActivity> T currentActivity() {
		if (fragmentActivities.size() == 0) {
			L.i("FragmentActivity堆栈 size = 0");
			return null;
		}
		return (T) fragmentActivities.peek();
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

	@Override public synchronized void popNotEqualActivity(Class clazz) {
		for (Iterator<FragmentActivity> iter = fragmentActivities.iterator(); iter.hasNext();) {
			FragmentActivity item = iter.next();
			if (!item.getClass().equals(clazz)) {
				item.finish();
				iter.remove();
			}
		}
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

	/**
	 * 退出程序
	 */
	@Override public void logout() {
		popAllActivityExceptMain(null);
		if (fragmentActivities.size() < 1) {
			/** 清空内存缓存picasso **/
			L.i("清空内存缓存-J2WHelper.getPicassoHelper().clearCache()");
			J2WHelper.threadPoolHelper().finish();// 线程池
			fragmentActivities.clear();
			viewMap.clear();
		}
	}

	@Override public boolean isUI(String name) {

		if (viewMap.get(name) != null) {
			return true;
		}
		return false;
	}

	@Override public <T> T getView(String name) {
		return (T) viewMap.get(name);
	}

	@Override public void pushView(String name, Object object) {
		viewMap.put(name, object);
	}

	@Override public void popView(String name) {
		viewMap.remove(name);
	}
}
