package j2w.team.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.log.L;
import j2w.team.common.utils.KeyboardUtils;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WIBiz;
import j2w.team.biz.J2WBizUtils;
import j2w.team.structure.R;
import j2w.team.view.adapter.J2WAdapterItem;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.J2WListViewMultiLayout;

/**
 * @创建人 sky
 * @创建时间 15/7/8 上午12:15
 * @类描述 activity
 */
public abstract class J2WActivity<D extends J2WIDisplay> extends ActionBarActivity {

	/**
	 * 定制对话框
	 * 
	 * @param initialBuilder
	 * @return
	 **/
	protected abstract Builder build(Builder initialBuilder);

	/**
	 * 初始化数据
	 *
	 * @param savedInstanceState
	 *            数据
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/** View层编辑器 **/
	private Builder				builder;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackBiz	= null;

	/** 显示调度对象 **/
	private D					display		= null;

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	@Override protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 初始化视图 **/
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		builder = new Builder(this, inflater);
		setContentView(build(builder).create());
		/** 初始化所有组建 **/
		ButterKnife.bind(this);
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushActivity(this);
		/** 初始化视图 **/
		J2WHelper.getInstance().onCreate(this, savedInstanceState);
		/** 初始化业务 **/
		attachBiz();
		/** 初始化视图组建 **/
		initData(savedInstanceState);
	}

	@Override protected void onStart() {
		super.onStart();
		J2WHelper.getInstance().onStart(this);
	}

	@Override protected void onResume() {
		super.onResume();
		attachBiz();
		if (builder.isOpenEventBus()) {
			J2WHelper.getEventBus().register(this);
		}
		J2WHelper.getInstance().onResume(this);
	}

	@Override protected void onPause() {
		super.onPause();
		detachBiz();
		J2WHelper.getInstance().onPause(this);
	}

	@Override protected void onRestart() {
		super.onRestart();
		J2WHelper.getInstance().onRestart(this);
	}

	@Override protected void onStop() {
		super.onStop();
		J2WHelper.getInstance().onStop(this);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		/** 判断EventBus 然后销毁 **/
		if (builder.isOpenEventBus()) {
			J2WHelper.getEventBus().unregister(this);
		}
		/** 移除builder **/
		builder.detach();
		builder = null;
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popActivity(this);
		J2WHelper.getInstance().onDestroy(this);
	}

	/**
	 * 获取显示调度
	 * 
	 * @param objects
	 *            参数
	 * @return
	 */
	public D display(Object... objects) {
		if (objects.length > 0) {
			display.initDisplay(this);
		}
		return display;
	}

	/**
	 * 获取业务
	 * 
	 * @param biz
	 *            泛型
	 * @param <B>
	 * @return
	 */
	public <B extends J2WIBiz> B biz(Class<B> biz) {
		Preconditions.checkNotNull(biz, "请指定业务接口～");
		Object obj = stackBiz.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createBiz(biz, this, display);
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}

	/**
	 * 业务初始化
	 */
	synchronized final void attachBiz() {
		if (stackBiz == null) {
			stackBiz = new HashMap<>();
		}
		/** 创建业务类 **/
		if (display == null) {
			display = J2WBizUtils.createDisplay(this);
		}
	}

	/**
	 * 业务分离
	 */
	synchronized final void detachBiz() {
		for (Object b : stackBiz.values()) {
			((J2WIBiz) b).detach();
		}
		stackBiz.clear();
		stackBiz = null;
		display = null;
	}

	/**
	 * 屏幕点击事件 - 关闭键盘
	 *
	 * @param ev
	 * @return
	 */
	@Override public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			// 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
			View v = getCurrentFocus();
			if (KeyboardUtils.isShouldHideInput(v, ev)) {
				KeyboardUtils.hideSoftInput(J2WHelper.screenHelper().currentActivity());
			}
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 创建menu
	 * 
	 * @param menu
	 * @return
	 */
	@Override public boolean onCreateOptionsMenu(Menu menu) {
		if (builder.getToolbarMenuId() > 0) {
			getMenuInflater().inflate(builder.getToolbarMenuId(), menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	/********************** Actionbar业务代码 *********************/
	public Toolbar toolbar() {
		return builder.getToolbar();
	}

	/********************** ListView业务代码 *********************/

	public void addListHeader() {
		builder.addListHeader();
	}

	public void addListFooter() {
		builder.addListFooter();
	}

	public void removeListHeader() {
		builder.removeListHeader();
	}

	public void removeListFooter() {
		builder.removeListFooter();
	}

	public void listRefreshing(boolean bool) {
		builder.listRefreshing(bool);
	}

	public void listLoadMoreOpen() {
		builder.loadMoreOpen();
	}

	protected J2WListAdapter adapter() {
		return builder.getAdapter();
	}

	protected ListView listView() {
		return builder.getListView();
	}

	/**
	 * 自定义对话框生成器
	 */
	protected static class Builder implements AbsListView.OnScrollListener {

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
		public Builder(J2WActivity context, LayoutInflater inflater) {
			this.mContext = context;
			this.mInflater = inflater;
		}

		/**
		 * 布局ID
		 */
		private int	layoutId;

		int getLayoutId() {
			return layoutId;
		}

		public void layoutId(int layoutId) {
			this.layoutId = layoutId;
		}

		/**
		 * actionbar
		 */

		private Toolbar							toolbar;

		private Toolbar.OnMenuItemClickListener	menuListener;

		private int								toolbarId;

		private int								toolbarMenuId;

		private int								toolbarDrawerId;

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

		public void toolbarDrawerId(int toolbarDrawerId) {
			this.toolbarDrawerId = toolbarDrawerId;
		}

		public void toolbarMenuListener(Toolbar.OnMenuItemClickListener menuListener) {
			this.menuListener = menuListener;
		}

		public void toolbarId(int toolbarId) {
			this.toolbarId = toolbarId;
		}

		public void toolbarMenuId(int toolbarMenuId) {
			this.toolbarMenuId = toolbarMenuId;
		}

		/**
		 * EventBus开关
		 */
		private boolean	isOpenEventBus;

		boolean isOpenEventBus() {
			return isOpenEventBus;
		}

		public void isOpenEventBus(boolean isOpenEventBus) {
			this.isOpenEventBus = isOpenEventBus;
		}

		/**
		 * ListView
		 */
		J2WListAdapter								j2WListAdapter;

		ListView									listView;

		View										header;

		View										footer;

		SwipeRefreshLayout							swipe_container;

		J2WRefreshListener							j2WRefreshListener;

		private boolean								mLoadMoreIsAtBottom;			// 加载更多
																					// 开关

		private int									mLoadMoreRequestedItemCount;	// 加载更多
																					// 数量

		private int									colorResIds[];

		private int									swipRefreshId;

		private int									listId;

		private int									listHeaderLayoutId;

		private int									listFooterLayoutId;

		private int									footerCount;

		private J2WAdapterItem						j2WAdapterItem;

		private J2WListViewMultiLayout				j2WListViewMultiLayout;

		private AdapterView.OnItemClickListener		itemListener;

		private AdapterView.OnItemLongClickListener	itemLongListener;

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
			Preconditions.checkNotNull(j2WListAdapter, "适配器没有初始化");
			return j2WListAdapter;
		}

		ListView getListView() {
			Preconditions.checkNotNull(listView, "没有设置布局文件ID,无法获取ListView");
			return listView;
		}

		int getSwipRefreshId() {
			return swipRefreshId;
		}

		public int[] getSwipeColorResIds() {
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
		}

		void loadMoreOpen() {
			mLoadMoreIsAtBottom = true;
			mLoadMoreRequestedItemCount = 0;
		}

		/**
		 * 创建
		 *
		 * @return
		 */
		View create() {
			L.i("Builder.create()");
			/** layout **/
			Preconditions.checkArgument(getLayoutId() > 0, "请给出布局文件ID");
			View view = mInflater.inflate(getLayoutId(), null, false);
			Preconditions.checkNotNull(view, "无法根据布局文件ID,获取View");
			/** actoinbar **/
			createActionbar(view);
			/** listview **/
			createListView(view);

			return view;
		}

		/**
		 * 清空所有
		 */
		void detach() {
			// 基础清除
			mContext = null;
			mInflater = null;
			// actionbar清除
			detachActionbar();
			// listview清除
			detachListView();
		}

		/**
		 * 标题栏
		 * 
		 * @param view
		 */
		private void createActionbar(View view) {
			if (getToolbarId() > 0) {
				toolbar = ButterKnife.findById(view, getToolbarId());
				Preconditions.checkNotNull(toolbar, "无法根据布局文件ID,获取Toolbar");

				if (getToolbarDrawerId() > 0) {
					DrawerLayout drawerLayout = ButterKnife.findById(view, getToolbarDrawerId());
					Preconditions.checkNotNull(drawerLayout, "无法根据布局文件ID,获取DrawerLayout");
					ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(mContext, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
					mDrawerToggle.syncState();
					drawerLayout.setDrawerListener(mDrawerToggle);
				}
				mContext.setSupportActionBar(toolbar);
				// 添加点击事件
				if (getMenuListener() != null) {
					toolbar.setOnMenuItemClickListener(getMenuListener());
				}
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
				Preconditions.checkNotNull(listView, "无法根据布局文件ID,获取ListView");
				// 添加头布局
				if (getListHeaderLayoutId() != 0) {
					header = mInflater.inflate(getListHeaderLayoutId(), null, false);
					Preconditions.checkNotNull(header, "无法根据布局文件ID,获取ListView 头布局");
					addListHeader();
				}
				// 添加尾布局
				if (getListFooterLayoutId() != 0) {
					footer = mInflater.inflate(getListFooterLayoutId(), null, false);
					Preconditions.checkNotNull(footer, "无法根据布局文件ID,获取ListView 尾布局");
					addListFooter();
				}
				// 设置上拉和下拉事件
				if (getSwipRefreshId() != 0) {
					swipe_container = ButterKnife.findById(view, getSwipRefreshId());
					Preconditions.checkNotNull(swipe_container, "无法根据布局文件ID,获取ListView的SwipRefresh下载刷新布局");
					Preconditions.checkNotNull(j2WRefreshListener, " ListView的SwipRefresh 下拉刷新和上拉加载事件没有设置");
					swipe_container.setOnRefreshListener(j2WRefreshListener);// 下载刷新
					listView.setOnScrollListener(this);// 加载更多
				}
				// 设置进度颜色
				if (getSwipeColorResIds() != null) {
					Preconditions.checkNotNull(swipe_container, "无法根据布局文件ID,获取ListView的SwipRefresh下载刷新布局");
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
				Preconditions.checkNotNull(j2WListAdapter, "适配器创建失败");
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
		}

		/** 自动加载更多 **/

		@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLoadMoreIsAtBottom && !swipe_container.isRefreshing()) {
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
}