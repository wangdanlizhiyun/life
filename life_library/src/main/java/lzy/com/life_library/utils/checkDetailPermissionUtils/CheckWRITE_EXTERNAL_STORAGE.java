package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckWRITE_EXTERNAL_STORAGE implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists() || !directory.canWrite()) return false;
        File file = new File(directory, "a.b.c.d");
        if (file.exists()) {
            return file.delete();
        } else {
            return file.createNewFile();
        }
    }
}
