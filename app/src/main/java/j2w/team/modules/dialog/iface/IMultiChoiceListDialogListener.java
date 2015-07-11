package j2w.team.modules.dialog.iface;

/**
 * Created by sky on 15/3/2.多选
 */
public interface IMultiChoiceListDialogListener {

	void onListItemsSelected(String[] values, int[] selectedPositions, int requestCode);
}
