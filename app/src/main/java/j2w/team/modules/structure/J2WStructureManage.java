package j2w.team.modules.structure;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.log.L;
import j2w.team.service.J2WService;
import j2w.team.structure.R;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */

public class J2WStructureManage<B extends J2WIBiz> implements J2WStructureIManage<B> {

	private Map<String, Object>	stack;

	/** 显示集合 **/
	private Map<String, Object>	stackDisplay;

	private B					biz;

	public J2WStructureManage() {
		/** 初始化集合 **/
		stack = new HashMap<>();
		stackDisplay = new HashMap<>();
	}

	@Override public B getBiz() {
		return biz;
	}

	@Override public void detach() {

		if (stack != null) {
			stack.clear();
			stack = null;
		}

		if (stackDisplay != null) {
			stackDisplay.clear();
			stackDisplay = null;
		}

		if (biz != null) {
			biz.detach();
			biz = null;
		}
	}

	@Override public void attachABiz(J2WActivity activity) {
		J2WHelper.screenHelper().pushView(activity.getClass().getName(), activity);
		/** 初始化 **/
		Class bizClass = J2WAppUtil.getSuperClassGenricType(activity.getClass(), 0);
		biz = (B) J2WHelper.createBiz(bizClass);
		stack.put(bizClass.getSimpleName(), biz);
	}

	@Override public void attachActivity(J2WActivity activity) {
		/** 初始化所有组建 **/
		ButterKnife.bind(activity);
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushActivity(activity);
		J2WHelper.screenHelper().pushView(activity.getClass().getName(), activity);
	}

	@Override public void detachActivity(J2WActivity activity) {
		/** 默认销毁化 **/
		detach();
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(activity);
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popActivity(activity);
		J2WHelper.screenHelper().popView(activity.getClass().getName());
	}

	@Override public void attachFBiz(J2WFragment fragment) {
		J2WHelper.screenHelper().pushView(fragment.getClass().getName(), fragment);
		/** 初始化 **/
		Class bizClass = J2WAppUtil.getSuperClassGenricType(fragment.getClass(), 0);
		biz = (B) J2WHelper.createBiz(bizClass);
		stack.put(bizClass.getSimpleName(), biz);
	}

	@Override public void attachFragment(J2WFragment fragment, View view) {
		/** 初始化所有组建 **/
		ButterKnife.bind(fragment, view);
	}

	@Override public void detachFragment(J2WFragment fragment) {
		/** 默认初始化 **/
		detach();
		J2WHelper.screenHelper().popView(fragment.getClass().getName());
		/** 清空注解view **/
		ButterKnife.unbind(fragment);
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(fragment.getActivity());
	}

	@Override public void attachDBiz(J2WDialogFragment fragment) {
		J2WHelper.screenHelper().pushView(fragment.getClass().getName(), fragment);
		/** 初始化 **/
		Class bizClass = J2WAppUtil.getSuperClassGenricType(fragment.getClass(), 0);
		biz = (B) J2WHelper.createBiz(bizClass);
		stack.put(bizClass.getSimpleName(), biz);
	}

	@Override public void attachDialogFragment(J2WDialogFragment dialogFragment, View view) {
		/** 初始化所有组建 **/
		ButterKnife.bind(dialogFragment, view);
	}

	@Override public void detachDialogFragment(J2WDialogFragment dialogFragment) {
		/** 清空注解view **/
		ButterKnife.unbind(dialogFragment);
		J2WHelper.screenHelper().popView(dialogFragment.getClass().getName());
		/** 默认初始化 **/
		detach();
		/** 关闭键盘 **/
		J2WKeyboardUtils.hideSoftInput(dialogFragment.getActivity());
	}

	@Override public void attachService(J2WService activity) {
		/** 添加到堆栈 **/
		J2WHelper.screenHelper().pushView(activity.getClass().getName(), activity);
		/** 初始化 **/
		Class bizClass = J2WAppUtil.getSuperClassGenricType(activity.getClass(), 0);
		biz = (B) J2WHelper.createBiz(bizClass);
		stack.put(bizClass.getSimpleName(), biz);
	}

	@Override public void detachService(J2WService activity) {
		/** 默认销毁化 **/
		detach();
		/** 从堆栈里移除 **/
		J2WHelper.screenHelper().popView(activity.getClass().getName());
	}

	@Override public <D extends J2WIDisplay> D display(Class<D> eClass) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		D obj = (D) stackDisplay.get(eClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.createDisplay(eClass);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stackDisplay.put(eClass.getSimpleName(), obj);
		}
		return obj;
	}

	@Override public <B> B biz(Class<B> biz) {
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
		Object obj = stack.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.createBiz(biz);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stack.put(biz.getSimpleName(), obj);

		}
		return (B) obj;
	}

	@Override public <H> H http(Class<H> hClass) {
		J2WCheckUtils.checkNotNull(hClass, "请指定View接口～");
		Object obj = stack.get(hClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.httpAdapter().create(hClass);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stack.put(hClass.getSimpleName(), obj);
		}
		return (H) obj;
	}

	@Override public boolean onKeyBack(int keyCode, FragmentManager fragmentManager, J2WActivity<B> bj2WActivity) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			int idx = fragmentManager.getBackStackEntryCount();
			if (idx > 1) {
				FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(idx - 1);
				Object view = fragmentManager.findFragmentByTag(entry.getName());
				if (view instanceof J2WFragment) {
					return ((J2WFragment)view).onKeyBack();
				}else if(view instanceof J2WDialogFragment){
					return ((J2WDialogFragment)view).onKeyBack();
				}
			} else {

				Object view = fragmentManager.findFragmentById(R.id.j2w_home);
				if (view instanceof J2WFragment) {
					return ((J2WFragment)view).onKeyBack();
				}else if(view instanceof J2WDialogFragment){
					return ((J2WDialogFragment)view).onKeyBack();
				}
			}
			if (bj2WActivity != null) {
				return bj2WActivity.onKeyBack();
			}
		}
		return false;
	}

	@Override public void printBackStackEntry(FragmentManager fragmentManager) {
		if (J2WHelper.getInstance().isLogOpen()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("[");
			for (Fragment fragment : fragmentManager.getFragments()) {
				if (fragment != null) {
					stringBuilder.append(",");
					stringBuilder.append(fragment.getClass().getSimpleName());
				}
			}
			stringBuilder.append("]");
			stringBuilder.deleteCharAt(1);
			L.tag("Activity FragmentManager:");
			L.i(stringBuilder.toString());
		}
	}
}