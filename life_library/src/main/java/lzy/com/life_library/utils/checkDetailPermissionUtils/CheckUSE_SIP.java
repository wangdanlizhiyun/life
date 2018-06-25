package lzy.com.life_library.utils.checkDetailPermissionUtils;

import android.content.Context;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;

/**
 * Created by lizhiyun on 2018/2/13.
 */

public class CheckUSE_SIP implements Check {
    @Override
    public Boolean check(Context context) throws Exception {
        try {
            if (!SipManager.isApiSupported(context)) {
                return true;
            }
            SipManager manager = SipManager.newInstance(context);
            if (manager == null) {
                return true;
            }
            SipProfile.Builder builder = new SipProfile.Builder("Permission", "127.0.0.1");
            builder.setPassword("132654");
            SipProfile profile = builder.build();
            manager.open(profile);
            manager.close(profile.getUriString());
        }finally {

        }
        return true;
    }
}
