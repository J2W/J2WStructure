package j2w.team.modules.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.provider.ContactsContract.CommonDataKinds.*;
import android.provider.ContactsContract.Contacts;

import j2w.team.modules.contact.bean.ContactAddress;
import j2w.team.modules.contact.bean.ContactDetailModel;
import j2w.team.modules.contact.bean.ContactEmail;
import j2w.team.modules.contact.bean.ContactIM;
import j2w.team.modules.contact.bean.ContactModel;
import j2w.team.modules.contact.bean.ContactPhone;
import j2w.team.modules.contact.bean.ContactWebsite;

/**
 * @创建人 sky
 * @创建时间 15/5/4 下午5:26
 * @类描述 电话本工具类
 */
public class ContactManage implements J2WIContact {

	private final Context	context;

	public ContactManage(Context context) {
		this.context = context;
	}

	private static final String[]	CONTACTS_ID			= new String[] { Contacts._ID };

	private static final String[]	CONTACTS			= new String[] { Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY };

	private static final String[]	PHONES_PROJECTION	= new String[] { Phone.CONTACT_ID, Phone.TYPE, Phone.NUMBER, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID };

	/**
	 * 获取联系人头像
	 *
	 * @param id
	 * @return
	 */
	@Override public Bitmap getContactPhotoByContactId(String id) {
		Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, id);
		InputStream photoInputStream = Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
		Bitmap photo = BitmapFactory.decodeStream(photoInputStream);
		if (photo != null) {
			return photo;
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), 0);
		return bitmap;
	}

	/**
	 * 根据名称搜索
	 *
	 * @param partialName
	 * @return
	 */
	@Override public ContactModel getPhoneContactByName(String partialName, boolean isPhone, boolean isEmail) {
		ContactModel contact = new ContactModel();

		ContentResolver contentResolver = context.getContentResolver();
		Cursor c = contentResolver.query(Contacts.CONTENT_URI, CONTACTS, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?", new String[] { partialName }, null);

		if (c.moveToFirst()) {
			while (c.moveToNext()) {
				String contactId = c.getString(c.getColumnIndex(Contacts.LOOKUP_KEY));
				String contactName = c.getString(c.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
				contact.contactId = contactId;
				contact.displayName = contactName;
				contact.phoneNumbers = getContactPhoneNumbersById(contactId);
				if (isPhone) {
					contact.photo = getContactPhotoByContactId(contactId);
				}
				if (isEmail) {
					contact.emailAddresses = getContactEmailByContactId(contactId);
				}
			}
			c.close();
		}
		return contact;
	}

	/**
	 * 获取所有联系人
	 *
	 * @return
	 */
	@Override public List<ContactModel> getAllPhoneContacts(String userName, boolean isPhone, boolean isEmail) {
		List<ContactModel> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append(userName);
		stringBuilder.append("%");

		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?", new String[] { stringBuilder.toString() }, null);
		ContactModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
				String name = idCursor.getString(idCursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
				contact = contact == null ? new ContactModel() : (ContactModel) contact.clone();
				contact.contactId = contactId;
				contact.displayName = name;
				contact.phoneNumbers = getContactPhoneNumbersById(contactId);
				if (isPhone) {
					contact.photo = getContactPhotoByContactId(contactId);
				}
				if (isEmail) {
					contact.emailAddresses = getContactEmailByContactId(contactId);
				}
				contacts.add(contact);
			} while (idCursor.moveToNext());
		}
		idCursor.close();

		return contacts;
	}

	@Override public List<ContactModel> getAllPhoneContactsByContactId(int id, boolean isPhone, boolean isEmail) {
		List<ContactModel> contacts = new ArrayList<>();
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS, Contacts._ID + " = ?", new String[] { String.valueOf(id) }, null);
		ContactModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
				String name = idCursor.getString(idCursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
				contact = contact == null ? new ContactModel() : (ContactModel) contact.clone();
				contact.contactId = contactId;
				contact.displayName = name;
				contact.phoneNumbers = getContactPhoneNumbersById(contactId);
				if (isPhone) {
					contact.photo = getContactPhotoByContactId(contactId);
				}
				if (isEmail) {
					contact.emailAddresses = getContactEmailByContactId(contactId);
				}
				contacts.add(contact);
			} while (idCursor.moveToNext());
		}
		idCursor.close();

		return contacts;
	}

	/**
	 * 获取所有联系人
	 *
	 * @return
	 */
	@Override public List<ContactModel> getAllPhoneContacts(boolean isPhone, boolean isEmail) {
		return getAllPhoneContacts("", isPhone, isEmail);
	}

	/**
	 * 获取所有联系人
	 *
	 * @return
	 */
	@Override public List<ContactModel> getAllPhoneContacts() {
		List<ContactModel> contacts = new ArrayList<>();

		Cursor phoneCursor = context.getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if (phoneCursor.moveToFirst()) {
			ContactModel contact = null;

			do {
				contact = contact == null ? new ContactModel() : (ContactModel) contact.clone();
				contact.contactId = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.CONTACT_ID));
				contact.displayName = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));
				ContactPhone item = new ContactPhone();
				item.phone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				item.phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));
				List<ContactPhone> items = new ArrayList<>();
				items.add(item);
				contact.phoneNumbers = items;
				contacts.add(contact);
			} while (phoneCursor.moveToNext());
		}

		phoneCursor.close();
		return contacts;
	}

	/**
	 * 返回联系人电话号码
	 *
	 * @param contactId
	 * @return
	 */
	private List<ContactPhone> getContactPhoneNumbersById(String contactId) {
		List<ContactPhone> phoneNumbers = new ArrayList<>();
		ContactPhone item = null;
		Cursor phoneCursor = context.getContentResolver().query(Phone.CONTENT_URI, new String[] { Phone.NUMBER, Phone.TYPE }, Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);
		if (phoneCursor.moveToFirst()) {
			do {
				item = item == null ? new ContactPhone() : (ContactPhone) item.clone();
				item.phone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				item.phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(Phone.TYPE));
				phoneNumbers.add(item);
			} while (phoneCursor.moveToNext());
		}

		phoneCursor.close();
		return phoneNumbers;
	}

	/**
	 * 返回联系人邮箱
	 *
	 * @param contactId
	 * @return
	 */
	private List<ContactEmail> getContactEmailByContactId(String contactId) {
		List<ContactEmail> emails = new ArrayList<>();
		ContactEmail item = null;
		Cursor emailCursor = context.getContentResolver().query(Email.CONTENT_URI, new String[] { Phone.NUMBER, Phone.TYPE }, Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);
		if (emailCursor.moveToFirst()) {
			do {
				item = item == null ? new ContactEmail() : (ContactEmail) item.clone();
				item.emailAddress = emailCursor.getString(emailCursor.getColumnIndex(Email.ADDRESS));
				item.emailType = emailCursor.getInt(emailCursor.getColumnIndex(Email.TYPE));
				emails.add(item);
			} while (emailCursor.moveToNext());
		}
		emailCursor.close();
		return emails;
	}

	/**
	 * 获取所有联系人 - 详情
	 *
	 * @return
	 */
	@Override public List<ContactDetailModel> getAllPhoneDetailContacts(String userName) {
		List<ContactDetailModel> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append(userName);
		stringBuilder.append("%");

		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?", new String[] { stringBuilder.toString() }, null);
		ContactDetailModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
				contact = getContactDataByContactId(contactId);
				contacts.add(contact);
			} while (idCursor.moveToNext());
		}
		idCursor.close();
		return contacts;
	}

	/**
	 * 获取所有联系人 - 详情
	 *
	 * @return
	 */
	@Override public List<ContactDetailModel> getAllPhoneDetailContacts() {
		return getAllPhoneDetailContacts("");
	}

	/**
	 * 获取
	 * 
	 * @param version
	 *            版本
	 * @return
	 */
	@Override public List<ContactDetailModel> getAllPhoneDetailContacts(int version) {
		List<ContactDetailModel> contacts = new ArrayList<>();

		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts._ID + " > ?", new String[] { String.valueOf(version) }, null);
		ContactDetailModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
				contact = getContactDataByContactId(contactId);
				contacts.add(contact);
			} while (idCursor.moveToNext());
		}
		idCursor.close();
		return contacts;
	}

	/**
	 * 获取版本
	 * 
	 * @return
	 */
	@Override public int getVersion() {
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, null, null, Contacts._ID + " desc limit 0,1 ");

		if(idCursor.moveToFirst()){
			int contactId = idCursor.getInt(idCursor.getColumnIndex(Contacts._ID));
			return contactId;
		}else{
			return 0;
		}
	}

	private ContactDetailModel getContactDataByContactId(String id) {
		ContactDetailModel contactModel = new ContactDetailModel();
		contactModel.contactId = id;
		contactModel.photo = getContactPhotoByContactId(id);

		// 邮件
		List<ContactEmail> emailAddresses = null;
		ContactEmail contactEmail = null;
		// 电话
		List<ContactPhone> phoneNumbers = null;
		ContactPhone contactPhone = null;
		// 网站
		List<ContactWebsite> contactWebsites = null;
		ContactWebsite contactWebsite = null;
		// IM
		List<ContactIM> contactIMs = null;
		ContactIM contactIM = null;
		// 地址
		List<ContactAddress> contactAddresses = null;
		ContactAddress contactAddress = null;
		Cursor contactInfoCursor = context.getContentResolver().query(Data.CONTENT_URI, new String[] { Data.MIMETYPE, Data.DATA1, Data.DATA2 }, Data.CONTACT_ID + " = ?", new String[] { id }, null);

		if (contactInfoCursor.moveToFirst()) {
			do {
				String mimeType = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(Data.MIMETYPE));
				String value = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(Data.DATA1));
				int type = contactInfoCursor.getInt(contactInfoCursor.getColumnIndex(Data.DATA2));

				if (mimeType.contains("/name")) {// 名称
					contactModel.name = value;
				} else if (mimeType.contains("/organization")) {// 公司
					contactModel.organization = value;
				} else if (mimeType.contains("/phone_v2")) {// 手机
					if (phoneNumbers == null) {
						phoneNumbers = new ArrayList<>();
					}
					contactPhone = contactPhone == null ? new ContactPhone() : (ContactPhone) contactPhone.clone();
					contactPhone.phone = value;
					contactPhone.phoneType = type;
					phoneNumbers.add(contactPhone);
				} else if (mimeType.contains("/email_v2")) {// 邮箱
					if (emailAddresses == null) {
						emailAddresses = new ArrayList<>();
					}
					contactEmail = contactEmail == null ? new ContactEmail() : (ContactEmail) contactEmail.clone();
					contactEmail.emailAddress = value;
					contactEmail.emailType = type;
					emailAddresses.add(contactEmail);
				} else if (mimeType.contains("/website")) {// 网站
					if (contactWebsites == null) {
						contactWebsites = new ArrayList<>();
					}
					contactWebsite = contactWebsite == null ? new ContactWebsite() : (ContactWebsite) contactWebsite.clone();
					contactWebsite.websit = value;
					contactWebsite.type = type;
					contactWebsites.add(contactWebsite);
				} else if (mimeType.contains("/im")) {// IM
					if (contactIMs == null) {
						contactIMs = new ArrayList<>();
					}
					contactIM = contactIM == null ? new ContactIM() : (ContactIM) contactIM.clone();
					contactIM.im = value;
					contactIM.type = type;
					contactIMs.add(contactIM);
				} else if (mimeType.contains("/nickname")) {// 昵称
					contactModel.nickname = value;
				} else if (mimeType.contains("/postal-address_v2")) {// 地址
					if (contactAddresses == null) {
						contactAddresses = new ArrayList<>();
					}
					contactAddress = contactAddress == null ? new ContactAddress() : (ContactAddress) contactAddress.clone();
					contactAddress.address = value;
					contactAddress.type = type;
					contactAddresses.add(contactAddress);
				} else if (mimeType.contains("/sip_address")) {// 联网电话
					contactModel.networkPhone = value;
				} else if (mimeType.contains("/contact_event")) {// 生日
					contactModel.birthday = value;
				} else if (mimeType.contains("/note")) {// 备注
					contactModel.note = value;
				} else if (mimeType.contains("/lunarBirthday")) {// 农历生日
					contactModel.lunarBirthday = value;
				}

			} while (contactInfoCursor.moveToNext());
			// 邮件
			contactModel.emailAddresses = emailAddresses;
			// 电话
			contactModel.phoneNumbers = phoneNumbers;
			// 网站
			contactModel.contactWebsites = contactWebsites;
			// IM
			contactModel.contactIMs = contactIMs;
			// 地址
			contactModel.contactAddresses = contactAddresses;
		}
		contactInfoCursor.close();
		return contactModel;
	}
}
