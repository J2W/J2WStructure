package j2w.team.modules.structure;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

import j2w.team.J2WHelper;
import j2w.team.common.utils.J2WAppUtil;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.J2WBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.modules.methodProxy.J2WProxy;

/**
 * @创建人 sky
 * @创建时间 16/4/11 下午1:29
 * @类描述
 */
public class J2WStructureModel {

    final int key;

    J2WProxy j2WProxy;

    private Object view;

    private ConcurrentHashMap<Class<?>, Object> stackHttp;

    private ConcurrentHashMap<Class<?>, Object> stackImpl;

    private ConcurrentHashMap<Class<?>, Object> stackDisplay;

    private Class service;

    public J2WStructureModel(Object view) {
        // 唯一标记
        key = view.hashCode();
        // 视图
        this.view = view;
        // 业务初始化
        service = J2WAppUtil.getSuperClassGenricType(view.getClass(), 0);
        J2WCheckUtils.validateServiceInterface(service);
        Object impl = getImplClass(service, view);
        j2WProxy = J2WHelper.methodsProxy().create(service, impl);
    }

    /**
     * 清空
     */
    public void clearAll() {
        this.view = null;
        service = null;
        if (stackHttp != null) {
            stackHttp.clear();
            stackHttp = null;
        }
        if (stackImpl != null) {
            stackImpl.clear();
            stackImpl = null;
        }
        if (stackDisplay != null) {
            stackDisplay.clear();
            stackDisplay = null;
        }
        if (j2WProxy != null) {
            j2WProxy.clearProxy();
            j2WProxy = null;
        }
    }

    /**
     * 调度
     *
     * @param displayClazz
     * @param <D>
     * @return
     */
    public synchronized <D extends J2WIDisplay> D display(Class<D> displayClazz) {
        if (stackDisplay == null) {
            stackDisplay = new ConcurrentHashMap();
        }
        D display = (D) stackDisplay.get(displayClazz);
        if (display == null) {
            J2WCheckUtils.checkNotNull(displayClazz, "display接口不能为空");
            J2WCheckUtils.validateServiceInterface(displayClazz);
            display = J2WHelper.structureHelper().createMainLooper(displayClazz, getImplClass(displayClazz, null));
            stackDisplay.put(displayClazz, display);
        }
        return display;
    }

    /**
     * 网络
     *
     * @param httpClazz
     * @param <H>
     * @return
     */
    public synchronized <H> H http(Class<H> httpClazz) {
        if (stackHttp == null) {
            stackHttp = new ConcurrentHashMap();
        }
        H http = (H) stackHttp.get(httpClazz);
        if (http == null) {
            J2WCheckUtils.checkNotNull(httpClazz, "http接口不能为空");
            J2WCheckUtils.validateServiceInterface(httpClazz);
            http = J2WHelper.httpAdapter().create(httpClazz);
            stackHttp.put(httpClazz, http);
        }
        return http;
    }

    /**
     * 实现
     *
     * @param implClazz
     * @param <P>
     * @return
     */
    public synchronized <P> P impl(Class<P> implClazz) {
        if (stackImpl == null) {
            stackImpl = new ConcurrentHashMap();
        }
        P impl = (P) stackImpl.get(implClazz);

        if (impl == null) {
            J2WCheckUtils.checkNotNull(implClazz, "impl接口不能为空");
            J2WCheckUtils.validateServiceInterface(implClazz);
            impl = J2WHelper.methodsProxy().createImpl(implClazz, getImplClass(implClazz, null));
            stackImpl.put(implClazz, impl);
        }
        return impl;
    }

    /**
     * 获取实现类
     *
     * @param service
     * @param <D>
     * @return
     */
    private <D> Object getImplClass(@NotNull Class<D> service, Object ui) {
        validateServiceClass(service);
        try {
            // 获取注解
            Impl impl = service.getAnnotation(Impl.class);
            J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
            /** 加载类 **/
            Class clazz = Class.forName(impl.value().getName());
            Constructor c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            J2WCheckUtils.checkNotNull(clazz, "业务类为空～");
            /** 创建类 **/
            Object o = c.newInstance();
            // 如果是业务类
            if (o instanceof J2WBiz) {
                ((J2WBiz) o).initUI(this);
            }
            return o;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，没有找到业务类！");
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，实例化异常！");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，访问权限异常！");
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，没有找到构造方法！");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，反射异常！");
        }
    }

    /**
     * 验证类 - 判断是否是一个接口
     *
     * @param service
     * @param <T>
     */
    private <T> void validateServiceClass(Class<T> service) {
        if (service == null || !service.isInterface()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(service);
            stringBuilder.append("，该类不是接口！");
            throw new IllegalArgumentException(stringBuilder.toString());
        }
    }

    public Object getView() {
        return view;
    }

    public Class getService() {
        return service;
    }

    public J2WProxy getJ2WProxy() {
        return j2WProxy;
    }
}