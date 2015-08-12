package j2w.team.view;

import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.common.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.common.view.PagerSlidingTabStrip;
import j2w.team.structure.R;
import j2w.team.view.adapter.J2WAdapterItem;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.J2WListViewMultiLayout;
import j2w.team.view.adapter.J2WTabsCustomListener;
import j2w.team.view.adapter.J2WViewPagerAdapter;
import j2w.team.view.adapter.recycleview.DividerGridItemDecoration;
import j2w.team.view.adapter.recycleview.DividerItemDecoration;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;
import j2w.team.view.adapter.recycleview.J2WRVAdapterItem;
import j2w.team.view.common.J2WRefreshListener;
import j2w.team.view.common.J2WViewPagerChangeListener;

/**
 * @创建人 sky
 * @创建时间 15/7/16 下午8:12
 * @类描述 一句话说明这个类是干什么的
 */
public class J2WBuilder implements AbsListView.OnScrollListener {

	/** 上下文 **/
	private J2WActivity		mContext;

	/** 布局加载器 **/
	private LayoutInflater	mInflater;

	/**
	 * 构造器
	 *
	 * @param context
	 * @param inflater
	 */
	public J2WBuilder(J2WActivity context, LayoutInflater inflater) {
		this.mContext = context;
		this.mInflater = inflater;
	}

	/**
	 * 布局ID
	 */
	private int			layoutId;

	private FrameLayout	contentRoot;

	int getLayoutId() {
		return layoutId;
	}

	public void layoutId(int layoutId) {
		this.layoutId = layoutId;
	}

	/**
	 * 显示状态切换
	 */

	private int			layoutLoadingId;

	private int			layoutEmptyId;

	private int			layoutBizErrorId;

	private int			layoutHttpErrorId;

	private View		layoutContent;

	private ViewStub	vsLoading;

	private View		layoutLoading;

	private View		layoutEmpty;

	private View		layoutBizError;

	private View		layoutHttpError;

	// 设置
	public void layoutLoadingId(int layoutLoadingId) {
		this.layoutLoadingId = layoutLoadingId;
	}

	public void layoutEmptyId(int layoutEmptyId) {
		this.layoutEmptyId = layoutEmptyId;
	}

	public void layoutBizErrorId(int layoutBizErrorId) {
		this.layoutBizErrorId = layoutBizErrorId;
	}

	public void layoutHttpErrorId(int layoutHttpErrorId) {
		this.layoutHttpErrorId = layoutHttpErrorId;
	}

	// 功能
	void layoutContent() {
		if (layoutContent == null) {
			return;
		}
		changeShowAnimation(layoutLoading, false);
		changeShowAnimation(layoutEmpty, false);
		changeShowAnimation(layoutBizError, false);
		changeShowAnimation(layoutHttpError, false);
		changeShowAnimation(layoutContent, true);
	}

	void layoutLoading() {
		if (layoutLoading == null) {
			return;
		}
		changeShowAnimation(layoutEmpty, false);
		changeShowAnimation(layoutBizError, false);
		changeShowAnimation(layoutHttpError, false);
		changeShowAnimation(layoutContent, false);
		if (layoutLoading == null && vsLoading != null) {
			layoutLoading = vsLoading.inflate();
			J2WCheckUtils.checkNotNull(layoutLoading, "无法根据布局文件ID,获取layoutLoading");
		}
		changeShowAnimation(layoutLoading, true);
	}

	void layoutEmpty() {
		if (layoutEmpty == null) {
			return;
		}
		changeShowAnimation(layoutBizError, false);
		changeShowAnimation(layoutHttpError, false);
		changeShowAnimation(layoutContent, false);
		changeShowAnimation(layoutLoading, false);
		changeShowAnimation(layoutEmpty, true);
	}

	void layoutBizError() {
		if (layoutBizError == null) {
			return;
		}
		changeShowAnimation(layoutEmpty, false);
		changeShowAnimation(layoutHttpError, false);
		changeShowAnimation(layoutContent, false);
		changeShowAnimation(layoutLoading, false);
		changeShowAnimation(layoutBizError, true);
	}

	void layoutHttpError() {
		if (layoutHttpError == null) {
			return;
		}
		changeShowAnimation(layoutEmpty, false);
		changeShowAnimation(layoutBizError, false);
		changeShowAnimation(layoutContent, false);
		changeShowAnimation(layoutLoading, false);
		changeShowAnimation(layoutHttpError, true);
	}

