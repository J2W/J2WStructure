package j2w.team.common.utils;

/**
 * Created by sky on 15/3/12.
 */
import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class J2WKeyboardUtils {

	/***
	 * 隐藏键盘
	 *
	 * @param acitivity
	 */
	public static void hideSoftInput(Activity acitivity) {
		InputMethodManager imm = (InputMethodManager) acitivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(acitivity.getWindow().getDecorView().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/***
	 * 显示键盘
	 *
	 * @param acitivity
	 * @param et
	 */
	public static void showSoftInput(Activity acitivity, EditText et) {
		if (et == null) return;
		et.requestFocus();
		InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(et, InputMethodManager.RESULT_UNCHANGED_SHOWN);

	}

	/***
	 * 延迟300毫秒-显示键盘 说明：延迟会解决 有时弹不出键盘的问题
	 *
	 * @param acitivity
	 * @param et
	 */
	public static void showSoftInputDelay(final Activity acitivity, final EditText et) {
		et.postDelayed(new Runnable() {

			@Override public void run() {
				showSoftInput(acitivity, et);
			}
		}, 300);
	}

	/**
	 * 判断是否显示
	 * @param activity
	 * @return
	 */
	public static boolean isSoftInput(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();
	}

	/**
	 * 判断键盘是否显示 如果是显示就隐藏
	 * 
	 * @param v
	 * @param event
	 * @return
	 */
	public static boolean isShouldHideInput(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] l = { 0, 0 };
			v.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
				// 点击EditText的事件，忽略它。
				return false;
			} else {
				return true;
			}
		}
		// 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
		return true;
	}
}