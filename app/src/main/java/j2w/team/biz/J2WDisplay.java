package j2w.team.biz;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import j2w.team.common.log.L;
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

	protected void intent(Class clazz) {
		if (clazz == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(clazz.getSimpleName());
		L.i(stringBuilder.toString());
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		mJ2WActivity.startActivity(intent);
	}

	protected void intent(Class clazz, int animstart, int animstop) {
		if (clazz == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(clazz.getSimpleName());
		L.i(stringBuilder.toString());
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		mJ2WActivity.startActivity(intent);
		mJ2WActivity.overridePendingTransition(animstart, animstop);
	}

	protected void intent(Class clazz, Bundle bundle) {
		if (clazz == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(clazz.getSimpleName());
		L.i(stringBuilder.toString());
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intent.putExtras(bundle);
		mJ2WActivity.startActivity(intent);
	}

	protected void intent(Intent intent) {
		if (intent == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		mJ2WActivity.startActivity(intent);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) protected void intent(Intent intent, Bundle bundle) {
		if (intent == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		mJ2WActivity.startActivity(intent, bundle);
	}

	protected void intentForResult(Intent intent, int requestCode) {
		if (intent == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		mJ2WActivity.startActivityForResult(intent, requestCode);
	}

	@Override public void intentFromFragment(Intent intent, Fragment fragment, int requestCode) {
		if (intent == null || fragment == null) {
			return;
		}
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

	protected void intentForResult(Class clazz, Bundle bundle, int requestCode) {
		if (clazz == null) {
			return;
		}
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(mJ2WActivity.getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(clazz.getSimpleName());
		L.i(stringBuilder.toString());
		Intent intent = new Intent();
		intent.setClass(mJ2WActivity, clazz);
		intent.putExtras(bundle);
		mJ2WActivity.startActivityForResult(intent, requestCode);
	}

}