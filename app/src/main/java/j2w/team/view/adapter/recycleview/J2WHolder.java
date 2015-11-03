package j2w.team.view.adapter.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * @创建人 sky
 * @创建时间 15/7/17 上午10:58
 * @类描述 适配器优化holder
 */
public abstract class J2WHolder<T> extends RecyclerView.ViewHolder {

	public J2WHolder(View itemView) {
		super(itemView);
		ButterKnife.bind(this, itemView);
	}

	public abstract void bindData(T t, int count);

}