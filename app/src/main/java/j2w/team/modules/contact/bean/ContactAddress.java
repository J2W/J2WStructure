package j2w.team.modules.contact.bean;

import android.content.Context;
import android.provider.ContactsContract;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午8:49
 * @类描述 地址
 */
public class ContactAddress implements Cloneable {

    public String	address;

    public int		type;

    public String getPhoneTypeValue(Context context) {

        return context.getString(ContactsContract.CommonDataKinds.SipAddress.getTypeLabelResource(type));
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