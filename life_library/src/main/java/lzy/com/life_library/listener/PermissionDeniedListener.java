package lzy.com.life_library.listener;

import java.util.List;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public interface PermissionDeniedListener {
    void onDenied(List<String> perms);
}