	void changeShowAnimation(View view, boolean visible) {
		if (view == null) {
			return;
		}
		Animation anim;
		if (visible) {
			if (view.getVisibility() == View.VISIBLE) {
				return;
			}
			view.setVisibility(View.VISIBLE);
			anim = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
		} else {
			if (view.getVisibility() == View.GONE) {
				return;
			}
			view.setVisibility(View.GONE);
			anim = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
		}

		anim.setDuration(mContext.getResources().getInteger(android.R.integer.config_shortAnimTime));
		view.startAnimation(anim);
	}

	/**
	 * actionbar
	 */

	private Toolbar							toolbar;

	private Toolbar.OnMenuItemClickListener	menuListener;

	private int								toolbarLayoutId	= R.layout.j2w_include_toolbar;

	private int								toolbarId		= R.id.toolbar;

	private int								toolbarMenuId;

	private int								toolbarDrawerId;

	private boolean							isOpenToolbar;

	private boolean							isOpenCustomToolbar;

	// 获取
	int getToolbarLayoutId() {
		return toolbarLayoutId;
	}

	public boolean isOpenCustomToolbar() {
		return isOpenCustomToolbar;
	}

	boolean isOpenToolbar() {
		return isOpenToolbar;
	}

	int getToolbarId() {
		return toolbarId;
	}

	int getToolbarMenuId() {
		return toolbarMenuId;
	}

	int getToolbarDrawerId() {
		return toolbarDrawerId;
	}

	Toolbar getToolbar() {
		return toolbar;
	}

	Toolbar.OnMenuItemClickListener getMenuListener() {
		return menuListener;
	}

	// 设置

	public void toolbarId(int toolbarId) {
		this.toolbarId = toolbarId;
		this.isOpenCustomToolbar = true;
	}

	public void toolbarDrawerId(int toolbarDrawerId) {
		this.toolbarDrawerId = toolbarDrawerId;
	}

	public void toolbarMenuListener(Toolbar.OnMenuItemClickListener menuListener) {
		this.menuListener = menuListener;
	}

	public void toolbarIsOpen(boolean isOpenToolbar) {
		this.isOpenToolbar = isOpenToolbar;
	}

	public void toolbarMenuId(int toolbarMenuId) {
		this.toolbarMenuId = toolbarMenuId;
	}

	/**
	 * EventBus开关
	 */
	private boolean	isOpenEventBus;

	// 获取
	boolean isOpenEventBus() {
		return isOpenEventBus;
	}

	// 设置
	public void isOpenEventBus(boolean isOpenEventBus) {
		this.isOpenEventBus = isOpenEventBus;
	}

	/**
	 * ListView
	 */
	private J2WListAdapter				j2WListAdapter;

	private ListView					listView;

	private View						header;

	private View						footer;

	private SwipeRefreshLayout			swipe_container;

	private J2WRefreshListener			j2WRefreshListener;

	private boolean						mLoadMoreIsAtBottom;			// 加载更多

	// 开关

	private int							mLoadMoreRequestedItemCount;	// 加载更多

	// 数量

	private int							colorResIds[];

	private int							swipRefreshId;

	private int							listId;

	private int							listHeaderLayoutId;

	private int							listFooterLayoutId;

	J2WAdapterItem						j2WAdapterItem;

	J2WListViewMultiLayout				j2WListViewMultiLayout;

	AdapterView.OnItemClickListener		itemListener;

	AdapterView.OnItemLongClickListener	itemLongListener;

	// 获取
	int getListId() {
		return listId;
	}

	J2WAdapterItem getJ2WAdapterItem() {
		return j2WAdapterItem;
	}

	J2WListViewMultiLayout getJ2WListViewMultiLayout() {
		return j2WListViewMultiLayout;
	}

	AdapterView.OnItemClickListener getItemListener() {
		return itemListener;
	}

	AdapterView.OnItemLongClickListener getItemLongListener() {
		return itemLongListener;
	}

	int getListHeaderLayoutId() {
		return listHeaderLayoutId;
	}

	int getListFooterLayoutId() {
		return listFooterLayoutId;
	}

	J2WListAdapter getAdapter() {
		J2WCheckUtils.checkNotNull(j2WListAdapter, "适配器没有初始化");
		return j2WListAdapter;
	}

	ListView getListView() {
		J2WCheckUtils.checkNotNull(listView, "没有设置布局文件ID,无法获取ListView");
		return listView;
	}

	int getSwipRefreshId() {
		return swipRefreshId;
	}

	int[] getSwipeColorResIds() {
		return colorResIds;
	}

