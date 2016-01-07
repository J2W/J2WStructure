package j2w.team.view.common;

/**
 * @创建人 sky
 * @创建时间 15/5/14 下午5:56
 * @类描述 公共视图接口
 */
public interface J2WIViewCommon {

	/**
	 * 进度布局
	 *
	 * @return
	 */
	int layoutLoading();

	/**
	 * 空布局
	 *
	 * @return
	 */
	int layoutEmpty();

	/**
	 * 网络业务错误
	 *
	 * @return
	 */
	int layoutBizError();

	/**
	 * 网络错误
	 * 
	 * @return
	 */
	int layoutHttpError();

}
