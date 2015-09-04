package j2w.team.modules.systemuihider;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

/**
 * @创建人 sky
 * @创建时间 15/9/5 上午1:11
 * @类描述 控制标题栏和状态栏
 */
public class J2WSystemUiHiderBase extends J2WSystemUiHider {

	private boolean	mVisible	= true;

	protected J2WSystemUiHiderBase(AppCompatActivity activity, View anchorView, int flags) {
		super(activity, anchorView, flags);
	}

	@Override public void setup() {
		if ((mFlags & FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES) == 0) {
			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	@Override public boolean isVisible() {
		return mVisible;
	}

	@Override public void hide() {
		if ((mFlags & FLAG_FULLSCREEN) != 0) {
			mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		mOnVisibilityChangeListener.onVisibilityChange(false);
		mVisible = false;
	}

	@Override public void show() {
		if ((mFlags & FLAG_FULLSCREEN) != 0) {
			mActivity.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
		mOnVisibilityChangeListener.onVisibilityChange(true);
		mVisible = true;
	}
}