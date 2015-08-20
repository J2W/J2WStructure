package j2w.team.biz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:40
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
@Impl(J2WDisplay.class)
public interface J2WIDisplay {

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	Context context();

	/**
	 * 设置 activity
	 *
	 * @param activity
	 *            activity
	 */
	void initDisplay(J2WActivity activity);

	/**
	 * 设置 fragment
	 * 
	 * @param fragment
	 */
	void initDisplay(J2WFragment fragment);

	/**
	 * 设置 dialogfragment
	 * 
	 * @param fragment
	 */
	void initDisplay(J2WDialogFragment fragment);

	/**
	 * 设置 上下文
	 * 
	 * @param context
	 */
	void initDisplay(Context context);

	/**
	 * 获取碎片管理
	 * 
	 * @return
	 */
	FragmentManager manager();

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
	 * 销毁引用
	 */
	void detach();

	void commitAdd(Fragment fragment);

	void commitAdd(int layoutId, Fragment fragment);

	void commitReplace(Fragment fragment);

	void commitReplace(int layoutId, Fragment fragment);

	void commitBackStack(Fragment fragment);

	void commitBackStack(int layoutId, Fragment fragment);

	/** 跳转intent **/

	void intent(Class clazz);

	void intent(Class clazz, Bundle bundle);

	void intent(Intent intent);

	void intent(Intent intent, Bundle options);

	void intentForResult(Class clazz, int requestCode);

	void intentForResult(Class clazz, Bundle bundle, int requestCode);

	void intentForResult(Intent intent, int requestCod);

	void intentForResult(Intent intent, Bundle options, int requestCode);

}