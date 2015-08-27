package j2w.team.view.adapter.recycleview.stickyheader;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * @创建人 sky
 * @创建时间 15/8/28 上午2:39
 * @类描述 固定列表头
 */
public abstract class J2WStickyAdapterItem<T, HeaderViewHolder extends RecyclerView.ViewHolder> implements J2WStickyHeaders<HeaderViewHolder> {

	private List<T>	list;

	public void setItems(List items) {
		list = items;
	}

	protected List<T> getItems(){
		return list;
	}

}