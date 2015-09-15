package j2w.team.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WIBiz;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.view.J2WViewPager;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.dialog.iface.IDialogCancelListener;
import j2w.team.modules.dialog.provided.J2WIDialogFragment;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.modules.structure.J2WStructureManage;
import j2w.team.structure.R;
import j2w.team.view.adapter.J2WIViewPagerAdapter;
import j2w.team.view.adapter.J2WListAdapter;
import j2w.team.view.adapter.recycleview.HeaderRecyclerViewAdapterV1;

/**
 * @创建人 sky
 * @创建时间 15/8/8 下午1:29
 * @类描述 View层碎片 dialog
 */
public abstract class J2WDialogFragment<D extends J2WIDisplay> extends DialogFragment implements J2WIDialogFragment {

	private boolean					targetActivity;

	/** 请求编码 **/
	protected int					mRequestCode		= 2013 << 5;

	/** 请求默认值 **/
	public final static String		ARG_REQUEST_CODE	= "j2w_request_code";

	/** View层编辑器 **/
	private J2WBuilder				j2WBuilder;

	/** 结构 **/
	private J2WStructureIManage<D>	j2WStructureIManage;

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

	/**
	 * 自定义样式
	 *
	 * @return
	 */
	protected int getJ2WStyle() {
		return R.style.J2W_Dialog;
	}

	/**
	 * 是否可取消
	 *
	 * @return
	 */
	protected boolean isCancel() {
		return true;
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
	 * 创建Dialog
	 *
	 * @param savedInstanceState
	 * @return
	 */
	@Override public Dialog onCreateDialog(Bundle savedInstanceState) {
		// 获取参数
		Bundle args = getArguments();
		// 创建对话框
		Dialog dialog = new Dialog(getActivity(), getJ2WStyle());
		// 获取参数-设置是否可取消
		if (args != null) {
			dialog.setCanceledOnTouchOutside(isCancel());
		}
		return dialog;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 打开开关触发菜单项 **/
		setHasOptionsMenu(true);
		// 获取指定碎片
		final Fragment targetFragment = getTargetFragment();
		// 如果有指定碎片 从指定碎片里获取请求码，反之既然
		if (targetFragment != null) {
			mRequestCode = getTargetRequestCode();
		}
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** 初始化图片架构 **/
		J2WHelper.frescoHelper().initialize();
		/** 初始化视图 **/
		j2WBuilder = new J2WBuilder(this, inflater);
		View view = build(j2WBuilder).create();
		/** 状态栏颜色 **/
		j2WBuilder.initTint();
		/** 初始化结构 **/
		j2WStructureIManage = new J2WStructureManage();
		/** 初始化业务 **/
		j2WStructureIManage.attachDialogFragment(this, view);
		return view;
	}

	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData(getArguments());
	}

	@Override public void onResume() {
		super.onResume();
		/** 判断EventBus 是否注册 **/
		if (j2WBuilder.isOpenEventBus()) {
			J2WHelper.eventBus().register(this);
		}
		j2WStructureIManage.printBackStackEntry(getFragmentManager());
	}

	@Override public void onPause() {
		super.onPause();
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

	@Override public void onDestroyView() {
		super.onDestroyView();
		/** 关闭event **/
		if (j2WBuilder.isNotCloseEventBus()) {
			if (J2WHelper.eventBus().isRegistered(this)) {
				J2WHelper.eventBus().unregister(this);
			}
		}
		/** 清空注解view **/
		ButterKnife.unbind(this);
		/** 移除builder **/
		j2WBuilder.detach();
		j2WBuilder = null;
		/** 清楚结构 **/
		j2WStructureIManage.detachDialogFragment(this);
		j2WStructureIManage = null;
		// 销毁
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	public D display() {
		j2WStructureIManage.getDisplay().initDisplay(j2wView());
		return j2WStructureIManage.getDisplay();
	}

	public <N extends J2WIDisplay> N display(Class<N> eClass) {
		return j2WStructureIManage.display(eClass, j2wView());
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
		return j2WStructureIManage.biz(biz, j2wView());
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
			menu.clear();
			this.getActivity().getMenuInflater().inflate(j2WBuilder.getToolbarMenuId(), menu);
		}
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

	/********************** View业务代码 *********************/

	public J2WView j2wView() {
		return j2WBuilder.getJ2WView();
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
	protected void onVisible() {}

	/**
	 * 不可见
	 */
	protected void onInvisible() {}

	/**
	 * 返回键
	 */
	public boolean onKeyBack() {
		getActivity().onBackPressed();
		return true;
	}

	/********************** Dialog业务代码 *********************/
	/**
	 * 获取某种类型的所有侦听器
	 */
	protected <T> List<T> getDialogListeners(Class<T> listenerInterface) {
		final Fragment targetFragment = getTargetFragment();
		List<T> listeners = new ArrayList<>(2);
		if (targetFragment != null && listenerInterface.isAssignableFrom(targetFragment.getClass())) {
			listeners.add((T) targetFragment);
		}
		if (getActivity() != null && listenerInterface.isAssignableFrom(getActivity().getClass())) {
			listeners.add((T) getActivity());
		}
		return Collections.unmodifiableList(listeners);
	}

	/**
	 * 取消
	 *
	 * @param dialog
	 */
	@Override public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		for (IDialogCancelListener listener : getCancelListeners()) {
			listener.onCancelled(mRequestCode);
		}
	}

	/**
	 * 获取取消的所有事件
	 *
	 * @return
	 */
	protected List<IDialogCancelListener> getCancelListeners() {
		return getDialogListeners(IDialogCancelListener.class);
	}

	/**
	 * 显示碎片
	 *
	 * @return
	 */
	@Override public DialogFragment show(FragmentManager fragmentManager) {
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public DialogFragment show(FragmentManager fragmentManager, int mRequestCode) {
		this.mRequestCode = mRequestCode;
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment) {
		this.setTargetFragment(mTargetFragment, mRequestCode);
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public DialogFragment show(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode) {
		this.setTargetFragment(mTargetFragment, mRequestCode);
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public DialogFragment show(FragmentManager fragmentManager, Activity activity) {
		this.targetActivity = true;
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public DialogFragment show(FragmentManager fragmentManager, Activity activity, int mRequestCode) {
		this.targetActivity = true;
		this.mRequestCode = mRequestCode;
		show(fragmentManager, this.getClass().getSimpleName());
		return this;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			getActivity().onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 显示碎片-不保存activity状态
	 *
	 * @return
	 */
	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, int mRequestCode) {
		this.mRequestCode = mRequestCode;
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment) {
		if (mTargetFragment != null) {
			this.setTargetFragment(mTargetFragment, mRequestCode);
		}
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Fragment mTargetFragment, int mRequestCode) {
		if (mTargetFragment != null) {
			this.setTargetFragment(mTargetFragment, mRequestCode);
		}
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity) {
		this.targetActivity = true;
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}

	@Override public DialogFragment showAllowingStateLoss(FragmentManager fragmentManager, Activity activity, int mRequestCode) {
		this.targetActivity = true;
		this.mRequestCode = mRequestCode;
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.add(this, this.getClass().getSimpleName());
		ft.commitAllowingStateLoss();
		return this;
	}
}