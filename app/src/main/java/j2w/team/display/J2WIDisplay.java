package j2w.team.display;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import j2w.team.core.Impl;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:40
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public interface J2WIDisplay {

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	Context context();

	J2WActivity activity();

	/**
	 * 跳转
	 * 
	 * @param clazz
	 * @param fragment
	 * @param requestCode
	 */
	void intentFromFragment(Class clazz, Fragment fragment, int requestCode);

	/**
	 * 跳转
	 * 
	 * @param intent
	 * @param fragment
	 * @param requestCode
	 */
	void intentFromFragment(Intent intent, Fragment fragment, int requestCode);

	/**
	 * home键
	 */
	void onKeyHome();

	void popBackStack();

	void popBackStack(Class clazz);

	void popBackStack(String clazzName);

	void popBackStackAll();

	void commitAdd(Fragment fragment);

	void commitAdd(int layoutId, Fragment fragment);

	void commitReplace(Fragment fragment);

	void commitChildReplace(Fragment srcFragment, int layoutId, Fragment fragment);

	void commitReplace(int layoutId, Fragment fragment);

	void commitBackStack(Fragment fragment);

	void commitHideAndBackStack(Fragment srcFragment, Fragment fragment);

	void commitDetachAndBackStack(Fragment srcFragment, Fragment fragment);

	void commitBackStack(int layoutId, Fragment fragment);

	void commitBackStack(int layoutId, Fragment fragment, int animation);

	/** 跳转intent **/

	void intent(Class clazz);

	void intent(Class clazz, Bundle bundle);

	void intent(Intent intent);

	void intent(Intent intent, Bundle options);

	void intentForResult(Class clazz, int requestCode);

	void intentForResultFromFragment(Class clazz, Bundle bundle, int requestCode,Fragment fragment);

	void intentForResult(Class clazz, Bundle bundle, int requestCode);

	void intentForResult(Intent intent, int requestCod);

	void intentForResult(Intent intent, Bundle options, int requestCode);

	void intentAnimation(Class clazz, View view, Bundle bundle);

	void intentAnimation(Class clazz, int in, int out);

	void intentAnimation(Class clazz, int in, int out, Bundle bundle);

	void intentForResultAnimation(Class clazz, View view,int requestCode);

	void intentForResultAnimation(Class clazz, View view, Bundle bundle,int requestCode);

	void intentForResultAnimation(Class clazz, int in, int out,int requestCode);

	void intentForResultAnimation(Class clazz, int in, int out, Bundle bundle,int requestCode);
}