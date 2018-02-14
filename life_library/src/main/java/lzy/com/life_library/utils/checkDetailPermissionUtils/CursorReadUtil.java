package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.database.Cursor;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public class CursorReadUtil {
    public static void read(Cursor cursor) {
        int count = cursor.getCount();
        if (count > 0) {
            int type = cursor.getType(0);
            switch (type) {
                case Cursor.FIELD_TYPE_BLOB:
                case Cursor.FIELD_TYPE_NULL: {
                    break;
                }
                case Cursor.FIELD_TYPE_INTEGER:
                case Cursor.FIELD_TYPE_FLOAT:
                case Cursor.FIELD_TYPE_STRING:
                default: {
                    cursor.getString(0);
                    break;
                }
            }
        }
    }
}
