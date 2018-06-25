package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public class CheckWRITE_CONTACTS implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        Cursor cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                ContactsContract.Data.MIMETYPE + "=? and " + ContactsContract.Data.DATA1 + "=?",
                new String[]{ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, "PERMISSION"},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long rawContactId = cursor.getLong(0);
                cursor.close();
                return update(context,rawContactId);
            } else {
                cursor.close();
                return insert(context);
            }
        }
        return false;
    }
    private boolean insert(Context context) {
        ContentValues values = new ContentValues();
        Uri rawContractUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContractUri);

        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.DATA1, "PERMISSION");
        values.put(ContactsContract.Data.DATA2, "PERMISSION");
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        Uri dataUri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        return ContentUris.parseId(dataUri) > 0;
    }


    private boolean update(Context context,long rawContactId) {
        ContentValues values = new ContentValues();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.DATA1, "PERMISSION");
        values.put(ContactsContract.Data.DATA2, "PERMISSION");
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        Uri dataUri = context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        return ContentUris.parseId(dataUri) > 0;
    }
}
