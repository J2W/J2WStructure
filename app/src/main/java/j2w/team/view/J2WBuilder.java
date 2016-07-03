package j2w.team.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.modules.log.L;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.common.view.J2WSwipeBackLayout;
import j2w.team.common.view.J2WViewPager;
import j2w.team.modules.methodProxy.Background;
import j2w.team.structure.R;
import j2w.team.view.adapter.J2WAdapterItem;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.J2WListViewMultiLayout;
import j2w.team.view.adapter.J2WTabsCustomListener;
import j2w.team.view.adapter.J2WViewPagerAdapter;
import j2w.team.view.adapter.recycleview.J2WRVAdapter;
import j2w.team.view.adapter.recycleview.J2WRVAdapterItem;
import j2w.team.view.adapter.recycleview.stickyheader.J2WStickyHeaders;
import j2w.team.view.adapter.recycleview.stickyheader.StickyRecyclerHeadersDecoration;
import j2w.team.view.adapter.recycleview.stickyheader.StickyRecyclerHeadersTouchListener;
import j2w.team.view.common.J2WFooterListener;
import j2w.team.view.common.J2WRefreshListener;
import j2w.team.view.common.J2WViewPagerChangeListener;

/**
 * @创建人 sky
 * @创建时间 15/7/16 下午8:12
 * @类描述 编辑
 */
public class J2WBuilder implements AbsListView.OnScrollListener {

	/**
	 * UI
	 **/
	private J2WView			j2WView;

	/**
	 * 布局加载器
	 **/
	private LayoutInflater	mInflater;

	/**
	 * 构造器
	 *
	 * @param j2WActivity
	 * @param inflater
	 */
	public J2WBuilder(@NonNull J2WActivity j2WActivity, @NonNull LayoutInflater inflater) {
		j2WView = new J2WView();
		j2WView.initUI(j2WActivity);
		this.mInflater = inflater;
	}

	/**
	 * 构造器
	 *
	 * @param j2WFragment
	 * @param inflater
	 */
	public J2WBuilder(@NonNull J2WFragment j2WFragment, @NonNull LayoutInflater inflater) {
		j2WView = new J2WView();
		j2WView.initUI(j2WFragment);
		this.mInflater = inflater;
	}

	/**
	 * 构造器
	 *
	 * @param j2WDialogFragment
	 * @param inflater
	 */
	public J2WBuilder(@NonNull J2WDialogFragment j2WDialogFragment, @NonNull LayoutInflater inflater) {
		j2WView = new J2WView();
		j2WView.initUI(j2WDialogFragment);
		this.mInflater = inflater;
	}

	@Nullable public J2WView getJ2WView() {
		return j2WView;
	}

	/**
	 * 布局ID
	 */
	private int			layoutId;

	private FrameLayout	contentRoot;

	private int			contentRootColor;

	int getLayoutId() {
		return layoutId;
	}

	public void layoutId(@LayoutRes int layoutId) {
		this.layoutId = layoutId;
	}

