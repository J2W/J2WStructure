package j2w.team.modules.dialog.provided;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.structure.R;
import j2w.team.view.J2WBuilder;
import j2w.team.view.J2WDialogFragment;

/**
 * @创建人 sky
 * @创建时间 15/8/8 下午3:31
 * @类描述 进度条弹框
 */
public class J2WDialogProgressFragment extends J2WDialogFragment<J2WIBiz> {

	protected final static String	ARG_MESSAGE	= "message";

	protected final static String	ARG_CANCEL	= "cancel";

	public static J2WIDialogFragment getInstance(String msg) {
		return getInstance(msg, true);
	}

	public static J2WIDialogFragment getInstance(String msg, boolean cancel) {
		J2WDialogProgressFragment j2WDialogSimpleFragment = new J2WDialogProgressFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ARG_MESSAGE, msg);
		bundle.putBoolean(ARG_CANCEL, cancel);
		j2WDialogSimpleFragment.setArguments(bundle);
		return j2WDialogSimpleFragment;
	}

	/** 内容数据 **/
	private CharSequence	mMessage;

	TextView				vMessage;

	boolean					cancel;

	@Override protected J2WBuilder build(J2WBuilder initialJ2WBuilder) {
		initialJ2WBuilder.layoutId(R.layout.j2w_dialog_progress);
		return initialJ2WBuilder;
	}

	@Override protected void initData(Bundle savedInstanceState) {
		vMessage = ButterKnife.findById(getView(), R.id.j2w_message);
		mMessage = getArguments().getString(ARG_MESSAGE);
		cancel = getArguments().getBoolean(ARG_CANCEL);
		setCancelable(cancel);
		// 设置内容值
		if (!J2WCheckUtils.isEmpty(mMessage)) {
			vMessage.setText(mMessage);
		}
	}
	@Override public void onStart() {
		super.onStart();
		if (getDialog() == null) {
			return;
		}
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
}