package com.esell.yixinfa.request.okhttp

import android.os.Build
import android.support.annotation.IntRange
import android.text.TextUtils
import android.view.TextureView
import com.esell.yixinfa.util.ApkHelper
import com.esell.yixinfa.util.LogUtils
import com.google.gson.Gson
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

object OkhttpManager {
    val MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8")//mdiatype 这个需要和服务端保持一致
    private val MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8")//mdiatype 这个需要和服务端保持一致
    private val MEDIA_OBJECT_STREAM = MediaType.parse("")


    fun get(): OkhttpRequestBuilder {
        return OkhttpRequestBuilder().type(RequestType.GET)
    }

    fun post(): OkhttpRequestBuilder {
        return OkhttpRequestBuilder().type(RequestType.POST)
    }

    fun mergeParams(paramsMap: HashMap<String, Any>?): String {
        val tempParams = StringBuilder()
        try {
            if (paramsMap != null) {
                var pos = 0
                for (key in paramsMap.keys) {
                    if (pos > 0) {
                        tempParams.append("&")
                    }
                    tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap[key].toString(), "utf-8")))
                    pos++
                }
            }
        } catch (e: Exception) {
        }

        return tempParams.toString()
    }

    fun addHeaders(): Request.Builder {

        return Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE)
                .addHeader("appVersion", "" + ApkHelper.getInstance().versionCode)
    }

    fun getJsonObjectString(hashMap: Map<String, Any>?): String {
        val `object` = JSONObject()
        if (hashMap != null) {
            for (entry in hashMap.entries) {
                try {
                    `object`.put(entry.key, entry.value)
                } catch (e: JSONException) {
                    e.printStackTrace()
                    break
                }

            }
        }
        return `object`.toString()
    }

    private val mOkHttpClient: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
            .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
            .addInterceptor(MyOkHttpRetryInterceptor(MyOkHttpRetryInterceptor.Builder()))
            .build()


    class OkhttpRequest {
        var type: RequestType = RequestType.POST
        var url: String = ""
        var paramsMap: HashMap<String, Any>? = null
        var callBack: ResultCallBack<*>? = null
        var isSyn: Boolean = false
        var retryBean: RetryBean? = null
        var cacheSeconds: Long = 0
        var cacheKey: String = ""
        var parserDataListener: ParserDataListener = CommenParser()
        private fun <T> successCallBack(call: Call, isSyn: Boolean, content: String, callBack: ResultCallBack<T>?): Boolean {
            if (callBack == null) return false
            if (call.isCanceled) return false
            val t = callBack.getT(content) ?: return false
            if (isSyn) {
                callBack.onSuccess(t)
            } else {
                launch(UI) {
                    callBack.onSuccess(t)
                }
            }
            return true
        }

        private fun <T> failedCallBack(call: Call, isSyn: Boolean, errorMsg: String, callBack: ResultCallBack<T>?) {
            if (callBack == null) return
            if (call.isCanceled) return
            ResponseCache.getResponse(cacheKey)?.let {

                if (successCallBack(call, isSyn, it, callBack)) {
                    LogUtils.e("test", "使用缓存 $cacheKey")
                    return
                }
            }
            if (isSyn) {
                callBack.onFailed(errorMsg)
            } else {
                launch(UI) {
                    callBack.onFailed(errorMsg)
                }
            }
        }

        fun execute(): Call? {
            var call: Call
            val request: Request
            when (type) {
                RequestType.GET -> {
                    url = String.format("%s?%s", url, mergeParams(paramsMap))
                    try {
                        request = addHeaders().url(url).tag(retryBean).build()
                    }catch (e:Exception){
                        e.printStackTrace()
                        return null
                    }
                    call = mOkHttpClient.newCall(request)
                    if (ResponseCache.ifUseCache(cacheKey, cacheSeconds)) {
                        ResponseCache.getResponse(cacheKey)?.let {
                            if (successCallBack(call, isSyn, it, callBack)) {
                                LogUtils.e("test", "使用缓存 $cacheKey")
                                return call
                            }
                        }
                    }
                    if (isSyn) {
                        try {
                            val response = call.execute()
                            commenJudgeResponse(call, isSyn, response)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            failedCallBack(call, isSyn, e.toString(), callBack)
                        }

                    } else {
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                failedCallBack(call, isSyn, "访问失败:" + e.message, callBack)
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                commenJudgeResponse(call, isSyn, response)
                            }
                        })
                    }

                }
                RequestType.POST -> {
                    val lStringStringMap = HashMap<String, Any>()
                    val payload = getJsonObjectString(paramsMap)
                    lStringStringMap["payload"] = payload
                    url = String.format("%s?%s", url, OkHttpHelper.getRequestParams(payload))
                    val params = mergeParams(lStringStringMap)
                    LogUtils.e("test", "url=$url")
                    LogUtils.e("test", "params=" + lStringStringMap.toString())
                    val body = RequestBody.create(MEDIA_TYPE_JSON, params)
                    try {
                        request = addHeaders().url(url).post(body).tag(retryBean).build()
                    }catch (e:Exception){return null}
                    call = mOkHttpClient.newCall(request)
                    if (ResponseCache.ifUseCache(cacheKey, cacheSeconds)) {
                        ResponseCache.getResponse(cacheKey)?.let {

                            if (successCallBack(call, isSyn, it, callBack)) {
                                LogUtils.e("test", "使用缓存 $cacheKey")
                                return call
                            }
                        }
                    }
                    if (isSyn) {
                        try {
                            val response = call.execute()
                            commenJudgeResponse(call, isSyn, response)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            failedCallBack(call, isSyn, e.toString(), callBack)
                        }

                    } else {
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                failedCallBack(call, isSyn, "访问失败:" + e.message, callBack)
                            }

                            @Throws(IOException::class)
                            override fun onResponse(call: Call, response: Response) {
                                commenJudgeResponse(call, isSyn, response)
                            }
                        })
                    }
                }

            }
            return call
        }

        @Throws(IOException::class)
        fun commenJudgeResponse(call: Call, isSyn: Boolean, response: Response) {
            if (response.isSuccessful) {
                val result: String = parserDataListener.parse(response.body()?.string()) ?: ""
                if (successCallBack(call, isSyn, result, callBack)) {
                    ResponseCache.save(cacheKey, result)
                    return
                }
                failedCallBack(call, isSyn, "error request", callBack)

            } else {
                failedCallBack(call, isSyn, "error request", callBack)
            }
        }
    }

    interface ParserDataListener {
        fun parse(content: String?): String?
    }

    class CommenParser : ParserDataListener {
        override fun parse(content: String?): String? {
            try {
                val `object` = JSONObject(content)
                val code = `object`.optInt("code")
                var message = `object`.optString("message")
                if (TextUtils.isEmpty(message)) {
                    message = `object`.optString("msg")
                }
                if (code >= 0 && "OK" == message && !TextUtils.isEmpty(`object`.optString("payload"))) {
                    return `object`.optString("payload")

                }
            } catch (e: Exception) {

            }
            return null
        }
    }

    class NoParser : ParserDataListener {
        override fun parse(content: String?): String? {
            return content
        }
    }

    class OkhttpRequestBuilder {
        private var type: RequestType = RequestType.POST
        private var url: String = ""
        private var paramsMap: HashMap<String, Any> = HashMap()
        private var callBack: ResultCallBack<*>? = null
        private var isSyn: Boolean = false
        private var retryTimes: Int = 0
        private var retryInterval: Long = 0
        private var cacheSeconds: Long = 0
        private var cacheKey: String = ""
        private var parserDataListener: ParserDataListener = CommenParser()

        init {
            isSyn = false
            retryTimes = 0
            retryInterval = 5000
        }

        fun parserDataListener(parserDataListener: ParserDataListener): OkhttpRequestBuilder {
            this.parserDataListener = parserDataListener
            return this
        }

        fun cacheKey(cacheKey: String): OkhttpRequestBuilder {
            this.cacheKey = cacheKey
            return this
        }

        fun cacheSeconds(@IntRange(from = 1) cacheSeconds: Long): OkhttpRequestBuilder {
            this.cacheSeconds = cacheSeconds
            return this
        }

        fun retryTimes(retryTimes: Int): OkhttpRequestBuilder {
            this.retryTimes = retryTimes
            return this
        }

        fun retryInterval(retryInterval: Int): OkhttpRequestBuilder {
            this.retryInterval = retryInterval.toLong()
            return this
        }

        fun syn(): OkhttpRequestBuilder {
            this.isSyn = true
            return this
        }

        fun type(type: RequestType): OkhttpRequestBuilder {
            this.type = type
            return this
        }

        fun url(url: String): OkhttpRequestBuilder {
            this.url = url
            return this
        }

        fun params(paramsMap: HashMap<String, Any>): OkhttpRequestBuilder {
            this.paramsMap = paramsMap
            return this
        }

        fun callBack(callBack: ResultCallBack<*>): OkhttpRequestBuilder {
            this.callBack = callBack
            return this
        }

        fun build(): OkhttpRequest {
            val okhttpRequest = OkhttpRequest()
            okhttpRequest.type = type
            okhttpRequest.url = url
            okhttpRequest.paramsMap = paramsMap
            okhttpRequest.callBack = callBack
            okhttpRequest.isSyn = isSyn
            if (retryTimes > 0) {
                okhttpRequest.retryBean = RetryBean(retryTimes, retryInterval)
            }
            okhttpRequest.cacheSeconds = cacheSeconds
            okhttpRequest.cacheKey = cacheKey
            okhttpRequest.parserDataListener = parserDataListener
            return okhttpRequest
        }


    }


    private interface iResultCallBack<T> {
        fun onSuccess(t: T)

        fun onFailed(errorMsg: String)
    }

    abstract class ResultCallBack<T> : iResultCallBack<T> {
        private var clazz: Class<T>

        init {
            try {
                val entityClass = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
                clazz = entityClass
            } catch (e: Exception) {
                clazz = String::class.java as Class<T>
            }
        }

        fun getT(content: String): T? {
            if (clazz == String::class.java) {
                return content as T
            } else {
                var t: T? = null
                try {
                    t = Gson().fromJson(content, clazz)
                } catch (e: Exception) {
                }
                return t
            }
        }
    }

}
