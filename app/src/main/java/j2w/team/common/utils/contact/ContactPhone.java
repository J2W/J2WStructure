package j2w.team.common.utils.contact;

import android.content.Context;
import android.provider.ContactsContract;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午5:31
 * @类描述 电话
 */
public class ContactPhone implements Cloneable {

	public String	phone;

	public int		phoneType;

	public String getPhoneTypeValue(Context context) {

		return context.getString(ContactsContract.CommonDataKinds.Phone.getTypeLabelResource(phoneType));
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