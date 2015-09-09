package j2w.team.modules.dialog.provided;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import j2w.team.display.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.modules.dialog.iface.INegativeButtonDialogListener;
import j2w.team.modules.dialog.iface.INeutralButtonDialogListener;
import j2w.team.modules.dialog.iface.IPositiveButtonDialogListener;
import j2w.team.structure.R;
import j2w.team.view.J2WBuilder;
import j2w.team.view.J2WDialogFragment;

/**
 * @创建人 sky
 * @创建时间 15/8/8 下午3:31
 * @类描述 简单弹框
 */
public class J2WDialogSimpleFragment extends J2WDialogFragment<J2WIDisplay> implements View.OnClickListener, DialogInterface.OnShowListener {

	protected final static String	ARG_MESSAGE			= "message";

	protected final static String	ARG_TITLE			= "title";

	protected final static String	ARG_POSITIVE_BUTTON	= "positive_button";

	protected final static String	ARG_NEGATIVE_BUTTON	= "negative_button";

	protected final static String	ARG_NEUTRAL_BUTTON	= "neutral_button";

	public static J2WIDialogFragment getInstance(String title, String msg, String positiveBtn) {
		return getInstance(title, msg, positiveBtn, "", "");
	}

	public static J2WIDialogFragment getInstance(String title, String msg, String positiveBtn, String negativeBtn) {
		return getInstance(title, msg, positiveBtn, negativeBtn, "");
	}

	public static J2WIDialogFragment getInstance(String title, String msg, String positiveBtn, String negativeBtn, String neutralBtn) {
		J2WDialogSimpleFragment j2WDialogSimpleFragment = new J2WDialogSimpleFragment();

		Bundle bundle = new Bundle();
		bundle.putString(ARG_TITLE, title);
		bundle.putString(ARG_MESSAGE, msg);
		bundle.putString(ARG_POSITIVE_BUTTON, positiveBtn);
		bundle.putString(ARG_NEGATIVE_BUTTON, negativeBtn);
		bundle.putString(ARG_NEUTRAL_BUTTON, neutralBtn);
		j2WDialogSimpleFragment.setArguments(bundle);
		return j2WDialogSimpleFragment;
	}

	/** 标题值 **/
	private CharSequence	mTitle;

	/** 内容数据 **/
	private CharSequence	mMessage;

	/** 正面-按钮值 **/
	private CharSequence	mPositiveButtonText;

	/** 负面-按钮值 **/
	private CharSequence	mNegativeButtonText;

	/** 中性-按钮值 **/
	private CharSequence	mNeutralButtonText;

	TextView				vTitle;

	TextView				vMessage;

	Button					vPositiveButton;

	Button					vNegativeButton;

	Button					vNeutralButton;

	Button					vPositiveButtonStacked;

	Button					vNegativeButtonStacked;

	Button					vNeutralButtonStacked;

	View					vButtonsDefault;

	View					vButtonsStacked;

