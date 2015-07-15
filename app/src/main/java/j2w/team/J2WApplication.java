package j2w.team;

import android.app.Application;
import android.os.Bundle;

import j2w.team.common.log.L;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.view.J2WActivity;
import j2w.team.view.common.J2WIViewCommon;

/**
 * Created by sky on 15/1/26. 说明：使用架构必须继承
 */
public abstract class J2WApplication extends Application implements J2WIViewCommon {

	/**
	 * 日志是否打印
	 * 
	 * @return true 打印 false 不打印
	 */
	public abstract boolean isLogOpen();

	/**
	 * 获取网络适配器
	 * 
	 * @return
	 */
	public abstract J2WRestAdapter getRestAdapter();

	/**
	 * 应用程序启动首先被执行
	 */
	@Override public void onCreate() {
		super.onCreate();
		// 初始化Application
		J2WHelper.with(this);
		// 初始化网络适配器
		J2WHelper.createRestAdapter(getRestAdapter());
		// 日志初始化
		L.init(isLogOpen(), this);
	}

	/**
	 * View层 公共方法
	 */

	@Override public void onSaveInstanceState(J2WActivity j2WIView, Bundle outState) {

	}

	@Override public void onCreate(J2WActivity j2WIView, Bundle bundle) {

	}

	@Override public void onStart(J2WActivity j2WIView) {

	}

	@Override public void onResume(J2WActivity j2WIView) {

	}

	@Override public void onPause(J2WActivity j2WIView) {

	}

	@Override public void onStop(J2WActivity j2WIView) {

	}

	@Override public void onDestroy(J2WActivity j2WIView) {

	}

	@Override public void onRestart(J2WActivity j2WIView) {

	}

}
