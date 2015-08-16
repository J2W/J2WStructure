package j2w.team.common.utils.contact;

import android.content.Context;
import android.provider.ContactsContract;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午5:31
 * @类描述 邮件
 */
public class ContactEmail implements Cloneable {

	public String	emailAddress;

	public int		emailType;

	public String getEmailTypeValue(Context context) {
		return context.getString(ContactsContract.CommonDataKinds.Email.getTypeLabelResource(emailType));
	}

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