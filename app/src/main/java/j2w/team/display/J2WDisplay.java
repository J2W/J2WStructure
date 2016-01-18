package j2w.team.display;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;

import j2w.team.J2WHelper;
import j2w.team.modules.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.structure.R;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	@Override public Context context() {
		return J2WHelper.screenHelper().currentActivity();
	}

	@Override public J2WActivity activity() {
		return J2WHelper.screenHelper().currentActivity();
	}

	@Override public void intentFromFragment(Class clazz, Fragment fragment, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(activity(), clazz);
		intentFromFragment(intent, fragment, requestCode);
	}

	@Override public void intentFromFragment(Intent intent, Fragment fragment, int requestCode) {
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		ComponentName component = intent.getComponent();
		stringBuilder.append(component == null ? "" : component.getClassName());
		stringBuilder.append(" Tag :");
		stringBuilder.append(fragment.getClass().getSimpleName());

		L.i(stringBuilder.toString());
		activity().startActivityFromFragment(fragment, intent, requestCode);
	}

	/** 跳转fragment **/
	@Override public void commitAdd(Fragment fragment) {
		commitAdd(R.id.j2w_home, fragment);
	}

	@Override public void commitAdd(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		activity().getSupportFragmentManager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitReplace(Fragment fragment) {
		commitReplace(R.id.j2w_home, fragment);
	}

	@Override public void commitChildReplace(Fragment srcFragment, int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		srcFragment.getChildFragmentManager().beginTransaction().replace(layoutId, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitReplace(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		activity().getSupportFragmentManager().beginTransaction().replace(layoutId, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitBackStack(Fragment fragment) {
		commitBackStack(R.id.j2w_home, fragment);
	}

	@Override public void commitHideAndBackStack(Fragment srcFragment, Fragment fragment) {
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		FragmentTransaction transaction = activity().getSupportFragmentManager().beginTransaction();
		transaction.hide(srcFragment);
		transaction.add(R.id.j2w_home, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(srcFragment.getClass().getSimpleName());
		stringBuilder.append(" 隐藏，仅仅是设为不可见，并不会销毁, ");
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitDetachAndBackStack(Fragment srcFragment, Fragment fragment) {
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		FragmentTransaction transaction = activity().getSupportFragmentManager().beginTransaction();
		transaction.detach(srcFragment);
		transaction.add(R.id.j2w_home, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(srcFragment.getClass().getSimpleName());
		stringBuilder.append(" UI中移除 状态依然由FragmentManger维护, ");
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitBackStack(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");

		activity().getSupportFragmentManager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitBackStack(int layoutId, Fragment fragment, int animation) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkArgument(animation > 0, "动画 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");

		activity().getSupportFragmentManager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName())
				.setTransition(animation != 0 ? animation : FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getName());
		L.i(stringBuilder.toString());
	}

	/** 跳转intent **/
	@Override public void intent(Class clazz) {
		intent(clazz, null);
	}

	@Override public void intent(Class clazz, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(activity(), clazz);
		intent(intent, bundle);
	}

	@Override public void intent(Intent intent) {
		intent(intent, null);
	}

	@Override public void intent(Intent intent, Bundle options) {
		intentForResult(intent, options, -1);
	}

	@Override public void intentForResult(Class clazz, int requestCode) {
		intentForResult(clazz, null, requestCode);
	}

	@Override public void intentForResult(Class clazz, Bundle bundle, int requestCode) {
		Intent intent = new Intent();
		intent.setClass(activity(), clazz);
		intentForResult(intent, bundle, requestCode);
	}

	@Override public void intentForResult(Intent intent, int requestCod) {
		intentForResult(intent, null, requestCod);
	}

	/** 根据某个View 位置 启动跳转动画 **/

	@Override public void intentAnimation(Class clazz, View view, Bundle bundle) {
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(activity().getClass().getName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(clazz.getName());
		Intent intent = new Intent();
		intent.setClass(activity(), clazz);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		ActivityCompat.startActivity(activity(), intent, ActivityOptionsCompat.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle());
	}

	@Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public void intentForResult(Intent intent, Bundle options, int requestCode) {
		J2WCheckUtils.checkNotNull(intent, "intent不能为空～");
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(activity().getClass().getName());
		stringBuilder.append(" 跳转到 ");
		ComponentName component = intent.getComponent();
		stringBuilder.append(component == null ? "" : component.getClassName());
		L.i(stringBuilder.toString());
		if (options != null) {
			intent.putExtras(options);
		}
		activity().startActivityForResult(intent, requestCode);
	}

	@Override public void onKeyHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
		intent.addCategory(Intent.CATEGORY_HOME);
		context().startActivity(intent);
	}

	@Override public void popBackStack() {
		activity().getSupportFragmentManager().popBackStackImmediate();
	}

	@Override public void popBackStack(Class clazz) {
		activity().getSupportFragmentManager().popBackStackImmediate(clazz.getName(), 0);
	}

	@Override public void popBackStack(String clazzName) {
		activity().getSupportFragmentManager().popBackStackImmediate(clazzName, 0);
	}

	@Override public void popBackStackAll() {
		activity().getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
}