package com.esell.yixinfa.request.okhttp

import java.io.IOException
import java.io.InterruptedIOException

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyOkHttpRetryInterceptor(builder: Builder) : Interceptor {
    private var executionCount: Int = 0//最大重试次数
    private var retryInterval: Long = 5_000//重试的间隔

    init {
        this.executionCount = builder.executionCount
        this.retryInterval = builder.retryInterval
    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        var response = doRequest(chain, request)
        var retryNum = 0
        while ((!response.isSuccessful) && retryNum <= executionCount) {
            var nextInterval = retryInterval
            val retryBean:Any? = request.tag()
            if (retryBean is RetryBean){
                nextInterval = retryBean.retryInterval
                executionCount = retryBean.retryTimes
            }
            try {
                Thread.sleep(nextInterval)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                throw InterruptedIOException()
            }

            retryNum++
            // retry the request
            response = doRequest(chain, request)
        }
        return response
    }

    private fun doRequest(chain: Interceptor.Chain, request: Request): Response {
        return chain.proceed(request)
    }

    class Builder {
        var executionCount: Int = 0
        var retryInterval: Long = 0

        init {
            executionCount = 3
            retryInterval = 1000
        }

        fun executionCount(executionCount: Int): Builder {
            this.executionCount = executionCount
            return this
        }

        fun retryInterval(retryInterval: Long): Builder {
            this.retryInterval = retryInterval
            return this
        }

        fun build(): MyOkHttpRetryInterceptor {
            return MyOkHttpRetryInterceptor(this)
        }
    }

}
