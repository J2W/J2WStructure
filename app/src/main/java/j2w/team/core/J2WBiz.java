package j2w.team.core;

import java.util.HashMap;
import java.util.Map;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.display.J2WIDisplay;

/**
 * Created by sky on 15/2/1.
 */
public abstract class J2WBiz<U> implements J2WIBiz {

	private Map<String, Object>	stack;

	private U					u;

	public J2WBiz() {
		/** 初始化集合 **/
		stack = new HashMap<>();
		Class bizClass = J2WAppUtil.getSuperClassGenricType(getClass(), 0);
		u = (U) J2WHelper.createUI(bizClass);
	}

	protected <H> H http(Class<H> hClass) {
		J2WCheckUtils.checkNotNull(hClass, "请指定View接口～");
		Object obj = stack.get(hClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.httpAdapter().create(hClass);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stack.put(hClass.getSimpleName(), obj);
		}
		return (H) obj;
	}

	protected <I> I createImpl(Class<I> inter) {
		J2WCheckUtils.checkNotNull(inter, "请指定View接口～");
		Object obj = stack.get(inter.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.createBiz(inter);
			J2WCheckUtils.checkUINotNull(obj, "没有实现接口");
			stack.put(inter.getSimpleName(), obj);
		}
		return (I) obj;
	}

	protected <D extends J2WIDisplay> D display(Class<D> eClass) {
		J2WCheckUtils.checkNotNull(eClass, "display接口不能为空");
		D obj = (D) stack.get(eClass.getSimpleName());
		if (obj == null) {// 如果没有索索到
			obj = J2WHelper.createDisplay(eClass);
			J2WCheckUtils.checkNotNull(obj, "没有实现接口");
			stack.put(eClass.getSimpleName(), obj);
		}
		return obj;
	}

	/**
	 * View层 回调引用
	 *
	 * @return
	 */
	protected U UI() {
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
