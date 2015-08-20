package j2w.team.view.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import j2w.team.biz.J2WIBiz;
import j2w.team.biz.J2WIDisplay;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;
import j2w.team.view.J2WDialogFragment;
import j2w.team.view.J2WFragment;
import j2w.team.view.J2WView;

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

	public abstract V newViewHolder(ViewGroup viewGroup, int type);

	private J2WRVAdapterItem() {}

	/**
	 * 数据
	 */
	private List				mItems;

	/**
	 * 布局加载起
	 */
	protected LayoutInflater	mLayoutInflater;

	private J2WView				j2WView;

	public J2WRVAdapterItem(J2WActivity j2WActivity) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		this.j2WView = j2WActivity.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	public J2WRVAdapterItem(J2WFragment j2WFragment) {
		J2WCheckUtils.checkNotNull(j2WFragment, "View层不存在");
		this.j2WView = j2WFragment.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	public J2WRVAdapterItem(J2WDialogFragment j2WDialogFragment) {
		J2WCheckUtils.checkNotNull(j2WDialogFragment, "View层不存在");
		this.j2WView = j2WDialogFragment.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	@Override public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		V v = newViewHolder(viewGroup, viewType);
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
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public void add(int position, Object object) {
		mItems.add(position, object);
		headerRecyclerViewAdapterV1.notifyItemInserted(position);
	}

	public void add(Object object) {
		mItems.add(object);
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public void addList(int position, List list) {
		mItems.addAll(position, list);
		headerRecyclerViewAdapterV1.notifyItemInserted(position);
	}

	public void addList(List list) {
		mItems.addAll(list);
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public void delete(int position) {
		mItems.remove(position);
		headerRecyclerViewAdapterV1.notifyItemRemoved(position);
	}

	public void delete(Object object) {
		mItems.remove(object);
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	HeaderRecyclerViewAdapterV1	headerRecyclerViewAdapterV1;

	public void setHeaderRecyclerViewAdapterV1(HeaderRecyclerViewAdapterV1 headerRecyclerViewAdapterV1) {
		this.headerRecyclerViewAdapterV1 = headerRecyclerViewAdapterV1;
	}

	public void clear() {
		mItems.clear();
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public T getItem(int position) {
		return (T) mItems.get(position);
	}

	/**
	 * 获取业务
	 *
	 * @param biz
	 *            泛型
	 * @param <B>
	 * @return
	 */
	protected <B extends J2WIBiz> B biz(Class<B> biz) {
		return j2WView.biz(biz);
	}

	/**
	 * 获取显示调度
	 *
	 * @return
	 */
	protected <E extends J2WIDisplay> E display() {
		return j2WView.display();
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

	public boolean isHeaderAndFooter(int position) {
		return false;
	}

	public void detach() {
		if(headerRecyclerViewAdapterV1 != null){
			headerRecyclerViewAdapterV1 = null;
		}
	}

	@Override public int getItemCount() {
		if (mItems == null) {
			return 0;
		}
		return mItems.size();
	}

}