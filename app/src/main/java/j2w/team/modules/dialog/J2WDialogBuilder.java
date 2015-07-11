package j2w.team.modules.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import j2w.team.common.log.L;
import j2w.team.J2WHelper;

/**
 * Created by sky on 15/3/1. Dialog编辑器
 */
public abstract class J2WDialogBuilder<T extends J2WDialogBuilder<T>> {

	/**
	 * 默认值
	 */
	/** 请求默认值 **/
	public final static int								DEFAULT_REQUEST_CODE			= -42;

	/** TAG默认值 **/
	public final static String							DEFAULT_TAG						= "j2w_simple_dialog";

	/** 可取消默认值 **/
	public final static String							ARG_CANCELABLE_ON_TOUCH_OUTSIDE	= "j2w_cancelable";

	/** 请求默认值 **/
	public final static String							ARG_REQUEST_CODE				= "j2w_request_code";

	/** TAG值 **/
	private String										mTag							= DEFAULT_TAG;

	/** fragment堆栈管理器 **/
	protected final FragmentManager						mFragmentManager;

	/** 上下文 **/
	protected final Context								mContext;

	/** 当前编辑类 **/
	protected final Class<? extends J2WDialogFragment>	mClass;

	/** dialog 属性 **/
	private boolean										mCancelable						= true;

	/** dialog 属性 **/
	private boolean										mCancelableOnTouchOutside		= true;

	/** 目标碎片 **/
	private Fragment									mTargetFragment;

	/** 请求编号 **/
	private int											mRequestCode					= DEFAULT_REQUEST_CODE;

	/** 构造函数 */
	public J2WDialogBuilder(Class<? extends J2WDialogFragment> clazz) {
		// 获取堆栈管理器
		mFragmentManager = J2WHelper.screenHelper().currentActivity().getSupportFragmentManager();
		// 获取上下文
		mContext = J2WHelper.getInstance().getApplicationContext();
		// 赋值
		mClass = clazz;
	}

	/**
	 * 设置可取消
	 *
	 * @param cancelable
	 *            取消
	 * @return
	 */
	public T setCancelable(boolean cancelable) {
		mCancelable = cancelable;
		return self();
	}

	/**
	 * 设置可取消-手势触摸
	 *
	 * @param cancelable
	 * @return
	 */
	public T setCancelableOnTouchOutside(boolean cancelable) {
		mCancelableOnTouchOutside = cancelable;
		if (cancelable) {
			mCancelable = cancelable;
		}
		return self();
	}

	/**
	 * 设置目标碎片
	 *
	 * @param fragment
	 *            碎片
	 * @param requestCode
	 *            请求编号
	 * @return
	 */
	public T setTargetFragment(Fragment fragment, int requestCode) {
		mTargetFragment = fragment;
		mRequestCode = requestCode;
		return self();
	}

	/**
	 * 设置请求编号
	 *
	 * @param requestCode
	 *            编号
	 * @return
	 */
	public T setRequestCode(int requestCode) {
		mRequestCode = requestCode;
		return self();
	}

	/**
	 * 设置TAG值
	 *
	 * @param tag
	 *            TAG
	 * @return
	 */
	public T setTag(String tag) {
		mTag = tag;
		return self();
	}

	/** 当前编辑类 **/
	protected abstract T self();

	/** 参数 **/
	protected abstract Bundle prepareArguments();

	/**
	 * 创建碎片
	 * 
	 * @return
	 */
	private J2WDialogFragment create() {
		L.tag("J2WDialogFragment");
		L.i("create()");
		// 获取参数
		final Bundle args = prepareArguments();

		// 反射生成碎片
		final J2WDialogFragment fragment = (J2WDialogFragment) Fragment.instantiate(mContext, mClass.getName(), args);
		// 设置可取消值
		args.putBoolean(ARG_CANCELABLE_ON_TOUCH_OUTSIDE, mCancelableOnTouchOutside);
		// 判断是否设置了目标碎片
		if (mTargetFragment != null) {
			// 设置目标碎片和请求编号
			fragment.setTargetFragment(mTargetFragment, mRequestCode);
		} else {
			// 设置请求编号
			args.putInt(ARG_REQUEST_CODE, mRequestCode);
		}
		// 设置可取消
		fragment.setCancelable(mCancelable);
		return fragment;
	}

	/**
	 * 显示碎片
	 * 
	 * @return
	 */
	public DialogFragment show() {
		L.tag("J2WDialogFragment");
		L.i("show() : TAG" + mTag);
		J2WDialogFragment fragment = create();
		fragment.show(mFragmentManager, mTag);
		return fragment;
	}

	/**
	 * 显示碎片-不保存activity状态
	 * 
	 * @return
	 */
	public DialogFragment showAllowingStateLoss() {
		L.tag("J2WDialogFragment");
		L.i("showAllowingStateLoss() : TAG :" + mTag);
		J2WDialogFragment fragment = create();
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.add(fragment, mTag);
		ft.commitAllowingStateLoss();
		return fragment;
	}

}
