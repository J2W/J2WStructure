package j2w.team.modules.contact;

import android.graphics.Bitmap;

import java.util.List;

import j2w.team.biz.Impl;
import j2w.team.modules.contact.bean.ContactDetailModel;
import j2w.team.modules.contact.bean.ContactModel;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午10:16
 * @类描述 通讯录接口
 */
@Impl(ContactManage.class)
public interface J2WIContact {

	/**
	 * 获取联系人头像
	 *
	 * @param id
	 * @return
	 */
	Bitmap getContactPhotoByContactId(String id);

	/**
	 * 根据名称搜索
	 *
	 * @param partialName
	 * @param isPhone
	 *            开关
	 * @param isEmail
	 *            开关
	 * @return
	 */
	ContactModel getPhoneContactByName(String partialName, boolean isPhone, boolean isEmail);

	/**
	 * 获取所有联系人
	 *
	 * @param userName
	 *            用户名
	 * @param isPhone
	 *            开关
	 * @param isEmail
	 *            开关
	 * @return
	 */
	List<ContactModel> getAllPhoneContacts(String userName, boolean isPhone, boolean isEmail);

	List<ContactModel> getAllPhoneContacts(boolean isPhone, boolean isEmail);

	List<ContactModel> getAllPhoneContacts();

	/**
	 * 获取所有联系人 详情
	 * 
	 * @param userName
	 * @param isPhone
	 * @param isEmail
	 * @return
	 */
	List<ContactDetailModel> getAllPhoneDetailContacts(String userName, boolean isPhone, boolean isEmail);

	List<ContactDetailModel> getAllPhoneDetailContacts(boolean isPhone, boolean isEmail);


	int getVersion();
}