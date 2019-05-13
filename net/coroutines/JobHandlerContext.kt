package com.gucci.showad.coroutines

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by 李志云 2019/1/2 11:28
 */

@UseExperimental(InternalCoroutinesApi::class)
val UI = HandlerContext(Handler(Looper.getMainLooper()),"UI")
@UseExperimental(InternalCoroutinesApi::class)
fun Handler.asCoroutineDispatcher() = HandlerContext(this)

@InternalCoroutinesApi
public class  HandlerContext(private val handler:Handler, private val name:String? = null):CoroutineDispatcher(), Delay {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        handler.post(block)
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        handler.postDelayed({ with(continuation){resumeUndispatched(Unit)}},timeMillis)
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        handler.postDelayed(block,timeMillis)
        return object :DisposableHandle{
            override fun dispose() {
                handler.removeCallbacks(block)
            }
        }
    }

    override fun toString(): String {
        return name ?: handler.toString()
    }

    override fun equals(other: Any?): Boolean {
        return other is HandlerContext && other.handler === handler
    }

    override fun hashCode(): Int {
        return System.identityHashCode(handler)
    }
}