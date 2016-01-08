package j2w.team;

import android.app.Application;

import j2w.team.modules.J2WModulesManage;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.modules.methodProxy.J2WMethods;
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
	 * 获取配置管理器
	 * 
	 * @return
	 */
	public J2WModulesManage getModulesManage() {
		return new J2WModulesManage(this);
	}

	public void initHelper(J2WModulesManage j2WModulesManage) {
		J2WHelper.with(j2WModulesManage);
	}

	/**
	 * 应用程序启动首先被执行
	 */
	@Override public void onCreate() {
		super.onCreate();
		// 初始化
		mJ2WModulesManage = getModulesManage();
		// 初始化Application
		initHelper(mJ2WModulesManage);
		// 初始化 HTTP
		mJ2WModulesManage.initJ2WRestAdapter(getRestAdapter(new J2WRestAdapter.Builder()));
		// 初始化 LOG
		mJ2WModulesManage.initLog(isLogOpen());
		// 初始化 代理方法
		mJ2WModulesManage.initMehtodProxy(getMethodInterceptor(new J2WMethods.Builder()));
	}
}
