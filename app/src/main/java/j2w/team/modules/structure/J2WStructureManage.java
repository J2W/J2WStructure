package j2w.team.modules.structure;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.J2WApplication;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WBiz;
import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.common.log.L;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.display.J2WIDisplay;
import j2w.team.receiver.J2WReceiver;
import j2w.team.service.J2WService;
import j2w.team.structure.R;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */

public class J2WStructureManage<D extends J2WIDisplay> implements J2WStructureIManage<D> {

	/** 业务集合 **/
	private Map<String, Object>	stackBiz;

	/** 显示集合 **/
	private Map<String, Object>	stackDisplay;

	private D					display;

	@Override public D getDisplay() {
		return display;
	}

	@Override public void attach() {
		/** 初始化集合 **/
		stackBiz = new HashMap<>();
		stackDisplay = new HashMap<>();
	}

	@Override public void detach() {
		/** 清除 **/
		for (Object item : stackDisplay.values()) {
			J2WIDisplay j2WIDisplay = (J2WIDisplay) item;
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
	}

	@Override public void attachActivity(J2WActivity activity) {
		/** 默认初始化 **/
		attach();
		/** 初始化所有组建 **/
		ButterKnife.bind(activity);
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushActivity(activity);
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(activity.getClass(), 0);
		display = (D) J2WBizUtils.createDisplay(displayClass);
		stackDisplay.put(displayClass.getSimpleName(), display);

	}

	@Override public void detachActivity(J2WActivity activity) {
		/** 默认销毁化 **/
		detach();
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(activity);
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popActivity(activity);
	}

	@Override public void attachFragment(J2WFragment fragment, View view) {
		/** 默认初始化 **/
		attach();
		/** 初始化所有组建 **/
		ButterKnife.bind(fragment, view);
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(fragment.getClass(), 0);
		display = (D) J2WBizUtils.createDisplay(displayClass);
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void detachFragment(J2WFragment fragment) {
		/** 默认初始化 **/
		detach();
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(fragment.getActivity());
	}

	@Override public void attachDialogFragment(J2WDialogFragment dialogFragment, View view) {
		/** 默认初始化 **/
		attach();
		/** 初始化所有组建 **/
		ButterKnife.bind(dialogFragment, view);
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(dialogFragment.getClass(), 0);
		display = (D) J2WBizUtils.createDisplay(displayClass);
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void detachDialogFragment(J2WDialogFragment dialogFragment) {
		/** 默认初始化 **/
		detach();
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(dialogFragment.getActivity());
	}

	@Override public void attachReceiver(J2WReceiver j2WReceiver) {
		/** 默认初始化 **/
		attach();
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(j2WReceiver.getClass(), 0);
		display = (D) J2WBizUtils.createDisplayNotView(displayClass, J2WHelper.getInstance());
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void detachReceiver(J2WReceiver j2WReceiver) {
		/** 默认初始化 **/
		detach();
	}

	@Override public void attachService(J2WService j2WService) {
		/** 默认初始化 **/
		attach();
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(j2WService.getClass(), 0);
		display = (D) J2WBizUtils.createDisplayNotView(displayClass, J2WHelper.getInstance());
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void detachService(J2WService j2WService) {
		/** 默认初始化 **/
		detach();
	}

	@Override public void attachBiz(J2WBiz j2WBiz, J2WView j2WView) {
		/** 默认初始化 **/
		attach();
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(j2WBiz.getClass(), 0);
		display = (D) J2WBizUtils.createDisplayBiz(displayClass, j2WView);
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void attachBiz(J2WBiz j2WBiz, Object callback) {
		/** 默认初始化 **/
		attach();
		/** 初始化显示 **/
		Class displayClass = J2WAppUtil.getSuperClassGenricType(j2WBiz.getClass(), 0);
		display = (D) J2WBizUtils.createDisplayNotView(displayClass, J2WHelper.getInstance());
		stackDisplay.put(displayClass.getSimpleName(), display);
	}

	@Override public void detachBiz(J2WBiz j2WBiz) {
		/** 默认销毁化 **/
		detach();
	}

	@Override public <N extends J2WIDisplay> N display(Class<N> eClass, J2WView j2WView) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		N obj = (N) stackDisplay.get(eClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createDisplay(eClass);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackDisplay.put(eClass.getSimpleName(), obj);
		}
		obj.initDisplay(j2WView);
		return obj;
	}

	@Override public <N extends J2WIDisplay> N display(Class<N> eClass, Object object) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		N obj = (N) stackDisplay.get(eClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createDisplayNotView(eClass, J2WHelper.getInstance());
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackDisplay.put(eClass.getSimpleName(), obj);
		}
		obj.initDisplay(J2WHelper.getInstance());
		return obj;
	}

	@Override public <B extends J2WIBiz> B biz(Class<B> biz, J2WView j2WView) {
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
		Object obj = stackBiz.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createBiz(biz, j2WView);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}

	@Override public <B extends J2WIBiz> B biz(Class<B> biz, Object object) {
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
		Object obj = stackBiz.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createBiz(biz, this);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}

	@Override public <H> H http(Class<H> hClass, J2WBiz j2WBiz) {
		J2WCheckUtils.checkNotNull(hClass, "请指定View接口～");
		Object obj = stackBiz.get(hClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.httpAdapter().create(hClass, j2WBiz);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stackBiz.put(hClass.getSimpleName(), obj);
		}
		return (H) obj;
	}

	@Override public <I> I createImpl(Class<I> inter, J2WBiz j2WBiz) {
		J2WCheckUtils.checkNotNull(inter, "请指定View接口～");
		Object obj = stackBiz.get(inter.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createImpl(inter, j2WBiz);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stackBiz.put(inter.getSimpleName(), obj);
		}
		return (I) obj;
	}

	@Override public <U> U ui(Class<U> ui, J2WBiz j2WBiz, Object object) {
		J2WCheckUtils.checkNotNull(ui, "请指定View接口～");
		Object obj = stackBiz.get(ui.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createUI(ui, object, j2WBiz);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stackBiz.put(ui.getSimpleName(), obj);
		}
		return (U) obj;
	}

	@Override public boolean onKeyBack(int keyCode, FragmentManager fragmentManager) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			int idx = fragmentManager.getBackStackEntryCount();
			if (idx > 1) {
				FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(idx - 1);
				J2WFragment j2WFragment = (J2WFragment) fragmentManager.findFragmentByTag(entry.getName());
				if (j2WFragment != null) {
					return j2WFragment.onKeyBack();
				}
			} else {
				J2WFragment j2WFragment = (J2WFragment) fragmentManager.findFragmentById(R.id.j2w_home);
				if (j2WFragment != null) {
					return j2WFragment.onKeyBack();
				}
			}
		}
		return false;
	}

	@Override public void printBackStackEntry(FragmentManager fragmentManager) {
		if (J2WHelper.getInstance().isLogOpen()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("(");
			for (Fragment fragment : fragmentManager.getFragments()) {
				if(fragment != null){
					stringBuilder.append(fragment.getClass().getSimpleName());
					stringBuilder.append(",");
				}
			}
			stringBuilder.append(")");
			L.tag("display");
			L.i(stringBuilder.toString());
		}

	}
}