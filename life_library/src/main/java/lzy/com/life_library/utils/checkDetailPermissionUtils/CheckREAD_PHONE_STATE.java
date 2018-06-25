package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckREAD_PHONE_STATE implements Check {
    @SuppressLint("MissingPermission")
    @Override
    public Boolean check(Context context) throws Exception {
        try{
            TelephonyManager service = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return !TextUtils.isEmpty(service.getDeviceId()) || !TextUtils.isEmpty(service.getSubscriberId());

        }catch (Exception e){

        }
        return false;
    }
}