	public void layoutColor(@ColorRes int color) {
		this.contentRootColor = color;
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
	public void layoutLoadingId(@LayoutRes int layoutLoadingId) {
		this.layoutLoadingId = layoutLoadingId;
	}

	public void layoutEmptyId(@LayoutRes int layoutEmptyId) {
		this.layoutEmptyId = layoutEmptyId;
	}

	public void layoutBizErrorId(@LayoutRes int layoutBizErrorId) {
		this.layoutBizErrorId = layoutBizErrorId;
	}

	public void layoutHttpErrorId(@LayoutRes int layoutHttpErrorId) {
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
		if (layoutLoadingId < 1) {
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

	void changeShowAnimation(@NonNull View view, boolean visible) {
		if (view == null) {
			return;
		}
		Animation anim;
		if (visible) {
			if (view.getVisibility() == View.VISIBLE) {
				return;
			}
			view.setVisibility(View.VISIBLE);
			anim = AnimationUtils.loadAnimation(j2WView.activity(), android.R.anim.fade_in);
		} else {
			if (view.getVisibility() == View.GONE) {
				return;
			}
			view.setVisibility(View.GONE);
			anim = AnimationUtils.loadAnimation(j2WView.activity(), android.R.anim.fade_out);
		}

		anim.setDuration(j2WView.activity().getResources().getInteger(android.R.integer.config_shortAnimTime));
		view.startAnimation(anim);
	}

	/**
	 * 键盘
	 */
	private boolean autoShouldHideInput = true;

	public void autoKeyBoard(boolean auto) {
		this.autoShouldHideInput = auto;
	}

	boolean isAutoKeyBoard() {
		return this.autoShouldHideInput;
	}

	/**
	 * swipback
	 */

	private boolean									isOpenSwipBackLayout;

	private ImageView								ivShadow;

	private J2WSwipeBackLayout.DragEdge				dragEdge;

	private J2WSwipeBackLayout.SwipeBackListener	listener;

	public void swipBackIsOpen(boolean isOpenSwipBackLayout) {
		this.isOpenSwipBackLayout = isOpenSwipBackLayout;
	}

	public void swipBackDragEdge(@NonNull J2WSwipeBackLayout.DragEdge dragEdge) {
		this.dragEdge = dragEdge;
	}

	public void swipBackListener(@NonNull J2WSwipeBackLayout.SwipeBackListener listener) {
		this.listener = listener;
	}

	// 获取
	boolean isOpenSwipBackLayout() {
		return isOpenSwipBackLayout;
	}

	@Nullable J2WSwipeBackLayout.DragEdge getDragEdge() {
		return dragEdge;
	}

	@Nullable J2WSwipeBackLayout.SwipeBackListener getListener() {
		return listener;
	}

	/**
	 * TintManger
	 */
	private int tintColor;

	int getTintColor() {
		return tintColor;
	}

	boolean isTintColor() {
		return tintColor > 0;
	}

	public void tintColor(@ColorRes int tintColor) {
		this.tintColor = tintColor;
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

	private boolean							isOpenToolbarBack;

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

	boolean isOpenToolbarBack() {
		return isOpenToolbarBack;
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

	@Nullable Toolbar getToolbar() {
		return toolbar;
	}

	@Nullable Toolbar.OnMenuItemClickListener getMenuListener() {
		return menuListener;
	}

	// 设置

	public void toolbarId(@IdRes int toolbarId) {
		this.toolbarId = toolbarId;
		this.isOpenCustomToolbar = true;
	}

	public void toolbarLayoutId(@LayoutRes int toolbarLayoutId) {
		this.toolbarLayoutId = toolbarLayoutId;
	}

	public void toolbarDrawerId(@DrawableRes int toolbarDrawerId) {
		this.toolbarDrawerId = toolbarDrawerId;
	}

	public void toolbarMenuListener(@NonNull Toolbar.OnMenuItemClickListener menuListener) {
		this.menuListener = menuListener;
	}

	public void toolbarIsBack(@NonNull boolean isOpenToolbarBack) {
		this.isOpenToolbarBack = isOpenToolbarBack;
	}

	public void toolbarIsOpen(@NonNull boolean isOpenToolbar) {
		this.isOpenToolbar = isOpenToolbar;
	}

	public void toolbarMenuId(@IdRes int toolbarMenuId) {
		this.toolbarMenuId = toolbarMenuId;
	}

	/**
	 * EventBus开关
	 */
	private boolean	isOpenEventBus;

	private boolean	isNotCloseEvent;	// 是否关闭

	// 获取
	boolean isOpenEventBus() {
		return isOpenEventBus;
	}

	boolean isNotCloseEventBus() {
		return isNotCloseEvent;
	}

	// 设置
	public void isOpenEventBus(boolean isOpenEventBus) {
		this.isOpenEventBus = isOpenEventBus;
	}

	public void isNoCloseEventBus(boolean isNotCloseEvent) {
		this.isNotCloseEvent = isNotCloseEvent;
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

	@Nullable J2WAdapterItem getJ2WAdapterItem() {
		return j2WAdapterItem;
	}

	@Nullable J2WListViewMultiLayout getJ2WListViewMultiLayout() {
		return j2WListViewMultiLayout;
	}

	@Nullable AdapterView.OnItemClickListener getItemListener() {
		return itemListener;
	}

	@Nullable AdapterView.OnItemLongClickListener getItemLongListener() {
		return itemLongListener;
	}

	int getListHeaderLayoutId() {
		return listHeaderLayoutId;
	}

	int getListFooterLayoutId() {
		return listFooterLayoutId;
	}

	@Nullable J2WListAdapter getAdapter() {
		J2WCheckUtils.checkNotNull(j2WListAdapter, "适配器没有初始化");
		return j2WListAdapter;
	}

	@Nullable ListView getListView() {
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
	public void listHeaderLayoutId(@LayoutRes int listHeaderLayoutId) {
		this.listHeaderLayoutId = listHeaderLayoutId;
	}

	public void listFooterLayoutId(@LayoutRes int listFooterLayoutId) {
		this.listFooterLayoutId = listFooterLayoutId;
	}

	public void listViewOnItemClick(@NonNull AdapterView.OnItemClickListener itemListener) {
		this.itemListener = itemListener;
	}

	public void listViewOnItemLongClick(@NonNull AdapterView.OnItemLongClickListener itemLongListener) {
		this.itemLongListener = itemLongListener;
	}

	public void listViewId(@IdRes int listId, @NonNull J2WAdapterItem j2WAdapterItem) {
		this.listId = listId;
		this.j2WAdapterItem = j2WAdapterItem;
	}

	public void listViewId(@IdRes int listId, @NonNull J2WListViewMultiLayout j2WListViewMultiLayout) {
		this.listId = listId;
		this.j2WListViewMultiLayout = j2WListViewMultiLayout;
	}

	public void listSwipRefreshId(@IdRes int swipRefreshId, @NonNull J2WRefreshListener j2WRefreshListener) {
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

	private int															recyclerviewId;

	private int															recyclerviewColorResIds[];

	private int															recyclerviewSwipRefreshId;

	private J2WFooterListener											j2WFooterListener;

	private RecyclerView												recyclerView;

	private J2WRVAdapterItem											j2WRVAdapterItem;

	private J2WRVAdapter												j2WRVAdapter;

	private RecyclerView.LayoutManager									layoutManager;					// 布局管理器

	private RecyclerView.ItemAnimator									itemAnimator;					// 动画

	private RecyclerView.ItemDecoration									itemDecoration;					// 分割线

	private SwipeRefreshLayout											recyclerviewSwipeContainer;

	private J2WRefreshListener											recyclerviewJ2WRefreshListener;

	private StickyRecyclerHeadersTouchListener.OnHeaderClickListener	onHeaderClickListener;

	private boolean														isHeaderFooter;

	// 获取
	int getRecyclerviewId() {
		return recyclerviewId;
	}

	RecyclerView getRecyclerView() {
		J2WCheckUtils.checkNotNull(recyclerView, "RecyclerView没有找到，查看布局里是否存在~");
		return recyclerView;
	}

	@Deprecated J2WRVAdapterItem getJ2WRVAdapterItem() {
		return j2WRVAdapterItem;
	}

	@Nullable J2WRVAdapter getJ2WRVAdapterItem2() {
		return j2WRVAdapter;
	}

	@Nullable public RecyclerView.LayoutManager getLayoutManager() {
		return layoutManager;
	}

	@Nullable RecyclerView.ItemAnimator getItemAnimator() {
		return itemAnimator;
	}

	@Nullable RecyclerView.ItemDecoration getItemDecoration() {
		return itemDecoration;
	}

	int[] getRecyclerviewColorResIds() {
		return recyclerviewColorResIds;
	}

	int getRecyclerviewSwipRefreshId() {
		return recyclerviewSwipRefreshId;
	}

	public void recyclerviewGridOpenHeaderFooter(boolean bool) {
		this.isHeaderFooter = bool;
	}

	// 设置
	public void recyclerviewId(@IdRes int recyclerviewId) {
		this.recyclerviewId = recyclerviewId;
	}

	public void recyclerviewLoadingMore(@NonNull J2WFooterListener j2WFooterListener) {
		this.j2WFooterListener = j2WFooterListener;
	}

	public void recyclerviewStickyHeaderClick(@NonNull StickyRecyclerHeadersTouchListener.OnHeaderClickListener onHeaderClickListener) {
		this.onHeaderClickListener = onHeaderClickListener;
	}

	@Deprecated public void recyclerviewAdapterItem(@NonNull J2WRVAdapterItem j2WRVAdapterItem) {
		this.j2WRVAdapterItem = j2WRVAdapterItem;
	}

	public void recyclerviewAdapter(@NonNull J2WRVAdapter j2WRVAdapter) {
		this.j2WRVAdapter = j2WRVAdapter;
	}

	public void recyclerviewGridManager(@NonNull GridLayoutManager gridLayoutManager) {
		this.layoutManager = gridLayoutManager;
	}

	public void recyclerviewLinearManager(@NonNull LinearLayoutManager linearLayoutManager) {
		this.layoutManager = linearLayoutManager;
	}

	public void recyclerviewAnimator(@NonNull RecyclerView.ItemAnimator itemAnimator) {
		this.itemAnimator = itemAnimator;
	}

	public void recyclerviewLinearLayoutManager(int direction, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		boolean reverse = false;
		if (reverseLayout != null && reverseLayout.length > 0) {
			reverse = reverseLayout[0];
		}
		this.layoutManager = new LinearLayoutManager(j2WView.activity(), direction, reverse);
		this.itemDecoration = itemDecoration;
		this.itemAnimator = itemAnimator;
	}

	public void recyclerviewGridLayoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		boolean reverse = false;
		if (reverseLayout != null && reverseLayout.length > 0) {
			reverse = reverseLayout[0];
		}
		this.layoutManager = new GridLayoutManager(j2WView.activity(), spanCount, direction, reverse);
		this.itemDecoration = itemDecoration;
		this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
	}

	public void recyclerviewStaggeredGridyoutManager(int direction, int spanCount, RecyclerView.ItemDecoration itemDecoration, RecyclerView.ItemAnimator itemAnimator, boolean... reverseLayout) {
		this.layoutManager = new StaggeredGridLayoutManager(spanCount, direction);
		this.itemDecoration = itemDecoration;
		this.itemAnimator = itemAnimator == null ? new DefaultItemAnimator() : itemAnimator;
	}

	public void recyclerviewColorResIds(int... recyclerviewColorResIds) {
		this.recyclerviewColorResIds = recyclerviewColorResIds;
	}

	public void recyclerviewSwipRefreshId(@IdRes int recyclerviewSwipRefreshId, @NonNull J2WRefreshListener recyclerviewJ2WRefreshListener) {
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

	private LinearLayout				customView;

	private int[]						showItems;

	private J2WViewPagerChangeListener	viewPagerChangeListener;

	private J2WTabsCustomListener		j2WTabsCustomListener;

	private J2WViewPagerAdapter			j2WViewPagerAdapter;

	private FragmentManager				fragmentManager;

	private int							customLayout;

	// 获取
	int getViewpagerId() {
		return viewpagerId;
	}

	boolean isViewPager() {
		return viewpagerId > 0;
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

	@Nullable J2WViewPagerChangeListener getViewPagerChangeListener() {
		return viewPagerChangeListener;
	}

	@Nullable J2WTabsCustomListener getJ2WTabsCustomListener() {
		return j2WTabsCustomListener;
	}

	@Nullable J2WViewPagerAdapter getViewPagerAdapter() {
		return j2WViewPagerAdapter;
	}

	@Nullable public J2WViewPager getViewPager() {
		return j2WViewPager;
	}

	// 设置
	public void viewPagerId(@IdRes int viewpagerId,@NonNull FragmentManager fragmentManager) {
		this.viewpagerId = viewpagerId;
		this.fragmentManager = fragmentManager;
	}

	public void viewPagerTabsId(@IdRes int tabsId, int tabsType) {
		this.tabsId = tabsId;
		this.tabsType = tabsType;
	}

	public void viewPagerChangeListener(@NonNull  J2WViewPagerChangeListener viewPagerChangeListener) {
		this.viewPagerChangeListener = viewPagerChangeListener;
	}

	public void tabsCustomListener(@NonNull J2WTabsCustomListener j2WTabsCustomListener) {
		this.j2WTabsCustomListener = j2WTabsCustomListener;
	}

	public void viewPageroffScreenPageLimit(int viewPageroffScreenPageLimit) {
		this.viewPageroffScreenPageLimit = viewPageroffScreenPageLimit;
	}

	public void tabsCustomLayout(@LayoutRes int customLayout, int... showItems) {
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
		View view = createActionbar(contentRoot);
		return view;
	}

	/**
	 * 清空所有
	 */
	void detach() {
		// 清楚
		if (j2WView != null) {
			j2WView.detach();
			j2WView = null;
		}
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
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private void createLayout() {
		contentRoot = new FrameLayout(j2WView.context());
		if (contentRootColor > 0) {
			contentRoot.setBackgroundColor(contentRootColor);
		}
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

		// 内容
		if (getLayoutId() > 0) {
			layoutContent = mInflater.inflate(getLayoutId(), null, false);
			J2WCheckUtils.checkNotNull(layoutContent, "无法根据布局文件ID,获取layoutContent");
			contentRoot.addView(layoutContent, layoutParams);
		}

		// 如果swiplayout打开
		if (isOpenSwipBackLayout()) {
			contentRoot.removeView(layoutContent);

			J2WSwipeBackLayout j2WSwipeBackLayout = new J2WSwipeBackLayout(j2WView.activity());
			j2WSwipeBackLayout.setId(R.id.j2w_swipe_layout);
			ivShadow = new ImageView(j2WView.activity());
			ivShadow.setBackgroundColor(j2WView.activity().getResources().getColor(R.color.j2w_black_p50));
			j2WSwipeBackLayout.addOnSwipeBackListener(new J2WSwipeBackLayout.SwipeBackListener() {

				@Override public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
					ivShadow.setAlpha(1 - fractionScreen);
				}
			});
			if (getDragEdge() != null) {
				j2WSwipeBackLayout.setDragEdge(getDragEdge());
			}
			if (getListener() != null) {
				j2WSwipeBackLayout.addOnSwipeBackListener(getListener());
			}
			j2WSwipeBackLayout.addView(layoutContent, layoutParams);
			contentRoot.addView(ivShadow, layoutParams);
			contentRoot.addView(j2WSwipeBackLayout, layoutParams);
			layoutContent = j2WSwipeBackLayout;
		}

		// 进度条
		layoutLoadingId = layoutLoadingId > 0 ? layoutLoadingId : J2WHelper.getInstance().layoutLoading();
		if (layoutLoadingId > 0) {
			vsLoading = new ViewStub(j2WView.activity());
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
		contentRoot = null;
		mInflater = null;
		layoutContent = null;
		layoutBizError = null;
		layoutHttpError = null;
		layoutEmpty = null;
		vsLoading = null;
		layoutLoading = null;
		dragEdge = null;
		listener = null;
		ivShadow = null;
	}

	/**
	 * 标题栏
	 *
	 * @param view
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) private View createActionbar(View view) {
		if (isOpenToolbar()) {
			final RelativeLayout toolbarRoot = new RelativeLayout(j2WView.context());
			toolbarRoot.setId(R.id.j2w_home);
			toolbarRoot.setFitsSystemWindows(true);
			// 添加toolbar布局
			mInflater.inflate(getToolbarLayoutId(), toolbarRoot, true);
			// 添加内容布局
			RelativeLayout.LayoutParams contentLayoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			contentLayoutParams.addRule(RelativeLayout.BELOW, R.id.toolbar);
			toolbarRoot.addView(view, contentLayoutParams);
			toolbar = ButterKnife.findById(toolbarRoot, getToolbarId());

			J2WCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");

			if (getToolbarDrawerId() > 0) {
				DrawerLayout drawerLayout = ButterKnife.findById(view, getToolbarDrawerId());
				J2WCheckUtils.checkNotNull(drawerLayout, "无法根据布局文件ID,获取DrawerLayout");
				ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(j2WView.activity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);
				mDrawerToggle.syncState();
				drawerLayout.setDrawerListener(mDrawerToggle);
			}
			// 添加点击事件
			if (getMenuListener() != null) {
				toolbar.setOnMenuItemClickListener(getMenuListener());
			}
			if (getToolbarMenuId() > 0) {
				toolbar.inflateMenu(getToolbarMenuId());
			}
			if (isOpenToolbarBack()) {
				toolbar.setNavigationOnClickListener(new View.OnClickListener() {

					@Override public void onClick(View v) {
						J2WKeyboardUtils.hideSoftInput(j2WView.activity());
						switch (j2WView.getState()) {
							case J2WView.STATE_ACTIVITY:
								j2WView.activity().onKeyBack();
								break;
							case J2WView.STATE_FRAGMENT:
								j2WView.fragment().onKeyBack();
								break;
							case J2WView.STATE_DIALOGFRAGMENT:
								j2WView.dialogFragment().onKeyBack();
								break;
						}
					}
				});
			} else {
			}

			toolbarRoot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

			return toolbarRoot;
		} else if (isOpenCustomToolbar()) {
			view.setId(R.id.j2w_home);
			view.setFitsSystemWindows(true);
			toolbar = ButterKnife.findById(view, getToolbarId());

			J2WCheckUtils.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");
			if (getToolbarDrawerId() > 0) {
				DrawerLayout drawerLayout = ButterKnife.findById(view, getToolbarDrawerId());
				J2WCheckUtils.checkNotNull(drawerLayout, "无法根据布局文件ID,获取DrawerLayout");
				ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(j2WView.activity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);
				mDrawerToggle.syncState();
				drawerLayout.setDrawerListener(mDrawerToggle);
			}
			if (isOpenToolbarBack()) {
				toolbar.setNavigationOnClickListener(new View.OnClickListener() {

					@Override public void onClick(View v) {
						J2WKeyboardUtils.hideSoftInput(j2WView.activity());
						switch (j2WView.getState()) {
							case J2WView.STATE_ACTIVITY:
								j2WView.activity().onKeyBack();
								break;
							case J2WView.STATE_FRAGMENT:
								j2WView.fragment().onKeyBack();
								break;
							case J2WView.STATE_DIALOGFRAGMENT:
								j2WView.dialogFragment().onKeyBack();
								break;
						}
					}
				});
			}
			// 添加点击事件
			if (getMenuListener() != null) {
				toolbar.setOnMenuItemClickListener(getMenuListener());
			}
			if (getToolbarMenuId() > 0) {
				toolbar.inflateMenu(getToolbarMenuId());
			}

			return view;
		} else {
			view.setId(R.id.j2w_home);
			view.setFitsSystemWindows(true);
			return view;
		}
	}

	private void detachActionbar() {
		menuListener = null;
		toolbar = null;
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
			j2WListAdapter = j2WListViewMultiLayout == null ? new J2WListAdapter(j2WView, getJ2WAdapterItem()) : new J2WListAdapter(j2WView, j2WListViewMultiLayout);
			J2WCheckUtils.checkNotNull(j2WListAdapter, "适配器创建失败");
			// 设置适配器
			listView.setAdapter(j2WListAdapter);
		}
	}

	private void detachListView() {
		if (j2WListAdapter != null) {
			j2WListAdapter.detach();
			j2WListAdapter = null;
		}
		listView = null;
		header = null;
		footer = null;
		j2WAdapterItem = null;
		j2WListViewMultiLayout = null;
		itemListener = null;
		itemLongListener = null;
		swipe_container = null;
		colorResIds = null;
		j2WRefreshListener = null;
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
			recyclerView.setLayoutManager(layoutManager);
			if (j2WRVAdapter != null) {
				// 扩展适配器
				if (j2WRVAdapter instanceof J2WStickyHeaders) {
					J2WStickyHeaders j2WStickyHeaders = (J2WStickyHeaders) j2WRVAdapter;
					final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(j2WStickyHeaders);
					recyclerView.addItemDecoration(headersDecor);

					if (onHeaderClickListener != null) {
						StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
						touchListener.setOnHeaderClickListener(onHeaderClickListener);
						recyclerView.addOnItemTouchListener(touchListener);

					}
					j2WRVAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

						@Override public void onChanged() {
							headersDecor.invalidateHeaders();
						}
					});
				}
				recyclerView.setAdapter(j2WRVAdapter);
				if (isHeaderFooter) {
					final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
					J2WCheckUtils.checkNotNull(gridLayoutManager, "LayoutManger，不是GridLayoutManager");
					gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

						@Override public int getSpanSize(int position) {
							return j2WRVAdapter.isHeaderAndFooter(position) ? gridLayoutManager.getSpanCount() : 1;
						}
					});
				}
				// 设置Item增加、移除动画
				recyclerView.setItemAnimator(getItemAnimator());
				// 添加分割线
				if (getItemDecoration() != null) {
					recyclerView.addItemDecoration(getItemDecoration());
				}
				// 优化
				recyclerView.setHasFixedSize(true);
				// 设置上拉和下拉事件
				if (getRecyclerviewSwipRefreshId() != 0) {
					recyclerviewSwipeContainer = ButterKnife.findById(view, getRecyclerviewSwipRefreshId());
					J2WCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
					J2WCheckUtils.checkNotNull(recyclerviewJ2WRefreshListener, " recyclerview的SwipRefresh 下拉刷新和上拉加载事件没有设置");
					recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

						@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
							super.onScrollStateChanged(recyclerView, newState);
							if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
								if (recyclerviewJ2WRefreshListener.onScrolledToBottom()) {
									mLoadMoreRequestedItemCount = j2WRVAdapter.getItemCount();
									mLoadMoreIsAtBottom = false;
								}
							}
						}

						@Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
							super.onScrolled(recyclerView, dx, dy);
							if (layoutManager instanceof LinearLayoutManager) {
								int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
								mLoadMoreIsAtBottom = j2WRVAdapter.getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == j2WRVAdapter.getItemCount();
							}
						}
					});// 加载更多
					recyclerviewSwipeContainer.setOnRefreshListener(recyclerviewJ2WRefreshListener);// 下载刷新
				} else {
					if (j2WFooterListener != null) {
						recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

							@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
								super.onScrollStateChanged(recyclerView, newState);
								if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
									if (j2WFooterListener.onScrolledToBottom()) {
										mLoadMoreRequestedItemCount = j2WRVAdapter.getItemCount();
										mLoadMoreIsAtBottom = false;
									}
								}
							}

							@Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
								super.onScrolled(recyclerView, dx, dy);
								if (layoutManager instanceof LinearLayoutManager) {
									int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
									mLoadMoreIsAtBottom = j2WRVAdapter.getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == j2WRVAdapter.getItemCount();
								}
							}
						});
					}
				}
			} else if (j2WRVAdapterItem != null) {
				// 扩展适配器
				if (j2WRVAdapterItem instanceof J2WStickyHeaders) {
					J2WStickyHeaders j2WStickyHeaders = (J2WStickyHeaders) j2WRVAdapterItem;
					final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(j2WStickyHeaders);
					recyclerView.addItemDecoration(headersDecor);

					if (onHeaderClickListener != null) {
						StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(recyclerView, headersDecor);
						touchListener.setOnHeaderClickListener(onHeaderClickListener);
						recyclerView.addOnItemTouchListener(touchListener);

					}
					j2WRVAdapterItem.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

						@Override public void onChanged() {
							headersDecor.invalidateHeaders();
						}
					});
				}
				recyclerView.setAdapter(j2WRVAdapterItem);
				if (isHeaderFooter) {
					final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
					J2WCheckUtils.checkNotNull(gridLayoutManager, "LayoutManger，不是GridLayoutManager");
					gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

						@Override public int getSpanSize(int position) {
							return j2WRVAdapterItem.isHeaderAndFooter(position) ? gridLayoutManager.getSpanCount() : 1;
						}
					});
				}
				// 设置Item增加、移除动画
				recyclerView.setItemAnimator(getItemAnimator());
				// 添加分割线
				if (getItemDecoration() != null) {
					recyclerView.addItemDecoration(getItemDecoration());
				}
				// 优化
				recyclerView.setHasFixedSize(true);

				// 设置上拉和下拉事件
				if (getRecyclerviewSwipRefreshId() != 0) {
					recyclerviewSwipeContainer = ButterKnife.findById(view, getRecyclerviewSwipRefreshId());
					J2WCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
					J2WCheckUtils.checkNotNull(recyclerviewJ2WRefreshListener, " recyclerview的SwipRefresh 下拉刷新和上拉加载事件没有设置");
					recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

						@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
							super.onScrollStateChanged(recyclerView, newState);
							if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
								if (recyclerviewJ2WRefreshListener.onScrolledToBottom()) {
									mLoadMoreRequestedItemCount = j2WRVAdapterItem.getItemCount();
									mLoadMoreIsAtBottom = false;
								}
							}
						}

						@Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
							super.onScrolled(recyclerView, dx, dy);
							if (layoutManager instanceof LinearLayoutManager) {
								int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
								mLoadMoreIsAtBottom = j2WRVAdapterItem.getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == j2WRVAdapterItem.getItemCount();
							}
						}
					});// 加载更多
					recyclerviewSwipeContainer.setOnRefreshListener(recyclerviewJ2WRefreshListener);// 下载刷新
				} else {
					if (j2WFooterListener != null) {
						recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

							@Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
								super.onScrollStateChanged(recyclerView, newState);
								if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom) {
									if (j2WFooterListener.onScrolledToBottom()) {
										mLoadMoreRequestedItemCount = j2WRVAdapterItem.getItemCount();
										mLoadMoreIsAtBottom = false;
									}
								}
							}

							@Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
								super.onScrolled(recyclerView, dx, dy);
								if (layoutManager instanceof LinearLayoutManager) {
									int lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
									mLoadMoreIsAtBottom = j2WRVAdapterItem.getItemCount() > mLoadMoreRequestedItemCount && lastVisibleItem + 1 == j2WRVAdapterItem.getItemCount();
								}
							}
						});
					}
				}
			} else {
				J2WCheckUtils.checkNotNull(null, "J2WRVAdapter适配器不能为空");
			}

			// 设置进度颜色
			if (getRecyclerviewColorResIds() != null) {
				J2WCheckUtils.checkNotNull(recyclerviewSwipeContainer, "无法根据布局文件ID,获取recyclerview的SwipRefresh下载刷新布局");
				recyclerviewSwipeContainer.setColorSchemeResources(getRecyclerviewColorResIds());
			}
		}
	}

	private void detachRecyclerView() {
		recyclerView = null;
		if (j2WRVAdapterItem != null) {
			j2WRVAdapterItem.clearCache();
			j2WRVAdapterItem = null;
		}
		if (j2WRVAdapter != null) {
			j2WRVAdapter.clearCache();
			j2WRVAdapter = null;
		}
		onHeaderClickListener = null;
		layoutManager = null;
		itemAnimator = null;
		itemDecoration = null;
		recyclerviewSwipeContainer = null;
		recyclerviewJ2WRefreshListener = null;
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

			if (getCustomLayout() > 0) {
				customView = ButterKnife.findById(view, getCustomLayout());
				J2WCheckUtils.checkNotNull(customView, "无法根据布局文件ID,获取tabs");
			}
			J2WCheckUtils.checkNotNull(fragmentManager, "fragmentManager不能为空");

			j2WViewPagerAdapter = new J2WViewPagerAdapter(getTabsType(), fragmentManager, customView, showItems, j2WViewPager, viewPagerChangeListener, j2WTabsCustomListener);

			j2WViewPager.setAdapter(j2WViewPagerAdapter);
			// 间隔距离
			final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, j2WView.activity().getResources().getDisplayMetrics());
			// 设置距离
			j2WViewPager.setPageMargin(pageMargin);
			// 预留数量
			j2WViewPager.setOffscreenPageLimit(getViewPageroffScreenPageLimit());
		}
	}

	private void detachViewPager() {
		if (j2WViewPagerAdapter != null) {
			j2WViewPagerAdapter.clearData();
		}
		j2WViewPager = null;
		customView = null;
		showItems = null;
		j2WViewPagerAdapter = null;
		fragmentManager = null;
		viewPagerChangeListener = null;
		j2WTabsCustomListener = null;
	}

	/**
	 * 自动加载更多
	 **/
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
