package j2w.team.modules.contact.bean;

/**
 * @创建人 sky
 * @创建时间 15/8/16 下午8:46
 * @类描述 网址
 */
public class ContactWebsite implements Cloneable {

	public String	websit;

	public int		type;

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