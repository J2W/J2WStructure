package j2w.team.view;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * @创建人 sky
 * @创建时间 15/8/8 下午9:49
 * @类描述 接口
 */
public interface J2WIDialogFragment {

	/**
	 * 显示碎片
	 *
	 * @return
	 */
	DialogFragment show(FragmentManager fragmentManager);

	DialogFragment show(FragmentManager fragmentManager, int mRequestCode);

	DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment);

	DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode);

	DialogFragment show(FragmentManager fragmentManager, Activity activity);

	DialogFragment show(FragmentManager fragmentManager, Activity activity, int mRequestCode);

	/**
	 * 显示碎片-不保存activity状态
	 *
	 * @return
	 */
	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager);

	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, int mRequestCode);

	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment);

	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode);

	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity);

	DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity, int mRequestCode);
}