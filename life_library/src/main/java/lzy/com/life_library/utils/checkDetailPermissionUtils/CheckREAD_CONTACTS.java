package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public class CheckREAD_CONTACTS implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        try {
            String[] projection = new String[]{ContactsContract.Data._ID, ContactsContract.Data.DATA1};
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
            if (cursor != null) {
                try {
                    CursorReadUtil.read(cursor);
                } finally {
                    cursor.close();
                }
                return true;
            } else {
                return false;
            }
        }finally {

        }
    }
}
