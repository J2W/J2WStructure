package j2w.team.modules.biz;

import java.util.HashMap;
import java.util.Map;

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


}