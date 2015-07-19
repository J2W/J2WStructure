package j2w.team.view.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/17 上午10:51
 * @类描述 RecyclerView 适配器
 */
public abstract class J2WRVAdapterItem<T, V extends J2WViewHolder> extends RecyclerView.Adapter<V> {

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
	public abstract void bindData(V viewholder, T t, int position, int count);

	public abstract V newViewHolder(ViewGroup viewGroup, View view, int type);

	/**
	 * 设置布局
	 *
	 * @return 布局ID
	 */
	public abstract int getItemLayout();

	private J2WRVAdapterItem() {}

	/**
	 * 数据
	 */
	private List				mItems;

	/**
	 * activity
	 */
	protected J2WActivity		j2WActivity;

	/**
	 * 布局加载起
	 */
	protected LayoutInflater	mLayoutInflater;

	public J2WRVAdapterItem(J2WActivity j2WActivity) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		this.j2WActivity = j2WActivity;
		this.mLayoutInflater = j2WActivity.getLayoutInflater();
	}

	@Override public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		V v = null;
		if (getItemLayout() == 0) {
			v = newViewHolder(viewGroup, null, viewType);
		} else {
			View view = mLayoutInflater.inflate(getItemLayout(), viewGroup, false);
			v = newViewHolder(viewGroup, view, viewType);
		}
		return v;
	}

	@Override public void onBindViewHolder(V v, int position) {
		bindData(v, getItem(position), position, getItemCount());
	}

	public List getItems() {
		return mItems;
	}

	public void setItems(List items) {
		mItems = items;
	}

	public void add(int position, Object object) {
		mItems.add(position, object);
	}

	public void add(Object object) {
		mItems.add(object);
	}

	public void addList(int position, List list) {
		mItems.addAll(position, list);
	}

	public void addList(List list) {
		mItems.addAll(list);
	}

	public void delete(int position) {
		mItems.remove(position);
	}

	public void delete(Object object) {
		mItems.remove(object);
	}

	public void clear() {
		mItems.clear();
	}

	public T getItem(int position) {
		return (T) mItems.get(position);
	}

	@Override public int getItemCount() {
		if(mItems == null){
			return 0;
		}
		return mItems.size();
	}

}