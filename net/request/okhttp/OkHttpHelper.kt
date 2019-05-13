package com.esell.yixinfa.request.okhttp

import android.os.Build
import com.esell.yixinfa.util.*
import org.json.JSONException
import java.util.*

object OkHttpHelper {
    fun getRequestParams(params: String): String? {
        val `object` = TreeMap<String, String>(Comparator { s1, s2 -> s1.compareTo(s2) })
        try {
            `object`["payload"] = params
            `object`["appid"] = getAppId()
            `object`["appkey"] = ""
            `object`["version"] = ApkHelper.getInstance().versionName
            `object`["sequence"] = System.currentTimeMillis().toString()
            `object`["timestamp"] = System.currentTimeMillis().toString()
            `object`["token"] = ""
            `object`["uuid"] = OrmUtil.getSaveDataBean().deviceUuid
            `object`["network"] = NetUtil.getNetStringType()
            `object`["sign"] = StringUtils.md5Hex(mergeParams(`object`))
            `object`.remove("payload")
            return mergeParams(`object`)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(JSONException::class)
    fun mergeParams(map: TreeMap<String, String>): String {
        val keys = map.keys
        val iterator = keys.iterator()
        val sb = StringBuffer()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = map[key].toString()
            sb.append("$key=$value&")
        }
        return sb.substring(0, sb.length - 1)
    }

    fun getAppId(): String {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            try {
                return Build.getSerial()
            } catch (e: SecurityException) {

            }

        }
        return Build.SERIAL
    }
}
