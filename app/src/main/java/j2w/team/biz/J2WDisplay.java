package j2w.team.biz;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;

import j2w.team.common.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	private J2WActivity			mJ2WActivity;

	private Context				context;

	private J2WFragment			mJ2WFragment;

	private J2WDialogFragment	mJ2WDialogFragment;

	private Toolbar				toolbar;

	protected static final int	ACTIVITY		= 99999;

	protected static final int	FRAGMENT		= 88888;

	protected static final int	DIALOGFRAGMENT	= 77777;

	private int					type;

	@Override public Context context() {
		return context;
	}

	@Override public void initDisplay(J2WActivity j2WActivity) {
		mJ2WActivity = j2WActivity;
		context = j2WActivity;
		toolbar = mJ2WActivity.toolbar();
		type = ACTIVITY;
	}

	@Override public void initDisplay(J2WFragment fragment) {
		mJ2WFragment = fragment;
		mJ2WActivity = (J2WActivity) fragment.getActivity();
		context = mJ2WActivity;
		toolbar = mJ2WFragment.toolbar();
		type = FRAGMENT;
	}

	@Override public void initDisplay(J2WDialogFragment fragment) {
		mJ2WDialogFragment = fragment;
		mJ2WActivity = (J2WActivity) fragment.getActivity();
		context = mJ2WActivity;
		toolbar = mJ2WDialogFragment.toolbar();
		type = DIALOGFRAGMENT;
	}

	@Override public void initDisplay(Context context) {
		this.context = context;
	}

	@Override public FragmentManager manager() {
		return mJ2WActivity.getSupportFragmentManager();
	}

	@Override public void intentFromFragment(Class clazz, Fragment fragment, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intentFromFragment(intent, fragment, requestCode);
	}

	@Override public void intentFromFragment(Intent intent, Fragment fragment, int requestCode) {
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		stringBuilder.append(" Tag :");
		stringBuilder.append(fragment.getClass().getSimpleName());

		L.i(stringBuilder.toString());
		mJ2WActivity.startActivityFromFragment(fragment, intent, requestCode);
	}

	protected Toolbar toolbar(int... types) {
		if (types.length > 0) {
			type = types[0];
		}
		switch (type) {
			case DIALOGFRAGMENT:
				toolbar = mJ2WDialogFragment.toolbar();
				toolbar = toolbar == null ? mJ2WActivity.toolbar() : toolbar;
			case FRAGMENT:
				toolbar = mJ2WFragment.toolbar();
				toolbar = toolbar == null ? mJ2WActivity.toolbar() : toolbar;
				break;
			case ACTIVITY:
				toolbar = mJ2WActivity.toolbar();
				break;
		}

		J2WCheckUtils.checkNotNull(toolbar, "标题栏没有打开，无法调用");
		return toolbar;
	}

	protected J2WActivity activity() {
		J2WCheckUtils.checkNotNull(mJ2WActivity, "无法获取Activity，编码问题");
		return mJ2WActivity;
	}

	protected void intent(Class clazz) {
		intent(clazz, null);
	}

	protected void intent(Class clazz, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intent(intent, bundle);
	}

	protected void intent(Intent intent) {
		intent(intent, null);
	}

	protected void intent(Intent intent, Bundle options) {
		intentForResult(intent, options, -1);
	}

	protected void intentForResult(Class clazz, int requestCode) {
		intentForResult(clazz, null, requestCode);
	}

	protected void intentForResult(Class clazz, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intentForResult(intent, bundle, requestCode);
	}

	protected void intentForResult(Intent intent, int requestCod) {
		intentForResult(intent, null, requestCod);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) protected void intentForResult(Intent intent, Bundle options, int requestCode) {
		J2WCheckUtils.checkNotNull(intent, "intent不能为空～");
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		if (options != null) {
			intent.putExtras(options);
		}
		mJ2WActivity.startActivityForResult(intent, requestCode);
	}
}