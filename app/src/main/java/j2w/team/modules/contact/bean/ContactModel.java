package j2w.team.modules.contact.bean;

import android.graphics.Bitmap;

import java.util.List;

/**
 * @创建人 sky
 * @创建时间 15/5/4 下午5:33
 * @类描述 联系人实体类
 */
public class ContactModel implements Cloneable {

	/**
	 * 数据库-联系人ID
	 */
	public String				contactId;

	/**
	 * 联系人头像
	 */
	public Bitmap				photo;

	/**
	 * 用户名称
	 */
	public String				displayName;

	/**
	 * 最后更新时间
	 */
	public long					lastUpdate;

	/**
	 * 电子邮件, Key = type of email, Value = Email address
	 */
	public List<ContactEmail>	emailAddresses;

	/**
	 * 所有电话号码, Key = type of phone number, Value = phone numbers
	 */
	public List<ContactPhone>	phoneNumbers;

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
