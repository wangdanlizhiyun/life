package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CalendarContract;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public class CheckREAD_CALENDAR implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        try {
            String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.NAME};
            @SuppressLint("MissingPermission") Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
            if (cursor != null) {
                try {
                    CursorReadUtil.read(cursor);
                } finally {
                    cursor.close();
                }
                return true;
            }else {
                return false;
            }
        }finally {

        }
    }
}
