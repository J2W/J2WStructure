package j2w.team;

import android.app.Application;
import android.os.Bundle;

import j2w.team.modules.J2WModulesManage;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.methodProxy.J2WMethods;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;
import j2w.team.view.common.J2WIViewCommon;

/**
 * Created by sky on 15/1/26. 说明：使用架构必须继承
 */
public abstract class J2WApplication extends Application implements J2WIViewCommon {

	/**
	 * modules 管理
	 */
	J2WModulesManage	mJ2WModulesManage	= null;

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
	public abstract J2WRestAdapter getRestAdapter(J2WRestAdapter.Builder builder);

	/**
	 * 方法拦截器适配
	 * 
	 * @param builder
	 * @return
	 */
	public abstract J2WMethods getMethodInterceptor(J2WMethods.Builder builder);

	/**
	 * 应用程序启动首先被执行
	 */
	@Override public void onCreate() {
		super.onCreate();
		// 初始化Modules
		mJ2WModulesManage = new J2WModulesManage(this);
		// 初始化Application
		J2WHelper.with(mJ2WModulesManage);
		// 初始化 HTTP
		mJ2WModulesManage.initJ2WRestAdapter(getRestAdapter(new J2WRestAdapter.Builder()));
		// 初始化 LOG
		mJ2WModulesManage.initLog(isLogOpen());
		// 初始化 代理方法
		mJ2WModulesManage.initMehtodProxy(getMethodInterceptor(new J2WMethods.Builder()));
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

	@Override public void onFragmentCreated(J2WFragment j2WFragment, Bundle savedInstanceState) {

	}

	@Override public void onFragmentStart(J2WFragment j2WFragment) {

	}

	@Override public void onFragmentResume(J2WFragment j2WFragment) {

	}

	@Override public void onFragmentPause(J2WFragment j2WFragment) {

	}

	@Override public void onFragmentStop(J2WFragment j2WFragment) {

	}

	@Override public void onFragmentDestroy(J2WFragment j2WFragment) {

	}
}
