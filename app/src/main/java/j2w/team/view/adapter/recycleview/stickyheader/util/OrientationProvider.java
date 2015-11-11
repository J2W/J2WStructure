package j2w.team.view.adapter.recycleview.stickyheader.util;

import android.support.v7.widget.RecyclerView;

/**
 * Interface for getting the orientation of a RecyclerView from its
 * LayoutManager
 */
public interface OrientationProvider {

	int getOrientation(RecyclerView recyclerView);

	boolean isReverseLayout(RecyclerView recyclerView);
}
