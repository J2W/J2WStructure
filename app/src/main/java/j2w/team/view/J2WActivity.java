package j2w.team.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.modules.structure.J2WStructureManage;
import j2w.team.structure.R;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;

/**
 * @创建人 sky
 * @创建时间 15/7/8 上午12:15
 * @类描述 activity
 */
public abstract class J2WActivity<B extends J2WIBiz> extends AppCompatActivity {

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
	private J2WBuilder				j2WBuilder;

	/** 结构 **/
	private J2WStructureIManage<B>	j2WStructureIManage;

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 初始化视图 **/
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		j2WBuilder = new J2WBuilder(this, inflater);
		setContentView(build(j2WBuilder).create());
		/** 状态栏颜色 **/
		j2WBuilder.initTint();
		/** 初始化结构 **/
		j2WStructureIManage = new J2WStructureManage();
		/** 初始化业务 **/
		j2WStructureIManage.attachActivity(this);
		/** 初始化 **/
		J2WHelper.methodsProxy().activityInterceptor().onCreate(this, getIntent().getExtras(), savedInstanceState);
		/** 提交fragment **/
		if (savedInstanceState == null) {
			Fragment fragment = createFragment();
			if (fragment != null) {
				FragmentManager fm = getSupportFragmentManager();
				fm.beginTransaction().add(R.id.j2w_home, fragment).commit();
				fm.executePendingTransactions();
			}
		}
		initData(getIntent().getExtras());
	}

	protected Fragment createFragment() {
		return null;
	}

	@Override protected void onStart() {
		super.onStart();
		J2WHelper.methodsProxy().activityInterceptor().onStart(this);
	}

	@Override protected void onResume() {
		super.onResume();
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			if (!J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().register(this);
			}
		}
		listLoadMoreOpen();
		J2WHelper.methodsProxy().activityInterceptor().onResume(this);
	}

	public B getBiz() {
		return biz();
	}

	/**
	 * 设置输入法
	 * 
	 * @param mode
	 */
	public void setSoftInputMode(int mode) {
		getWindow().setSoftInputMode(mode);
	}

	@Override protected void onPause() {
		super.onPause();
		J2WHelper.methodsProxy().activityInterceptor().onPause(this);
		/** 判断EventBus 是否销毁 **/
		if (j2WBuilder.isOpenEventBus()) {
			if (!j2WBuilder.isNotCloseEventBus()) {
				if (J2WHelper.eventBus().isRegistered(this)) {
					J2WHelper.eventBus().unregister(this);
				}
			}
		}
		// 恢复初始化
		listRefreshing(false);
	}

	@Override protected void onRestart() {
		super.onRestart();
		J2WHelper.methodsProxy().activityInterceptor().onRestart(this);
	}

	@Override protected void onStop() {
		super.onStop();
		J2WHelper.methodsProxy().activityInterceptor().onStop(this);
	}

	@Override protected void onDestroy() {
		super.onDestroy();
		/** 关闭event **/
		if (J2WHelper.eventBus().isRegistered(this)) {
			J2WHelper.eventBus().unregister(this);
		}
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;
		/** 清楚结构 **/
		j2WStructureIManage.detachActivity(this);
		j2WStructureIManage = null;

		J2WHelper.methodsProxy().activityInterceptor().onDestroy(this);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
	 * 获取显示调度
	 *
	 * @return
	 */
	public <N extends J2WIDisplay> N display(Class<N> eClass) {
		return j2WStructureIManage.display(eClass);
	}

	public J2WStructureIManage getStructureManage() {
		return j2WStructureIManage;
	}

	/**
	 * 获取业务
	 *
	 * @return
	 */
	public B biz() {
		return j2WStructureIManage.getBiz();
	}

	public <C> C biz(Class<C> service) {
		return j2WStructureIManage.biz(service);
	}

	@Override public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (j2WStructureIManage.onKeyBack(keyCode, getSupportFragmentManager())) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/********************** View业务代码 *********************/

	public <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) getSupportFragmentManager().findFragmentByTag(clazz.getName());
	}

	public J2WView j2wView() {
		return j2WBuilder.getJ2WView();
	}

	public boolean isOpenTint() {
		return j2WBuilder.isTintColor();
	}

	/********************** Actionbar业务代码 *********************/

	protected void showContent() {
		if (j2WBuilder != null) {
			j2WBuilder.layoutContent();
		}
	}

	protected void showLoading() {
		if (j2WBuilder != null) {
			j2WBuilder.layoutLoading();
		}
	}

	protected void showBizError() {
		if (j2WBuilder != null) {
			j2WBuilder.layoutBizError();
		}
	}

	protected void showEmpty() {
		if (j2WBuilder != null) {
			j2WBuilder.layoutEmpty();
		}
	}

	protected void showHttpError() {
		if (j2WBuilder != null) {
			j2WBuilder.layoutHttpError();
		}
	}

	/********************** Actionbar业务代码 *********************/
	public Toolbar toolbar() {
		return j2WBuilder.getToolbar();
	}

	public SystemBarTintManager tintManager() {
		return j2WBuilder.getTintManager();
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
		if (j2WBuilder != null) {
			j2WBuilder.addListHeader();
		}
	}

	protected void addListFooter() {
		if (j2WBuilder != null) {
			j2WBuilder.addListFooter();
		}
	}

	protected void removeListHeader() {
		if (j2WBuilder != null) {
			j2WBuilder.removeListHeader();
		}
	}

	protected void removeListFooter() {
		if (j2WBuilder != null) {
			j2WBuilder.removeListFooter();
		}

	}

	protected void listRefreshing(boolean bool) {
		if (j2WBuilder != null) {
			j2WBuilder.listRefreshing(bool);
		}
	}

	protected void listLoadMoreOpen() {
		if (j2WBuilder != null) {
			j2WBuilder.loadMoreOpen();
		}
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