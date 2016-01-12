package j2w.team.core;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureIManage;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/1.
 */
public abstract class J2WBiz<U> implements J2WIBiz {

	private Map<String, Object>	stack;

	private U					u;

	private Object				UI;

	public J2WBiz() {
		/** 初始化集合 **/
		stack = new HashMap<>();
		Class bizClass = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
		Impl uiImpl = (Impl) bizClass.getAnnotation(Impl.class);
		UI = J2WHelper.UI(uiImpl.value().getName());
		u = (U) J2WHelper.createUI(bizClass);
	}

	protected <H> H http(Class<H> hClass) {
		J2WCheckUtils.checkNotNull(hClass, "请指定View接口～");
		J2WCheckUtils.validateServiceInterface(hClass);
		return (H) structureManage(UI).http(hClass);
	}

	protected <I> I createImpl(Class<I> inter) {
		J2WCheckUtils.checkNotNull(inter, "请指定View接口～");
		J2WCheckUtils.validateServiceInterface(inter);
		return (I) structureManage(UI).biz(inter);
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		J2WCheckUtils.validateServiceInterface(eClass);
		return (D) structureManage(UI).display(eClass);
	}

	protected <B extends J2WIBiz> B biz() {
		J2WCheckUtils.checkNotNull(this.getClass().getInterfaces()[0], "display接口不能为空");
		J2WCheckUtils.validateServiceInterface(this.getClass().getInterfaces()[0]);
		return (B) structureManage(UI).biz(this.getClass().getInterfaces()[0]);
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		J2WCheckUtils.checkNotNull(service, "请指定View接口～");
		J2WCheckUtils.validateServiceInterface(this.getClass().getInterfaces()[0]);

		if (service.equals(this.getClass().getInterfaces()[0])) {
			return biz();
		}

		Object biz = stack.get(service.getSimpleName());
		if (biz == null) {// 如果没有索索到
			Impl impl = service.getAnnotation(Impl.class);
			Class bizClass = J2WAppUtil.getSuperClassGenricType(impl.value(), 0);
			Impl uiImpl = (Impl) bizClass.getAnnotation(Impl.class);
			Object ui = J2WHelper.UI(uiImpl.value().getName());
			biz = structureManage(ui).biz(service);
			J2WCheckUtils.checkNotNull(biz, "没有实现接口");
			stack.put(service.getSimpleName(), biz);
		}

		return (C) biz;
	}

	/**
	 * 获取结构管理器
	 * 
	 * @return
	 */
	private J2WStructureIManage structureManage(Object object) {
		J2WStructureIManage j2WStructureIManage = null;
		if (object instanceof J2WFragment) {
			j2WStructureIManage = ((J2WFragment) object).getStructureManage();
		} else if (object instanceof J2WActivity) {
			j2WStructureIManage = ((J2WActivity) object).getStructureManage();
		} else if (object instanceof J2WDialogFragment) {
			j2WStructureIManage = ((J2WDialogFragment) object).getStructureManage();
		}
		return j2WStructureIManage;
	}

	/**
	 * View层 回调引用
	 *
	 * @return
	 */
	protected U ui() {
		return u;
	}

	@Override public void detach() {
		if (stack != null) {
			stack.clear();
			stack = null;
		}
		u = null;
	}
}
