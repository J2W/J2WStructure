package j2w.team.modules.contact.bean;

import android.content.Context;
import android.provider.ContactsContract;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午8:45
 * @类描述 IM
 */
public class ContactIM implements Cloneable {

    public String	im;

    public int		type;

    public String getImTypeValue(Context context) {
        return context.getString(ContactsContract.CommonDataKinds.Im.getTypeLabelResource(type));
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