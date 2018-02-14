package lzy.com.life_library.listener;

/**
 * Created by lizhiyun on 2018/2/14.
 */

public interface LifeCycleListener {
    void onStart();
    void onResume();
    void onPause();
    void onStop();
    void onDestory();
}
