package j2w.team.view.adapter.recycleview;

import android.view.ViewGroup;

/**
 * @创建人 sky
 * @创建时间 15/7/18 上午9:41
 * @类描述 一句话说明这个类是干什么的
 */
public interface HeaderRecyclerView<T extends J2WViewHolder> {

    T onCreateHeaderViewHolder(ViewGroup parent, int viewType);

    void onBindHeaderView(T holder, int position);
}