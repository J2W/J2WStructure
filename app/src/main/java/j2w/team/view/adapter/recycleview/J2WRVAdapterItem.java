package j2w.team.view.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

/**
 * @创建人 sky
 * @创建时间 15/7/17 上午10:51
 * @类描述 RecyclerView 适配器
 */
@Deprecated
public abstract class J2WRVAdapterItem<T, V extends J2WViewHolder> extends RecyclerView.Adapter<V> {

	public abstract V newViewHolder(ViewGroup viewGroup, int type);

	private J2WRVAdapterItem() {}

	/**
	 * 数据
	 */
	private List	mItems;

	/**
	 * 布局加载起
	 */
	private J2WView	j2WView;

	public J2WRVAdapterItem(J2WActivity j2WActivity) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		this.j2WView = j2WActivity.j2wView();
	}

	public J2WRVAdapterItem(J2WFragment j2WFragment) {
		J2WCheckUtils.checkNotNull(j2WFragment, "View层不存在");
		this.j2WView = j2WFragment.j2wView();
	}

	public J2WRVAdapterItem(J2WDialogFragment j2WDialogFragment) {
		J2WCheckUtils.checkNotNull(j2WDialogFragment, "View层不存在");
		this.j2WView = j2WDialogFragment.j2wView();
	}

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

	@Override public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		V holder = newViewHolder(viewGroup, viewType);
		return holder;
	}

	@Override public void onBindViewHolder(V v, int position) {
		bindData(v, getItem(position), position, getItemCount());
	}

	public J2WView getUI() {
		return j2WView;
	}

	public List<T> getItems() {
		return mItems;
	}

	public void setItems(List items) {
		mItems = items;
		notifyDataSetChanged();
	}

	public void add(int position, Object object) {
		if (object == null || getItems() == null || position < 0 || position > getItems().size()) {
			return;
		}
		mItems.add(position, object);
		notifyItemInserted(position);
	}

	public void add(Object object) {
		if (object == null || getItems() == null) {
			return;
		}
		mItems.add(object);
		notifyItemInserted(mItems.size());

	}

	public void addList(int position, List list) {
		if (list == null || list.size() < 1 || getItems() == null || position < 0 || position > getItems().size()) {
			return;
		}
		mItems.addAll(position, list);
		notifyItemRangeInserted(position, list.size());

	}

	public void addList(List list) {
		if (list == null || list.size() < 1 || getItems() == null) {
			return;
		}
		int postion = getItemCount();
		mItems.addAll(list);
		notifyItemRangeInserted(postion, list.size());
	}

	public void delete(int position) {
		if (getItems() == null || position < 0 || getItems().size() < position) {
			return;
		}
		mItems.remove(position);
		notifyItemRemoved(position);
	}

	public void delete(List list) {
		if (list == null || list.size() < 1 || getItems() == null) {
			return;
		}
		int position = getItemCount();
		mItems.removeAll(list);
		notifyItemRangeRemoved(position, list.size());
	}

	public void delete(int position, List list) {
		if (list == null || list.size() < 1 || getItems() == null) {
			return;
		}
		mItems.removeAll(list);
		notifyItemRangeRemoved(position, list.size());
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}

	public T getItem(int position) {
		return (T) mItems.get(position);
	}

	public void updateData() {
		notifyDataSetChanged();
	}

	public J2WRVAdapterItem adapter() {
		return this;
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

	public <V extends J2WFragment> V fragment() {
		return j2WView.fragment();
	}

	public <A extends J2WActivity> A activity() {
		return j2WView.activity();
	}

	public <D extends J2WDialogFragment> D dialogFragment() {
		return j2WView.dialogFragment();
	}

	public <B extends J2WIBiz> B biz(Class<B> service) {
		return j2WView.biz(service);
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

	@Override public int getItemCount() {
		if (mItems == null) {
			return 0;
		}
		return mItems.size();
	}

	public boolean isHeaderAndFooter(int position) {
		return false;
	}

	public void clearCache() {
		if (mItems != null) {
			mItems.clear();
			mItems = null;
		}
	}
}