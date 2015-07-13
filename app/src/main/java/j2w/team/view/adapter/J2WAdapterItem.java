package j2w.team.view.adapter;

import android.view.View;

import j2w.team.biz.J2WIBiz;
import j2w.team.view.J2WActivity;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class J2WAdapterItem<T> implements Cloneable {

	private J2WActivity	j2WActivity	= null;

	void setJ2WActivity(J2WActivity j2WActivity) {
		this.j2WActivity = j2WActivity;
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
	 * @param biz
	 *            泛型
	 * @param <B>
	 * @return
	 */
	protected  <B extends J2WIBiz> B biz(Class<B> biz) {
		return (B) j2WActivity.biz(biz);
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
