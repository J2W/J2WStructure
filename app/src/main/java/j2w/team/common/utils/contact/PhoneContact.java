package j2w.team.common.utils.contact;

import android.graphics.Bitmap;

import java.util.Map;

/**
 * @创建人 sky
 * @创建时间 15/5/4 下午5:33
 * @类描述 联系人实体类
 */
public class PhoneContact implements Cloneable {

	/**
	 * 用户名称
	 */
	public String				displayName;

	/**
	 * 电子邮件, Key = type of email, Value = Email address
	 */
	public Map<String, String>	emailAddresses;

	/**
	 * 所有电话号码, Key = type of phone number, Value = phone numbers
	 */
	public Map<String, String>	phoneNumbers;

	/**
	 * 联系人头像
	 */
	public Bitmap				photo;

	/**
	 * 数据库-联系人key
	 */
	public String				lookupKey;

	/**
	 * 数据库-联系人ID
	 */
	public String				contactId;

	/**
	 * 克隆
	 * 
	 * @return
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
