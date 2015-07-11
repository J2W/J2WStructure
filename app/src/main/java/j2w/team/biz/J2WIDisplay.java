package j2w.team.biz;

import android.support.v7.app.ActionBarActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:40
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public interface J2WIDisplay {

	/**
	 * 设置 activity
	 * 
	 * @param activity activity
	 * @param objects 参数
	 */
	void setActivity(ActionBarActivity activity, Object... objects);
}