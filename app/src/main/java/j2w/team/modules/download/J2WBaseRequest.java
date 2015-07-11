package j2w.team.modules.download;


import j2w.team.common.log.L;

/**
 * @创建人 sky
 * @创建时间 15/4/3 下午3:05
 * @类描述 请求父类
 */
public class J2WBaseRequest implements Comparable<J2WBaseRequest> {

	/**
	 * 请求优先级
	 */
	public enum Priority {
		/**
		 * 最低
		 */
		LOW,
		/**
		 * 正常
		 */
		NORMAL,
		/**
		 * 高
		 */
		HIGH,
		/**
		 * 及时
		 */
		IMMEDIATE
	}

	/**
	 * 状态
	 */
	private int				mDownloadState;

	/**
	 * 请求ID
	 */
	int						mRequestId;

	/**
	 * 请求TAG
	 */
	private String			mReuestTag;

	/**
	 * 是否取消
	 */
	boolean					mCanceled	= false;

	/**
	 * 请求队列
	 */
	J2WDownloadRequestQueue	mRequestQueue;

	/**
	 * 获取请求Id
	 *
	 * @return 请求ID
	 */
	public final int getRequestId() {
		return mRequestId;
	}

	/**
	 * 设置请求Id
	 *
	 * @param requestId
	 *            请求Id
	 */
	final void setRequestId(int requestId) {
		mRequestId = requestId;
	}

	/**
	 * 获取请求TAG
	 *
	 * @return
	 */
	public final String getRequestTag() {
		return mReuestTag;
	}

	/**
	 * 设置请求TAG
	 *
	 * @param requestTag
	 */
	final void setRequestTag(String requestTag) {
		this.mReuestTag = requestTag;
	}

	/**
	 * 初始化优先级 - 默认正常
	 */
	protected Priority	mPriority	= Priority.NORMAL;

	/**
	 * 获取优先级
	 *
	 * @return 优先级
	 */
	public Priority getPriority() {
		return mPriority;
	}

	/**
	 * 设置队列
	 *
	 * @param downloadQueue
	 *            队列
	 */
	void setDownloadRequestQueue(J2WDownloadRequestQueue downloadQueue) {
		mRequestQueue = downloadQueue;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCanceled() {
		return mCanceled;
	}

	/**
	 * 取消请求
	 */
	public void cancel() {
		mCanceled = true;
	}

	/**
	 * 获取状态
	 *
	 * @return
	 */
	int getDownloadState() {
		return mDownloadState;
	}

	/**
	 * 设置状态
	 *
	 * @param mDownloadState
	 */
	void setDownloadState(int mDownloadState) {
		switch (mDownloadState) {
			case J2WIDownloadMagnager.STATUS_PENDING:
				L.tag("J2WIDownloadMagnager");
				L.i("目前正在等待状态");
                break;
			case J2WIDownloadMagnager.STATUS_STARTED:
				L.tag("J2WIDownloadMagnager");
				L.i("开始状态");
                break;
			case J2WIDownloadMagnager.STATUS_CONNECTING:
				L.tag("J2WIDownloadMagnager");
				L.i("联网状态");
                break;
			case J2WIDownloadMagnager.STATUS_RUNNING:
				L.tag("J2WIDownloadMagnager");
				L.i("运行状态");
                break;
			case J2WIDownloadMagnager.STATUS_SUCCESSFUL:
				L.tag("J2WIDownloadMagnager");
				L.i("完成状态");
                break;
			case J2WIDownloadMagnager.STATUS_FAILED:
				L.tag("J2WIDownloadMagnager");
				L.i("失败状态");
                break;
			case J2WIDownloadMagnager.STATUS_NOT_FOUND:
				L.tag("J2WIDownloadMagnager");
				L.i("失败状态 - 没有找到");
                break;
		}
		this.mDownloadState = mDownloadState;
	}

	/**
	 * 从队列中删除
	 */
	public void finish() {
		mRequestQueue.finish(this);
	}

	/**
	 * 排序
	 *
	 * @param another
	 * @return
	 */
	@Override public int compareTo(J2WBaseRequest another) {
		Priority left = this.getPriority();
		Priority right = another.getPriority();
		return left == right ? this.mRequestId - another.mRequestId : right.ordinal() - left.ordinal();
	}
}
