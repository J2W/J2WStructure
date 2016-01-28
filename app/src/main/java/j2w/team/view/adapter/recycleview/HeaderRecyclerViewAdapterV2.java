package j2w.team.view.adapter.recycleview;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import j2w.team.common.utils.J2WCheckUtils;

/**
 * @创建人 sky
 * @创建时间 15/7/17 下午9:40
 * @类描述 适配器扩展 增加头部和尾部布局
 */
public class HeaderRecyclerViewAdapterV2<V extends J2WHolder> extends RecyclerView.Adapter<V> {

	private static final int		TYPE_HEADER			= Integer.MIN_VALUE;

	private static final int		TYPE_FOOTER			= Integer.MIN_VALUE + 1;

	private static final int		TYPE_ADAPTEE_OFFSET	= 2;

	private final J2WRVAdapter	mAdaptee;

	public HeaderRecyclerViewAdapterV2(J2WRVAdapter adaptee) {
		mAdaptee = adaptee;
	}

	public J2WRVAdapter getAdapter() {
		return mAdaptee;
	}

	@Override public V onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == TYPE_HEADER && mAdaptee instanceof HeaderRecyclerView) {
			return (V) ((HeaderView) mAdaptee).onCreateHeaderViewHolder(parent, viewType);
		} else if (viewType == TYPE_FOOTER && mAdaptee instanceof FooterRecyclerView) {
			return (V) ((FooterView) mAdaptee).onCreateFooterViewHolder(parent, viewType);
		}
		return (V) mAdaptee.onCreateViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
	}

	@Override public void onBindViewHolder(V holder, int position) {
		if (position == 0 && holder.getItemViewType() == TYPE_HEADER && useHeader()) {
			((HeaderView) mAdaptee).onBindHeaderView(holder, position);
		} else if (position == mAdaptee.getItemCount() && holder.getItemViewType() == TYPE_FOOTER && useFooter()) {
			((FooterView) mAdaptee).onBindFooterView(holder, position);
		} else {
			mAdaptee.onBindViewHolder(holder, position - (useHeader() ? 1 : 0));
		}
	}

	@Override public int getItemCount() {
		int itemCount = mAdaptee.getItemCount();
		if (useHeader()) {
			itemCount += 1;
		}
		if (useFooter()) {
			itemCount += 1;
		}
		return itemCount;
	}

	private boolean useHeader() {
		if (mAdaptee instanceof HeaderRecyclerView) {
			return true;
		}
		return false;
	}

	private boolean useFooter() {
		if (mAdaptee instanceof FooterRecyclerView) {
			return true;
		}
		return false;
	}

	public void setItems(List items) {
		if (!J2WCheckUtils.equal(items, mAdaptee.getItems())) {
			mAdaptee.setItems(items);
		}
	}

	public void add(int position, Object object) {
		if (object == null || mAdaptee.getItems() == null || position < 0 || position > mAdaptee.getItems().size()) {
			return;
		}
		mAdaptee.add(position, object);
	}

	public void add(Object object) {
		if (object == null || mAdaptee.getItems() == null) {
			return;
		}
		mAdaptee.add(object);
	}

	public void addList(int position, List list) {
		if (list == null || list.size() < 1 || mAdaptee.getItems() == null || position < 0 || position > mAdaptee.getItems().size()) {
			return;
		}
		mAdaptee.addList(position, list);
	}

	public void addList(List list) {
		if (list == null || list.size() < 1 || mAdaptee.getItems() == null) {
			return;
		}
		mAdaptee.addList(list);
	}

	public void delete(int position) {
		if (mAdaptee.getItems() == null || position < 0 || mAdaptee.getItems().size() < position) {
			return;
		}
		mAdaptee.delete(position);
	}
	public void clear() {
		if (mAdaptee.getItems() == null) {
			return;
		}
		mAdaptee.clear();
	}
	public <T> T getItem(int position) {
		return (T) mAdaptee.getItem(position);
	}

	@Override public int getItemViewType(int position) {
		if (position == 0 && useHeader()) {
			return TYPE_HEADER;
		}
		if (position == mAdaptee.getItemCount() && useFooter()) {
			return TYPE_FOOTER;
		}
		if (mAdaptee.getItemCount() >= Integer.MAX_VALUE - TYPE_ADAPTEE_OFFSET) {
			new IllegalStateException("HeaderRecyclerViewAdapter offsets your BasicItemType by " + TYPE_ADAPTEE_OFFSET + ".");
		}
		return mAdaptee.getItemViewType(position) + TYPE_ADAPTEE_OFFSET;
	}



}