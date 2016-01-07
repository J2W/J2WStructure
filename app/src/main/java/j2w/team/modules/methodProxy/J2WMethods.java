package j2w.team.modules.methodProxy;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.Impl;
import j2w.team.core.plugin.J2WActivityInterceptor;
import j2w.team.core.plugin.J2WEndInterceptor;
import j2w.team.core.plugin.J2WErrorInterceptor;
import j2w.team.core.plugin.J2WFragmentInterceptor;
import j2w.team.core.plugin.J2WHttpErrorInterceptor;
import j2w.team.core.plugin.J2WStartInterceptor;

/**
 * @创建人 sky
 * @创建时间 16/1/5
 * @类描述 方法代理处理
 */
public final class J2WMethods {

    final Map<Method, J2WMethod> methodHandlerCache;

    final Stack<String> stack;

    final J2WActivityInterceptor j2WActivityInterceptor;

    final J2WFragmentInterceptor j2WFragmentInterceptor;

    final ArrayList<J2WStartInterceptor> j2WStartInterceptor;        // 方法开始拦截器

    final ArrayList<J2WEndInterceptor> j2WEndInterceptor;            // 方法结束拦截器

    final ArrayList<J2WErrorInterceptor> j2WErrorInterceptor;        // 方法错误拦截器

    final ArrayList<J2WHttpErrorInterceptor> j2WHttpErrorInterceptor;    // 方法错误拦截器

    public J2WMethods(J2WActivityInterceptor j2WActivityInterceptor, J2WFragmentInterceptor j2WFragmentInterceptor, ArrayList<J2WStartInterceptor> j2WStartInterceptor,
                      ArrayList<J2WEndInterceptor> j2WEndInterceptor, ArrayList<J2WErrorInterceptor> j2WErrorInterceptor, ArrayList<J2WHttpErrorInterceptor> j2WHttpErrorInterceptor) {
        this.methodHandlerCache = new LinkedHashMap<>();
        this.stack = new Stack<>();
        this.j2WEndInterceptor = j2WEndInterceptor;
        this.j2WStartInterceptor = j2WStartInterceptor;
        this.j2WErrorInterceptor = j2WErrorInterceptor;
        this.j2WHttpErrorInterceptor = j2WHttpErrorInterceptor;
        this.j2WActivityInterceptor = j2WActivityInterceptor;
        this.j2WFragmentInterceptor = j2WFragmentInterceptor;
    }

