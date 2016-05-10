package j2w.team.core;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.structure.J2WStructureModel;

/**
 * Created by sky on 15/2/1.
 */
public abstract class J2WBiz<U> implements J2WIBiz {

	private U					u;

	private Class				ui;

	private J2WStructureModel	j2WStructureModel;

	protected <H> H http(Class<H> hClass) {
		if (j2WStructureModel == null || j2WStructureModel.getView() == null) {
			return J2WHelper.http(hClass);
		}
		return j2WStructureModel.http(hClass);
	}

	protected <I> I impl(Class<I> inter) {
		if (j2WStructureModel == null || j2WStructureModel.getView() == null) {
			return J2WHelper.impl(inter);
		}
		return j2WStructureModel.impl(inter);
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		if (j2WStructureModel == null || j2WStructureModel.getView() == null) {
			return J2WHelper.display(eClass);
		}
		return j2WStructureModel.display(eClass);
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		if (j2WStructureModel != null && j2WStructureModel.isSupterClass(service)) {
			if (j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
				return J2WHelper.structureHelper().createNullService(service);
			}
			return (C) j2WStructureModel.getJ2WProxy().proxy;
		} else if (j2WStructureModel != null && service.equals(j2WStructureModel.getService())) {
			if (j2WStructureModel.getJ2WProxy() == null || j2WStructureModel.getJ2WProxy().proxy == null) {
				return J2WHelper.structureHelper().createNullService(service);
			}
			return (C) j2WStructureModel.getJ2WProxy().proxy;
		} else {
			return J2WHelper.biz(service);
		}
	}

	/**
	 * View层 回调引用
	 *
	 * @return
	 */
	protected U ui() {
		if (u == null) {
			Class ui = J2WAppUtil.getSuperClassGenricType(this.getClass(), 0);
			return (U) J2WHelper.structureHelper().createNullService(ui);
		}
		return u;
	}

	/**
	 * View层 回调
	 * 
	 * @param clazz
	 * @param <V>
	 * @return
	 */
	protected <V> V ui(Class<V> clazz) {
		if (clazz.equals(ui)) {
			return (V) ui();
		} else {
			return J2WHelper.structureHelper().createNullService(clazz);
		}
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
		ui = J2WAppUtil.getSuperClassGenricType(this.getClass(), 0);
		u = (U) J2WHelper.structureHelper().createMainLooper(ui, j2WStructureModel.getView());
	}

	@Override public void detach() {
		u = null;
		ui = null;
		j2WStructureModel = null;
	}
}
