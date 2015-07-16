package j2w.team.view.adapter;

import j2w.team.view.model.J2WModelPager;

/**
 * @创建人 sky
 * @创建时间 15/7/16 下午8:30
 * @类描述 一句话说明这个类是干什么的
 */
public interface J2WIViewPagerAdapter {

	/**
	 * 设置数据
	 * 
	 * @param viewPagerDatas
	 */
	void modelPagers(J2WModelPager... viewPagerDatas);

	/**
	 * 替换数据
	 * 
	 * @param modelPagers
	 */
	void replaceModelPagers(J2WModelPager... modelPagers);

    J2WModelPager[] modelPagers();

    J2WModelPager modelPager(int position);
}