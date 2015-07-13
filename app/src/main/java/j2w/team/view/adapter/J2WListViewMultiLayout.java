package j2w.team.view.adapter;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午9:39
 * @类描述 ListView 多布局接口
 */
public interface J2WListViewMultiLayout {

	/**
	 * 类型
	 * 
	 * @param position
	 * @return
	 */
	int getJ2WViewType(int position);

	/**
	 * 类型数量
	 * 
	 * @return
	 */
	int getJ2WViewTypeCount();

	/**
	 * 根据类型获取适配器Item
	 * 
	 * @param type
	 * @return
	 */
	J2WAdapterItem getJ2WAdapterItem(int type);
}