package j2w.team.modules.structure;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import j2w.team.J2WHelper;
import j2w.team.biz.J2WBizUtils;
import j2w.team.biz.J2WIBiz;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.common.utils.J2WKeyboardUtils;
import j2w.team.display.J2WIDisplay;
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

	@Override public <B extends J2WIBiz> B biz(Class<B> biz, J2WView j2WView) {
		J2WCheckUtils.checkNotNull(biz, "请指定业务接口～");
		Object obj = stackBiz.get(biz.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WBizUtils.createBiz(biz, j2WView);
			stackBiz.put(biz.getSimpleName(), obj);
		}
		return (B) obj;
	}
}