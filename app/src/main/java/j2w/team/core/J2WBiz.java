package j2w.team.core;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.core.exception.J2WNotUIPointerException;
import j2w.team.display.J2WIDisplay;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * Created by sky on 15/2/1.
 */
public abstract class J2WBiz<U> implements J2WIBiz {

	private U				u;

	private NotCacheMethods	notCacheMethods;

	private Object			ui;				// 没有代理的ui

	protected <H> H http(Class<H> hClass) {
		return J2WHelper.structureHelper().http(hClass);
	}

	protected <I> I impl(Class<I> inter) {
		return J2WHelper.structureHelper().impl(inter);
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		return J2WHelper.structureHelper().display(eClass);
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		if (notCacheMethods != null) {
			if (ui instanceof J2WActivity) {
				return (C) ((J2WActivity) ui).biz(service);
			} else if (ui instanceof J2WFragment) {
				return (C) ((J2WFragment) ui).biz(service);
			} else if (ui instanceof J2WDialogFragment) {
				return (C) ((J2WDialogFragment) ui).biz(service);
			}
		}
		return J2WHelper.structureHelper().biz(service);
	}

	/**
	 * View层 回调引用
	 *
	 * @return
	 */
	protected U ui() {
		if (u == null) {
			throw new J2WNotUIPointerException("View层没有显示,就调用了该View的业务~");
		}
		return u;
	}

	@Override public void initUI(Object j2WView) {
		if (j2WView != null) {
			ui = j2WView;
			notCacheMethods = j2WView.getClass().getAnnotation(NotCacheMethods.class);
			Class ui = J2WAppUtil.getSuperClassGenricType(this.getClass(), 0);
			u = (U) J2WHelper.structureHelper().createMainLooper(ui, j2WView);
		}
	}

	@Override public void detach() {
		ui = null;
		u = null;
		notCacheMethods = null;
	}
}
