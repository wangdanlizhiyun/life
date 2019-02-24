package lzy.com.life

/**
 * Created by 李志云 2019/2/2 15:18
 */
class Message {
    var what:Int = 0
    var obj:Any? = null
    var next:Message? = null
    var target:Handler? = null
    fun recycle(){
        obj = null
        next = null
        target = null
    }
}