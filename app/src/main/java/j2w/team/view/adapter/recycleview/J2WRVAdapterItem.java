package j2w.team.view.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import j2w.team.core.J2WIBiz;
import j2w.team.display.J2WIDisplay;
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
@Deprecated
public abstract class J2WRVAdapterItem<T, V extends J2WViewHolder> extends RecyclerView.Adapter<V> {

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
		V v = newViewHolder(viewGroup, viewType);
		return v;
	}

	@Override public void onBindViewHolder(V v, int position) {
		bindData(v, getItem(position), position, getItemCount());
	}

	public J2WView getUI(){
		return j2WView;
	}

	public List<T> getItems() {
		return mItems;
	}

	public void setItems(List items) {
		mItems = items;

		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public void add(int position, Object object) {
		headerRecyclerViewAdapterV1.notifyItemInserted(position);
		mItems.add(position, object);
		headerRecyclerViewAdapterV1.notifyItemRangeChanged(position, getItemCount());
	}

	public void add(Object object) {
		headerRecyclerViewAdapterV1.notifyItemInserted(mItems.size());
		mItems.add(object);
		headerRecyclerViewAdapterV1.notifyItemRangeChanged(getItemCount() - 1, getItemCount());

	}

	public void addList(int position, List list) {
		headerRecyclerViewAdapterV1.notifyItemInserted(position);
		mItems.addAll(position, list);
		headerRecyclerViewAdapterV1.notifyItemRangeChanged(position, list.size());

	}

	public void addList(List list) {
		headerRecyclerViewAdapterV1.notifyItemInserted(getItemCount());
		mItems.addAll(list);
		headerRecyclerViewAdapterV1.notifyItemRangeChanged(getItemCount() - 1, getItemCount());
	}

	public void delete(int position) {
		headerRecyclerViewAdapterV1.notifyItemRemoved(position);
		mItems.remove(position);
		headerRecyclerViewAdapterV1.notifyItemRangeChanged(position, getItemCount());
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

	public void updateData() {
		headerRecyclerViewAdapterV1.notifyDataSetChanged();
	}

	public HeaderRecyclerViewAdapterV1 adapter() {
		return headerRecyclerViewAdapterV1;
	}

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

	public J2WFragment fragment() {
		return j2WView.fragment();
	}

	public J2WActivity activity() {
		return j2WView.activity();
	}

	public J2WDialogFragment dialogFragment() {
		return j2WView.dialogFragment();
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
		if (headerRecyclerViewAdapterV1 != null) {
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