	/**
	 * 创建Dialog
	 *
	 * @param savedInstanceState
	 * @return
	 */
	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		// 获取参数
		Bundle args = getArguments();
		// 创建对话框
		Dialog dialog = new Dialog(getActivity(), getJ2WStyle());
		// 获取参数-设置是否可取消
		if (args != null) {
			dialog.setCanceledOnTouchOutside(isCancel());
		}
		// 调用show()时触发事件
		dialog.setOnShowListener(this);
		return dialog;
	}

	@Override protected J2WBuilder build(J2WBuilder initialJ2WBuilder) {
		initialJ2WBuilder.layoutId(R.layout.j2w_dialog);
		return initialJ2WBuilder;
	}

	@Override protected void initData(Bundle savedInstanceState) {

		vTitle = ButterKnife.findById(getView(), R.id.j2w_title);
		vMessage = ButterKnife.findById(getView(), R.id.j2w_message);
		vPositiveButton = ButterKnife.findById(getView(), R.id.j2w_button_positive);
		vNegativeButton = ButterKnife.findById(getView(), R.id.j2w_button_negative);
		vNeutralButton = ButterKnife.findById(getView(), R.id.j2w_button_neutral);
		vPositiveButtonStacked = ButterKnife.findById(getView(), R.id.j2w_button_positive_stacked);
		vNegativeButtonStacked = ButterKnife.findById(getView(), R.id.j2w_button_negative_stacked);
		vNeutralButtonStacked = ButterKnife.findById(getView(), R.id.j2w_button_neutral_stacked);
		vButtonsDefault = ButterKnife.findById(getView(), R.id.j2w_buttons_default);
		vButtonsStacked = ButterKnife.findById(getView(), R.id.j2w_buttons_stacked);

		mTitle = getArguments().getString(ARG_TITLE);
		mMessage = getArguments().getString(ARG_MESSAGE);
		mPositiveButtonText = getArguments().getString(ARG_POSITIVE_BUTTON);
		mNegativeButtonText = getArguments().getString(ARG_NEGATIVE_BUTTON);
		mNeutralButtonText = getArguments().getString(ARG_NEUTRAL_BUTTON);

		// 设置标题值
		set(vTitle, mTitle);
		// 设置内容值
		set(vMessage, mMessage);
		// 设置填充标题和消息
		setPaddingOfTitleAndMessage(vTitle, vMessage);

		/**
		 * 判断字体长度 显示不同样式
		 */
		if (shouldStackButtons()) {
			set(vPositiveButtonStacked, mPositiveButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			set(vNegativeButtonStacked, mNegativeButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			set(vNeutralButtonStacked, mNeutralButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			vButtonsDefault.setVisibility(View.GONE);
			vButtonsStacked.setVisibility(View.VISIBLE);
		} else {
			set(vPositiveButton, mPositiveButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			set(vNegativeButton, mNegativeButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			set(vNeutralButton, mNeutralButtonText, !J2WCheckUtils.isEmpty(mPositiveButtonText));
			vButtonsDefault.setVisibility(View.VISIBLE);
			vButtonsStacked.setVisibility(View.GONE);
		}
		// 判断按钮值
		if (TextUtils.isEmpty(mPositiveButtonText) && TextUtils.isEmpty(mNegativeButtonText) && TextUtils.isEmpty(mNeutralButtonText)) {
			vButtonsDefault.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置填充标题和消息
	 */
	private void setPaddingOfTitleAndMessage(TextView vTitle, TextView vMessage) {
		int grid6 = getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_title);
		int grid4 = getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_msg);
		if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mMessage)) {
			vTitle.setPadding(grid6, grid6, grid6, grid4);
			vMessage.setPadding(grid6, 0, grid6, grid4);
		} else if (TextUtils.isEmpty(mTitle)) {
			vMessage.setPadding(grid6, grid4, grid6, grid4);
		} else if (TextUtils.isEmpty(mMessage)) {
			vTitle.setPadding(grid6, grid6, grid6, grid4);
		}
	}

	private void set(Button button, CharSequence text, boolean listener) {
		set(button, text);
		if (listener) {
			button.setOnClickListener(this);
		}
	}

	private void set(TextView textView, CharSequence text) {
		if (text != null) {
			textView.setText(text);
		} else {
			textView.setVisibility(View.GONE);
		}
	}

	/**
	 * 判断按钮字体长度
	 *
	 * @return
	 */
	private boolean shouldStackButtons() {
		return shouldStackButton(mPositiveButtonText) || shouldStackButton(mNegativeButtonText) || shouldStackButton(mNeutralButtonText);
	}

	/**
	 * 判断按钮的字体长度
	 *
	 * @param text
	 * @return
	 */
	private boolean shouldStackButton(CharSequence text) {
		final int MAX_BUTTON_CHARS = 12;
		return text != null && text.length() > MAX_BUTTON_CHARS;
	}

	@Override public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.j2w_button_positive || i == R.id.j2w_button_positive_stacked) {
			for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
				listener.onPositiveButtonClicked(mRequestCode);
			}
		} else if (i == R.id.j2w_button_negative || i == R.id.j2w_button_negative_stacked) {
			for (INegativeButtonDialogListener listener : getNegativeButtonDialogListeners()) {
				listener.onNegativeButtonClicked(mRequestCode);
			}
		} else if (i == R.id.j2w_button_neutral || i == R.id.j2w_button_neutral_stacked) {
			for (INeutralButtonDialogListener listener : getNeutralButtonDialogListeners()) {
				listener.onNeutralButtonClicked(mRequestCode);
			}
		}
		dismiss();

	}

	protected List<IPositiveButtonDialogListener> getPositiveButtonDialogListeners() {
		return getDialogListeners(IPositiveButtonDialogListener.class);
	}

	protected List<INegativeButtonDialogListener> getNegativeButtonDialogListeners() {
		return getDialogListeners(INegativeButtonDialogListener.class);
	}

	protected List<INeutralButtonDialogListener> getNeutralButtonDialogListeners() {
		return getDialogListeners(INeutralButtonDialogListener.class);
	}

	@Override public void onShow(DialogInterface dialog) {
		if (getView() != null) {
			ScrollView vMessageScrollView = ButterKnife.findById(getView(), R.id.j2w_message_scrollview);
			ListView vListView = ButterKnife.findById(getView(), R.id.j2w_list);
			FrameLayout vCustomViewNoScrollView = ButterKnife.findById(getView(), R.id.j2w_custom);
			boolean customViewNoScrollViewScrollable = false;
			if (vCustomViewNoScrollView.getChildCount() > 0) {
				View firstChild = vCustomViewNoScrollView.getChildAt(0);
				if (firstChild instanceof ViewGroup) {
					customViewNoScrollViewScrollable = isScrollable((ViewGroup) firstChild);
				}
			}
			boolean listViewScrollable = isScrollable(vListView);
			boolean messageScrollable = isScrollable(vMessageScrollView);
			boolean scrollable = listViewScrollable || messageScrollable || customViewNoScrollViewScrollable;
			modifyButtonsBasedOnScrollableContent(scrollable);
		}
	}

	/**
	 * 是否滚动
	 *
	 * @param listView
	 *            列表
	 * @return
	 */
	private boolean isScrollable(ViewGroup listView) {
		int totalHeight = 0;
		for (int i = 0; i < listView.getChildCount(); i++) {
			totalHeight += listView.getChildAt(i).getMeasuredHeight();
		}
		return listView.getMeasuredHeight() < totalHeight;
	}

	/**
	 * 如果内容是可滚动.
	 */
	private void modifyButtonsBasedOnScrollableContent(boolean scrollable) {
		if (getView() == null) {
			return;
		}
		View vButtonDivider = getView().findViewById(R.id.j2w_button_divider);
		View vButtonsBottomSpace = getView().findViewById(R.id.j2w_buttons_bottom_space);
		View vDefaultButtons = getView().findViewById(R.id.j2w_buttons_default);
		View vStackedButtons = getView().findViewById(R.id.j2w_buttons_stacked);
		if (vDefaultButtons.getVisibility() == View.GONE && vStackedButtons.getVisibility() == View.GONE) {
			vButtonDivider.setVisibility(View.GONE);
			vButtonsBottomSpace.setVisibility(View.GONE);
		} else if (scrollable) {
			vButtonDivider.setVisibility(View.VISIBLE);
			vButtonsBottomSpace.setVisibility(View.GONE);
		} else {
			vButtonDivider.setVisibility(View.GONE);
			vButtonsBottomSpace.setVisibility(View.VISIBLE);
		}
	}
}