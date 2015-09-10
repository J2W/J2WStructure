package j2w.team.modules.structure;

import android.view.View;

import j2w.team.biz.J2WIBiz;
import j2w.team.display.J2WIDisplay;
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
	 * 显示
	 * 
	 * @param eClass
	 * @param j2WView
	 * @return
	 */
	<N extends J2WIDisplay> N display(Class<N> eClass, J2WView j2WView);
	/**
	 * 业务
	 * 
	 * @param biz
	 * @param j2WView
	 * @param <B>
	 * @return
	 */
	<B extends J2WIBiz> B biz(Class<B> biz, J2WView j2WView);
}