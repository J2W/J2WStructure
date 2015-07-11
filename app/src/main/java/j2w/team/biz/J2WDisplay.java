package j2w.team.biz;

import android.support.v7.app.ActionBarActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	protected ActionBarActivity	mActionBarActivity;

	@Override public void setActivity(ActionBarActivity activity, Object... objects) {
		mActionBarActivity = activity;
	}
}