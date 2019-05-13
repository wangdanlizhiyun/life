package com.esell.yixinfa.request;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;

public class NetWork {
    private static final String TAG = "NetWork";
    /**
     * 设置启动WIFI
     *
     * @param context
     */
    public static void init(Context context) {
        String proxyStr = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Uri uri = Uri.parse("content://telephony/carriers/preferapn");
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                cursor.moveToNext();
                proxyStr = cursor.getString(cursor.getColumnIndex("proxy"));
                if (null != proxyStr) {
                    proxyStr = proxyStr.trim();
                    mPort = cursor.getInt(cursor.getColumnIndex("port"));
                }
            }
        }

        mProxyStr = (null == proxyStr ? null : (0 == proxyStr.length() ? null : proxyStr));
    }



    public static String getProxyHost() {
        return mProxyStr;
    }

    public static int getProxyPort() {
        return mPort;
    }

    private static String mProxyStr;
    private static int mPort;
}
