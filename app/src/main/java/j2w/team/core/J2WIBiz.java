package j2w.team.core;


import j2w.team.view.J2WView;

/**
 * Created by sky on 15/2/7. 业务
 */
public interface J2WIBiz {

	void initUI(Object j2WView);

	/**
	 * 清空
	 */
	void detach();

}
