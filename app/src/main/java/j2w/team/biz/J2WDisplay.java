package j2w.team.biz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import j2w.team.common.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	protected J2WActivity	mJ2WActivity;

	@Override public Context context() {
		return mJ2WActivity;
	}

	@Override public void initDisplay(J2WActivity j2WActivity) {
		mJ2WActivity = j2WActivity;
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

	protected void intent(Class clazz) {
		intent(clazz, null);
	}

	protected void intent(Class clazz, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intent.putExtras(bundle);
		intent(intent, bundle);
	}

	protected void intent(Intent intent) {
		intent(intent, null);
	}

	protected void intent(Intent intent, Bundle options) {
		if (options != null) {
			intentForResult(intent, options, -1);
		} else {
			intentForResult(intent, -1);
		}
	}

	protected void intentForResult(Class clazz, int requestCode) {
		intentForResult(clazz, null, requestCode);
	}

	protected void intentForResult(Class clazz, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intent.putExtras(bundle);
		intentForResult(intent, bundle, requestCode);
	}

	protected void intentForResult(Intent intent, int requestCod) {
		intentForResult(intent, null, requestCod);
	}

	protected void intentForResult(Intent intent, Bundle options, int requestCode) {
		J2WCheckUtils.checkNotNull(intent, "intent不能为空～");
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		mJ2WActivity.startActivityForResult(intent, requestCode, options);
	}
}