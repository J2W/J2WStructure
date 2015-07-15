package j2w.team.view.common;

import android.os.Bundle;

import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/5/14 下午5:56
 * @类描述 公共视图接口
 */
public interface J2WIViewCommon {

	/**
	 * 公共
	 */
	void onSaveInstanceState(J2WActivity j2WIView, Bundle outState);

	/**
	 * activity
	 */
	void onCreate(J2WActivity j2WIView, Bundle bundle);

	void onStart(J2WActivity j2WIView);

	void onResume(J2WActivity j2WIView);

	void onPause(J2WActivity j2WIView);

	void onStop(J2WActivity j2WIView);

	void onDestroy(J2WActivity j2WIView);

	void onRestart(J2WActivity j2WIView);

	/**
	 * 进度布局
	 *
	 * @return
	 */
	int layoutLoading();

	/**
	 * 空布局
	 *
	 * @return
	 */
	int layoutEmpty();

	/**
	 * 网络业务错误
	 *
	 * @return
	 */
	int layoutBizError();

	/**
	 * 网络错误
	 * 
	 * @return
	 */
	int layoutHttpError();

}
