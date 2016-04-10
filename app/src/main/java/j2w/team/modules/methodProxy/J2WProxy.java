package j2w.team.modules.methodProxy;

import java.util.concurrent.ConcurrentHashMap;

import j2w.team.core.J2WIBiz;

/**
 * @创建人 sky
 * @创建时间 16/4/8 下午9:37
 * @类描述 代理类
 */
public class J2WProxy {

    public J2WIBiz impl;                                // 实现类

    public Object proxy;                                // 代理类

    public ConcurrentHashMap<String, J2WMethod> methodCache = new ConcurrentHashMap();    // 方法缓存


    /**
     * 清空
     */
    public void clearProxy() {
        impl.detach();
        impl = null;
        proxy = null;
        methodCache.clear();
        methodCache = null;
    }
}
