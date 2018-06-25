package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import java.util.List;

import lzy.com.life_library.entity.PermissionType;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckLOCATION implements Check {
    @Override
    public Boolean check(Context context) throws Exception {

        return ContextCompat.checkSelfPermission(context, PermissionType.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, PermissionType.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ;
    }

}
