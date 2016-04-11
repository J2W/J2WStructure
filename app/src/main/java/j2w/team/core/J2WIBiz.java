package j2w.team.core;


import j2w.team.modules.structure.J2WStructureModel;

/**
 * Created by sky on 15/2/7. 业务
 */
public interface J2WIBiz {

	void initUI(J2WStructureModel j2WView);

	/**
	 * 清空
	 */
	void detach();

}
