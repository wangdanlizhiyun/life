package lzy.com.life_library.listener;

import android.content.Intent;

/**
 * Created by lizhiyun on 2018/1/17.
 */

public interface ResultListener {
    void onResultOk(Intent intent);
    void onResultCancel(Intent intent);
    void onResultFirstUser(Intent intent);
}
