package j2w.team.biz;


import j2w.team.view.J2WActivity;

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
	 */
	void initDisplay(J2WActivity activity);
}