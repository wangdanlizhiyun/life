package com.esell.yixinfa.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.esell.yixinfa.EsellApplication;

/**
 * Created by Xing.Su on 2017/9/21.
 */

public class NetUtil {

    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_WIFI = NETWORK_TYPE_NONE + 1;
    public static final int NETWORK_TYPE_2G = NETWORK_TYPE_WIFI + 1;
    public static final int NETWORK_TYPE_3G = NETWORK_TYPE_2G + 1;
    public static final int NETWORK_TYPE_4G = NETWORK_TYPE_3G + 1;// LTE

    public static int netType() {
        int type = NETWORK_TYPE_NONE;
        ConnectivityManager cm = (ConnectivityManager) EsellApplication.Companion.getSInstance().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo && wifiInfo.isConnected()) {
            type = NETWORK_TYPE_WIFI;
        }
        if (null != mobileInfo && mobileInfo.isConnected()) {
            TelephonyManager tm = (TelephonyManager) EsellApplication.Companion.getSInstance().getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm.getNetworkType();
            String strSubTypeName = mobileInfo.getSubtypeName();
            switch (netType) {
                case 1:
                case 16:
                case 2:
                case 4:
                case 7:
                case 11:
                    type = NETWORK_TYPE_2G;
                    break;
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                case 17:
                    type = NETWORK_TYPE_3G;
                    break;
                case 13:
                case 18:
                case 19:
                    type = NETWORK_TYPE_4G;
                    break;
                default:
                    type = NETWORK_TYPE_2G;
                    break;
            }

        }
        return type;
    }
    public static String getNetStringType() {
        int type = netType();
        switch (type) {
            case 1:
                return "WIFI";
            case 2:
                return "2G";
            case 3:
                return "3G";
            case 4:
                return "4G";
        }
        return "2G";
    }



    /**
     * 检查是否有网络联接
     *
     * @return boolean
     */
    public static boolean checkNetwork() {
        boolean result = false;
        try{
            ConnectivityManager cm = (ConnectivityManager) EsellApplication.Companion.getSInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netinfo = cm.getActiveNetworkInfo();
            if (netinfo != null && netinfo.isConnected()) {
                result = true;
            } else {
                result = false;
            }
        }catch (Exception e){

        }

        return result;
    }
}
