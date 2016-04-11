package j2w.team.core;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.core.exception.J2WNotUIPointerException;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureModel;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * Created by sky on 15/2/1.
 */
public abstract class J2WBiz<U> implements J2WIBiz {

	private U					u;

	private J2WStructureModel	j2WStructureModel;

	protected <H> H http(Class<H> hClass) {
		return j2WStructureModel.http(hClass);
	}

	protected <I> I impl(Class<I> inter) {
		return j2WStructureModel.impl(inter);
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		return j2WStructureModel.display(eClass);
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		if (j2WStructureModel.getService().equals(service)) {
			return (C) j2WStructureModel.getJ2WProxy().proxy;
		} else {
			return J2WHelper.structureHelper().biz(service);
		}
	}

	/**
	 * View层 回调引用
	 *
	 * @return
	 */
	protected U ui() {
		if (u == null) {
			throw new J2WNotUIPointerException("视图被销毁");
		}
		return u;
	}

	/**
	 * View层 是否存在
	 * 
	 * @return
	 */
	public boolean isUI() {
		return u != null;
	}

	@Override public void initUI(J2WStructureModel j2WStructureModel) {
		this.j2WStructureModel = j2WStructureModel;
		Class ui = J2WAppUtil.getSuperClassGenricType(this.getClass(), 0);
		u = (U) J2WHelper.structureHelper().createMainLooper(ui, j2WStructureModel.getView());
	}

	@Override public void detach() {
		u = null;
		j2WStructureModel = null;
	}
}
