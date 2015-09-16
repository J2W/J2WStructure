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

import j2w.team.common.log.L;
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

	private J2WView	j2WView;

	private Context	context;

	@Override public Context context() {
		return context;
	}

	@Override public void initDisplay(J2WView j2WView) {
		this.j2WView = j2WView;
		this.context = j2WView.context();
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
	protected <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) j2WView.manager().findFragmentByTag(clazz.getName());
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

	protected Toolbar toolbar(int... types) {
		Toolbar toolbar = j2WView.toolbar(types);
		J2WCheckUtils.checkNotNull(toolbar, "标题栏没有打开，无法调用");
		return toolbar;
	}

	@Override public J2WActivity activity() {
		J2WCheckUtils.checkNotNull(j2WView, "Activity没有初始化");
		return j2WView.activity();
	}

	@Override public J2WFragment fragment() {
		J2WCheckUtils.checkNotNull(j2WView, "Activity没有初始化");
		return j2WView.fragment();
	}

	@Override public boolean isActivity() {
		if (j2WView == null) {
			return false;
		}
		if (j2WView.activity() == null) {
			return false;
		}
		return true;
	}

	/** 跳转fragment **/
	@Override public void commitAdd(Fragment fragment) {
		commitAdd(R.id.j2w_home, fragment);
	}

	@Override public void commitAdd(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
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

	@Override public void commitReplace(int layoutId, Fragment fragment) {
		J2WCheckUtils.checkArgument(layoutId > 0, "提交布局ID 不能为空~");
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");
		manager().beginTransaction().replace(layoutId, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
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

	@Override public void commitHideAndBackStack(Fragment fragment) {
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");

		beginTransaction().hide(j2WView.fragment()).add(R.id.j2w_home, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(j2WView.fragment().getClass().getSimpleName());
		stringBuilder.append(" 隐藏，仅仅是设为不可见，并不会销毁, ");
		stringBuilder.append("fragment: ");
		stringBuilder.append(fragment.getClass().getSimpleName());
		stringBuilder.append(" 提交到 ");
		stringBuilder.append(activity().getClass().getSimpleName());
		L.i(stringBuilder.toString());
	}

	@Override public void commitDetachAndBackStack(Fragment fragment) {
		J2WCheckUtils.checkNotNull(fragment, "fragment不能为空~");

		beginTransaction().detach(j2WView.fragment()).add(R.id.j2w_home, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName())
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commitAllowingStateLoss();
		L.tag("J2WDisplay");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("fragment: ");
		stringBuilder.append(j2WView.fragment().getClass().getSimpleName());
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

		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.commitAllowingStateLoss();
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

		manager().beginTransaction().add(layoutId, fragment, fragment.getClass().getName()).addToBackStack(fragment.getClass().getName())
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

	@Override public void detach() {
		context = null;
	}

	@Override public FragmentTransaction beginTransaction() {
		return j2WView.activity().getSupportFragmentManager().beginTransaction();
	}

	@Override public void onKeyHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
		intent.addCategory(Intent.CATEGORY_HOME);
		context().startActivity(intent);
	}

	@Override public void popBackStack() {
		manager().popBackStack();
	}

	@Override public void popBackStack(Class clazz) {
		manager().popBackStack(clazz.getName(), 0);
	}

	@Override public void popBackStackAll() {
		manager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}
}