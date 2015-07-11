package j2w.team.modules.dialog.provided;

import java.util.Arrays;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import j2w.team.modules.dialog.J2WDialogBuilder;
import j2w.team.modules.dialog.J2WDialogFragment;
import j2w.team.modules.dialog.iface.IDialogCancelListener;
import j2w.team.modules.dialog.iface.IListDialogListener;
import j2w.team.modules.dialog.iface.IMultiChoiceListDialogListener;
import j2w.team.structure.R;

/**
 * Created by sky on 15/3/2. 列表对话框
 */
public class ListDialogFragment extends J2WDialogFragment {

	protected static final String	ARG_ITEMS			= "items";

	protected static final String	ARG_CHECKED_ITEMS	= "checkedItems";

	protected static final String	ARG_MODE			= "choiceMode";

	protected final static String	ARG_TITLE			= "title";

	protected final static String	ARG_POSITIVE_BUTTON	= "positive_button";

	protected final static String	ARG_NEGATIVE_BUTTON	= "negative_button";

	public static SimpleListDialogBuilder createBuilder() {
		return new SimpleListDialogBuilder();
	}

	private static int[] asIntArray(SparseBooleanArray checkedItems) {
		int checked = 0;
		for (int i = 0; i < checkedItems.size(); i++) {
			int key = checkedItems.keyAt(i);
			if (checkedItems.get(key)) {
				++checked;
			}
		}

		int[] array = new int[checked];
		for (int i = 0, j = 0; i < checkedItems.size(); i++) {
			int key = checkedItems.keyAt(i);
			if (checkedItems.get(key)) {
				array[j++] = key;
			}
		}
		Arrays.sort(array);
		return array;
	}

	private ListAdapter prepareAdapter(int itemLayoutId) {
		return new ArrayAdapter<Object>(getActivity(), itemLayoutId, R.id.j2w_text, getItems()) {
			@Override public View getView(int position, View convertView, ViewGroup parent) {
				return super.getView(position, convertView, parent);
			}
		};
	}

