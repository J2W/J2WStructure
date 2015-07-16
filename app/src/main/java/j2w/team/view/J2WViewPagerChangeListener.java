package j2w.team.view;

import android.view.View;

/**
 * @创建人 sky
 * @创建时间 15/7/16 下午3:49
 * @类描述 事件
 */
public interface J2WViewPagerChangeListener {

	/**
	 * ViewPager 滑动事件 - 滑动过程
	 *
	 * @param left
	 *            左视图
	 * @param right
	 *            右视图
	 * @param v
	 *            数值
	 * @param i2
	 *            偏移量
	 */
	void onExtraPageScrolled(View left, View right, float v, int i2);

	/**
	 * ViewPager 滑动事件 - 滑动完成
	 *
	 * @param current
	 *            当前
	 * @param old
	 *            过去
	 * @param currentPosition
	 *            当前坐标
	 * @param oldPosition
	 *            过去坐标
	 */
	void onExtraPageSelected(View current, View old, int currentPosition, int oldPosition);

	/**
	 * ViewPager 滑动事件 - 滑动改变
	 *
	 * @param i
	 */
	void onExtraPageScrollStateChanged(int i);
}