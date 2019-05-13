package com.gucci.showad.coroutines

import android.view.View
import kotlinx.coroutines.runBlocking

/**
 * Created by 李志云 2019/1/2 16:24
 */
class TimeMessage(var delayMiles: Long, var setTime: Long)

val mTimeRunnableMap = HashMap<Runnable, TimeMessage>()
fun View.startTime(action: suspend () -> Unit, delayMiles: Long): Runnable {
    var runnable: Runnable? = null
    runnable = kotlinx.coroutines.Runnable {
        runBlocking {
            action()
            mTimeRunnableMap.remove(runnable)
        }
    }
    postDelayed(runnable, delayMiles)
    mTimeRunnableMap[runnable] = TimeMessage(delayMiles, System.currentTimeMillis())
    return runnable
}

fun View.pauseTime(runnable: Runnable?) {
    val timeMessage = mTimeRunnableMap[runnable]
    timeMessage?.let {
        val costTime = System.currentTimeMillis() - it.setTime
        if (costTime > it.delayMiles) {
            mTimeRunnableMap.remove(runnable)
        } else {
            removeCallbacks(runnable)
            it.delayMiles = it.delayMiles - costTime
            it.setTime = System.currentTimeMillis()
        }
    }
}

fun View.resumeTime(runnable: Runnable?) {
    val timeMessage = mTimeRunnableMap[runnable]
    timeMessage?.let {
        if (it.delayMiles >= 0) {
            removeCallbacks(runnable)
            postDelayed(runnable, it.delayMiles)
            it.setTime = System.currentTimeMillis()
        } else {
            mTimeRunnableMap.remove(runnable)
        }
    }
}

fun View.stopTime(runnable: Runnable?) {
    removeCallbacks(runnable)
    mTimeRunnableMap.remove(runnable)
}