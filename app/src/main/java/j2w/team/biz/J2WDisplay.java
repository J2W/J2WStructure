package j2w.team.biz;

import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/11 下午2:39
 * @类描述 统一控制TitleBar、Drawer以及所有Activity和Fragment跳转
 */
public class J2WDisplay implements J2WIDisplay {

	protected J2WActivity	mJ2WActivity;

	@Override public void initDisplay(J2WActivity j2WActivity) {
		mJ2WActivity = j2WActivity;
	}
}