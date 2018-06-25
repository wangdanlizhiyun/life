package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckREAD_SMS implements Check {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Boolean check(Context context) throws Exception {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{Telephony.Sms._ID, Telephony.Sms.ADDRESS, Telephony.Sms.PERSON, Telephony.Sms.BODY};
        Cursor cursor = contentResolver.query(Telephony.Sms.CONTENT_URI, projection, null, null, null);
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
    }
}
