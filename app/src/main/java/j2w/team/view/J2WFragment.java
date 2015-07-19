package j2w.team.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;

/**
 * @创建人 sky
 * @创建时间 15/7/18 上午11:49
 * @类描述 View层碎片
 */
public abstract class J2WFragment<D extends J2WIDisplay> extends Fragment implements View.OnTouchListener {

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

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 打开开关触发菜单项 **/
		setHasOptionsMenu(true);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** 初始化视图 **/
		j2WBuilder = new J2WBuilder((J2WActivity) getActivity(), inflater);
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
		initData(savedInstanceState);
	}

	@Override public void onResume() {
		super.onResume();
		attachBiz();
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			J2WHelper.eventBus().register(this);
		}
	}

	@Override public void onPause() {
		super.onPause();
		detachBiz();
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;

		/** 清空注解view **/
		ButterKnife.unbind(this);
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
			display.initDisplay((J2WActivity) getActivity());
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
		/** 判断EventBus 是否销毁 **/
		if (j2WBuilder.isOpenEventBus()) {
			J2WHelper.eventBus().unregister(this);
		}
		// 恢复初始化
		listRefreshing(false);
	}

	/**
	 * 创建menu
	 *
	 * @param menu
	 * @return
	 */
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (j2WBuilder.getToolbarMenuId() > 0) {
			this.getActivity().getMenuInflater().inflate(j2WBuilder.getToolbarMenuId(), menu);
		}
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

	/********************** Actionbar业务代码 *********************/

	public void showContent() {
		j2WBuilder.layoutContent();
	}

	public void showLoading() {
		j2WBuilder.layoutLoading();
	}

	public void showBizError() {
		j2WBuilder.layoutBizError();
	}

	public void showEmpty() {
		j2WBuilder.layoutEmpty();
	}

	public void showHttpError() {
		j2WBuilder.layoutHttpError();
	}

	/********************** Actionbar业务代码 *********************/
	public Toolbar toolbar() {
		return j2WBuilder.getToolbar();
	}

	/********************** RecyclerView业务代码 *********************/

	public HeaderRecyclerViewAdapterV1 adapterRecycler() {
		return j2WBuilder.getJ2WRVAdapterItem();
	}

	/********************** ListView业务代码 *********************/

	public void addListHeader() {
		j2WBuilder.addListHeader();
	}

	public void addListFooter() {
		j2WBuilder.addListFooter();
	}

	public void removeListHeader() {
		j2WBuilder.removeListHeader();
	}

	public void removeListFooter() {
		j2WBuilder.removeListFooter();
	}

	public void listRefreshing(boolean bool) {
		j2WBuilder.listRefreshing(bool);
	}

	public void listLoadMoreOpen() {
		j2WBuilder.loadMoreOpen();
	}

	protected J2WListAdapter adapter() {
		return j2WBuilder.getAdapter();
	}

	protected ListView listView() {
		return j2WBuilder.getListView();
	}

	/********************** ViewPager业务代码 *********************/

	public J2WIViewPagerAdapter viewPagerAdapter() {
		return j2WBuilder.getViewPagerAdapter();
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