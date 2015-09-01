package j2w.team.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;
import j2w.team.view.adapter.recycleview.stickyheader.J2WStickyAdapterItem;

/**
 * @创建人 sky
 * @创建时间 15/7/8 上午12:15
 * @类描述 activity
 */
public abstract class J2WActivity<D extends J2WIDisplay> extends ActionBarActivity {

	/**
	 * 定制
	 * 
	 * @param initialJ2WBuilder
	 * @return
	 **/
	protected abstract J2WBuilder build(J2WBuilder initialJ2WBuilder);

	/**
	 * 初始化数据
	 *
	 * @param savedInstanceState
	 *            数据
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/** View层编辑器 **/
	private J2WBuilder			j2WBuilder;

	/** 业务逻辑对象 **/
	private Map<String, Object>	stackBiz	= null;

	/** 显示调度对象 **/
	private D					display		= null;

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 初始化图片架构 **/
		J2WHelper.frescoHelper().initialize();
		/** 初始化视图 **/
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		j2WBuilder = new J2WBuilder(this, inflater);
		setContentView(build(j2WBuilder).create());
		/** 状态栏颜色 **/
		j2WBuilder.initTint();
		/** 初始化所有组建 **/
		ButterKnife.bind(this);
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushActivity(this);
		/** 初始化视图 **/
		J2WHelper.getInstance().onCreate(this, getIntent().getExtras());
		/** 初始化业务 **/
		attachBiz();
		/** 初始化视图组建 **/
		initData(getIntent().getExtras());
	}

	@Override protected void onStart() {
		super.onStart();
		J2WHelper.getInstance().onStart(this);
	}

	@Override protected void onResume() {
		super.onResume();
		attachBiz();
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			if (!J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().register(this);
			}
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
		/** 初始化业务 **/
		attachBiz();
		J2WHelper.getInstance().onRestart(this);
	}

	@Override protected void onStop() {
		super.onStop();
		J2WHelper.getInstance().onStop(this);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		/** 关闭event **/
		if(j2WBuilder.isNotCloseEventBus()){
			if(J2WHelper.eventBus().isRegistered(this)){
				J2WHelper.eventBus().unregister(this);
			}
		}
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popActivity(this);
		J2WHelper.getInstance().onDestroy(this);
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		return display;
	}

	public <E extends J2WIDisplay> E display(Class<E> e) {
		return (E) display;
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
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
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
		listLoadMoreOpen();
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) getSupportFragmentManager().findFragmentByTag(clazz.getSimpleName());
	}

	/**
	 * 业务分离
	 */
	synchronized final void detachBiz() {
		for (Object b : stackBiz.values()) {
			J2WIBiz j2WIBiz = (J2WIBiz) b;
			if (j2WIBiz != null) {
				j2WIBiz.detach();
			}
		}
		if (stackBiz != null) {
			stackBiz.clear();
			stackBiz = null;
		}
		if (display != null) {
			display.detach();
			display = null;
		}
		/** 判断EventBus 是否销毁 **/
		if (j2WBuilder.isOpenEventBus()) {
			if(!j2WBuilder.isNotCloseEventBus()){
				if(J2WHelper.eventBus().isRegistered(this)){
					J2WHelper.eventBus().unregister(this);
				}
			}
		}
		// 恢复初始化
		listRefreshing(false);
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
			if (J2WKeyboardUtils.isShouldHideInput(v, ev)) {
				J2WKeyboardUtils.hideSoftInput(this);
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
		if (j2WBuilder.getToolbarMenuId() > 0) {
			getMenuInflater().inflate(j2WBuilder.getToolbarMenuId(), menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 回调
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 初始化业务 **/
		attachBiz();
	}

	/********************** View业务代码 *********************/

	public J2WView j2wView() {
		return j2WBuilder.getJ2WView();
	}

	public boolean isOpenTint() {
		return j2WBuilder.isTintColor();
	}

	/********************** Actionbar业务代码 *********************/

	protected void showContent() {
		j2WBuilder.layoutContent();
	}

	protected void showLoading() {
		j2WBuilder.layoutLoading();
	}

	protected void showBizError() {
		j2WBuilder.layoutBizError();
	}

	protected void showEmpty() {
		j2WBuilder.layoutEmpty();
	}

	protected void showHttpError() {
		j2WBuilder.layoutHttpError();
	}

	/********************** Actionbar业务代码 *********************/
	public Toolbar toolbar() {
		return j2WBuilder.getToolbar();
	}

	/********************** RecyclerView业务代码 *********************/

	protected HeaderRecyclerViewAdapterV1 adapterRecycler() {
		return j2WBuilder.getJ2WRVAdapterItem();
	}

	protected RecyclerView.LayoutManager recyclerLayoutManager() {
		return j2WBuilder.getLayoutManager();
	}

	protected RecyclerView recyclerView() {
		return j2WBuilder.getRecyclerView();
	}

	/********************** ListView业务代码 *********************/

	protected void addListHeader() {
		j2WBuilder.addListHeader();
	}

	protected void addListFooter() {
		j2WBuilder.addListFooter();
	}

	protected void removeListHeader() {
		j2WBuilder.removeListHeader();
	}

	protected void removeListFooter() {
		j2WBuilder.removeListFooter();
	}

	protected void listRefreshing(boolean bool) {
		j2WBuilder.listRefreshing(bool);
	}

	protected void listLoadMoreOpen() {
		j2WBuilder.loadMoreOpen();
	}

	protected J2WListAdapter adapter() {
		return j2WBuilder.getAdapter();
	}

	protected ListView listView() {
		return j2WBuilder.getListView();
	}

	/********************** ViewPager业务代码 *********************/

	protected J2WIViewPagerAdapter viewPagerAdapter() {
		return j2WBuilder.getViewPagerAdapter();
	}

	protected J2WViewPager viewPager() {
		return j2WBuilder.getViewPager();
	}
}