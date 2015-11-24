package j2w.team.modules.contact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import j2w.team.common.log.L;
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
public class ContactManage implements J2WIContact, J2WIWriteContact {

	private final Context	context;

	public ContactManage(Context context) {
		this.context = context;
	}

	private static final String[]	CONTACTS_ID			= new String[] { Contacts.NAME_RAW_CONTACT_ID, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP };

	private static final String[]	CONTACTS			= new String[] { Contacts.NAME_RAW_CONTACT_ID, Contacts.DISPLAY_NAME_PRIMARY };

	private static final String[]	PHONES_PROJECTION	= new String[] { Phone.CONTACT_ID, Phone.TYPE, Phone.NUMBER, Contacts.DISPLAY_NAME, Contacts.PHOTO_ID, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP };

	private static final String[]	PHONES_ID			= new String[] { Data.RAW_CONTACT_ID };

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
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
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
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS, Contacts.NAME_RAW_CONTACT_ID + " = ?", new String[] { String.valueOf(id) }, null);

		ContactModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
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

	@Override public List<ContactModel> getAllPhoneContacts(boolean isPhone, boolean isEmail) {
		return getAllPhoneContacts("", isPhone, isEmail);
	}

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
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
				long lastUpdate = idCursor.getLong(idCursor.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
				contact = getContactDataByContactId(contactId);
				contact.lastUpdate = lastUpdate;
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
	@Override public List<String> getAllPhoneDetailIDs() {
		List<String> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append("");
		stringBuilder.append("%");
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?", new String[] { stringBuilder.toString() }, null);
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
				contacts.add(contactId);
			} while (idCursor.moveToNext());
		}
		idCursor.close();
		return contacts;
	}

	@Override public List<String> getAllPhoneDetailIDs(int version) {
		List<String> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append("");
		stringBuilder.append("%");
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + Contacts.NAME_RAW_CONTACT_ID + " > ?", new String[] { stringBuilder.toString(),
				String.valueOf(version) }, null);
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
				contacts.add(contactId);
			} while (idCursor.moveToNext());
		}
		idCursor.close();
		return contacts;
	}

	@Override public List<String> getAllPhoneDetailIDs(long time) {
		List<String> contacts = new ArrayList<>();

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("%");
		stringBuilder.append("");
		stringBuilder.append("%");
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?", new String[] { stringBuilder.toString(),
				String.valueOf(time) }, null);
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
				contacts.add(contactId);
			} while (idCursor.moveToNext());
		}
		idCursor.close();
		return contacts;
	}

	@Override public List<String> getFilterPhoneNumber(String number) {
		List<String> contacts = new ArrayList<>();

		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Data.CONTENT_URI, PHONES_ID, Data.DATA1 + "=?", new String[] { number }, null);

		if (idCursor.moveToFirst()) {
			do {
				String idCursorString = idCursor.getString(idCursor.getColumnIndex(Data.RAW_CONTACT_ID));
				contacts.add(idCursorString);
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
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, Contacts.NAME_RAW_CONTACT_ID + " > ?", new String[] { String.valueOf(version) }, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " desc");
		ContactDetailModel contact = null;
		if (idCursor.moveToFirst()) {
			do {
				String contactId = idCursor.getString(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
				long lastUpdate = idCursor.getLong(idCursor.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
				contact = getContactDataByContactId(contactId);
				contact.lastUpdate = lastUpdate;
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
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, null, null, Contacts.NAME_RAW_CONTACT_ID + " desc limit 0,1 ");

		if (idCursor.moveToFirst()) {
			int contactId = idCursor.getInt(idCursor.getColumnIndex(Contacts.NAME_RAW_CONTACT_ID));
			return contactId;
		} else {
			return 0;
		}
	}

	@Override public long getLastTime() {
		ContentResolver contentResolver = context.getContentResolver();
		Cursor idCursor = contentResolver.query(Contacts.CONTENT_URI, CONTACTS_ID, null, null, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " desc limit 0,1 ");

		if (idCursor.moveToFirst()) {
			long contactId = idCursor.getLong(idCursor.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
			return contactId;
		} else {
			return 0;
		}
	}

	@Override public ContactDetailModel getContactDataByContactId(String id) {
		ContactDetailModel contactModel = new ContactDetailModel();

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
		Cursor contactInfoCursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID, Data.MIMETYPE, Data.DATA1, Data.DATA2, Contacts.CONTACT_LAST_UPDATED_TIMESTAMP }, Data.RAW_CONTACT_ID + " = ?", new String[] { id }, null);

		if (contactInfoCursor.moveToFirst()) {
			contactModel.lastUpdate = contactInfoCursor.getLong(contactInfoCursor.getColumnIndex(Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
			contactModel.contactId = contactInfoCursor.getString(contactInfoCursor.getColumnIndex(Data.RAW_CONTACT_ID));
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

	@Override public void writeSystemContact(String name, String organization, String note, List<ContactPhone> phone, List<ContactAddress> address, List<ContactEmail> emails) throws RemoteException,
			OperationApplicationException {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		// 第一个参数：内容提供者的主机名
		// 第二个参数：要执行的操作
		ArrayList<ContentProviderOperation> operations = new ArrayList<>();

		// 操作1.添加Google账号，这里值为null，表示不添加
		ContentProviderOperation operation = ContentProviderOperation.newInsert(uri).withValue("account_name", null)// account_name:Google账号
				.build();
		operations.add(operation);

		uri = Uri.parse("content://com.android.contacts/data");

		// 操作2.添加data表中name字段
		if (!TextUtils.isEmpty(name)) {

			ContentProviderOperation operation2 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/name").withValue(ContactsContract.Data.DATA1, name).build();
			operations.add(operation2);
		}

		// 操作3.添加data表中organization字段
		if (!TextUtils.isEmpty(organization)) {

			ContentProviderOperation operation3 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/organization").withValue(ContactsContract.Data.DATA1, organization).build();
			operations.add(operation3);
		}

		if (!TextUtils.isEmpty(note)) {
			ContentProviderOperation operation3 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
					.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE).withValue(ContactsContract.Data.DATA1, note).build();
			operations.add(operation3);
		}

		// 操作4.添加data表中phone字段
		if (phone != null) {
			for (ContactPhone item : phone) {
				ContentProviderOperation operation4 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2").withValue(ContactsContract.Data.DATA2, item.phoneType)
						.withValue(ContactsContract.Data.DATA1, item.phone).build();
				operations.add(operation4);
			}
		}

		// 操作5.添加data表中的Email字段
		if (emails != null) {
			for (ContactEmail item : emails) {
				ContentProviderOperation operation5 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/email_v2").withValue(ContactsContract.Data.DATA2, item.emailType)
						.withValue(ContactsContract.Data.DATA1, item.emailAddress).build();
				operations.add(operation5);
			}
		}
		// 操作6.添加data表中的地址字段
		if (address != null) {
			for (ContactAddress item : address) {
				ContentProviderOperation operation6 = ContentProviderOperation.newInsert(uri).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
						.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2").withValue(ContactsContract.Data.DATA2, item.type)
						.withValue(ContactsContract.Data.DATA1, item.address).build();
				operations.add(operation6);
			}
		}

		resolver.applyBatch("com.android.contacts", operations);
	}

	@Override public void updateSystemContact(String id, String name, String organization, String note, List<ContactPhone> phone, List<ContactAddress> address, List<ContactEmail> emails)
			throws RemoteException, OperationApplicationException {

		if (TextUtils.isEmpty(id)) {
			return;
		}

		ArrayList<ContentProviderOperation> operations = new ArrayList<>();
		ArrayList<ContentProviderOperation> operationsDelete = new ArrayList<>();
		Uri uri = ContactsContract.Data.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();

		ContentResolver resolver = context.getContentResolver();

		// 操作1.添加data表中name字段
		if (!TextUtils.isEmpty(name)) {
			ContentProviderOperation operation = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/name" })
					.withValue(ContactsContract.Data.DATA1, name).build();
			operations.add(operation);
		}

		// 操作2.添加data表中organization字段
		if (!TextUtils.isEmpty(organization)) {
			ContentProviderOperation operation = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/organization" })
					.withValue(ContactsContract.Data.DATA1, organization).build();
			operations.add(operation);
		} else {
			// 先删除
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/organization" });
			operationsDelete.add(operation.build());
		}
		if (!TextUtils.isEmpty(note)) {

			ContentProviderOperation contentProviderOperation = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
					.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/note" })
					.withValue(ContactsContract.Data.DATA1, note).build();
			operations.add(contentProviderOperation);
		} else {
			// 先删除
			ContentProviderOperation.Builder contentProviderOperation = ContentProviderOperation.newDelete(uri);
			contentProviderOperation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/note" });
			operationsDelete.add(contentProviderOperation.build());
		}

		// 操作3.添加data表中的Email字段
		if (emails != null && emails.size() > 0) {
			// 先删除
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/email_v2" });
			operationsDelete.add(operation.build());

			// 在添加
			for (ContactEmail item : emails) {
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
				operation.withValue(ContactsContract.Data.RAW_CONTACT_ID, id);
				operation.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
				operation.withValue(ContactsContract.Data.DATA2, item.emailType);
				operation.withValue(ContactsContract.Data.DATA4, item.emailAddress);
				operation.withValue(ContactsContract.Data.DATA1, item.emailAddress);
				operations.add(operation.build());

			}
		} else {
			// 先删除
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/email_v2" });
			operationsDelete.add(operation.build());
		}

		// 操作4.添加data表中的地址字段
		if (address != null && address.size() > 0) {
			// 先删除
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/postal-address_v2" });
			operationsDelete.add(operation.build());

			// 在添加
			for (ContactAddress item : address) {
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
				operation.withValue(ContactsContract.Data.RAW_CONTACT_ID, id);
				operation.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/postal-address_v2");
				operation.withValue(ContactsContract.Data.DATA2, item.type);
				operation.withValue(ContactsContract.Data.DATA4, item.address);
				operation.withValue(ContactsContract.Data.DATA1, item.address);
				operations.add(operation.build());
			}
		} else {
			// 先删除
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/postal-address_v2" });
			operationsDelete.add(operation.build());
		}

		// 操作5.添加data表中的phone字段
		if (phone != null && phone.size() > 0) {
			ContentProviderOperation.Builder operation = ContentProviderOperation.newDelete(uri);
			operation.withSelection(Data.RAW_CONTACT_ID + "=?" + " AND " + ContactsContract.Data.MIMETYPE + "=?", new String[] { id, "vnd.android.cursor.item/phone_v2" });
			operationsDelete.add(operation.build());

			for (ContactPhone item : phone) {
				operation = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
				operation.withValue(ContactsContract.Data.RAW_CONTACT_ID, id);
				operation.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
				operation.withValue(ContactsContract.Data.DATA2, item.phoneType);
				operation.withValue(ContactsContract.Data.DATA4, item.phone);
				operation.withValue(ContactsContract.Data.DATA1, item.phone);
				operations.add(operation.build());
			}
		}
		if (operations.size() > 0) {

			ContentProviderResult rsDelete[] = resolver.applyBatch(ContactsContract.AUTHORITY, operationsDelete);
			for (ContentProviderResult s : rsDelete) {
				L.i(s.toString());
			}
			ContentProviderResult rs[] = resolver.applyBatch(ContactsContract.AUTHORITY, operations);

			for (ContentProviderResult s : rs) {
				L.i(s.toString());
			}
		}
	}
}
