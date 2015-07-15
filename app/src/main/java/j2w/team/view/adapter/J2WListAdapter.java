package j2w.team.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import butterknife.ButterKnife;
import j2w.team.common.utils.J2WCheckUtils;
import j2w.team.view.J2WActivity;

/**
 * @创建人 sky
 * @创建时间 15/7/13 上午10:26
 * @类描述 列表适配器
 */
public class J2WListAdapter extends BaseAdapter {

	private J2WListAdapter() {}

	/**
	 * 数据
	 */
	private List					mItems;

	/**
	 * activity
	 */
	private J2WActivity				j2WActivity;

	/**
	 * 适配器Item
	 */
	private J2WAdapterItem			j2WAdapterItem;

	/**
	 * 多布局接口
	 */
	private J2WListViewMultiLayout	j2WListViewMultiLayout;

	/**
	 * 布局加载起
	 */
	private LayoutInflater			mLayoutInflater;

	public J2WListAdapter(J2WActivity j2WActivity, J2WAdapterItem j2WAdapterItem) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		J2WCheckUtils.checkNotNull(j2WAdapterItem, "ListView Item类不存在");
		this.j2WActivity = j2WActivity;
		this.j2WAdapterItem = j2WAdapterItem;
		this.mLayoutInflater = j2WActivity.getLayoutInflater();
	}

	public J2WListAdapter(J2WActivity j2WActivity, J2WListViewMultiLayout j2WListViewMultiLayout) {
		J2WCheckUtils.checkNotNull(j2WActivity, "View层不存在");
		J2WCheckUtils.checkNotNull(j2WListViewMultiLayout, "ListView 多布局接口不存在");
		this.j2WActivity = j2WActivity;
		this.j2WListViewMultiLayout = j2WListViewMultiLayout;
		this.mLayoutInflater = j2WActivity.getLayoutInflater();
	}

	public void setItems(List items) {
		if (!J2WCheckUtils.equal(items, mItems)) {
			mItems = items;
			notifyDataSetChanged();
		}
	}

	public void add(int position, Object object) {
		if (object == null || mItems == null || position < 0 || position > mItems.size()) {
			return;
		}
		mItems.add(position, object);
		notifyDataSetChanged();
	}

	public void add(Object object) {
		if (object == null || mItems == null) {
			return;
		}
		mItems.add(object);
		notifyDataSetChanged();
	}

	public void addList(int position, List list) {
		if (list == null || list.size() < 1 || mItems == null || position < 0 || position > mItems.size()) {
			return;
		}
		mItems.addAll(position, list);
		notifyDataSetChanged();
	}

	public void addList(List list) {
		if (list == null || list.size() < 1 || mItems == null) {
			return;
		}
		mItems.addAll(list);
		notifyDataSetChanged();
	}

	public void delete(int position) {
		if (mItems == null || position < 1 || mItems.size() < position) {
			return;
		}
		mItems.remove(position);
		notifyDataSetChanged();
	}

	public void delete(Object object) {
		if (mItems == null || mItems.size() < 1) {
			return;
		}
		mItems.remove(object);
		notifyDataSetChanged();
	}

	public void clear() {
		if (mItems == null) {
			return;
		}
		mItems.clear();
		notifyDataSetChanged();
	}

	@Override public int getCount() {
		return mItems == null ? 0 : mItems.size();
	}

	@Override public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override public long getItemId(int position) {
		return position;
	}

	@Override public int getViewTypeCount() {
		return j2WListViewMultiLayout == null ? 1 : j2WListViewMultiLayout.getJ2WViewTypeCount();
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		J2WAdapterItem item = null;
		if (convertView == null) {
			if (j2WListViewMultiLayout == null) {
				item = createItem(); // 单类型
			} else {
				item = createMultiItem(position);// 多类型
			}
			convertView = mLayoutInflater.inflate(item.getItemLayout(), parent, false);
			// 初始化布局
			item.init(convertView);
			// 初始化
			ButterKnife.bind(item, convertView);
			// 设置Tag标记
			convertView.setTag(item);
		}
		// 获取item
		item = item == null ? (J2WAdapterItem) convertView.getTag() : item;
		// 绑定数据
		item.bindData(getItem(position), position, getCount());
		return convertView;
	}

	/**
	 * 单类型
	 * 
	 * @return
	 */
	private J2WAdapterItem createItem() {
		J2WAdapterItem itemClone = (J2WAdapterItem) this.j2WAdapterItem.clone();
		itemClone.setJ2WActivity(j2WActivity);
		return itemClone;
	}

	/**
	 * 多类型
	 * 
	 * @param position
	 * @return
	 */
	private J2WAdapterItem createMultiItem(int position) {
		int type = j2WListViewMultiLayout.getJ2WViewType(position);
		return j2WListViewMultiLayout.getJ2WAdapterItem(type);
	}
}