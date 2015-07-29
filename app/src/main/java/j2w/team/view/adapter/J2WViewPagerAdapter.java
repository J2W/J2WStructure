package j2w.team.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.common.view.PagerSlidingTabStrip;
import j2w.team.view.common.J2WViewPagerChangeListener;
import j2w.team.view.model.J2WModelPager;

/**
 * @创建人 sky
 * @创建时间 15/4/24 下午3:09
 * @类描述 ViewPager 默认适配器
 */
public class J2WViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, PagerSlidingTabStrip.TabsTypeProvider, J2WIViewPagerAdapter {

	protected J2WModelPager[]			viewPagerDatas;			// 数据类型

	protected FragmentManager			fragmentManager;			// 管理器

	protected int						currentPageIndex	= -1;	// 当前page索引（切换之前）

	protected String					tag;						// 标记

	protected PagerSlidingTabStrip		tabs;						// 标题

	protected J2WViewPager				pager;						// viewpager

	/**
	 * 记录Viewpager Item
	 */
	protected View						oldView				= null;

	/**
	 * 就Viewpager 坐标
	 */
	protected int						oldPosition			= -1;

	/**
	 * 父类容器
	 */
	ViewGroup							container;

	private View						left;						// 滑动渐变
																	// 左面视图

	private View						right;						// 滑动渐变
																	// 右面视图

	private J2WViewPagerChangeListener	j2WViewPagerChangeListener;

	private J2WTabsCustomListener		j2WTabsCustomListener;

	private int							type;

	/**
	 * 初始化
	 * 
	 * @param fragmentManager
	 *            管理器
	 * @param tabs
	 *            标题
	 * @param pager
	 *            内容
	 */
	public J2WViewPagerAdapter(int type, FragmentManager fragmentManager, PagerSlidingTabStrip tabs, J2WViewPager pager, J2WViewPagerChangeListener j2WViewPagerChangeListener,
			J2WTabsCustomListener j2WTabsCustomListener) {
		this.type = type;
		this.fragmentManager = fragmentManager;
		this.tabs = tabs;
		this.pager = pager;
		if (tabs != null) {
			this.tabs.setOnPageChangeListener(this);
		} else {
			this.pager.setOnPageChangeListener(this);
		}
		this.j2WViewPagerChangeListener = j2WViewPagerChangeListener;
		this.j2WTabsCustomListener = j2WTabsCustomListener;
	}

	/**
	 * 设置数据
	 * 
	 * @param viewPagerDatas
	 */
	@Override public void modelPagers(J2WModelPager... viewPagerDatas) {
		J2WCheckUtils.checkNotNull(viewPagerDatas, "J2WModelPager 不能为空");
		this.viewPagerDatas = viewPagerDatas;
		notifyDataSetChanged();
		if (tabs != null && viewPagerDatas.length > 0) {
			tabs.notifyDataSetChanged();
		}
	}

	/**
	 * 替换
	 * 
	 * @param modelPagers
	 *            数据
	 */
	@Override public void replaceModelPagers(J2WModelPager... modelPagers) {
		J2WCheckUtils.checkNotNull(viewPagerDatas, "J2WModelPager 不能为空");
		J2WCheckUtils.checkNotNull(container, "container 不能为空,初始化的时候不要调用该方法");

		for (J2WModelPager modelPager : modelPagers) {
			int position = modelPager.position;
			container.removeView(viewPagerDatas[position].fragment.getView());
			FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
			fragmentTransaction.detach(viewPagerDatas[position].fragment).commitAllowingStateLoss();
			viewPagerDatas[position] = modelPager;
		}
		notifyDataSetChanged();
		if (tabs != null && modelPagers.length > 0) {
			tabs.notifyDataSetChanged();
		}
	}

	/**
	 * 根据返回值 刷新
	 * 
	 * @param object
	 * @return
	 */
	@Override public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	/**
	 * 返回数据集
	 * 
	 * @return
	 */
	public J2WModelPager[] modelPagers() {
		return viewPagerDatas;
	}

	/**
	 * 返回单个数据
	 * 
	 * @param position
	 * @return
	 */
	public J2WModelPager modelPager(int position) {
		return viewPagerDatas[position];
	}

	/**
	 * 返回标题
	 * 
	 * @param position
	 * @return
	 */
	@Override public CharSequence getPageTitle(int position) {
		return viewPagerDatas[position].title;
	}

	/**
	 * 返回数量
	 * 
	 * @return
	 */
	@Override public int getCount() {
		if (viewPagerDatas == null) {
			return 0;
		}
		return viewPagerDatas.length;
	}

	/**
	 * 销毁
	 * 
	 * @param container
	 * @param position
	 * @param object
	 */
	@Override public void destroyItem(ViewGroup container, int position, Object object) {
		this.container = container;
		container.removeView(viewPagerDatas[position].fragment.getView()); // 移出viewpager两边之外的page布局
	}

