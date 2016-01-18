package j2w.team.modules.structure;

import android.support.v4.app.FragmentManager;
import android.view.View;

import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.service.J2WService;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;

/**
 * @创建人 sky
 * @创建时间 15/9/10 下午3:57
 * @类描述 结构管理器
 */
public interface J2WStructureIManage<B extends J2WIBiz> {

	B getBiz();

	/**
	 * deafualt
	 */

	void attach();

	void detach();

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
	 * service
	 */
	void attachService(J2WService activity);

	void detachService(J2WService activity);

	<D extends J2WIDisplay> D display(Class<D> eClass);

	<B extends J2WIBiz> B biz(Class<B> biz);

	<H> H http(Class<H> hClass);

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