package j2w.team.modules.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import j2w.team.common.log.L;
import j2w.team.modules.dialog.iface.IDialogCancelListener;
import j2w.team.structure.R;

/**
 * Created by sky on 15/2/28. dialog 基类
 */
public abstract class J2WDialogFragment extends DialogFragment implements DialogInterface.OnShowListener {

	/** 请求编码 **/
	protected int	mRequestCode;

	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		L.tag("J2WDialogFragment");
		L.i("onCreateDialog()");
		// 获取参数
		Bundle args = getArguments();
		// 创建对话框
		Dialog dialog = new Dialog(getActivity(), getJ2WStyle());
		// 获取参数-设置是否可取消
		if (args != null) {
			dialog.setCanceledOnTouchOutside(args.getBoolean(J2WDialogBuilder.ARG_CANCELABLE_ON_TOUCH_OUTSIDE));
		}
		// 调用show()时触发事件
		dialog.setOnShowListener(this);
		return dialog;
	}

	public int getJ2WStyle() {
		return R.style.J2W_Dialog;
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.tag("J2WDialogFragment");
		L.i("onCreateView()");
		Builder builder = new Builder(getActivity(), inflater, container);
		return build(builder).create();
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.tag("J2WDialogFragment");
		L.i("onActivityCreated()");
		// 获取指定碎片
		final Fragment targetFragment = getTargetFragment();
		// 如果有指定碎片 从指定碎片里获取请求码，反之既然
		if (targetFragment != null) {
			mRequestCode = getTargetRequestCode();
		} else {
			Bundle args = getArguments();
			if (args != null) {
				mRequestCode = args.getInt(J2WDialogBuilder.ARG_REQUEST_CODE, 0);
			}
		}
	}

	public int getRequestCode() {
		return mRequestCode;
	}

	@Override public void onDestroyView() {
		L.tag("J2WDialogFragment");
		L.i("onDestroyView()");
		// 销毁
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	/** 定制对话框 **/
	protected abstract Builder build(Builder initialBuilder);

	@Override public void onShow(DialogInterface dialog) {
		L.tag("J2WDialogFragment");
		L.i("onShow()");
		if (getView() != null) {
			ScrollView vMessageScrollView = (ScrollView) getView().findViewById(R.id.j2w_message_scrollview);
			ListView vListView = (ListView) getView().findViewById(R.id.j2w_list);
			FrameLayout vCustomViewNoScrollView = (FrameLayout) getView().findViewById(R.id.j2w_custom);
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
	 * 取消
	 *
	 * @param dialog
	 */
	@Override public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		for (IDialogCancelListener listener : getCancelListeners()) {
			listener.onCancelled(mRequestCode);
		}
	}

	/**
	 * 获取取消的所有事件
	 *
	 * @return
	 */
	protected List<IDialogCancelListener> getCancelListeners() {
		return getDialogListeners(IDialogCancelListener.class);
	}

	/**
	 * 获取某种类型的所有侦听器
	 */
	protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
		final Fragment targetFragment = getTargetFragment();
		List<T> listeners = new ArrayList<T>(2);
		if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
			listeners.add((T) targetFragment);
		}
		if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
			listeners.add((T) getActivity());
		}
		return Collections.unmodifiableList(listeners);
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

	/**
	 * 自定义对话框生成器
	 */
	protected static class Builder {

		/** 上下文 **/
		private final Context					mContext;

		/** ViewGroup **/
		private final ViewGroup					mContainer;

		/** 布局加载器 **/
		private final LayoutInflater			mInflater;

		/** 标题值 **/
		private CharSequence					mTitle		= null;

		private int								mTitleColor	= 0;

		/** 正面-按钮值 **/
		private CharSequence					mPositiveButtonText;

		/** 正面-按钮事件 **/
		private View.OnClickListener			mPositiveButtonListener;

		/** 负面-按钮值 **/
		private CharSequence					mNegativeButtonText;

		/** 负面-按钮事件 **/
		private View.OnClickListener			mNegativeButtonListener;

		/** 中性-按钮值 **/
		private CharSequence					mNeutralButtonText;

		/** 中性-按钮事件 **/
		private View.OnClickListener			mNeutralButtonListener;

		/** 内容数据 **/
		private CharSequence					mMessage;

		/** 主要布局 **/
		private View							mCustomView;

		/** 列表适配器 **/
		private ListAdapter						mListAdapter;

		/** 列表选中id **/
		private int								mListCheckedItemIdx;

		/** 选择模式 **/
		private int								mChoiceMode;

		/** 列表选中id集合 **/
		private int[]							mListCheckedItemMultipleIds;

		/** 列表选中事件 **/
		private AdapterView.OnItemClickListener	mOnItemClickListener;

		/**
		 * 构造器
		 *
		 * @param context
		 * @param inflater
		 * @param container
		 */
		public Builder(Context context, LayoutInflater inflater, ViewGroup container) {
			this.mContext = context;
			this.mContainer = container;
			this.mInflater = inflater;
		}

		/**
		 * 获取布局加载器
		 *
		 * @return
		 */
		public LayoutInflater getLayoutInflater() {
			return mInflater;
		}

		/**
		 * 设置标题
		 *
		 * @param titleId
		 * @return
		 */
		public Builder setTitle(int titleId) {
			this.mTitle = mContext.getText(titleId);
			return this;
		}

		/**
		 * 设置标题颜色
		 * 
		 * @param mTitleColor
		 * @return
		 */
		public Builder setTilteColor(int mTitleColor) {
			this.mTitleColor = mContext.getResources().getColor(mTitleColor);
			return this;
		}

		/**
		 * 设置标题
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(CharSequence title) {
			this.mTitle = title;
			return this;
		}

		/**
		 * 设置正面按钮
		 *
		 * @param textId
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(int textId, final View.OnClickListener listener) {
			mPositiveButtonText = mContext.getText(textId);
			mPositiveButtonListener = listener;
			return this;
		}

		/**
		 * 设置正面按钮
		 *
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setPositiveButton(CharSequence text, final View.OnClickListener listener) {
			mPositiveButtonText = text;
			mPositiveButtonListener = listener;
			return this;
		}

		/**
		 * 设置负面按钮
		 *
		 * @param textId
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(int textId, final View.OnClickListener listener) {
			mNegativeButtonText = mContext.getText(textId);
			mNegativeButtonListener = listener;
			return this;
		}

		/**
		 * 设置负面按钮
		 *
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setNegativeButton(CharSequence text, final View.OnClickListener listener) {
			mNegativeButtonText = text;
			mNegativeButtonListener = listener;
			return this;
		}

		/**
		 * 设置中性按钮
		 *
		 * @param textId
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(int textId, final View.OnClickListener listener) {
			mNeutralButtonText = mContext.getText(textId);
			mNeutralButtonListener = listener;
			return this;
		}

		/**
		 * 设置中性按钮
		 *
		 * @param text
		 * @param listener
		 * @return
		 */
		public Builder setNeutralButton(CharSequence text, final View.OnClickListener listener) {
			mNeutralButtonText = text;
			mNeutralButtonListener = listener;
			return this;
		}

		/**
		 * 设置内容
		 *
		 * @param messageId
		 * @return
		 */
		public Builder setMessage(int messageId) {
			mMessage = mContext.getText(messageId);
			return this;
		}

		/**
		 * 设置内容
		 *
		 * @param message
		 * @return
		 */
		public Builder setMessage(CharSequence message) {
			mMessage = message;
			return this;
		}

		/**
		 * 设置内容集合
		 *
		 * @param listAdapter
		 * @param checkedItemIds
		 * @param choiceMode
		 * @param listener
		 * @return
		 */
		public Builder setItems(ListAdapter listAdapter, int[] checkedItemIds, int choiceMode, final AdapterView.OnItemClickListener listener) {
			mListAdapter = listAdapter;
			mListCheckedItemMultipleIds = checkedItemIds;
			mOnItemClickListener = listener;
			mChoiceMode = choiceMode;
			mListCheckedItemIdx = -1;
			return this;
		}

		/**
		 * 设置内容集合
		 *
		 * @param listAdapter
		 * @param checkedItemIdx
		 * @param listener
		 * @return
		 */
		public Builder setItems(ListAdapter listAdapter, int checkedItemIdx, final AdapterView.OnItemClickListener listener) {
			mListAdapter = listAdapter;
			mOnItemClickListener = listener;
			mListCheckedItemIdx = checkedItemIdx;
			mChoiceMode = AbsListView.CHOICE_MODE_NONE;
			return this;
		}

		/**
		 * 设置主要布局
		 *
		 * @param view
		 * @return
		 */
		public Builder setView(View view) {
			mCustomView = view;
			return this;
		}

		/**
		 * 创建
		 *
		 * @return
		 */
		public View create() {
			// 获取默认布局
			LinearLayout content = (LinearLayout) mInflater.inflate(R.layout.j2w_dialog, mContainer, false);
			/**
			 * 获取控件
			 */
			TextView vTitle = ButterKnife.findById(content, R.id.j2w_title);
			TextView vMessage = ButterKnife.findById(content, R.id.j2w_message);

			FrameLayout vCustomView = ButterKnife.findById(content, R.id.j2w_custom);
			Button vPositiveButton = ButterKnife.findById(content, R.id.j2w_button_positive);
			Button vNegativeButton = ButterKnife.findById(content, R.id.j2w_button_negative);
			Button vNeutralButton = ButterKnife.findById(content, R.id.j2w_button_neutral);
			Button vPositiveButtonStacked = ButterKnife.findById(content, R.id.j2w_button_positive_stacked);
			Button vNegativeButtonStacked = ButterKnife.findById(content, R.id.j2w_button_negative_stacked);
			Button vNeutralButtonStacked = ButterKnife.findById(content, R.id.j2w_button_neutral_stacked);
			View vButtonsDefault = ButterKnife.findById(content, R.id.j2w_buttons_default);
			View vButtonsStacked = ButterKnife.findById(content, R.id.j2w_buttons_stacked);
			ListView vList = ButterKnife.findById(content, R.id.j2w_list);

			// 设置标题样式
			// vTitle.setTextAppearance(mContext,
			// R.style.J2W_TextView_Title_Dark);
			// 设置内容样式
			// vMessage.setTextAppearance(mContext,
			// R.style.J2W_TextView_Message_Dark);
			// 设置标题值
			set(vTitle, mTitle);
			// 设置内容值
			set(vMessage, mMessage);
			// 设置填充标题和消息
			setPaddingOfTitleAndMessage(vTitle, vMessage);
			// 如果不为空 加入到布局里
			if (mCustomView != null) {
				vCustomView.addView(mCustomView);
			}
			// 列表适配器
			if (mListAdapter != null) {
				vList.setAdapter(mListAdapter);
				vList.setOnItemClickListener(mOnItemClickListener);
				if (mListCheckedItemIdx != -1) {
					vList.setSelection(mListCheckedItemIdx);
				}
				if (mListCheckedItemMultipleIds != null) {
					vList.setChoiceMode(mChoiceMode);
					for (int i : mListCheckedItemMultipleIds) {
						vList.setItemChecked(i, true);
					}
				}
			}

			/**
			 * 判断字体长度 显示不同样式
			 */
			if (shouldStackButtons()) {
				set(vPositiveButtonStacked, mPositiveButtonText, mPositiveButtonListener);
				set(vNegativeButtonStacked, mNegativeButtonText, mNegativeButtonListener);
				set(vNeutralButtonStacked, mNeutralButtonText, mNeutralButtonListener);
				vButtonsDefault.setVisibility(View.GONE);
				vButtonsStacked.setVisibility(View.VISIBLE);
			} else {
				set(vPositiveButton, mPositiveButtonText, mPositiveButtonListener);
				set(vNegativeButton, mNegativeButtonText, mNegativeButtonListener);
				set(vNeutralButton, mNeutralButtonText, mNeutralButtonListener);
				vButtonsDefault.setVisibility(View.VISIBLE);
				vButtonsStacked.setVisibility(View.GONE);
			}
			// 判断按钮值
			if (TextUtils.isEmpty(mPositiveButtonText) && TextUtils.isEmpty(mNegativeButtonText) && TextUtils.isEmpty(mNeutralButtonText)) {
				vButtonsDefault.setVisibility(View.GONE);
			}

			return content;
		}

		/**
		 * 设置填充标题和消息
		 */
		private void setPaddingOfTitleAndMessage(TextView vTitle, TextView vMessage) {
			int grid6 = mContext.getResources().getDimensionPixelSize(R.dimen.dialog_title);
			int grid4 = mContext.getResources().getDimensionPixelSize(R.dimen.dialog_msg);
			if (!TextUtils.isEmpty(mTitle) && !TextUtils.isEmpty(mMessage)) {
				vTitle.setPadding(grid6, grid6, grid6, grid4);
				vMessage.setPadding(grid6, 0, grid6, grid4);
			} else if (TextUtils.isEmpty(mTitle)) {
				vMessage.setPadding(grid6, grid4, grid6, grid4);
			} else if (TextUtils.isEmpty(mMessage)) {
				vTitle.setPadding(grid6, grid6, grid6, grid4);
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

		private void set(Button button, CharSequence text, View.OnClickListener listener) {
			set(button, text);
			if (listener != null) {
				button.setOnClickListener(listener);
			}
		}

		private void set(TextView textView, CharSequence text) {
			if (text != null) {
				textView.setText(text);
			} else {
				textView.setVisibility(View.GONE);
			}
			if (mTitleColor != 0) {
				textView.setTextColor(mTitleColor);
			}
		}
	}
}
