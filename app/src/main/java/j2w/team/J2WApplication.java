package j2w.team;

import android.app.Application;
import android.os.Bundle;

import com.facebook.imagepipeline.backends.okhttp.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import j2w.team.common.log.L;
import j2w.team.modules.J2WModulesManage;
import j2w.team.modules.http.J2WRestAdapter;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WFragment;
import j2w.team.view.common.J2WIViewCommon;
import j2w.team.view.model.J2WConstants;

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
	 * 应用程序启动首先被执行
	 */
	@Override public void onCreate() {
		super.onCreate();
		// 初始化Modules
		mJ2WModulesManage = new J2WModulesManage(this);
		// 初始化Application
		J2WHelper.with(mJ2WModulesManage);
		// 初始化 HTTP
		mJ2WModulesManage.setJ2WRestAdapter(getRestAdapter(mJ2WModulesManage.getJ2WRestAdapterBuilder()));
		// 初始化 fresco
		mJ2WModulesManage.setImagePipelineConfig(initImagePipelineConfig(OkHttpImagePipelineConfigFactory.newBuilder(this, initFrescoHttpClient())));
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

	/**
	 * fresco 默认配置
	 * 
	 * @return
	 */
	protected ImagePipelineConfig initImagePipelineConfig(ImagePipelineConfig.Builder builder) {
		return builder.build();
	}

	/**
	 * fresco 低层网络
	 * 
	 * @return
	 */
	protected OkHttpClient initFrescoHttpClient() {
		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setConnectTimeout(J2WConstants.DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 连接超时
		okHttpClient.setReadTimeout(J2WConstants.DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 读取超时
		okHttpClient.setWriteTimeout(J2WConstants.DEFAULT_TIME_OUT, TimeUnit.SECONDS);// 写入超时
		return okHttpClient;
	}

}