    /**
     * 创建
     *
     * @param service
     * @param <T>
     * @return
     */
    public <T> T create(final Class<T> service) {
        validateServiceInterface(service);
        // 实现类
        final Object impl = getImplClass(service);
        J2WCheckUtils.checkNotNull(impl, "接口没有设置@Impl(class)，请设置～");
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                J2WMethod j2WMethod = loadJ2WMethod(method, service, impl);
                return j2WMethod.invoke(args);
            }
        });
    }

    public <T> T createMain(final Class<T> service) {
        validateServiceInterface(service);
        // 实现类
        final Object impl = getImplClass(service);
        J2WCheckUtils.checkNotNull(impl, "接口没有设置@Impl(class)，请设置～");
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object... args) throws Throwable {
                J2WMethod j2WMethod = loadJ2WMainMethod(method, service, impl);
                return j2WMethod.invoke(args);
            }
        });
    }

    /**
     * 获取拦截器
     *
     * @return
     */
    public J2WActivityInterceptor activityInterceptor() {
        return j2WActivityInterceptor;
    }

    /**
     * 获取拦截器
     *
     * @return
     */
    public J2WFragmentInterceptor fragmentInterceptor() {
        return j2WFragmentInterceptor;
    }

    /**
     * 加载UI和display接口
     *
     * @param <T>
     * @param service
     * @param impl
     * @return
     */
    private <T> J2WMethod loadJ2WMainMethod(Method method, Class<T> service, Object impl) {
        J2WMethod j2WMethod;

        synchronized (methodHandlerCache) {
            j2WMethod = methodHandlerCache.get(method);
            if (j2WMethod == null) {
                j2WMethod = J2WMethod.createMainMethod(this, impl, method, service);
                methodHandlerCache.put(method, j2WMethod);
            }
        }
        return j2WMethod;
    }

    /**
     * 加载接口
     *
     * @param <T>
     * @param service
     * @param impl
     * @return
     */
    private <T> J2WMethod loadJ2WMethod(Method method, Class<T> service, Object impl) {
        J2WMethod j2WMethod;

        synchronized (methodHandlerCache) {
            j2WMethod = methodHandlerCache.get(method);
            if (j2WMethod == null) {
                j2WMethod = J2WMethod.createMethod(this, impl, method, service);
                methodHandlerCache.put(method, j2WMethod);
            }
        }
        return j2WMethod;
    }

    /**
     * 获取实现类
     *
     * @param service
     * @param <D>
     * @return
     */
    <D> Object getImplClass(@NotNull Class<D> service) {
        validateServiceClass(service);
        try {
            // 获取注解
            Impl impl = service.getAnnotation(Impl.class);
            J2WCheckUtils.checkNotNull(impl, "该接口没有指定实现类～");
            /** 加载类 **/
            Class clazz = Class.forName(impl.value().getName());
            J2WCheckUtils.checkNotNull(clazz, "业务类为空～");
            /** 创建类BIZ **/
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，没有找到业务类！");
        } catch (java.lang.InstantiationException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，实例化异常！");
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.valueOf(service) + "，访问权限异常！");
        }
    }

    /**
     * 验证
     *
     * @param service
     * @param <T>
     */
    private <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("该类不是接口");
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

    public static class Builder {

        private J2WActivityInterceptor j2WActivityInterceptor;    // activity拦截器

        private J2WFragmentInterceptor j2WFragmentInterceptor;    // activity拦截器

        private ArrayList<J2WStartInterceptor> j2WStartInterceptors;        // 方法开始拦截器

        private ArrayList<J2WEndInterceptor> j2WEndInterceptors;        // 方法结束拦截器

        private ArrayList<J2WErrorInterceptor> j2WErrorInterceptors;        // 方法错误拦截器

        private ArrayList<J2WHttpErrorInterceptor> j2WHttpErrorInterceptors;    // 方法网络错误拦截器

        public void setActivityInterceptor(J2WActivityInterceptor j2WActivityInterceptor) {
            this.j2WActivityInterceptor = j2WActivityInterceptor;
        }

        public void setFragmentInterceptor(J2WFragmentInterceptor j2WFragmentInterceptor) {
            this.j2WFragmentInterceptor = j2WFragmentInterceptor;

        }

        public Builder addStartInterceptor(J2WStartInterceptor j2WStartInterceptor) {
            if (j2WStartInterceptors == null) {
                j2WStartInterceptors = new ArrayList<>();
            }
            if (!j2WStartInterceptors.contains(j2WStartInterceptor)) {
                j2WStartInterceptors.add(j2WStartInterceptor);
            }
            return this;
        }

        public Builder addEndInterceptor(J2WEndInterceptor j2WEndInterceptor) {
            if (j2WEndInterceptors == null) {
                j2WEndInterceptors = new ArrayList<>();
            }
            if (!j2WEndInterceptors.contains(j2WEndInterceptor)) {
                j2WEndInterceptors.add(j2WEndInterceptor);
            }
            return this;
        }

        public void addErrorInterceptor(J2WErrorInterceptor j2WErrorInterceptor) {
            if (j2WErrorInterceptors == null) {
                j2WErrorInterceptors = new ArrayList<>();
            }
            if (!j2WErrorInterceptors.contains(j2WErrorInterceptor)) {
                j2WErrorInterceptors.add(j2WErrorInterceptor);
            }
        }

        public void addErrorInterceptor(J2WHttpErrorInterceptor j2WHttpErrorInterceptor) {
            if (j2WHttpErrorInterceptors == null) {
                j2WHttpErrorInterceptors = new ArrayList<>();
            }
            if (!j2WHttpErrorInterceptors.contains(j2WHttpErrorInterceptor)) {
                j2WHttpErrorInterceptors.add(j2WHttpErrorInterceptor);
            }
        }

        public J2WMethods build() {
            // 默认值
            ensureSaneDefaults();
            return new J2WMethods(j2WActivityInterceptor, j2WFragmentInterceptor, j2WStartInterceptors, j2WEndInterceptors, j2WErrorInterceptors, j2WHttpErrorInterceptors);
        }

        private void ensureSaneDefaults() {
            if (j2WStartInterceptors == null) {
                j2WStartInterceptors = new ArrayList<>();
            }
            if (j2WEndInterceptors == null) {
                j2WEndInterceptors = new ArrayList<>();
            }
            if (j2WErrorInterceptors == null) {
                j2WErrorInterceptors = new ArrayList<>();
            }
            if (j2WHttpErrorInterceptors == null) {
                j2WHttpErrorInterceptors = new ArrayList<>();
            }
            if (j2WFragmentInterceptor == null) {
                j2WFragmentInterceptor = J2WFragmentInterceptor.NONE;
            }
            if (j2WActivityInterceptor == null) {
                j2WActivityInterceptor = J2WActivityInterceptor.NONE;
            }
        }

    }
}
