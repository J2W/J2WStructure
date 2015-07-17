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

	public abstract V newViewHolder(View view);

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
	private List			mItems;

	/**
	 * activity
	 */
	private J2WActivity		j2WActivity;

	/**
	 * 布局加载起
	 */
	private LayoutInflater	mLayoutInflater;

	public J2WRVAdapterItem(J2WActivity j2WActivity) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		this.j2WActivity = j2WActivity;
		this.mLayoutInflater = j2WActivity.getLayoutInflater();
	}

	@Override public V onCreateViewHolder(ViewGroup viewGroup, int position) {
		View view = mLayoutInflater.inflate(getItemLayout(), viewGroup, false);
		V v = newViewHolder(view);
		return v;
	}

	@Override public void onBindViewHolder(V v, int position) {
		bindData(v, getItem(position), position, getItemCount());
	}

	public void setItems(List items) {
		if (!J2WCheckUtils.equal(items, mItems)) {
			mItems = items;
			notifyDataSetChanged();
		}
	}

	public void add(int position, Object object) {
		if (object == null || mItems == null || position < 0 || position > mItems.size()) {
			return;
		}
		mItems.add(position, object);
        notifyItemInserted(position);
	}

	public void add(Object object) {
		if (object == null || mItems == null) {
			return;
		}
		mItems.add(object);

		notifyDataSetChanged();
	}

	public void addList(int position, List list) {
		if (list == null || list.size() < 1 || mItems == null || position < 0 || position > mItems.size()) {
			return;
		}
		mItems.addAll(position, list);
        notifyItemInserted(position);

    }

	public void addList(List list) {
		if (list == null || list.size() < 1 || mItems == null) {
			return;
		}
		mItems.addAll(list);
		notifyDataSetChanged();
	}

	public void delete(int position) {
		if (mItems == null || position < 0 || mItems.size() < position) {
			return;
		}
		mItems.remove(position);
        notifyItemRemoved(position);
	}

	public void delete(Object object) {
		if (mItems == null || mItems.size() < 1) {
			return;
		}
		mItems.remove(object);
		notifyDataSetChanged();
	}

	public void clear() {
		if (mItems == null) {
			return;
		}
		mItems.clear();
		notifyDataSetChanged();
	}

	public T getItem(int position) {
		return (T) mItems.get(position);
	}

	@Override public int getItemCount() {
		return mItems.size();
	}


}