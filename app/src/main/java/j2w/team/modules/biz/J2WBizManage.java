package j2w.team.modules.biz;

import java.util.HashMap;
import java.util.Map;

import j2w.team.biz.J2WIBiz;

/**
 * @创建人 sky
 * @创建时间 15/9/8 下午4:59
 * @类描述 业务管理器
 */
public class J2WBizManage implements J2WBizIManage {

	private final Map<String, Object>	stackBiz;

	public J2WBizManage() {
		this.stackBiz = new HashMap<>();
	}

	@Override public <B extends J2WIBiz> B biz(Class<B> biz) {
		return null;
	}
}