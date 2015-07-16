package j2w.team.view.adapter;

import android.view.View;

import j2w.team.view.model.J2WModelPager;

/**
 * @创建人 sky
 * @创建时间 15/7/16 下午5:55
 * @类描述 ViewPager Tabs 自定义
 */
public interface J2WTabsCustomListener {

	/**
	 * 初始化 TabHost - item 样式
	 *
	 * @return　 布局ID
	 */
	int getViewPagerItemLayout();

	/**
	 * 初始化 TabHost - item 值
	 *
	 * @param view
	 *            TabItem
	 * @param modelPager
	 *            Item对象
	 */
	void initTab(View view, J2WModelPager modelPager);
}