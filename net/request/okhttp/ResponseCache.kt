package com.esell.yixinfa.request.okhttp

import android.text.TextUtils
import android.util.LruCache
import com.esell.yixinfa.bean.ResponseCacheBean
import com.esell.yixinfa.util.NetUtil
import com.esell.yixinfa.util.OrmUtil

object ResponseCache {
    private val lCache = LruCache<String, String>(30)
    private val lock = Any()

    fun save(key: String, content: String) {
        synchronized(lock) {
            if (!TextUtils.isEmpty(key)) {
                lCache.put(key, content)
                OrmUtil.lLiteOrm.save(ResponseCacheBean(key, content, System.currentTimeMillis()))
            }
        }
    }

    fun getResponse(key: String): String? {
        synchronized(lock) {
            if (lCache[key] != null) {
                return lCache[key]
            } else {
                val responseCacheBean: ResponseCacheBean? = OrmUtil.lLiteOrm.queryById(key, ResponseCacheBean::class.java)
                return responseCacheBean?.content
            }
        }
    }


    fun ifUseCache(key: String, cacheTime: Long): Boolean {
        synchronized(lock) {
            if (TextUtils.isEmpty(key)) {
                return false
            }
            if (NetUtil.checkNetwork()) {
                val responseCacheBean: ResponseCacheBean = OrmUtil.lLiteOrm.queryById(key, ResponseCacheBean::class.java)
                        ?: return false
                return !TextUtils.isEmpty(responseCacheBean.content) &&
                        cacheTime > (System.currentTimeMillis() - responseCacheBean.saveTime) / 1000
            } else {
                return true
            }
        }
    }

    fun clear(key:String){
        synchronized(lock) {
            if (TextUtils.isEmpty(key)) {
                lCache.remove(key)
                OrmUtil.lLiteOrm.delete(getResponse(key))
            }
        }
    }
}
