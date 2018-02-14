package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CallLog;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckWRITE_CALL_LOG implements Check {
    @SuppressLint("MissingPermission")
    @Override
    public Boolean check(Context context) throws Throwable {
        ContentResolver resolver = context.getContentResolver();
        try {
            ContentValues content = new ContentValues();
            content.put(CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE);
            content.put(CallLog.Calls.NUMBER, "0");
            content.put(CallLog.Calls.DATE, 19760808);
            content.put(CallLog.Calls.NEW, "0");
            Uri resourceUri = resolver.insert(CallLog.Calls.CONTENT_URI, content);
            return ContentUris.parseId(resourceUri) > 0;
        } finally {
            resolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls.NUMBER + "=?", new String[]{"1"});
        }
    }
}