	// 设置
	public void listHeaderLayoutId(int listHeaderLayoutId) {
		this.listHeaderLayoutId = listHeaderLayoutId;
	}

	public void listFooterLayoutId(int listFooterLayoutId) {
		this.listFooterLayoutId = listFooterLayoutId;
	}

	public void listViewOnItemClick(AdapterView.OnItemClickListener itemListener) {
		this.itemListener = itemListener;
	}

	public void listViewOnItemLongClick(AdapterView.OnItemLongClickListener itemLongListener) {
		this.itemLongListener = itemLongListener;
	}

	public void listViewId(int listId, J2WAdapterItem j2WAdapterItem) {
		this.listId = listId;
		this.j2WAdapterItem = j2WAdapterItem;
	}

	public void listViewId(int listId, J2WListViewMultiLayout j2WListViewMultiLayout) {
		this.listId = listId;
		this.j2WListViewMultiLayout = j2WListViewMultiLayout;
	}

	public void listSwipRefreshId(int swipRefreshId, J2WRefreshListener j2WRefreshListener) {
		this.swipRefreshId = swipRefreshId;
		this.j2WRefreshListener = j2WRefreshListener;
	}

	public void listSwipeColorResIds(int... colorResIds) {
		this.colorResIds = colorResIds;
	}

	// 功能
	void addListHeader() {
		if (listView != null && header != null) {
			listView.addHeaderView(header);
		}
	}

	void addListFooter() {
		if (listView != null && footer != null) {
			listView.addFooterView(footer);
		}
	}

	void removeListHeader() {
		if (listView != null && header != null) {
			listView.removeHeaderView(header);
		}
	}

	void removeListFooter() {
		if (listView != null && footer != null) {
			listView.removeFooterView(footer);
		}
	}

	void listRefreshing(boolean bool) {
		if (swipe_container != null) {
			swipe_container.setRefreshing(bool);
		}
		if (recyclerviewSwipeContainer != null) {
			recyclerviewSwipeContainer.setRefreshing(bool);
		}
	}

	void loadMoreOpen() {
		mLoadMoreIsAtBottom = true;
		mLoadMoreRequestedItemCount = 0;
	}

	/**
	 * RecyclerView 替代ListView GradView 可以实现瀑布流
	 */

	private int							recyclerviewId;

	private int							recyclerviewColorResIds[];

	private int							recyclerviewSwipRefreshId;

	private RecyclerView				recyclerView;

	private J2WRVAdapterItem			j2WRVAdapterItem;

	private HeaderRecyclerViewAdapterV1	headerRecyclerViewAdapterV1;

	private RecyclerView.LayoutManager	layoutManager;					// 布局管理器

	private RecyclerView.ItemAnimator	itemAnimator;					// 动画

	private RecyclerView.ItemDecoration	itemDecoration;				// 分割线

	private SwipeRefreshLayout			recyclerviewSwipeContainer;

	private J2WRefreshListener			recyclerviewJ2WRefreshListener;

	// 获取
	int getRecyclerviewId() {
		return recyclerviewId;
	}

	HeaderRecyclerViewAdapterV1 getJ2WRVAdapterItem() {
		return headerRecyclerViewAdapterV1;
	}

	RecyclerView.LayoutManager getLayoutManager() {
		return layoutManager;
	}

	RecyclerView.ItemAnimator getItemAnimator() {
		return itemAnimator;
	}

	RecyclerView.ItemDecoration getItemDecoration() {
		return itemDecoration;
	}

	int[] getRecyclerviewColorResIds() {
		return recyclerviewColorResIds;
	}

	int getRecyclerviewSwipRefreshId() {
		return recyclerviewSwipRefreshId;
	}

	// 设置
	public void recyclerviewId(int recyclerviewId) {
		this.recyclerviewId = recyclerviewId;
	}

	public void recyclerviewAdapterItem(J2WRVAdapterItem j2WRVAdapterItem) {
		this.j2WRVAdapterItem = j2WRVAdapterItem;
	}

	public void recyclerviewLinearLayoutManager(int direction, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		this.layoutManager = new LinearLayoutManager(mContext, direction, reverseLayout == null ? true : false);
		this.itemDecoration = itemDecoration == null ? new DividerItemDecoration(mContext, direction) : itemDecoration;
		this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
	}