	private void buildMultiChoice(Builder builder) {
		builder.setItems(prepareAdapter(R.layout.j2w_dialog_list_item_multichoice), asIntArray(getCheckedItems()), AbsListView.CHOICE_MODE_MULTIPLE, new AdapterView.OnItemClickListener() {

			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SparseBooleanArray checkedPositions = ((ListView) parent).getCheckedItemPositions();
				setCheckedItems(new SparseBooleanArrayParcelable(checkedPositions));
			}
		});
	}

	private void buildSingleChoice(Builder builder) {
		builder.setItems(prepareAdapter(R.layout.j2w_dialog_list_item_singlechoice), asIntArray(getCheckedItems()), AbsListView.CHOICE_MODE_SINGLE, new AdapterView.OnItemClickListener() {

			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SparseBooleanArray checkedPositions = ((ListView) parent).getCheckedItemPositions();
				setCheckedItems(new SparseBooleanArrayParcelable(checkedPositions));
			}
		});
	}

	private void buildNormalChoice(Builder builder) {
		builder.setItems(prepareAdapter(R.layout.j2w_dialog_list_item), -1, new AdapterView.OnItemClickListener() {

			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				for (IListDialogListener listener : getSingleDialogListeners()) {
					listener.onListItemSelected(getItems()[position], position, mRequestCode);
				}
				dismiss();
			}
		});
	}

	@Override protected Builder build(Builder builder) {
		final String title = getTitle();
		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		if (!TextUtils.isEmpty(getNegativeButtonText())) {
			builder.setNegativeButton(getNegativeButtonText(), new View.OnClickListener() {

				@Override public void onClick(View view) {
					for (IDialogCancelListener listener : getCancelListeners()) {
						listener.onCancelled(mRequestCode);
					}
					dismiss();
				}
			});
		}

		if (getMode() != AbsListView.CHOICE_MODE_NONE) {
			View.OnClickListener positiveButtonClickListener = null;
			switch (getMode()) {
				case AbsListView.CHOICE_MODE_MULTIPLE:
					positiveButtonClickListener = new View.OnClickListener() {

						@Override public void onClick(View view) {
							// prepare multiple results
							final int[] checkedPositions = asIntArray(getCheckedItems());
							final String[] items = getItems();
							final String[] checkedValues = new String[checkedPositions.length];
							int i = 0;
							for (int checkedPosition : checkedPositions) {
								if (checkedPosition >= 0 && checkedPosition < items.length) {
									checkedValues[i++] = items[checkedPosition];
								}
							}

							for (IMultiChoiceListDialogListener listener : getMutlipleDialogListeners()) {
								listener.onListItemsSelected(checkedValues, checkedPositions, mRequestCode);
							}
							dismiss();
						}
					};
					break;
				case AbsListView.CHOICE_MODE_SINGLE:
					positiveButtonClickListener = new View.OnClickListener() {

						@Override public void onClick(View view) {
							int selectedPosition = -1;
							final int[] checkedPositions = asIntArray(getCheckedItems());
							final String[] items = getItems();
							for (int i : checkedPositions) {
								if (i >= 0 && i < items.length) {
									selectedPosition = i;
									break;
								}
							}

							if (selectedPosition != -1) {
								for (IListDialogListener listener : getSingleDialogListeners()) {
									listener.onListItemSelected(items[selectedPosition], selectedPosition, mRequestCode);
								}
							} else {
								for (IDialogCancelListener listener : getCancelListeners()) {
									listener.onCancelled(mRequestCode);
								}
							}
							dismiss();
						}
					};
					break;
			}

			String positiveButton = getPositiveButtonText();
			if (TextUtils.isEmpty(getPositiveButtonText())) {
				positiveButton = getString(android.R.string.ok);
			}
			builder.setPositiveButton(positiveButton, positiveButtonClickListener);
		}

		final String[] items = getItems();
		if (items != null && items.length > 0) {
			@ChoiceMode
			final int mode = getMode();
			switch (mode) {
				case AbsListView.CHOICE_MODE_MULTIPLE:
					buildMultiChoice(builder);
					break;
				case AbsListView.CHOICE_MODE_SINGLE:
					buildSingleChoice(builder);
					break;
				case AbsListView.CHOICE_MODE_NONE:
					buildNormalChoice(builder);
					break;
			}
		}

		return builder;
	}

	/**
	 * Get dialog listeners. There might be more than one listener.
	 *
	 * @return Dialog listeners
	 * @since 2.1.0
	 */
	private List<IListDialogListener> getSingleDialogListeners() {
		return getDialogListeners(IListDialogListener.class);
	}

	/**
	 * Get dialog listeners. There might be more than one listener.
	 *
	 * @return Dialog listeners
	 * @since 2.1.0
	 */
	private List<IMultiChoiceListDialogListener> getMutlipleDialogListeners() {
		return getDialogListeners(IMultiChoiceListDialogListener.class);
	}

	private String getTitle() {
		return getArguments().getString(ARG_TITLE);
	}

	@SuppressWarnings("ResourceType") @ChoiceMode private int getMode() {
		return getArguments().getInt(ARG_MODE);
	}

	private String[] getItems() {
		return getArguments().getStringArray(ARG_ITEMS);
	}

	@NonNull private SparseBooleanArrayParcelable getCheckedItems() {
		SparseBooleanArrayParcelable items = getArguments().getParcelable(ARG_CHECKED_ITEMS);
		if (items == null) {
			items = new SparseBooleanArrayParcelable();
		}
		return items;
	}

	private void setCheckedItems(SparseBooleanArrayParcelable checkedItems) {
		getArguments().putParcelable(ARG_CHECKED_ITEMS, checkedItems);
	}

	private String getPositiveButtonText() {
		return getArguments().getString(ARG_POSITIVE_BUTTON);
	}

	private String getNegativeButtonText() {
		return getArguments().getString(ARG_NEGATIVE_BUTTON);
	}

	@IntDef({ AbsListView.CHOICE_MODE_MULTIPLE, AbsListView.CHOICE_MODE_SINGLE, AbsListView.CHOICE_MODE_NONE })
	public @interface ChoiceMode {}

	public static class SimpleListDialogBuilder extends J2WDialogBuilder<SimpleListDialogBuilder> {

		private String			title;

		private String[]		items;

		@ChoiceMode private int	mode;

		private int[]			checkedItems;

		private String			cancelButtonText;

		private String			confirmButtonText;

		public SimpleListDialogBuilder() {
			super(ListDialogFragment.class);
		}

		@Override protected SimpleListDialogBuilder self() {
			return this;
		}

		private Resources getResources() {
			return mContext.getResources();
		}

		public SimpleListDialogBuilder setTitle(String title) {
			this.title = title;
			return this;
		}

		public SimpleListDialogBuilder setTitle(int titleResID) {
			this.title = getResources().getString(titleResID);
			return this;
		}

		public SimpleListDialogBuilder setCheckedItems(int[] positions) {
			this.checkedItems = positions;
			return this;
		}

		public SimpleListDialogBuilder setSelectedItem(int position) {
			this.checkedItems = new int[] { position };
			return this;
		}

		public SimpleListDialogBuilder setChoiceMode(@ChoiceMode int choiceMode) {
			this.mode = choiceMode;
			return this;
		}

		public SimpleListDialogBuilder setItems(String[] items) {
			this.items = items;
			return this;
		}

		public SimpleListDialogBuilder setItems(int itemsArrayResID) {
			this.items = getResources().getStringArray(itemsArrayResID);
			return this;
		}

		public SimpleListDialogBuilder setConfirmButtonText(String text) {
			this.confirmButtonText = text;
			return this;
		}

		public SimpleListDialogBuilder setConfirmButtonText(int confirmBttTextResID) {
			this.confirmButtonText = getResources().getString(confirmBttTextResID);
			return this;
		}

		public SimpleListDialogBuilder setCancelButtonText(String text) {
			this.cancelButtonText = text;
			return this;
		}

		public SimpleListDialogBuilder setCancelButtonText(int cancelBttTextResID) {
			this.cancelButtonText = getResources().getString(cancelBttTextResID);
			return this;
		}

		@Override public ListDialogFragment show() {
			return (ListDialogFragment) super.show();
		}

		@Override protected Bundle prepareArguments() {
			Bundle args = new Bundle();
			args.putString(ARG_TITLE, title);
			args.putString(ARG_POSITIVE_BUTTON, confirmButtonText);
			args.putString(ARG_NEGATIVE_BUTTON, cancelButtonText);

			args.putStringArray(ARG_ITEMS, items);

			SparseBooleanArrayParcelable sparseArray = new SparseBooleanArrayParcelable();
			for (int index = 0; checkedItems != null && index < checkedItems.length; index++) {
				sparseArray.put(checkedItems[index], true);
			}
			args.putParcelable(ARG_CHECKED_ITEMS, sparseArray);
			args.putInt(ARG_MODE, mode);

			return args;
		}
	}

    /**
     * 数据结构
     */
	public static class SparseBooleanArrayParcelable extends SparseBooleanArray implements Parcelable {
        public Parcelable.Creator<SparseBooleanArrayParcelable> CREATOR = new Parcelable.Creator<SparseBooleanArrayParcelable>() {
            @Override
            public SparseBooleanArrayParcelable createFromParcel(Parcel source) {
                SparseBooleanArrayParcelable read = new SparseBooleanArrayParcelable();
                int size = source.readInt();

                int[] keys = new int[size];
                boolean[] values = new boolean[size];

                source.readIntArray(keys);
                source.readBooleanArray(values);

                for (int i = 0; i < size; i++) {
                    read.put(keys[i], values[i]);
                }

                return read;
            }

            @Override
            public SparseBooleanArrayParcelable[] newArray(int size) {
                return new SparseBooleanArrayParcelable[size];
            }
        };

        public SparseBooleanArrayParcelable() {

        }

        public SparseBooleanArrayParcelable(SparseBooleanArray sparseBooleanArray) {
            for (int i = 0; i < sparseBooleanArray.size(); i++) {
                this.put(sparseBooleanArray.keyAt(i), sparseBooleanArray.valueAt(i));
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            int[] keys = new int[size()];
            boolean[] values = new boolean[size()];

            for (int i = 0; i < size(); i++) {
                keys[i] = keyAt(i);
                values[i] = valueAt(i);
            }

            dest.writeInt(size());
            dest.writeIntArray(keys);
            dest.writeBooleanArray(values);
        }
    }
}
