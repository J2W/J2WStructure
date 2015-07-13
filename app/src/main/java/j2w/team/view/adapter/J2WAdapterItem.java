package j2w.team.view.adapter;

import android.view.View;

import j2w.team.view.J2WActivity;

/**
 * Created by sky on 15/2/6. 适配器
 */
public abstract class J2WAdapterItem<T> {

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
	public abstract void bindData(T t, int position, int count, J2WActivity j2WActivity);

}
