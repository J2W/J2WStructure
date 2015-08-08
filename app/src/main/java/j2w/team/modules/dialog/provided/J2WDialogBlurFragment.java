package j2w.team.modules.dialog.provided;

import android.content.DialogInterface;
import android.os.Bundle;

import j2w.team.biz.J2WIDisplay;
import j2w.team.modules.dialog.blur.BlurDialogEngine;
import j2w.team.view.J2WDialogFragment;

/**
 * @创建人 sky
 * @创建时间 15/6/24 上午10:34
 * @类描述 背景模糊弹框
 */
public abstract class J2WDialogBlurFragment<T extends J2WIDisplay> extends J2WDialogFragment<T> {

	private static final String	TAG								= J2WDialogBlurFragment.class.getSimpleName();

	public static final String	BUNDLE_KEY_DOWN_SCALE_FACTOR	= "bundle_key_down_scale_factor";

	public static final String	BUNDLE_KEY_BLUR_RADIUS			= "bundle_key_blur_radius";

	private BlurDialogEngine mBlurEngine;

	private boolean				mDebugEnable;

	public J2WDialogBlurFragment() {
		mDebugEnable = false;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBlurEngine = new BlurDialogEngine(getActivity());
		mBlurEngine.debug(mDebugEnable);

		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey(BUNDLE_KEY_BLUR_RADIUS)) {
				mBlurEngine.setBlurRadius(args.getInt(BUNDLE_KEY_BLUR_RADIUS));
			}
			if (args.containsKey(BUNDLE_KEY_DOWN_SCALE_FACTOR)) {
				mBlurEngine.setDownScaleFactor(args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR));
			}
		}
	}

	@Override public void onResume() {
		super.onResume();
		mBlurEngine.onResume(getRetainInstance());
	}

	@Override public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
		mBlurEngine.onDismiss();
	}

	@Override public void onDestroy() {
		super.onDestroy();
		mBlurEngine.onDestroy();
	}

	@Override public void onDestroyView() {
		if (getDialog() != null) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	public void debug(boolean debugEnable) {
		mDebugEnable = debugEnable;
	}

	public void setDownScaleFactor(float factor) {
		if (factor > 0) {
			mBlurEngine.setDownScaleFactor(factor);
		}
	}

	public void setBlurRadius(int radius) {
		if (radius > 0) {
			mBlurEngine.setBlurRadius(radius);
		}
	}
}
