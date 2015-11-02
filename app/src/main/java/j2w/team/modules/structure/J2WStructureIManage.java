package j2w.team.modules.structure;

import android.support.v4.app.FragmentManager;
import android.view.View;

import j2w.team.biz.J2WBiz;
import j2w.team.biz.J2WCallBack;
import j2w.team.biz.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.receiver.J2WReceiver;
import j2w.team.service.J2WService;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */
public interface J2WStructureIManage<D extends J2WIDisplay> {

	D getDisplay();

	/**
	 * deafualt
	 */

	void attach();

	void detach();

	/**
	 * biz
	 */

	void attachBiz(J2WBiz j2WBiz, J2WView j2WView);

	void attachBiz(J2WBiz j2WBiz, Object callback);

	void detachBiz(J2WBiz j2WBiz);

	/**
	 * activity
	 */
	void attachActivity(J2WActivity activity);

	void detachActivity(J2WActivity activity);

	/**
	 * fragment
	 */
	void attachFragment(J2WFragment fragment, View view);

	void detachFragment(J2WFragment fragment);

	/**
	 * dialogfragment
	 */
	void attachDialogFragment(J2WDialogFragment dialogFragment, View view);

	void detachDialogFragment(J2WDialogFragment dialogFragment);

	/**
	 * receiver
	 * 
	 * @param j2WReceiver
	 */
	void attachReceiver(J2WReceiver j2WReceiver);

	void detachReceiver(J2WReceiver j2WReceiver);

	/**
	 * service
	 * 
	 * @param j2WService
	 */
	void attachService(J2WService j2WService);

	void detachService(J2WService j2WService);

	/**
	 * 显示
	 * 
	 * @param eClass
	 * @param j2WView
	 * @return
	 */
	<N extends J2WIDisplay> N display(Class<N> eClass, J2WView j2WView);

	<N extends J2WIDisplay> N display(Class<N> eClass, Object object);

	/**
	 * 业务
	 * 
	 * @param biz
	 * @param j2WView
	 * @param <B>
	 * @return
	 */
	<B extends J2WIBiz> B biz(Class<B> biz, J2WView j2WView);

	<B extends J2WIBiz> B biz(Class<B> biz);

	/**
	 * 网络
	 * 
	 * @param hClass
	 * @param j2WBiz
	 * @param <H>
	 * @return
	 */
	<H> H http(Class<H> hClass, J2WBiz j2WBiz);

	/**
	 * 实现
	 * 
	 * @param inter
	 * @param j2WBiz
	 * @param <I>
	 * @return
	 */
	<I> I createImpl(Class<I> inter, J2WBiz j2WBiz);

	/**
	 * UI
	 * 
	 * @param ui
	 * @param j2WBiz
	 * @param object
	 * @param <U>
	 * @return
	 */
	<U> U ui(Class<U> ui, J2WBiz j2WBiz, Object object);

	/**
	 * 拦截back 交给 fragment onKeyBack
	 * 
	 * @param keyCode
	 * @param fragmentManager
	 * @return
	 */
	boolean onKeyBack(int keyCode, FragmentManager fragmentManager);

	/**
	 * 打印堆栈内容
	 * 
	 * @param fragmentManager
	 */
	void printBackStackEntry(FragmentManager fragmentManager);
}