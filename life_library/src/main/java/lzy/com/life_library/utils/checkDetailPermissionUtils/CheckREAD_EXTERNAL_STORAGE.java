package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckREAD_EXTERNAL_STORAGE implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        File directory = Environment.getExternalStorageDirectory();
        if (directory.exists() && directory.canRead()) {
            long modified = directory.lastModified();
            String[] pathList = directory.list();
            return modified > 0 && pathList != null;
        }else {
            return false;
        }
    }
}
