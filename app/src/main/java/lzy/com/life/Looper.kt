package lzy.com.life


/**
 * Created by 李志云 2019/2/2 15:18
 */
class Looper {
    var mQueue:MessageQueue = MessageQueue()

    companion object {
        val threadLocal = ThreadLocal<Looper>()
        fun prepare(){
            if (null != threadLocal.get()){
                throw RuntimeException("${Thread.currentThread()} 已经存在loolper")
            }
            threadLocal.set(Looper())
        }

        fun myLooper():Looper{
            return threadLocal.get()
        }

        fun loop(){
            val looper = Looper.myLooper()
            val queue = looper.mQueue
            while (true){
                val next = queue.next()
                next?.target?.handleMessage(next)
            }
        }
    }
    fun quit(){
        mQueue.quit()
    }
}