	public void recyclerviewGridLayoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		this.layoutManager = new GridLayoutManager(mContext, spanCount, direction, reverseLayout == null ? true : false);
		this.itemDecoration = itemDecoration == null ? new DividerGridItemDecoration(mContext) : itemDecoration;
		this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
	}

	public void recyclerviewStaggeredGridyoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		this.layoutManager = new StaggeredGridLayoutManager(spanCount, direction);
		this.itemDecoration = itemDecoration == null ? new DividerGridItemDecoration(mContext) : itemDecoration;
		this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
	}

	public void recyclerviewColorResIds(int... recyclerviewColorResIds) {
		this.recyclerviewColorResIds = recyclerviewColorResIds;
	}

	public void recyclerviewSwipRefreshId(int recyclerviewSwipRefreshId, J2WRefreshListener recyclerviewJ2WRefreshListener) {
		this.recyclerviewSwipRefreshId = recyclerviewSwipRefreshId;
		this.recyclerviewJ2WRefreshListener = recyclerviewJ2WRefreshListener;
	}

	/**
	 * ViewPager
	 */

	private int							viewpagerId;

	private int							tabsId;

	private int							tabsType;

	private int							viewPageroffScreenPageLimit	= 1;

	private J2WViewPager				j2WViewPager;

	private PagerSlidingTabStrip		tabs;

	private LinearLayout				customView;

	private int[]						showItems;

	private J2WViewPagerChangeListener	viewPagerChangeListener;

	private J2WTabsCustomListener		j2WTabsCustomListener;

	private J2WViewPagerAdapter			j2WViewPagerAdapter;

	private FragmentManager				fragmentManager;

	private int							customLayout;

	/**
	 * ViewPager tabs 类型
	 */
	public static final int				TABS_TYPE_CUSTOM			= 7777 << 1;

	public static final int				TABS_TYPE_COUNT				= 6666 << 1;

	public static final int				TABS_TYPE_ICON				= 5555 << 1;

	public static final int				TABS_TYPE_DEFAULT			= 0;

	// Tabs 属性

	private boolean						tabsShouldExpand			= true;

	private int							tabsDividerColor			= android.R.color.transparent;

	private int							tabsUnderlineHeight			= 1;

	private int							tabsUnderlineColor			= android.R.color.transparent;

	private int							tabsIndicatorHeight			= 1;

	private int							tabsTextSize				= 12;

	private int							tabsIndicatorColor			= R.color.J2WTabsBackground;

	private int							tabsSelectedTextColor		= R.color.J2WTabsBackground;

	private int							tabsTextColor				= R.color.J2WTabsText;

	private int							tabsBackgroundResource		= android.R.color.transparent;

	private int							tabsItemBackground			= 0;

	private int							tabsTabWidth				= 0;

	private boolean						tabsIsCurrentItemAnimation	= false;

	private int							tabsMargins					= 0;

	private int							tabsPaddingLeftRight		= 20;

	// 获取
	int getViewpagerId() {
		return viewpagerId;
	}

	int getTabsId() {
		return tabsId;
	}

	int getTabsType() {
		return tabsType;
	}

	int getCustomLayout() {
		return customLayout;
	}

	int getViewPageroffScreenPageLimit() {
		return viewPageroffScreenPageLimit;
	}

	PagerSlidingTabStrip getTabs() {
		return tabs;
	}

	boolean getTabsShouldExpand() {
		return tabsShouldExpand;
	}

	int getTabsDividerColor() {
		return tabsDividerColor;
	}

	int getTabsUnderlineHeight() {
		return tabsUnderlineHeight;
	}

	int getTabsUnderlineColor() {
		return tabsUnderlineColor;
	}

	int getTabsIndicatorHeight() {
		return tabsIndicatorHeight;
	}

	int getTabsTextSize() {
		return tabsTextSize;
	}

	int getTabsIndicatorColor() {
		return tabsIndicatorColor;
	}

	int getTabsSelectedTextColor() {
		return tabsSelectedTextColor;
	}

	int getTabsTextColor() {
		return tabsTextColor;
	}

	int getTabsItemBackground() {
		return tabsItemBackground;
	}

	int getTabsBackgroundResource() {
		return tabsBackgroundResource;
	}

	int getTabsTabWidth() {
		return tabsTabWidth;
	}

	boolean getTabsIsCurrentItemAnimation() {
		return tabsIsCurrentItemAnimation;
	}

	int getTabsMargins() {
		return tabsMargins;
	}

	int getTabsPaddingLeftRight() {
		return tabsPaddingLeftRight;
	}

	J2WViewPagerChangeListener getViewPagerChangeListener() {
		return viewPagerChangeListener;
	}

	J2WTabsCustomListener getJ2WTabsCustomListener() {
		return j2WTabsCustomListener;
	}

	J2WViewPagerAdapter getViewPagerAdapter() {
		return j2WViewPagerAdapter;
	}

	// 设置
	public void viewPagerId(int viewpagerId, FragmentManager fragmentManager) {
		this.viewpagerId = viewpagerId;
		this.fragmentManager = fragmentManager;
	}

	public void viewPagerTabsId(int tabsId, int tabsType) {
		this.tabsId = tabsId;
		this.tabsType = tabsType;
	}

	public void viewPagerChangeListener(J2WViewPagerChangeListener viewPagerChangeListener) {
		this.viewPagerChangeListener = viewPagerChangeListener;
	}

	public void tabsCustomListener(J2WTabsCustomListener j2WTabsCustomListener) {
		this.j2WTabsCustomListener = j2WTabsCustomListener;
	}

	public void viewPageroffScreenPageLimit(int viewPageroffScreenPageLimit) {
		this.viewPageroffScreenPageLimit = viewPageroffScreenPageLimit;
	}

	public void tabsShouldExpand(boolean tabsShouldExpand) {
		this.tabsShouldExpand = tabsShouldExpand;
	}

	public void tabsDividerColor(int tabsDividerColor) {
		this.tabsDividerColor = tabsDividerColor;
	}

	public void tabsUnderlineHeight(int tabsUnderlineHeight) {
		this.tabsUnderlineHeight = tabsUnderlineHeight;
	}

	public void tabsUnderlineColor(int tabsUnderlineColor) {
		this.tabsUnderlineColor = tabsUnderlineColor;
	}

	public void tabsIndicatorHeight(int tabsIndicatorHeight) {
		this.tabsIndicatorHeight = tabsIndicatorHeight;
	}

	public void tabsTextSize(int tabsTextSize) {
		this.tabsTextSize = tabsTextSize;
	}

	public void tabsIndicatorColor(int tabsIndicatorColor) {
		this.tabsIndicatorColor = tabsIndicatorColor;
	}

	public void tabsSelectedTextColor(int tabsSelectedTextColor) {
		this.tabsSelectedTextColor = tabsSelectedTextColor;
	}

	public void tabsTextColor(int tabsTextColor) {
		this.tabsTextColor = tabsTextColor;
	}

	public void tabsBackgroundResource(int tabsBackgroundResource) {
		this.tabsBackgroundResource = tabsBackgroundResource;
	}

	public void tabsItemBackground(int tabsItemBackground) {
		this.tabsItemBackground = tabsItemBackground;
	}

	public void tabsMargins(int tabsMargins) {
		this.tabsMargins = tabsMargins;
	}

	public void tabsPaddingLeftRight(int tabsPaddingLeftRight) {
		this.tabsPaddingLeftRight = tabsPaddingLeftRight;
	}

	public void tabsTabWidth(int tabsTabWidth) {
		this.tabsTabWidth = tabsTabWidth;
	}

	public void tabsIsCurrentItemAnimation(boolean tabsIsCurrentItemAnimation) {
		this.tabsIsCurrentItemAnimation = tabsIsCurrentItemAnimation;
	}

	public void tabsCustomLayout(int customLayout,int... showItems) {
		this.customLayout = customLayout;
		this.showItems = showItems;
	}

	/**
	 * 创建
	 *
	 * @return
	 */
	View create() {
		L.i("J2WBuilder.create()");
		/** layout **/
		createLayout();
		/** listview **/
		createListView(contentRoot);
		/** recyclerview **/
		createRecyclerView(contentRoot);
		/** viewpager **/
		createViewPager(contentRoot);
		/** actoinbar **/
		View toolbarRoot = createActionbar(contentRoot);
		return toolbarRoot;
	}

	/**
	 * 清空所有
	 */
	void detach() {
		// 基础清除
		detachLayout();
		// actionbar清除
		detachActionbar();
		// listview清除
		detachListView();
		// recyclerview清楚
		detachRecyclerView();
		// viewpager清楚
		detachViewPager();
	}

	/**
	 * 布局
	 *
	 * @return
	 */
	private void createLayout() {
		contentRoot = new FrameLayout(mContext);
		// 内容布局
		J2WCheckUtils.checkArgument(getLayoutId() > 0, "请给出布局文件ID");
		layoutContent = mInflater.inflate(getLayoutId(), null, false);
		J2WCheckUtils.checkNotNull(layoutContent, "无法根据布局文件ID,获取layoutContent");
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		contentRoot.addView(layoutContent, layoutParams);

		// 进度条

		layoutLoadingId = layoutLoadingId > 0 ? layoutLoadingId : J2WHelper.getInstance().layoutLoading();
		if (layoutLoadingId > 0) {
			vsLoading = new ViewStub(mContext);
			vsLoading.setLayoutResource(layoutLoadingId);
			contentRoot.addView(vsLoading, layoutParams);
		}

		// 空布局
		layoutEmptyId = layoutEmptyId > 0 ? layoutEmptyId : J2WHelper.getInstance().layoutEmpty();
		if (layoutEmptyId > 0) {
			layoutEmpty = mInflater.inflate(layoutEmptyId, null, false);
			J2WCheckUtils.checkNotNull(layoutEmpty, "无法根据布局文件ID,获取layoutEmpty");
			contentRoot.addView(layoutEmpty, layoutParams);
			layoutEmpty.setVisibility(View.GONE);
		}

		// 业务错误布局
		layoutBizErrorId = layoutBizErrorId > 0 ? layoutBizErrorId : J2WHelper.getInstance().layoutBizError();
		if (layoutBizErrorId > 0) {
			layoutBizError = mInflater.inflate(layoutBizErrorId, null, false);
			J2WCheckUtils.checkNotNull(layoutBizError, "无法根据布局文件ID,获取layoutBizError");
			contentRoot.addView(layoutBizError, layoutParams);
			layoutBizError.setVisibility(View.GONE);
		}

		// 网络错误布局
		layoutHttpErrorId = layoutHttpErrorId > 0 ? layoutHttpErrorId : J2WHelper.getInstance().layoutHttpError();
		if (layoutHttpErrorId > 0) {
			J2WCheckUtils.checkArgument(layoutHttpErrorId > 0, "网络错误布局Id不能为空,重写公共布局Application.layoutBizError 或者 在Buider.layout里设置");
			layoutHttpError = mInflater.inflate(layoutHttpErrorId, null, false);
			J2WCheckUtils.checkNotNull(layoutHttpError, "无法根据布局文件ID,获取layoutHttpError");
			contentRoot.addView(layoutHttpError, layoutParams);
			layoutHttpError.setVisibility(View.GONE);
		}
	}

	private void detachLayout() {
		mContext = null;
		contentRoot = null;
		mInflater = null;
		layoutContent = null;
		layoutBizError = null;
		layoutHttpError = null;
		layoutEmpty = null;
	}

	/**
	 * 标题栏
	 *
	 * @param view
	 */
	private View createActionbar(View view) {
		if (isOpenToolbar()) {
			final LinearLayout toolbarRoot = new LinearLayout(mContext);
			toolbarRoot.setOrientation(LinearLayout.VERTICAL);

			mInflater.inflate(getToolbarLayoutId(), toolbarRoot, true);

			toolbarRoot.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

			toolbar = ButterKnife.findById(toolbarRoot, getToolbarId());

			J2WCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");
			mContext.setSupportActionBar(toolbar);
			if (getToolbarDrawerId() > 0) {
				DrawerLayout drawerLayout = ButterKnife.findById(view, getToolbarDrawerId());
				J2WCheckUtils.checkNotNull(drawerLayout, "无法根据布局文件ID,获取DrawerLayout");
				ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(mContext, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
				mDrawerToggle.syncState();
				drawerLayout.setDrawerListener(mDrawerToggle);
			}
			// 添加点击事件
			if (getMenuListener() != null) {
				toolbar.setOnMenuItemClickListener(getMenuListener());
			}
			toolbarRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

			return toolbarRoot;
		} else if (isOpenCustomToolbar()) {
			toolbar = ButterKnife.findById(view, getToolbarId());
			J2WCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");
			if (getToolbarDrawerId() > 0) {
				DrawerLayout drawerLayout = ButterKnife.findById(view, getToolbarDrawerId());
				J2WCheckUtils.checkNotNull(drawerLayout, "无法根据布局文件ID,获取DrawerLayout");
				ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(mContext, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
				mDrawerToggle.syncState();
				drawerLayout.setDrawerListener(mDrawerToggle);
			}
			// 添加点击事件
			if (getMenuListener() != null) {
				toolbar.setOnMenuItemClickListener(getMenuListener());
			}
			return view;
		} else {
			return view;
		}
	}

	private void detachActionbar() {
		menuListener = null;
	}

	/**
	 * 列表
	 *
	 * @param view
	 */
	private void createListView(View view) {
		if (getListId() > 0) {
			listView = ButterKnife.findById(view, getListId());
			J2WCheckUtils.checkNotNull(listView, "无法根据布局文件ID,获取ListView");
			// 添加头布局
			if (getListHeaderLayoutId() != 0) {
				header = mInflater.inflate(getListHeaderLayoutId(), null, false);
				J2WCheckUtils.checkNotNull(header, "无法根据布局文件ID,获取ListView 头布局");
				addListHeader();
			}
			// 添加尾布局
			if (getListFooterLayoutId() != 0) {
				footer = mInflater.inflate(getListFooterLayoutId(), null, false);
				J2WCheckUtils.checkNotNull(footer, "无法根据布局文件ID,获取ListView 尾布局");
				addListFooter();
			}
			// 设置上拉和下拉事件
			if (getSwipRefreshId() != 0) {
				swipe_container = ButterKnife.findById(view, getSwipRefreshId());
				J2WCheckUtils.checkNotNull(swipe_container, "无法根据布局文件ID,获取ListView的SwipRefresh下载刷新布局");
				J2WCheckUtils.checkNotNull(j2WRefreshListener, " ListView的SwipRefresh 下拉刷新和上拉加载事件没有设置");
				swipe_container.setOnRefreshListener(j2WRefreshListener);// 下载刷新
				listView.setOnScrollListener(this);// 加载更多
			}
			// 设置进度颜色
			if (getSwipeColorResIds() != null) {
				J2WCheckUtils.checkNotNull(swipe_container, "无法根据布局文件ID,获取ListView的SwipRefresh下载刷新布局");
				swipe_container.setColorSchemeResources(getSwipeColorResIds());
			}
			// 添加点击事件
			if (getItemListener() != null) {
				listView.setOnItemClickListener(getItemListener());
			}
			if (getItemLongListener() != null) {
				listView.setOnItemLongClickListener(getItemLongListener());
			}
			// 创建适配器
			j2WListAdapter = j2WListViewMultiLayout == null ? new J2WListAdapter(mContext, getJ2WAdapterItem()) : new J2WListAdapter(mContext, j2WListViewMultiLayout);
			J2WCheckUtils.checkNotNull(j2WListAdapter, "适配器创建失败");
			// 设置适配器
			listView.setAdapter(j2WListAdapter);
		}
	}

	private void detachListView() {
		j2WListAdapter = null;
		listView = null;
		header = null;
		footer = null;
		j2WAdapterItem = null;
		j2WListViewMultiLayout = null;
		itemListener = null;
		itemLongListener = null;
		swipe_container = null;
		colorResIds = null;
	}

	/**
	 * 列表
	 *
	 * @param view
	 */
	private void createRecyclerView(View view) {
		if (getRecyclerviewId() > 0) {
			recyclerView = ButterKnife.findById(view, getRecyclerviewId());
			J2WCheckUtils.checkNotNull(recyclerView, "无法根据布局文件ID,获取recyclerView");
			J2WCheckUtils.checkNotNull(layoutManager, "LayoutManger不能为空");
			J2WCheckUtils.checkNotNull(j2WRVAdapterItem, "J2WRVAdapterItem适配器不能为空");
			recyclerView.setLayoutManager(layoutManager);
			// 扩展适配器
			headerRecyclerViewAdapterV1 = new HeaderRecyclerViewAdapterV1(j2WRVAdapterItem);
			recyclerView.setAdapter(headerRecyclerViewAdapterV1);
			// 设置Item增加、移除动画
			recyclerView.setItemAnimator(getItemAnimator());
			// 添加分割线
			recyclerView.addItemDecoration(getItemDecoration());
			// 优化
			recyclerView.setHasFixedSize(true);
			// 设置上拉和下拉事件
			if (getRecyclerviewSwipRefreshId() != 0) {
				recyclerviewSwipeContainer = ButterKnife.findById(view, getRecyclerviewSwipRefreshId());
				J2WCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
				J2WCheckUtils.checkNotNull(recyclerviewJ2WRefreshListener, " recyclerview的SwipRefresh 下拉刷新和上拉加载事件没有设置");
				recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

					@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
						super.onScrollStateChanged(recyclerView, newState);
						if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
							if (recyclerviewJ2WRefreshListener.onScrolledToBottom()) {
								mLoadMoreRequestedItemCount = headerRecyclerViewAdapterV1.getItemCount();
								mLoadMoreIsAtBottom = false;
							}
						}
					}

					@Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
						super.onScrolled(recyclerView, dx, dy);
						if (layoutManager instanceof LinearLayoutManager) {
							int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
							mLoadMoreIsAtBottom = headerRecyclerViewAdapterV1.getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == headerRecyclerViewAdapterV1.getItemCount();
						}
					}
				});// 加载更多
				recyclerviewSwipeContainer.setOnRefreshListener(recyclerviewJ2WRefreshListener);// 下载刷新
			}
			// 设置进度颜色
			if (getRecyclerviewColorResIds() != null) {
				J2WCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
				recyclerviewSwipeContainer.setColorSchemeResources(getSwipeColorResIds());
			}
		}
	}

	private void detachRecyclerView() {
		recyclerView = null;
		j2WRVAdapterItem = null;
		layoutManager = null;
		itemAnimator = null;
		itemDecoration = null;
		recyclerviewSwipeContainer = null;
		recyclerviewJ2WRefreshListener = null;
		headerRecyclerViewAdapterV1 = null;
	}

	/**
	 * Viewpager
	 *
	 * @param view
	 */

	private void createViewPager(View view) {
		if (getViewpagerId() > 0) {
			j2WViewPager = ButterKnife.findById(view, getViewpagerId());
			J2WCheckUtils.checkNotNull(j2WViewPager, "无法根据布局文件ID,获取ViewPager");

			if (getTabsId() > 0) {
				tabs = ButterKnife.findById(view, getTabsId());
				J2WCheckUtils.checkNotNull(tabs, "无法根据布局文件ID,获取tabs");
				// 设置Tab是自动填充满屏幕的
				tabs.setShouldExpand(getTabsShouldExpand());
				// 设置Tab的分割线是透明的
				tabs.setDividerColor(getTabsDividerColor());
				// 设置Tab底部线的高度
				tabs.setUnderlineHeight(getTabsUnderlineHeight());
				// 设置Tab底部线的颜色
				tabs.setUnderlineColor(getTabsUnderlineColor());
				// 设置Tab 指示灯的高度
				tabs.setIndicatorHeight(getTabsIndicatorHeight());
				// 设置Tab标题文字的大小
				tabs.setTextSize(getTabsTextSize());
				// 设置Tab Indicator 指示灯的颜色
				tabs.setIndicatorColor(getTabsIndicatorColor());
				// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
				tabs.setSelectedTextColor(getTabsSelectedTextColor());
				// 设置Tab文字颜色
				tabs.setTextColor(getTabsTextColor());
				// 取消点击Tab时的背景色
				tabs.setTabItemBackground(getTabsItemBackground());
				// 设置背景颜色
				tabs.setBackgroundResource(getTabsBackgroundResource());
				// 设置Tabs宽度
				tabs.setTabWidth(getTabsTabWidth());
				// 设置Tasb切换动画
				tabs.setIsCurrentItemAnimation(getTabsIsCurrentItemAnimation());
				// 设置间距
				tabs.setTabMarginsLeftRight(getTabsMargins());
				// 设置padding
				tabs.setTabPaddingLeftRight(getTabsPaddingLeftRight());
			} else if (getCustomLayout() > 0) {
				customView = ButterKnife.findById(view, getCustomLayout());
				J2WCheckUtils.checkNotNull(customView, "无法根据布局文件ID,获取tabs");
			}
			J2WCheckUtils.checkNotNull(fragmentManager, "fragmentManager不能为空");

			j2WViewPagerAdapter = new J2WViewPagerAdapter(getTabsType(), fragmentManager, tabs, customView,showItems, j2WViewPager, viewPagerChangeListener, j2WTabsCustomListener);

			j2WViewPager.setAdapter(j2WViewPagerAdapter);
			// 间隔距离
			final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
			// 设置距离
			j2WViewPager.setPageMargin(pageMargin);
			// 预留数量
			j2WViewPager.setOffscreenPageLimit(getViewPageroffScreenPageLimit());
			if (tabs != null) {
				// 设置给头部
				tabs.setViewPager(j2WViewPager);
			}
		}
	}

	private void detachViewPager() {
		j2WViewPager = null;
		tabs = null;
		if (j2WViewPagerAdapter != null) {
			j2WViewPagerAdapter.clearData();
		}
		j2WViewPagerAdapter = null;
		fragmentManager = null;
		viewPagerChangeListener = null;
		j2WTabsCustomListener = null;
	}

	/** 自动加载更多 **/
	@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
			if (j2WRefreshListener.onScrolledToBottom()) {
				mLoadMoreRequestedItemCount = view.getCount();
				mLoadMoreIsAtBottom = false;
			}
		}
	}

	@Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mLoadMoreIsAtBottom = totalItemCount > mLoadMoreRequestedItemCount && firstVisibleItem + visibleItemCount == totalItemCount;
	}
}
