package j2w.team.core;

import j2w.team.J2WHelper;
import j2w.team.display.J2WIDisplay;

/**
 * @创建人 sky
 * @创建时间 16/4/13 下午6:02
 * @类描述 公共接口
 */
public class J2WCommonBiz implements J2WICommonBiz {

	protected <H> H http(Class<H> hClass) {
		return J2WHelper.http(hClass);
	}

	protected <I> I impl(Class<I> inter) {
		return J2WHelper.impl(inter);
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		return J2WHelper.display(eClass);
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		return J2WHelper.biz(service);
	}
}