package com.gucci.showad.coroutines

import kotlinx.coroutines.CancellationException
import java.lang.ref.WeakReference
import kotlin.coroutines.suspendCoroutine

/**
 * Created by 李志云 2019/1/2 17:06
 */
class WeakRef<T> internal constructor(any: T) {
    private val weakRef = WeakReference(any)
    suspend operator fun invoke(): T {
        return suspendCoroutine {
            val ref = weakRef.get() ?: throw CancellationException()
            ref
        }
    }
}

fun <T : Any> T.weakReference() = WeakRef(this)