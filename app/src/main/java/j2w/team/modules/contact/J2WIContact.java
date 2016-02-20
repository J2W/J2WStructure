package j2w.team.modules.contact;

import android.graphics.Bitmap;

import java.util.List;

import j2w.team.core.Impl;
import j2w.team.modules.contact.bean.ContactDetailModel;
import j2w.team.modules.contact.bean.ContactModel;
import j2w.team.modules.contact.bean.ContactUser;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午10:16
 * @类描述 通讯录接口
 */
@Impl(ContactManage.class)
public interface J2WIContact extends J2WIWriteContact {

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
	 * 获取联系人 - 根据姓名
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

	/**
	 * 获取联系人 - 根据ID
	 * 
	 * @param id
	 * @param isPhone
	 * @param isEmail
	 * @return
	 */
	List<ContactModel> getAllPhoneContactsByContactId(int id, boolean isPhone, boolean isEmail);

	/**
	 * 获取联系人 - 所有人
	 * 
	 * @param isPhone
	 * @param isEmail
	 * @return
	 */
	List<ContactModel> getAllPhoneContacts(boolean isPhone, boolean isEmail);

	/**
	 * 获取联系人 - 取单个手机号
	 * 
	 * @return
	 */
	List<ContactModel> getAllPhoneContacts();

	/**
	 * 获取联系人-详情 - 根据名称
	 * 
	 * @param userName
	 *            名称
	 * @return
	 */
	List<ContactDetailModel> getAllPhoneDetailContacts(String userName);

	/**
	 * 获取联系人-详情
	 * 
	 * @return
	 */
	List<ContactDetailModel> getAllPhoneDetailContacts();

	/**
	 * 获取联系人
	 * 
	 * @return
	 */
	List<String> getAllPhoneDetailIDs();

	/**
	 * 获取所有用户
	 * 
	 * @return
	 */
	List<ContactUser> getAllUser();

	/**
	 * 搜索用户
	 * 
	 * @param name
	 * @return
	 */
	List<ContactUser> getAllUser(String name);
	List<ContactUser> getAllUser(String name,List<String> contactIds);



	/**
	 * 获取联系人ID - 根据版本
	 * 
	 * @param version
	 * @return
	 */
	List<String> getAllPhoneDetailIDs(int version);

	List<String> getAllPhoneDetailIDs(long time);

	/**
	 * 根据手机号 过滤获取用户ID
	 * 
	 * @param number
	 * @return
	 */
	List<String> getFilterPhoneNumber(String number);

	/**
	 * 获取联系人 - 找出大于 ID 的数据
	 * 
	 * @param version
	 *            版本
	 * @return
	 */
	List<ContactDetailModel> getAllPhoneDetailContacts(int version);

	/**
	 * 获取版本
	 *
	 * @return
	 */
	int getVersion();

	/**
	 * 获取最后更新时间
	 * 
	 * @return
	 */
	long getLastTime();

	/**
	 * 根据ID 获取详细数据
	 * 
	 * @param id
	 * @return
	 */
	ContactDetailModel getContactDataByContactId(String id);
}