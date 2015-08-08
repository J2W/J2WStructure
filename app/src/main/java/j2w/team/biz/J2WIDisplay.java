package j2w.team.biz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:40
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
@Impl(J2WDisplay.class)
public interface J2WIDisplay {

	/**
	 * 获取上下文
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
	 * 获取碎片管理
	 * 
	 * @return
	 */
	FragmentManager manager();

}