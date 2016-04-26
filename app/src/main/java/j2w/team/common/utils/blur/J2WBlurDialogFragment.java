package j2w.team.common.utils.blur;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import j2w.team.core.J2WIBiz;
import j2w.team.view.J2WDialogFragment;

/**
 * crate by JC
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class J2WBlurDialogFragment<B extends J2WIBiz> extends J2WDialogFragment<B> {

	public static final String	BUNDLE_KEY_DOWN_SCALE_FACTOR	= "bundle_key_down_scale_factor";

	public static final String	BUNDLE_KEY_BLUR_RADIUS			= "bundle_key_blur_radius";

	private static final String	TAG								= J2WBlurDialogFragment.class.getSimpleName();

	private BlurDialogEngine	mBlurEngine;

	private boolean				mDebugEnable;

	public J2WBlurDialogFragment() {
		mDebugEnable = false;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBlurEngine = new BlurDialogEngine(getActivity());
		mBlurEngine.debug(mDebugEnable);
		mBlurEngine.setBlurActionBar(!isBlurActionBar());
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

	protected boolean isBlurActionBar() {
		return false;
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
