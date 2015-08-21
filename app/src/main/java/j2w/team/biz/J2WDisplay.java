package j2w.team.biz;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import j2w.team.common.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	private J2WView	j2WView;

	private Context	context;

	@Override public Context context() {
		return context;
	}

	@Override public void initDisplay(J2WActivity j2WActivity) {
		j2WView = j2WActivity.j2wView();
		context = j2WView.context();
	}

	@Override public void initDisplay(J2WFragment fragment) {
		j2WView = fragment.j2wView();
		context = j2WView.context();
	}

	@Override public void initDisplay(J2WDialogFragment fragment) {
		j2WView = fragment.j2wView();
		context = j2WView.context();
	}

	@Override public void initDisplay(Context context) {
		this.context = context;
	}

	@Override public FragmentManager manager() {
		return j2WView.manager();
	}

	/**
	 * 获取fragment
	 *
	 * @param clazz
	 * @return
	 */
	protected   <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) j2WView.manager().findFragmentByTag(clazz.getSimpleName());
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
		stringBuilder.append(intent.getComponent().getClassName());
		stringBuilder.append(" Tag :");
		stringBuilder.append(fragment.getClass().getSimpleName());

		L.i(stringBuilder.toString());
		activity().startActivityFromFragment(fragment, intent, requestCode);
	}

	protected Toolbar toolbar(int... types) {
		Toolbar toolbar = j2WView.toolbar(types);
		J2WCheckUtils.checkNotNull(toolbar, "标题栏没有打开，无法调用");
		return toolbar;
	}

	protected J2WActivity activity() {
		J2WCheckUtils.checkNotNull(j2WView, "Activity没有初始化");
		return j2WView.activity();
	}

	protected J2WFragment fragment() {
		J2WCheckUtils.checkNotNull(j2WView, "Activity没有初始化");
		return j2WView.fragment();
	}

	/** 跳转fragment **/
	@Override public void commitAdd(Fragment fragment) {
		commitAdd(android.R.id.content, fragment);
	}

	@Override public void commitAdd(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getSimpleName()).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitReplace(Fragment fragment) {
		commitReplace(android.R.id.content, fragment);
	}

	@Override public void commitReplace(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		manager().beginTransaction().replace(layoutId, fragment, fragment.getClass().getSimpleName()).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitBackStack(Fragment fragment) {
		commitBackStack(android.R.id.content, fragment);
	}

	@Override public void commitBackStack(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");

		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commitAllowingStateLoss();
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

		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).setTransition(animation).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
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

	@Override @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public void intentForResult(Intent intent, Bundle options, int requestCode) {
		J2WCheckUtils.checkNotNull(intent, "intent不能为空～");
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("从 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		stringBuilder.append(" 跳转到 ");
		stringBuilder.append(intent.getComponent().getClassName());
		L.i(stringBuilder.toString());
		if (options != null) {
			intent.putExtras(options);
		}
		activity().startActivityForResult(intent, requestCode);
	}

	@Override public void detach() {}
}