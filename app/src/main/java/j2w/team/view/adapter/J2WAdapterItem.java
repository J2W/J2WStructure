package j2w.team.view.adapter;

import android.view.View;

import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class J2WAdapterItem<T> implements Cloneable {

	private J2WView	j2WView;

	void setJ2WView(J2WView j2WView) {
		this.j2WView = j2WView;
	}

	/**
	 * 设置布局
	 * 
	 * @return 布局ID
	 */
	public abstract int getItemLayout();

	/**
	 * 初始化控件
	 * 
	 * @param contentView
	 *            ItemView
	 */
	public abstract void init(View contentView);

	/**
	 * 绑定数据
	 * 
	 * @param t
	 *            数据类型泛型
	 * @param position
	 *            下标
	 * @param count
	 *            数量
	 */
	public abstract void bindData(T t, int position, int count);

	/**
	 * 获取业务
	 *
	 * @param <B>
	 * @return
	 */
	protected <B extends J2WIBiz> B biz() {
		return j2WView.biz();
	}

	public <C extends J2WIBiz> C biz(Class<C> service) {
		return j2WView.biz(service);
	}

	public J2WFragment fragment() {
		return j2WView.fragment();
	}

	public J2WActivity activity() {
		return j2WView.activity();
	}

	public J2WDialogFragment dialogFragment() {
		return j2WView.dialogFragment();
	}

	public J2WView getUI() {
		return j2WView;
	}

	/**
	 * 获取调度
	 *
	 * @param e
	 * @param <E>
	 * @return
	 */
	protected <E extends J2WIDisplay> E display(Class<E> e) {
		return j2WView.display(e);
	}

	/**
	 * 获取fragment
	 *
	 * @param clazz
	 * @return
	 */
	public <T> T findFragment(Class<T> clazz) {
		J2WCheckUtils.checkNotNull(clazz, "class不能为空");
		return (T) j2WView.manager().findFragmentByTag(clazz.getSimpleName());
	}

	/**
	 * 克隆
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	@Override protected final Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
}