	/**
	 * 判断是否一致
	 * 
	 * @param view
	 * @param object
	 * @return
	 */
	@Override public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	/**
	 * 生成
	 * 
	 * @param container
	 * @param position
	 * @return
	 */
	@Override public Object instantiateItem(ViewGroup container, int position) {
		this.container = container;
		Fragment fragment = viewPagerDatas[position].fragment;
		if (!fragment.isAdded()) { // 如果fragment还没有added
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
			ft.add(fragment, fragment.getClass().getSimpleName() + position);
			ft.commitAllowingStateLoss();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			fragmentManager.executePendingTransactions();
			fragment.setHasOptionsMenu(false);// 设置actionbar不执行
		}
		if (fragment.getView() == null) {
			throw new NullPointerException("fragment,没有给布局，导致获取不到View");
		}

		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}

		pager.setObjectForPosition(fragment.getView(), position);

		return fragment.getView();
	}

	/**
	 * 滑动中
	 * 
	 * @param position
	 * @param positionOffset
	 * @param positionOffsetPixels
	 */
	@Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		left = pager.getChildAt(position);
		right = pager.getChildAt(position + 1);

		if (j2WViewPagerChangeListener != null) {
			j2WViewPagerChangeListener.onExtraPageScrolled(left, right, positionOffset, positionOffsetPixels);
		}

	}

	/**
	 * 滑动选择
	 * 
	 * @param position
	 */
	@Override public void onPageSelected(int position) {
		if (currentPageIndex == -1) {
			currentPageIndex = 0;
			oldView = pager.getChildAt(0);
			oldPosition = 0;
		}else{
			viewPagerDatas[currentPageIndex].fragment.onInvisible(); // 调用切换前Fargment的onPause()
		}

		// 调用切换前Fargment的onStop()
		if (viewPagerDatas[position].fragment.isAdded()) {
			viewPagerDatas[position].fragment.onVisible(); // 调用切换后Fargment的onResume()
		}

		currentPageIndex = position;
		if (j2WViewPagerChangeListener != null) {
			j2WViewPagerChangeListener.onExtraPageSelected(pager.getChildAt(position), oldView, position, oldPosition);
		}

		oldView = pager.getChildAt(position);// 缓存视图
		oldPosition = position; // 缓存坐标
	}

	/**
	 * 滑动状态
	 * 
	 * @param state
	 */
	@Override public void onPageScrollStateChanged(int state) {
		if (j2WViewPagerChangeListener != null) {
			j2WViewPagerChangeListener.onExtraPageScrollStateChanged(state);
		}
	}

	/**
	 * 获取类型
	 * 
	 * @return
	 */
	public int getTabsType() {
		return type;
	}

	/** 自定义 **/
	@Override public int getCustomTabView() {
		J2WCheckUtils.checkNotNull(j2WTabsCustomListener, "Viewpager Tabs 自定义事件不能为空");
		int layoutId = j2WTabsCustomListener.getViewPagerItemLayout();
		J2WCheckUtils.checkArgument(layoutId > 0, "Viewpager Tabs 自定义布局 不能为空~");
		return layoutId;
	}

	@Override public void initTabsItem(View view, int position) {
		J2WCheckUtils.checkNotNull(j2WTabsCustomListener, "Viewpager Tabs 自定义事件不能为空");
		J2WCheckUtils.checkPositionIndex(position, viewPagerDatas.length - 1, "Viewpager 获取Tabs 数量时 下标越界");
		j2WTabsCustomListener.initTab(view, viewPagerDatas[position]);
	}

	/** 图标 **/
	@Override public int getPageIconResId(int position) {
		J2WCheckUtils.checkPositionIndex(position, viewPagerDatas.length - 1, "Viewpager 获取Tabs 获取图标地址 下标越界");
		int resId = viewPagerDatas[position].icon;
		J2WCheckUtils.checkArgument(resId > 0, "Viewpager Tabs 图标地址 不能为空~");
		return resId;
	}

	/** 数量 **/
	@Override public String getPageCount(int position) {
		J2WCheckUtils.checkPositionIndex(position, viewPagerDatas.length - 1, "Viewpager 获取Tabs 数量时 下标越界");
		String count = String.valueOf(viewPagerDatas[position].count);
		if (J2WCheckUtils.isEmpty(count)) {
			return null;
		}
		return count;
	}

	public void setTitleCount(int position, String count) {
		J2WCheckUtils.checkPositionIndex(position, viewPagerDatas.length - 1, "Viewpager 获取Tabs 数量时 下标越界");
		if (tabs == null) {
			return;
		}
		viewPagerDatas[position].count = Integer.valueOf(count);
		tabs.notifyDataSetChanged();
	}
}
