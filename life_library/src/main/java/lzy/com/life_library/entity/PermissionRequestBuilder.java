package lzy.com.life_library.entity;

import android.text.TextUtils;
import android.util.LruCache;

import java.util.ArrayList;

import lzy.com.life_library.listener.PermissionDeniedListener;
import lzy.com.life_library.utils.LifeUtil;

/**
 * Created by lizhiyun on 2018/4/10.
 */

public class PermissionRequestBuilder {
    ArrayList<String> mPermissions;
    PermissionDeniedListener mPermissionDeniedListener;
    Runnable mRunnable;

    LruCache<String ,PermissionRequest> mPermissionRequestLruCache;

    public PermissionRequestBuilder() {
        mPermissions = new ArrayList<>(5);
        mPermissionRequestLruCache = new LruCache<>(1);
    }

    public PermissionRequestBuilder permission(String... permissions) {
        for (String permission : permissions
                ) {
            if (!TextUtils.isEmpty(permission)) {
                mPermissions.add(permission);
            }
        }
        return this;
    }

    public PermissionRequestBuilder deny(PermissionDeniedListener listener) {
        mPermissionDeniedListener = listener;
        return this;
    }


    public void run(Runnable runnable) {
        mRunnable = runnable;
        PermissionRequest permissionRequest = mPermissionRequestLruCache.get("permissionRequest");
        if (permissionRequest == null){
            permissionRequest = new PermissionRequest();
            mPermissionRequestLruCache.put("permissionRequest",permissionRequest);
        }
        permissionRequest.setPermissions(mPermissions.toArray(new String[]{}));
        permissionRequest.setPermissionDeniedListener(mPermissionDeniedListener);
        permissionRequest.setRunnable(mRunnable);
        LifeUtil.requestPermission(permissionRequest);
    }
}
