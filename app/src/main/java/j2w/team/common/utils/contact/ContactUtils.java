package j2w.team.common.utils.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;

/**
 * @创建人 sky
 * @创建时间 15/5/4 下午5:26
 * @类描述 电话本工具类
 */
public class ContactUtils {

	private static final ContactUtils	instance	= new ContactUtils();

	private Context						context;

	public static ContactUtils getInstance(Context context) {
		instance.setContext(context);
		return instance;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	private static final String[]	CONTACTS			= new String[] { Contacts._ID, Contacts.DISPLAY_NAME_PRIMARY };

	private static final String[]	PHONES_PROJECTION	= new String[] { Phone.CONTACT_ID, Phone.TYPE, Phone.NUMBER, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID };

	/**
	 * 获取联系人头像
	 *
	 * @param id
	 * @return
	 */
	public Bitmap getContactPhotoByContactId(String id) {
		Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
		InputStream photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), contactUri);
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
	public PhoneContact getPhoneContactByName(String partialName, boolean isPhone, boolean isEmail) {
		PhoneContact contact = new PhoneContact();

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
	public List<PhoneContact> getAllPhoneContacts(String userName, boolean isPhone, boolean isEmail) {
		List<PhoneContact> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append(userName);
		stringBuilder.append("%");

		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?", new String[] { stringBuilder.toString() }, null);
		PhoneContact contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts._ID));
				String name = idCursor.getString(idCursor.getColumnIndex(Contacts.DISPLAY_NAME_PRIMARY));
				contact = contact == null ? new PhoneContact() : (PhoneContact) contact.clone();
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
	public List<PhoneContact> getAllPhoneContacts(boolean isPhone, boolean isEmail) {
		return getAllPhoneContacts("", isPhone, isEmail);
	}

	/**
	 * 获取所有联系人
	 * 
	 * @return
	 */
	public List<PhoneContact> getAllPhoneContacts() {
		List<PhoneContact> contacts = new ArrayList<>();

		Cursor phoneCursor = context.getContentResolver().query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if (phoneCursor.moveToFirst()) {
			PhoneContact contact = null;

			do {
				contact = contact == null ? new PhoneContact() : (PhoneContact) contact.clone();
				contact.contactId = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.CONTACT_ID));
				contact.displayName = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.DISPLAY_NAME));
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				String phoneNumberType = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.TYPE));
				Map<String, String> phoneNumbers = new HashMap<>();
				phoneNumbers.put(phoneNumberType, phoneNumber);
				contact.phoneNumbers = phoneNumbers;
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
	private Map<String, String> getContactPhoneNumbersById(String contactId) {
		Map<String, String> phoneNumbers = new HashMap<>();

		Cursor phoneCursor = context.getContentResolver().query(Phone.CONTENT_URI, new String[] { Phone.NUMBER, Phone.TYPE }, Phone.CONTACT_ID + " = ?", new String[] { contactId }, null);
		if (phoneCursor.moveToFirst()) {
			do {
				String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
				String phoneNumberType = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.TYPE));
				phoneNumbers.put(phoneNumberType, phoneNumber);
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
	private Map<String, String> getContactEmailByContactId(String contactId) {
		Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[] { Phone.NUMBER, Phone.TYPE }, Phone.CONTACT_ID + " = ?",
				new String[] { contactId }, null);
		Map<String, String> emails = new HashMap<>();
		if (emailCursor.moveToFirst()) {
			do {
				String emailAddress = null;
				String emailType = null;
				emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
				emailType = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
				emails.put(emailType, emailAddress);
			} while (emailCursor.moveToNext());
		}
		emailCursor.close();
		return emails;
	}

}
