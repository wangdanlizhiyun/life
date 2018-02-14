package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by lizhiyun on 2018/2/12.
 */

public interface Check {
    public Boolean check(Context context) throws Throwable;
}
