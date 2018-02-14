package lzy.com.life_library.listener;

/**
 * Created by lizhiyun on 2018/2/14.
 * 是否切换到后台或前台，true为后台，false为回到前台
 */

public interface AppFourgroundOrBackgroundChangeListener {
    void change(Boolean isToBackground);
}
