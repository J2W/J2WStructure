package j2w.team.modules.dialog.provided;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import j2w.team.common.log.L;
import j2w.team.modules.dialog.J2WDialogBuilder;
import j2w.team.modules.dialog.J2WDialogFragment;
import j2w.team.modules.dialog.iface.INegativeButtonDialogListener;
import j2w.team.modules.dialog.iface.INeutralButtonDialogListener;
import j2w.team.modules.dialog.iface.IPositiveButtonDialogListener;

public class SimpleDialogFragment extends J2WDialogFragment {

	protected final static String	ARG_MESSAGE			= "message";

	protected final static String	ARG_TITLE			= "title";

	protected final static String	ARG_POSITIVE_BUTTON	= "positive_button";

	protected final static String	ARG_NEGATIVE_BUTTON	= "negative_button";

	protected final static String	ARG_NEUTRAL_BUTTON	= "neutral_button";

    /**
     * 创建进度条
     *
     * @return
     */
    public static SimpleDialogBuilder createBuilder() {
        return new SimpleDialogBuilder(SimpleDialogFragment.class);
    }

	@Override protected J2WDialogFragment.Builder build(J2WDialogFragment.Builder builder) {
        L.i("build()");
        final String title = getTitle();
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		final CharSequence message = getMessage();
		if (!TextUtils.isEmpty(message)) {
			builder.setMessage(message);
		}

		final String positiveButtonText = getPositiveButtonText();
		if (!TextUtils.isEmpty(positiveButtonText)) {
			builder.setPositiveButton(positiveButtonText, new View.OnClickListener() {

				@Override public void onClick(View view) {
					for (IPositiveButtonDialogListener listener : getPositiveButtonDialogListeners()) {
						listener.onPositiveButtonClicked(mRequestCode);
					}
					dismiss();
				}
			});
		}

		final String negativeButtonText = getNegativeButtonText();
		if (!TextUtils.isEmpty(negativeButtonText)) {
			builder.setNegativeButton(negativeButtonText, new View.OnClickListener() {

				@Override public void onClick(View view) {
					for (INegativeButtonDialogListener listener : getNegativeButtonDialogListeners()) {
						listener.onNegativeButtonClicked(mRequestCode);
					}
					dismiss();
				}
			});
		}

		final String neutralButtonText = getNeutralButtonText();
		if (!TextUtils.isEmpty(neutralButtonText)) {
			builder.setNeutralButton(neutralButtonText, new View.OnClickListener() {

				@Override public void onClick(View view) {
					for (INeutralButtonDialogListener listener : getNeutralButtonDialogListeners()) {
						listener.onNeutralButtonClicked(mRequestCode);
					}
					dismiss();
				}
			});
		}

		return builder;
	}

	protected CharSequence getMessage() {
		return getArguments().getCharSequence(ARG_MESSAGE);
	}

	protected String getTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	protected String getPositiveButtonText() {
		return getArguments().getString(ARG_POSITIVE_BUTTON);
	}

	protected String getNegativeButtonText() {
		return getArguments().getString(ARG_NEGATIVE_BUTTON);
	}

	protected String getNeutralButtonText() {
		return getArguments().getString(ARG_NEUTRAL_BUTTON);
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

    /**
     * 进度条编辑类
     */
	public static class SimpleDialogBuilder extends J2WDialogBuilder<SimpleDialogBuilder> {

		private String			mTitle;

		private CharSequence	mMessage;

		private String			mPositiveButtonText;

		private String			mNegativeButtonText;

		private String			mNeutralButtonText;

		protected SimpleDialogBuilder(Class<? extends SimpleDialogFragment> clazz) {
			super(clazz);
		}

		@Override protected SimpleDialogBuilder self() {
			return this;
		}

		public SimpleDialogBuilder setTitle(int titleResourceId) {
			mTitle = mContext.getString(titleResourceId);
			return this;
		}

		public SimpleDialogBuilder setTitle(String title) {
			mTitle = title;
			return this;
		}

		public SimpleDialogBuilder setMessage(int messageResourceId) {
			mMessage = mContext.getText(messageResourceId);
			return this;
		}

		public SimpleDialogBuilder setMessage(int resourceId, Object... formatArgs) {
			mMessage = Html.fromHtml(String.format(Html.toHtml(new SpannedString(mContext.getText(resourceId))), formatArgs));
			return this;
		}

		public SimpleDialogBuilder setMessage(CharSequence message) {
			mMessage = message;
			return this;
		}

		public SimpleDialogBuilder setPositiveButtonText(int textResourceId) {
			mPositiveButtonText = mContext.getString(textResourceId);
			return this;
		}

		public SimpleDialogBuilder setPositiveButtonText(String text) {
			mPositiveButtonText = text;
			return this;
		}

		public SimpleDialogBuilder setNegativeButtonText(int textResourceId) {
			mNegativeButtonText = mContext.getString(textResourceId);
			return this;
		}

		public SimpleDialogBuilder setNegativeButtonText(String text) {
			mNegativeButtonText = text;
			return this;
		}

		public SimpleDialogBuilder setNeutralButtonText(int textResourceId) {
			mNeutralButtonText = mContext.getString(textResourceId);
			return this;
		}

		public SimpleDialogBuilder setNeutralButtonText(String text) {
			mNeutralButtonText = text;
			return this;
		}

		@Override protected Bundle prepareArguments() {
            L.i("prepareArguments()");
			Bundle args = new Bundle();
			args.putCharSequence(SimpleDialogFragment.ARG_MESSAGE, mMessage);
			args.putString(SimpleDialogFragment.ARG_TITLE, mTitle);
			args.putString(SimpleDialogFragment.ARG_POSITIVE_BUTTON, mPositiveButtonText);
			args.putString(SimpleDialogFragment.ARG_NEGATIVE_BUTTON, mNegativeButtonText);
			args.putString(SimpleDialogFragment.ARG_NEUTRAL_BUTTON, mNeutralButtonText);

			return args;
		}
	}
}
