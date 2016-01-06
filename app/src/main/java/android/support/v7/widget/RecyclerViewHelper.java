package android.support.v7.widget;

/**
 * @创建人 sky
 * @创建时间 15/7/18 上午11:49
 * @类描述
 */
public class RecyclerViewHelper {

    public static int convertPreLayoutPositionToPostLayout(RecyclerView recyclerView, int position) {
        return recyclerView.mRecycler.convertPreLayoutPositionToPostLayout(position);
    }
}
