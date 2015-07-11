package j2w.team.modules.dialog.provided;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import j2w.team.common.log.L;
import j2w.team.modules.dialog.J2WDialogBuilder;
import j2w.team.modules.dialog.J2WDialogFragment;
import j2w.team.structure.R;

/**
 * Created by sky on 15/3/1. 进度条
 */
public class ProgressDailogFragment extends J2WDialogFragment {

	protected final static String	ARG_MESSAGE	= "message";

	protected final static String	ARG_TITLE	= "title";

	TextView						tvMessage;

	/**
	 * 创建进度条
	 * 
	 * @return
	 */
	public static ProgressDialogBuilder createBuilder() {
		return new ProgressDialogBuilder();
	}

	@Override protected Builder build(Builder initialBuilder) {
		L.i("build()");

		final LayoutInflater inflater = initialBuilder.getLayoutInflater();
		final View view = inflater.inflate(R.layout.j2w_dialog_progress, null, false);
		tvMessage = (TextView) view.findViewById(R.id.j2w_message);

		tvMessage.setText(getArguments().getString(ARG_MESSAGE));

		initialBuilder.setView(view);

		initialBuilder.setTitle(getArguments().getString(ARG_TITLE));

		return initialBuilder;
	}

	/**
	 * 设置文本
	 */
	public void setArgMessage(String value) {
		if (this.isAdded() || tvMessage != null) {
			tvMessage.setText(value);
		}else{
            Bundle bundle = getArguments();
            bundle.putString(SimpleDialogFragment.ARG_MESSAGE, value);
            this.setArguments(bundle);
        }
	}

	/**
	 * 进度条编辑类
	 */
	public static class ProgressDialogBuilder extends J2WDialogBuilder<ProgressDialogBuilder> {

		private String	mTitle;

		private String	mMessage	= mContext.getString(R.string.progress_dialog_value);

		ProgressDialogBuilder() {
			super(ProgressDailogFragment.class);
		}

		@Override protected ProgressDialogBuilder self() {
			return this;
		}

		public ProgressDialogBuilder setTitle(int titleResourceId) {
			mTitle = mContext.getString(titleResourceId);
			return this;
		}

		public ProgressDialogBuilder setTitle(String title) {
			mTitle = title;
			return this;
		}

		public ProgressDialogBuilder setMessage(int messageResourceId) {
			mMessage = mContext.getString(messageResourceId);
			return this;
		}

		public ProgressDialogBuilder setMessage(String message) {
			mMessage = message;
			return this;
		}

		@Override protected Bundle prepareArguments() {
			L.i("prepareArguments()");
			Bundle args = new Bundle();
			args.putString(SimpleDialogFragment.ARG_MESSAGE, mMessage);
			args.putString(SimpleDialogFragment.ARG_TITLE, mTitle);

			return args;
		}
	}
}
