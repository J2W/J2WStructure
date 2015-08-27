package j2w.team.view.adapter.recycleview.stickyheader;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
/**
 * @创建人 sky
 * @创建时间 15/8/28 上午2:00
 * @类描述
 */
public interface J2WStickyHeaders<HeaderViewHolder extends RecyclerView.ViewHolder> {


    HeaderViewHolder onCreateViewHolder(ViewGroup parent);


    void onBindViewHolder(HeaderViewHolder headerViewHolder, int position);


    long getHeaderId(int position);
}
