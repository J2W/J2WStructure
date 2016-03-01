package j2w.team.modules.structure;

import android.support.v4.app.FragmentManager;

import org.jetbrains.annotations.NotNull;

import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */
public interface J2WStructureIManage {

	void attach(Object view);

	void detach(Object view);

	<D extends J2WIDisplay> D display(Class<D> displayClazz);

	<B extends J2WIBiz> B biz(Class<B> bizClazz);

	<B extends J2WIBiz> B common(Class<B> service);

	<H> H http(Class<H> httpClazz);

	<P> P impl(Class<P> implClazz);

	<D> Object getImplClass(@NotNull Class<D> service, Object ui);

	<T> T createMainLooper(final Class<T> service, Object ui);

	/**
	 * 拦截back 交给 fragment onKeyBack
	 *
	 * @param keyCode
	 * @param fragmentManager
	 * @param bj2WActivity
	 * @return
	 */
	boolean onKeyBack(int keyCode, FragmentManager fragmentManager, J2WActivity bj2WActivity);

	/**
	 * 打印堆栈内容
	 *
	 * @param fragmentManager
	 */
	void printBackStackEntry(FragmentManager fragmentManager);

}