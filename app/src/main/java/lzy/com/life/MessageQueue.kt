package lzy.com.life

import java.util.*

/**
 * Created by 李志云 2019/2/2 15:18
 */
class MessageQueue {
    var mMessages:Message? = null
    val mLock = Object()
    var isQuit:Boolean = false


    fun enqueueMessage(message: Message){
        synchronized(mLock){
            if (isQuit) return
            var p = mMessages
            if (null == p){
                mMessages = message
            }else{
                var prev:Message?
                while (true){
                    prev = p
                    p = p?.next
                    if (null == p){
                        break
                    }
                }
                prev?.next = message
            }
            mLock.notify()
        }
    }

    fun next(): Message? {
        synchronized(mLock){
            var p:Message?
            while (true){
                if (isQuit) return null
                p= this.mMessages
                if (null != p)break
                mLock.wait()
            }
            mMessages = mMessages?.next
            return p
        }
    }

    fun quit(){
        synchronized(mLock){
            isQuit = true
            var message = mMessages
            while (null != message){
                val next = message.next
                message.recycle()
                message = next
            }
            mLock.notify()
        }

    }
}