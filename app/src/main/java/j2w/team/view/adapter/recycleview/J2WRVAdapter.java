package j2w.team.view.adapter.recycleview;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
public abstract class J2WRVAdapter<T, V extends J2WHolder> extends RecyclerView.Adapter<V> {

	public abstract V newViewHolder(ViewGroup viewGroup, int type);

	private J2WRVAdapter() {}

	/**
	 * 数据
	 */
	private List				mItems;

	/**
	 * 布局加载起
	 */
	protected LayoutInflater	mLayoutInflater;

	private J2WView				j2WView;

	public J2WRVAdapter(J2WActivity j2WActivity) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		this.j2WView = j2WActivity.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	public J2WRVAdapter(J2WFragment j2WFragment) {
		J2WCheckUtils.checkNotNull(j2WFragment, "View层不存在");
		this.j2WView = j2WFragment.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	public J2WRVAdapter(J2WDialogFragment j2WDialogFragment) {
		J2WCheckUtils.checkNotNull(j2WDialogFragment, "View层不存在");
		this.j2WView = j2WDialogFragment.j2wView();
		this.mLayoutInflater = this.j2WView.activity().getLayoutInflater();
	}

	@Override public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		V v = newViewHolder(viewGroup, viewType);
		return v;
	}

	@Override public void onBindViewHolder(V v, int position) {
		v.bindData(getItem(position), getItemCount());
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

	HeaderRecyclerViewAdapterV2	headerRecyclerViewAdapterV1;

	public void setHeaderRecyclerViewAdapterV2(HeaderRecyclerViewAdapterV2 headerRecyclerViewAdapterV2) {
		this.headerRecyclerViewAdapterV1 = headerRecyclerViewAdapterV2;
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

	public <V extends J2WFragment> V fragment() {
		return j2WView.fragment();
	}

	public <A extends J2WActivity> A activity() {
		return j2WView.activity();
	}

	public <D extends J2WDialogFragment> D dialogFragment() {
		return j2WView.dialogFragment();
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

	public J2WView getUI() {
		return j2WView;
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

	public void isShowHeader(boolean isShowFooter) {
		headerRecyclerViewAdapterV1.isShowHeader(isShowFooter);
	}

	public void isShowFooter(boolean isShowFooter) {
		headerRecyclerViewAdapterV1.isShowFooter(isShowFooter);
	}
}