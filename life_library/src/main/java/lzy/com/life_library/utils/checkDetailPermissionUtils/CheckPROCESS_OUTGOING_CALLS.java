package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import lzy.com.life_library.entity.PermissionType;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckPROCESS_OUTGOING_CALLS implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        return ContextCompat.checkSelfPermission(context, PermissionType.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;
    }
}
