package j2w.team.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureModel;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.J2WRVAdapter;
import j2w.team.view.adapter.recycleview.J2WRVAdapterItem;

/**
 * @创建人 sky
 * @创建时间 15/7/18 上午11:49
 * @类描述 View层碎片
 */
public abstract class J2WFragment<B extends J2WIBiz> extends Fragment implements View.OnTouchListener {

	private boolean		targetActivity;

	J2WStructureModel	j2WStructureModel;

	/**
	 * 定制
	 *
	 * @param initialJ2WBuilder
	 * @return
	 **/
	protected abstract J2WBuilder build(J2WBuilder initialJ2WBuilder);

	/**
	 * 数据
	 *
	 * @param savedInstanceState
	 */
	protected void createData(Bundle savedInstanceState) {

	}

	/**
	 * 初始化数据
	 *
	 * @param savedInstanceState
	 *            数据
	 */
	protected abstract void initData(Bundle savedInstanceState);

	/** View层编辑器 **/
	private J2WBuilder	j2WBuilder;

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 打开开关触发菜单项 **/
		setHasOptionsMenu(true);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** 初始化结构 **/
		j2WStructureModel = new J2WStructureModel(this);

		J2WHelper.structureHelper().attach(j2WStructureModel);
		/** 初始化视图 **/
		j2WBuilder = new J2WBuilder(this, inflater);
		View view = build(j2WBuilder).create();
		/** 初始化所有组建 **/
		ButterKnife.bind(this, view);
		/** 初始化点击事件 **/
		view.setOnTouchListener(this);// 设置点击事件
		return view;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentCreated(this, getArguments(), savedInstanceState);
		createData(savedInstanceState);
		initData(getArguments());
	}

	@Override public void onStart() {
		super.onStart();
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentStart(this);
	}

	@Override public void onResume() {
		super.onResume();
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentResume(this);
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			if (!J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().register(this);
			}
		}
		J2WHelper.structureHelper().printBackStackEntry(getFragmentManager());
		listLoadMoreOpen();
	}

	@Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		J2WHelper.methodsProxy().activityInterceptor().onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override public void onPause() {
		super.onPause();
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentPause(this);
		/** 关闭event **/
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

	@Override public void onStop() {
		super.onStop();
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentStop(this);
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 显示隐藏切换
	 * 
	 * @param hidden
	 */
	@Override public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!j2WBuilder.isViewPager()) {
			if (hidden) { // 隐藏
				onInvisible();
			} else {// 显示
				onVisible();
			}
		}
	}

	@Override public void onDetach() {
		super.onDetach();
		detach();
		if (J2WHelper.eventBus().isRegistered(this)) {
			J2WHelper.eventBus().unregister(this);
		}
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;
		J2WHelper.structureHelper().detach(j2WStructureModel);
		/** 清空注解view **/
		ButterKnife.unbind(this);
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(getActivity());
	}

	/**
	 * 清空
	 */
	protected void detach() {}

	@Override public void onDestroy() {
		super.onDestroy();
		J2WHelper.methodsProxy().fragmentInterceptor().onFragmentDestroy(this);
	}

	public <D extends J2WIDisplay> D display(Class<D> eClass) {
		if (j2WStructureModel == null || j2WStructureModel.getView() == null) {
			return J2WHelper.display(eClass);
		}
		return j2WStructureModel.display(eClass);
	}

	public B biz() {
		if (j2WStructureModel == null || j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
			Class service = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
			return (B) J2WHelper.structureHelper().createNullService(service);
		}
		return (B) j2WStructureModel.getJ2WProxy().proxy;
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		if (j2WStructureModel != null && service.equals(j2WStructureModel.getService())) {
			if (j2WStructureModel == null || j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
				return J2WHelper.structureHelper().createNullService(service);
			}
			return (C) j2WStructureModel.getJ2WProxy().proxy;
		}
		return J2WHelper.biz(service);
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

	/**
	 * 返回键
	 */
	public boolean onKeyBack() {
		getActivity().onBackPressed();
		return true;
	}

	/**
	 * 设置输入法
	 * 
	 * @param mode
	 */
	public void setSoftInputMode(int mode) {
		getActivity().getWindow().setSoftInputMode(mode);
	}

	/********************** View业务代码 *********************/
	/**
	 * 获取fragment
	 *
	 * @param clazz
	 * @return
	 */
	public <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) getFragmentManager().findFragmentByTag(clazz.getName());
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

	public J2WView j2wView() {
		return j2WBuilder == null ? null : j2WBuilder.getJ2WView();
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

	/********************** RecyclerView业务代码 *********************/
	@Deprecated protected J2WRVAdapterItem adapterRecycler() {
		return j2WBuilder == null ? null : j2WBuilder.getJ2WRVAdapterItem();
	}

	protected J2WRVAdapter recyclerAdapter() {
		return j2WBuilder == null ? null : j2WBuilder.getJ2WRVAdapterItem2();
	}

	protected RecyclerView.LayoutManager recyclerLayoutManager() {
		return j2WBuilder == null ? null : j2WBuilder.getLayoutManager();
	}

	public RecyclerView recyclerView() {
		return j2WBuilder == null ? null : j2WBuilder.getRecyclerView();
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
		return j2WBuilder == null ? null : j2WBuilder.getAdapter();
	}

	protected ListView listView() {
		return j2WBuilder == null ? null : j2WBuilder.getListView();
	}

	/********************** ViewPager业务代码 *********************/

	protected J2WIViewPagerAdapter viewPagerAdapter() {
		return j2WBuilder == null ? null : j2WBuilder.getViewPagerAdapter();
	}

	protected J2WViewPager viewPager() {
		return j2WBuilder == null ? null : j2WBuilder.getViewPager();
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