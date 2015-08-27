package j2w.team.view.adapter.recycleview.stickyheader;

import android.support.v7.widget.RecyclerView;

/**
 * @创建人 sky
 * @创建时间 15/8/28 上午2:00
 * @类描述 固定头列表
 */
public class J2WStickyHeadersBuilder {

	private RecyclerView			recyclerView;

	private RecyclerView.Adapter	adapter;

	private J2WStickyHeaders headersAdapter;

	private boolean					overlay;

	private boolean					isSticky;

	private J2WDrawOrder				drawOrder;

	public J2WStickyHeadersBuilder() {
		this.isSticky = true;
		this.drawOrder = J2WDrawOrder.OverItems;
	}

	public J2WStickyHeadersBuilder setRecyclerView(RecyclerView recyclerView) {
		this.recyclerView = recyclerView;
		return this;
	}

	public J2WStickyHeadersBuilder setStickyHeadersAdapter(J2WStickyHeaders adapter) {
		return setStickyHeadersAdapter(adapter, false);
	}

	public J2WStickyHeadersBuilder setStickyHeadersAdapter(J2WStickyHeaders adapter, boolean overlay) {
		this.headersAdapter = adapter;
		this.overlay = overlay;
		return this;
	}

	public J2WStickyHeadersBuilder setAdapter(RecyclerView.Adapter adapter) {
		if (!adapter.hasStableIds()) {
			throw new IllegalArgumentException("Adapter must have stable ids");
		}
		this.adapter = adapter;
		return this;
	}

	public J2WStickyHeadersBuilder setSticky(boolean isSticky) {
		this.isSticky = isSticky;

		return this;
	}

	public J2WStickyHeadersBuilder setDrawOrder(J2WDrawOrder drawOrder) {
		this.drawOrder = drawOrder;

		return this;
	}

	public J2WStickyHeadersItemDecoration build() {

		J2WHeaderStore store = new J2WHeaderStore(recyclerView, headersAdapter, isSticky);

		J2WStickyHeadersItemDecoration decoration = new J2WStickyHeadersItemDecoration(store, overlay, drawOrder);

		decoration.registerAdapterDataObserver(adapter);

		return decoration;
	}
}
