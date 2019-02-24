package lzy.com.life

/**
 * Created by 李志云 2019/2/2 15:18
 */
class Handler {
    lateinit var mQueue:MessageQueue
    constructor(){
        val mLooper = Looper.myLooper()
        mQueue = mLooper.mQueue
    }
    fun handleMessage(message: Message){

    }

    fun sendMessage(message: Message){
        message.target = this
        mQueue.enqueueMessage(message)
    }
}