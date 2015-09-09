package j2w.team.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.display.J2WIDisplay;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;

/**
 * @创建人 sky
 * @创建时间 15/7/18 上午11:49
 * @类描述 View层碎片
 */
public abstract class J2WFragment<D extends J2WIDisplay> extends Fragment implements View.OnTouchListener {

	private boolean				targetActivity;

	private Map<String, Object>	stackBiz;

	private Map<String, Object>	stackDisplay;

	D							display;

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
	private J2WBuilder	j2WBuilder;

	private Object		biz;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 打开开关触发菜单项 **/
		setHasOptionsMenu(true);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** 初始化图片架构 **/
		J2WHelper.frescoHelper().initialize();
		/** 初始化视图 **/
		j2WBuilder = new J2WBuilder(this, inflater);
		View view = build(j2WBuilder).create();
		/** 初始化所有组建 **/
		ButterKnife.bind(this, view);
		/** 初始化业务 **/
		attachBiz();
		/** 初始化点击事件 **/
		view.setOnTouchListener(this);// 设置点击事件
		return view;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		J2WHelper.getInstance().onFragmentCreated(this, savedInstanceState);

		/** 状态栏颜色 **/
		j2WBuilder.initTint();
		initData(getArguments());
	}

	@Override public void onStart() {
		super.onStart();
		J2WHelper.getInstance().onFragmentStart(this);
	}

	@Override public void onResume() {
		super.onResume();
		J2WHelper.getInstance().onFragmentResume(this);
		attachBiz();
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			if (!J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().register(this);
			}
		}
	}

	@Override public void onPause() {
		super.onPause();
		J2WHelper.getInstance().onFragmentPause(this);

		detachBiz();
	}

	@Override public void onStop() {
		super.onStop();
		J2WHelper.getInstance().onFragmentStop(this);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		/** 关闭event **/
		if (j2WBuilder.isNotCloseEventBus()) {
			if (J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().unregister(this);
			}
		}
        /**关闭键盘 **/
        J2WKeyboardUtils.hideSoftInput(getActivity());
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;

		/** 清空注解view **/
		ButterKnife.unbind(this);
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		display.initDisplay(j2wView());
		return display;
	}

	public <E extends J2WIDisplay> E display(Class<E> eClass) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		E obj = (E) stackDisplay.get(eClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createDisplay(eClass);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackDisplay.put(eClass.getSimpleName(), obj);
		}
		obj.initDisplay(j2wView());
		return obj;
	}
	/**
	 * 获取activity
	 * 
	 * @param <A>
	 * @return
	 */
	protected <A extends J2WActivity> A activity() {
		return (A) getActivity();
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
			obj = J2WBizUtils.createBiz(biz, this);
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

		if (stackDisplay == null) {
			stackDisplay = new HashMap<>();
		}
		if (display == null) {
			Class displayClass = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
			display = (D) J2WBizUtils.createDisplay(displayClass);
			stackDisplay.put(displayClass.getSimpleName(), display);
		}
		listLoadMoreOpen();
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
		for (Object b : stackDisplay.values()) {
			J2WIDisplay j2WIDisplay = (J2WIDisplay) b;
			if (j2WIDisplay != null) {
				j2WIDisplay.detach();
			}
		}
		if (stackDisplay != null) {
			stackDisplay.clear();
			stackDisplay = null;
		}
		if (display != null) {
			display.detach();
			display = null;
		}
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

	/**
	 * 是否设置目标活动
	 *
	 * @return
	 */
	public boolean isTargetActivity() {
		return targetActivity;
	}

	/**
	 * 设置目标活动
	 * 
	 * @param targetActivity
	 */
	public void setTargetActivity(boolean targetActivity) {
		this.targetActivity = targetActivity;
	}

	@Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 初始化业务 **/
		attachBiz();
	}

	/**
	 * 获取fragment
	 *
	 * @param clazz
	 * @return
	 */
	public <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) getFragmentManager().findFragmentByTag(clazz.getSimpleName());
	}

	/**
	 * 防止事件穿透
	 *
	 * @param v
	 *            View
	 * @param event
	 *            事件
	 * @return true 拦截 false 不拦截
	 */
	@Override public boolean onTouch(View v, MotionEvent event) {
		return true;
	}

	/********************** View业务代码 *********************/

	public J2WView j2wView() {
		return j2WBuilder.getJ2WView();
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

	/**
	 * 可见
	 */
	public void onVisible() {}

	/**
	 * 不可见
	 */
	public void onInvisible() {}
}