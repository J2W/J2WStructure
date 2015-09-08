package j2w.team.modules.biz;

import j2w.team.biz.J2WIBiz;

/**
 * @创建人 sky
 * @创建时间 15/9/8 下午5:02
 * @类描述 业务管理器接口
 */
public interface J2WBizIManage {

	/**
	 * 获取业务
	 * 
	 * @param biz
	 * @param <B>
	 * @return
	 */
	<B extends J2WIBiz> B biz(Class<B> biz);
}