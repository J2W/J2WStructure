package j2w.team.modules.toast;

import android.os.Looper;
import android.widget.Toast;

import j2w.team.J2WHelper;

/**
 * Created by wungko on 15/3/17. 弱交互Tost 消息弹窗 Update by skyJC on 15/8/05
 */
public class J2WToast {

	private Toast mToast = null;

	/**
	 * 简单Toast 消息弹出
	 * 
	 * @param msg
	 */
	public void show(final String msg) {
		// 判断是否在主线程
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();

		if (isMainLooper) {
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					showToast(msg, Toast.LENGTH_SHORT);
				}
			});
		} else {
			showToast(msg, Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 简单Toast 消息弹出
	 * 
	 * @param msg
	 */
	public void show(final int msg) {
		// 判断是否在主线程
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();

		if (isMainLooper) {
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					showToast(J2WHelper.getInstance().getString(msg), Toast.LENGTH_SHORT);
				}
			});
		} else {
			showToast(J2WHelper.getInstance().getString(msg), Toast.LENGTH_SHORT);
		}
	}

	/**
	 * 简单Toast 消息弹出
	 * 
	 * @param msg
	 */
	public void show(final String msg,final int duration) {
		// 判断是否在主线程
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();

		if (isMainLooper) {
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					showToast(msg, duration);
				}
			});
		} else {
			showToast(msg, duration);
		}
	}

	/**
	 * 简单Toast 消息弹出
	 * 
	 * @param msg
	 */
	public void show(final int msg,final int duration) {
		// 判断是否在主线程
		boolean isMainLooper = Looper.getMainLooper().getThread() != Thread.currentThread();

		if (isMainLooper) {
			J2WHelper.mainLooper().execute(new Runnable() {

				@Override public void run() {
					showToast(J2WHelper.getInstance().getString(msg), duration);
				}
			});
		} else {
			showToast(J2WHelper.getInstance().getString(msg), duration);
		}
	}

	/**
	 * 弹出提示
	 * 
	 * @param text
	 * @param duration
	 */
	protected void showToast(String text, int duration) {
		if (mToast == null) {
			mToast = Toast.makeText(J2WHelper.getInstance(), text, duration);
		} else {
			mToast.setText(text);
			mToast.setDuration(duration);
		}

		mToast.show();
	}

	public void clear() {
		if (mToast != null) {
			mToast.cancel();
			mToast = null;
		}
	}
}
